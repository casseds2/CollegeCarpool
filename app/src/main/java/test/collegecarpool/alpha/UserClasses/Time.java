package test.collegecarpool.alpha.UserClasses;

import java.util.Calendar;

public class Time {
    private int seconds, minutes, hours;

    public Time(){
        Calendar calendar = Calendar.getInstance();
        this.seconds = calendar.getTime().getSeconds();
        this.minutes = calendar.getTime().getMinutes();
        this.hours = calendar.getTime().getHours();
    }

    public String toString(){
        return hours + ":" + minutes + ":" + seconds;
    }

    public Time(int seconds, int minutes, int hours){
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
