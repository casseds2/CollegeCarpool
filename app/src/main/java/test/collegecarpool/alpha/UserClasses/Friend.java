package test.collegecarpool.alpha.UserClasses;

import java.util.HashMap;

public class Friend {

    private String friendID, friendName;

    public Friend(){}

    public Friend(String friendID, String friendName){
        this.friendID = friendID;
        this.friendName = friendName;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public String getFriendName() {
        return friendName;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("friendID", friendID);
        friendMap.put("userName", friendName);
        return friendMap;
    }
}
