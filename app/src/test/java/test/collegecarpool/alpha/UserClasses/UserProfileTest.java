package test.collegecarpool.alpha.UserClasses;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UserProfileTest {

    private UserProfile userProfile = new UserProfile();

    @Test
    public void toMap() throws Exception {
        HashMap<String, Object> info = new HashMap<>();
        info.put("firstName", "Stephen");
        info.put("secondName", "Cassedy");
        info.put("email", "stephen.cassedy2@mail.dcu.ie");
        info.put("latitude", 38.5647);
        info.put("longitude", 37.2159387);
        info.put("wallet", 10.0);
        UserProfile temp = new UserProfile("Stephen", "Cassedy", "stephen.cassedy2@mail.dcu.ie", 38.5647, 37.2159387, 10.0);
        assertEquals(info.size(), temp.toMap().size());
        for(Map.Entry <String, Object> entry : info.entrySet()){
            assertEquals(entry.getValue(), temp.toMap().get(entry.getKey()));
        }
    }

    @Test
    public void getLongitude() throws Exception {
        userProfile.setLongitude(32.222);
        assertEquals(userProfile.getLongitude(), 32.22, 0.01); //Specify range it can be off by
    }

    @Test
    public void getLatitude() throws Exception {
        userProfile.setLatitude(32.4326);
        assertEquals(userProfile.getLatitude(), 32.4326, 0.001);
    }

    @Test
    public void getFirstName() throws Exception {
        userProfile.setFirstName("Stephen");
        assertEquals(userProfile.getFirstName(), "Stephen");
    }

    @Test
    public void getSecondName() throws Exception {
        userProfile.setSecondName("Cassedy");
        assertEquals(userProfile.getSecondName(), "Cassedy");
    }

    @Test
    public void getEmail() throws Exception {
        userProfile.setEmail("stephen.cassedy2@mail.dcu.ie");
        assertEquals(userProfile.getEmail(), "stephen.cassedy2@mail.dcu.ie");
    }

    @Test
    public void getWallet() throws Exception {
        userProfile.setWallet(10.0);
        assertEquals(userProfile.getWallet(), 10.0, 0.001);
    }

    @Test
    public void setFirstName() throws Exception {
        userProfile.setFirstName("Stephen");
        assertEquals(userProfile.getFirstName(), "Stephen");
    }

    @Test
    public void setSecondName() throws Exception {
        userProfile.setSecondName("Cassedy");
        assertEquals(userProfile.getSecondName(), "Cassedy");
    }

    @Test
    public void setEmail() throws Exception {
        userProfile.setEmail("stephen.cassedy2@mail.dcu.ie");
        assertEquals(userProfile.getEmail(), "stephen.cassedy2@mail.dcu.ie");
    }

    @Test
    public void setLatitude() throws Exception {
        userProfile.setLatitude(10.34);
        assertEquals(userProfile.getLatitude(), 10.34, 0.001);
    }

    @Test
    public void setLongitude() throws Exception {
        userProfile.setLongitude(12.12);
        assertEquals(userProfile.getLongitude(), 12.12, 0.001);
    }

    @Test
    public void setWallet() throws Exception {
        userProfile.setWallet(10.0);
        assertEquals(userProfile.getWallet(), 10.0, 0.001);
    }
}