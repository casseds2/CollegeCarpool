package test.collegecarpool.alpha.MessagingActivities;

import java.util.HashMap;

public class Message {

    private String message;
    private String timeStamp;
    private String sender;
    private boolean copied;

    Message(){}

    Message(String sender, String message){
        this.sender = sender;
        this.message = message;
        copied = false;
        timeStamp = Long.toString(System.currentTimeMillis());
    }

    boolean getCopied(){
        return copied;
    }

    void setCopied(){
        copied = true;
    }

    HashMap<String, Object> toMap(){
        HashMap<String, Object> mapMessage = new HashMap<>();
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

    String getMessage(){
        return message;
    }

}
