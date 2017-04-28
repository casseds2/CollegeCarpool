package test.collegecarpool.alpha.UserClasses;

import java.io.Serializable;

public class Date implements Serializable{ //implements so it can be passed in Journey object as intent extra

    private int day;

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private int month;
    private int year;

    public Date(){}

    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay(){
        return this.day;
    }

    public int getMonth(){
        return this.month;
    }

    public int getYear(){
        return this.year;
    }

    public boolean compareTo(Date d){
        return day == d.day && month == d.month && year == d.year;
    }

    //If date is more present than d, return true if date is in future/present, false otherwise
    //Return True if Today is in the Before Date d
    public boolean isBefore(Date d){
        java.util.Date dateLocal = new java.util.Date(this.day, this.month, this.year);
        java.util.Date dateParam = new java.util.Date(d.day, d.month, d.year);
        return dateLocal.before(dateParam);
    }

    public String toString(){
        return day + "/" + month + "/" + year;
    }

}
