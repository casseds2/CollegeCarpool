package test.collegecarpool.alpha.MessagingActivities;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    private Message message = new Message();

    @Test
    public void getCopied() throws Exception {
        message.setCopied();
        assertEquals(message.getCopied(), true);
    }

    @Test
    public void setCopied() throws Exception {
        message.setCopied();
        assertEquals(message.getCopied(), true);
    }

    @Test
    public void toMap() throws Exception {

    }

    @Test
    public void setMessage() throws Exception {
        message.setMessage("Hello");
        assertEquals(message.getMessage(), "Hello");
    }

    @Test
    public void setTimeStamp() throws Exception {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        message.setTimeStamp(timeStamp);
        assertEquals(message.getTimeStamp(), timeStamp);
    }

    @Test
    public void getSender() throws Exception {
        message.setSender("Stephen");
        assertEquals(message.getSender(), "Stephen");
    }

    @Test
    public void getTimeStamp() throws Exception {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        message.setTimeStamp(timeStamp);
        assertEquals(message.getTimeStamp(), timeStamp);
    }

    @Test
    public void getMessage() throws Exception {
        message.setMessage("Hello");
        assertEquals(message.getMessage(), "Hello");
    }
}