package test.collegecarpool.alpha.MessagingActivities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class Message {

    private String message;
    private String timeStamp;
    private String sender;
    private String uid;
    private boolean copied;
    private FirebaseUser user;

    Message(){}

    Message(String sender, String message){
        this.sender = sender;
        this.message = message;
        copied = false;
        timeStamp = Long.toString(System.currentTimeMillis());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
    }

    boolean getCopied(){
        return copied;
    }

    public String getUid(){
        return uid;
    }

    void setUid(String uid){
        this.uid = uid;
    }

    void setCopied(){
        copied = true;
    }

    HashMap<String, Object> toMap(){
        HashMap<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("uid", uid);
        mapMessage.put("sender", sender);
        mapMessage.put("message", message);
        mapMessage.put("copied", copied);
        mapMessage.put("timeStamp", timeStamp);
        return mapMessage;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    void setSender(String sender){
        this.sender = sender;
    }

    void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    String getSender(){
        return sender;
    }

    String getTimeStamp(){
        return timeStamp;

    }

    public String getMessage(){
        return message;
    }
}
