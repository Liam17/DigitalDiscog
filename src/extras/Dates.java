package extras;

import java.util.Date;
import java.util.Calendar;

public class Dates {
    private String day;
    private String month;
    private String year;

    public Dates(String date){
        String[] dateDecomposed = date.split("/");
        day = dateDecomposed[0];
        month = dateDecomposed[1];
        year = dateDecomposed[2];
    }

    public int getDay(){
        int dayInt;
        return dayInt = Integer.parseInt(this.day);
    }

    public int getMonth(){
        int monthInt;
        return monthInt = Integer.parseInt(this.month);
    }

    public int getYear(){
        int yearInt;
        return yearInt = Integer.parseInt(this.year);
    }

    public int getYearsPassed(){
        int yearsPassed = 0;

        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        int yearNow = d.getYear() + 1900;
        int monthNow = d.getMonth() + 1;
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);

        if(this.getYear() < yearNow){
            if(this.getMonth() < monthNow){
                yearsPassed = yearNow - this.getYear();
            }
            else if(this.getMonth() == monthNow){
                if(this.getDay() < dayNow){
                    yearsPassed = yearNow - this.getYear();
                }
                else{
                    yearsPassed = yearNow - this.getYear() + 1;
                }
            }
            else{
                yearsPassed = yearNow - this.getYear() + 1;
            }
        }

        return yearsPassed;
    }

    public int whichCameLast(Dates first, Dates second){// Return 1 if first is more recent, 2 if second, 0 if they the same
        int which = 0;

        if (second.getYear() - first.getYear() >0){
            which = 2;
        }
        else if (second.getYear() - first.getYear() <0){
            which = 1;
        }
        else {
            if (second.getMonth() - first.getMonth() >0){
                which = 2;
            }
            else if (second.getMonth() - first.getMonth() <0){
                which = 1;
            }
            else{
                if (second.getDay() - first.getDay() >0){
                    which = 2;
                }
                else if (second.getDay() - first.getDay() <0){
                    which = 1;
                }
            }
        }
        return which;
    }

    public String toString(){
        return this.day + "/" + this.month + "/" + this.year;
    }
}
