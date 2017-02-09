package test.collegecarpool.alpha.UserClasses;

/**
 * Created by casseds95 for 4TH YEAR Project 07/02/2017.
 */

public class Message {

    private String message, messageSender;

    public Message(){}

    public Message(String message, String messageSender){
        this.message = message;
        this.messageSender = messageSender;
    }

    public String getMessage(){
        return message;
    }

    public String getMessageSender(){
        return messageSender;
    }
}
