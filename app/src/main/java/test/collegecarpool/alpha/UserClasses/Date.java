package test.collegecarpool.alpha.UserClasses;

public class Date {

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

    boolean compareTo(Date d){
        return day == d.day && month == d.month && year == d.year;
    }

    public String toString(){
        return day + "/" + month + "/" + year;
    }
}