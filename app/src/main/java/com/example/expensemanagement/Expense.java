package com.example.expensemanagement;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Expense {
    private String detail;
    private int money;
    private Calendar calendar;
    private int id_group;

    public static int NGAY = Calendar.DATE;
    public static int TUAN = Calendar.WEEK_OF_YEAR;
    public static int THANG = Calendar.MONTH;
    public static int NAM = Calendar.YEAR;

    public Expense(String detail, int money, Date date, int id_group) {
        this.detail = detail;
        this.money = money;
        this.calendar = calendar;
        this.id_group = id_group;
    }
    public Expense() {
        this.detail = "";
        this.money = 0;
        this.calendar = Calendar.getInstance();
        this.id_group = 0;
    }

    public boolean is_Day(Calendar day){
        day.set(Calendar.HOUR,0);
        day.set(Calendar.MINUTE,0);
        day.set(Calendar.SECOND,0);
        day.set(Calendar.MILLISECOND,0);
        Calendar day2= Calendar.getInstance();
        day2.setTimeInMillis(day.getTimeInMillis());
        day2.add(Calendar.DATE,1);
        if(calendar.getTimeInMillis() >=day.getTimeInMillis() && calendar.getTimeInMillis()<day2.getTimeInMillis())
            return true;
        else
            return false;
    }

    public boolean is_same(Calendar calendar1, int chose_type){
        switch (chose_type){
            case Calendar.DATE:
                if(calendar1.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                        && calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                        && calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                ) return true;
                else return false;
            case Calendar.WEEK_OF_YEAR:

                if(calendar1.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR)
                        && calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                ) return true;
                else  return false;

            case Calendar.MONTH:
                if(calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                        && calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                ) return true;
                else return false;

            case Calendar.YEAR:
                if(
                        calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                ) return true;
                else return false;

            default:
                throw new IllegalStateException("Unexpected value: " + chose_type);
        }

    }

    public  String toString(){
        return "tien="+money+" chi tiet="+detail+" id="+id_group+" ngay="+getngay();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
    public String getMoney_string(){
        return NumberFormat.getNumberInstance(Locale.US).format(money);
    }
    public static String getMoney_string(int a){
        return NumberFormat.getNumberInstance(Locale.US).format(a);
    }

    public String getngay(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String s= simpleDateFormat.format(calendar.getTime());
        return s;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getId_group() {
        return id_group;
    }

    public void setId_group(int id_group) {
        this.id_group = id_group;
    }
}
