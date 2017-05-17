package test.collegecarpool.alpha.UserClasses;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FriendTest {

    private Friend friend = new Friend();

    @Test
    public void getFriendID() throws Exception {
        friend.setFriendID("123");
        assertEquals(friend.getFriendID(), "123");
    }

    @Test
    public void setFriendName() throws Exception {
        friend.setFriendName("Stephen");
        assertEquals(friend.getFriendName(), "Stephen");
    }

    @Test
    public void setFriendID() throws Exception {
        friend.setFriendID("123");
        assertEquals(friend.getFriendID(), "123");
    }

    @Test
    public void getFriendName() throws Exception {
        friend.setFriendID("Stephen");
        assertEquals(friend.getFriendID(), "Stephen");
    }

    @Test
    public void toMap() throws Exception {
        HashMap<String, Object> info = new HashMap<>();
        info.put("friendID", "123");
        info.put("userName", "Stephen");
        Friend friend = new Friend("123", "Stephen");
        assertEquals(info.size(), friend.toMap().size());
        for(Map.Entry <String, Object> entry : info.entrySet()){
            assertEquals(entry.getValue(), friend.toMap().get(entry.getKey()));
        }
    }

}