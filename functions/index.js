//var functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

/*Send A Notification When A Message/Lift Request Received in Database*/
exports.sendMessageNotification = functions.database.ref("/UserProfile/{receiverID}/Messaging/{senderID}/").onWrite(event=>{

    if(!event.data.exists()){
        return console.log("No Event Data");
    }

    var database = admin.database();

    const receiverID = event.params.receiverID;
    const senderID = event.params.senderID;

    var userName;

    /*Get the Sender's Username*/
    var senderRef = database.ref("UserProfile/" + senderID);
    senderRef.once("value", function(snapshot){
       console.log(snapshot.val());
       userName = snapshot.val().firstName + " " + snapshot.val().secondName;
    });

    var receiverFcmToken;

    /*Read The Receiver's FCM From Firebase*/
    var receiverRef =  database.ref("/UserProfile/" + receiverID);
    receiverRef.once("value", function(snapshot){
        console.log(snapshot.val());
        receiverFcmToken = snapshot.val().fcmToken;
        console.log("FCM Token: " + receiverFcmToken);

        /*Set the Payload for The Notification*/
            const payload = {
                notification : {
                    title: "You have a new Message!",
                    body: userName + " Messaged You!",
                    //"data": {
                    //    senderID : "senderID"
                    //}
                }
            };

            /*Send The Notification*/
            admin.messaging().sendToDevice(receiverFcmToken, payload)
              .then(function(response) {
                console.log("Successfully sent message:", response);
              })
              .catch(function(error) {
                console.log("Error sending message:", error);
              });
    });

    console.log("New Message From UID:", senderID, " for user: ", receiverID);
});
