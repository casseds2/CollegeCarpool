
"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.messageNotification = functions.database.ref("/UserProfile/{receiverID}/Messaging/{senderID}/{timeStamp}")
    .onWrite(event => {
        const message = event.data.val();
        if(message.copied){
            return console.log("Message Was Copied Before");
        }
        console.log("Event Data: " + message);
        console.log("Event Copied: " + message.copied);
        console.log("Event Message: " + message.message);
        console.log("Event Sender: " + message.sender);
        console.log("Event time: " + message.timeStamp);

        message.copied = true;
        const receiverID = event.params.receiverID;
        const senderID = event.params.senderID;
        storeMessageForSender(senderID, receiverID, message);
        var senderName = message.sender;
        sendMessageNotification(receiverID, senderID, senderName, message);
        return event.data.ref.set(message);
    });

    /*Send The Message To The Other User*/
    function storeMessageForSender(senderID, receiverID, message){
        var database = admin.database();
        var messageObj = {};
        database.ref("UserProfile/" + senderID + "/Messaging/" + receiverID + "/" + message.timeStamp).set({
            copied : message.copied,
            message : message.message,
            sender : message.sender,
            timeStamp : message.timeStamp
        });
        console.log("Message Stored For Sender");
    }

    /*Possibly Send senderID as Part of Data so that when Notification is Clicked, message with user pops up through intents*/
    /*Send the Token to A Receiver*/
    function sendMessageNotification(receiverID, senderID, senderName, message){
        var database = admin.database();
        var ref = database.ref("UserProfile/" + receiverID);
        var fcmToken;
        ref.once("value", function(snapshot){
            fcmToken = snapshot.val().fcmToken;
            console.log("FCM Token: " + fcmToken);
            const payload = {
                notification : {
                    title: "You have a new Message!",
                    body: senderName + " Messaged You! \n",
                },
                data : {
                    "type" : "message",
                    "message" : message.message,
                    "senderID" : senderID
                }
            };
            sendNotification(fcmToken, payload);
        });
    }

    /*Send Notification*/
    function sendNotification(fcmToken, payload){
        admin.messaging().sendToDevice(fcmToken, payload)
          .then(function(response) {
            console.log("Successfully sent message:", response);
          })
          .catch(function(error) {
            console.log("Error sending message:", error);
          });
    }

/*Listens for A Ride Request*/
exports.rideRequest = functions.database.ref("/UserProfile/{driverID}/RideRequests/{requestID}/Request")
    .onWrite(event => {

        const request = event.data.val();
        console.log("Request: " + request);

        const driverID = event.params.driverID;
        const requestID = event.params.requestID;

        var senderName = request.username;
        console.log("SenderName: " + senderName);
        sendRequestNotification(driverID, requestID, senderName, request);
    });

    function sendRequestNotification(driverID, requestID, senderName, request){
            var database = admin.database();
            var ref = database.ref("UserProfile/" + driverID);
            var fcmToken;
            ref.once("value", function(snapshot){
                fcmToken = snapshot.val().fcmToken;
                console.log("FCM Token: " + fcmToken);
                const payload = {
                    notification : {
                        title: "You have a Ride Request!",
                        body: senderName + " Says Please Get Me! \n",
                    },
                    data : {
                        "type" : "rideRequest",
                        "lat" : request.lat,
                        "lng" : request.lng,
                        "time" : request.time,
                        "user" : request.user,
                        "username" : request.username
                    }
                };
                sendNotification(fcmToken, payload);
            });
        }

/*Listen For A Friend Request*/
exports.friendRequest = functions.database.ref("/UserProfile/{receiverID}/FriendRequest/{requestID}/")
    .onWrite(event => {

        const request = event.data.val();
        console.log("Friend Request: " + request);
        const userName = request.userName;
        const receiverID = event.params.receiverID;

        sendFriendRequestNotification(receiverID, userName);
    });

    function sendFriendRequestNotification(receiverID, userName){
        var database = admin.database();
        var ref = database.ref("UserProfile/" + receiverID);
        var fcmToken;
        ref.once("value", function(snapshot){
            fcmToken = snapshot.val().fcmToken;
            console.log("FCM Token: " + fcmToken);
            const payload = {
                notification : {
                    title: "New Friend Request",
                },
                data : {
                    "type" : "friendRequest",
                    "username" : userName
                }
            };
            sendNotification(fcmToken, payload);
        });
    }

/*Listen For A Response Status To A Ride Request*/
exports.requestResponse = functions.database.ref("/UserProfile/{driverID}/RideRequests/{requestID}/Response")
    .onWrite(event => {

        const response = event.data.val();
        console.log("Response: " + response);

        const status = response.Status;
        console.log("Status: " + status);

        const requestID = event.params.requestID;
        const driverID = event.params.driverID;

        sendStatusUpdateToRequester(requestID, driverID, status);
    });

    /*Send the Status Update To The Person Who Requested A Lift*/
    function sendStatusUpdateToRequester(requestID, driverID, status){
        var database = admin.database();
        var ref = database.ref("UserProfile/" + requestID);
        var fcmToken;
        ref.once("value", function(snapshot){
            fcmToken = snapshot.val().fcmToken;
            console.log("FCM Token: " + fcmToken);
            const payload = {
                notification : {
                    title: "Request Was " + status
                },
                data : {
                    "type" : "requestResponse",
                    "status" : status,
                    "driverID" : driverID
                }
            };
            sendNotification(fcmToken, payload);
        });
    }