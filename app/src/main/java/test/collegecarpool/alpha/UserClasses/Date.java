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
    public boolean inThePastTo(Date d){
        //return this.year < d.year && this.month < d.month && this.day < d.day;
        if(this.year <= d.year){
            if(this.month <= d.month){
                if(this.day <= d.day){
                    return false;
                }
                else
                    return true;
            }
            else
                return true;
        }
        else
            return true;
    }

    public String toString(){
        return day + "/" + month + "/" + year;
    }
}
