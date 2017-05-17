package test.collegecarpool.alpha.UserClasses;

import java.io.Serializable;

public class Date implements Serializable{ //implements so it can be passed in Journey object as intent extra

    private int day;
    private int month;
    private int year;

    public Date(){}

    public Date(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
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

    public boolean isEqualTo(Date d){
        return day == d.day && month == d.month && year == d.year;
    }

    //If date is more present than d, return true if date is in future/present, false otherwise
    //Return True if Today is before Date d
    public boolean isBefore(Date d){
        //java.util.Date dateLocal = new java.util.Date(this.day, this.month, this.year);
        //java.util.Date dateParam = new java.util.Date(d.day, d.month, d.year);
        //return dateLocal.before(dateParam);
        if(this.year <= d.getYear()){
            if(this.month <= d.getMonth()){
                return this.day <= d.getDay();
            }
            else
                return false;
        }
        else
            return false;
    }

    public String toString(){
        return day + "/" + month + "/" + year;
    }

}
