package test.collegecarpool.alpha.MessagingActivities;

public class Message {

    private String message;
    private String timeStamp;
    private String sender;

    public Message(){}

    Message(String sender, String message){
        this.sender = sender;
        this.message = message;
        timeStamp = Long.toString(System.currentTimeMillis());
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(String timeStamp) {
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
