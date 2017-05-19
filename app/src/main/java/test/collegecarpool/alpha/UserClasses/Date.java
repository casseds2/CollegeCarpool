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

    //Return True if this is before Date d
    public boolean isBefore(Date d){
        if(year <= d.getYear()){ //If Year is <= To
            if(year == d.getYear()){ //Years Are Equal To
                if(month <= d.getMonth()){ //If <= To
                    if(month == d.getMonth()){ //Is Equal To
                        if(day <= d.getDay()){ //If Day is Less Than Or Equal To
                            return true;
                        }
                        else
                            return false;
                    }
                    else{ //Is Less Than
                        return true;
                    }
                }
                else{
                    return false;
                }
            }
            else{ //If Today Year Is Less Than
                return true;
            }
        }
        else
            return false;
    }

    public String toString(){
        return day + "/" + month + "/" + year;
    }

}
