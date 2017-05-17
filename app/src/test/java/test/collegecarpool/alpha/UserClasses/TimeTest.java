package test.collegecarpool.alpha.UserClasses;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeTest {

    private Time time = new Time();

    @Test
    public void getSeconds() throws Exception {
        time.setSeconds(30);
        assertEquals(time.getSeconds(), 30);
    }

    @Test
    public void setSeconds() throws Exception {
        time.setSeconds(30);
        assertEquals(time.getSeconds(), 30);
    }

    @Test
    public void getMinutes() throws Exception {
        time.setMinutes(10);
        assertEquals(time.getMinutes(), 10);
    }

    @Test
    public void setMinutes() throws Exception {
        time.setMinutes(30);
        assertEquals(time.getMinutes(), 30);
    }

    @Test
    public void getHours() throws Exception {
        time.setHours(10);
        assertEquals(time.getHours(), 10);
    }

    @Test
    public void setHours() throws Exception {
        time.setHours(2);
        assertEquals(time.getHours(), 2);
    }

}