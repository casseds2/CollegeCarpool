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

    int getSeconds() {
        return seconds;
    }

    void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    int getMinutes() {
        return minutes;
    }

    void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    int getHours() {
        return hours;
    }

    void setHours(int hours) {
        this.hours = hours;
    }
}
