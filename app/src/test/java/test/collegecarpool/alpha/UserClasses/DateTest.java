package test.collegecarpool.alpha.UserClasses;

import static org.junit.Assert.*;

public class DateTest {

    private Date date = new Date();

    @org.junit.Test
    public void setDay() throws Exception {
        date.setDay(5);
        assertEquals(date.getDay(),5);
    }

    @org.junit.Test
    public void setMonth() throws Exception {
        date.setMonth(5);
        assertEquals(date.getMonth(), 5);
    }

    @org.junit.Test
    public void setYear() throws Exception {
        date.setYear(2017);
        assertEquals(date.getYear(), 2017);
    }

    @org.junit.Test
    public void getDay() throws Exception {
        Date date = new Date(1, 2, 2017);
        assertEquals(date.getDay(), 1);
    }

    @org.junit.Test
    public void getMonth() throws Exception {
        Date date = new Date(1, 2, 2017);
        assertEquals(date.getMonth(), 2);
    }

    @org.junit.Test
    public void getYear() throws Exception {
        Date date = new Date(1, 2, 2017);
        assertEquals(date.getYear(), 2017);
    }

    @org.junit.Test
    public void isEqualTo() throws Exception {
        Date date1 = new Date(1, 3, 2017);
        Date date2 = new Date(1, 3, 2017);
        assertEquals(!date1.isEqualTo(date2), false);
    }

    @org.junit.Test
    public void isBefore() throws Exception {
        Date date1 = new Date(21, 5, 2017);
        Date date2 = new Date(21, 5, 2017);
        assertEquals(date1.isBefore(date2), true);
    }
}