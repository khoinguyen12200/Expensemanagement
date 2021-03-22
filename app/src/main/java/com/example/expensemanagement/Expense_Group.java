package com.example.expensemanagement;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;

public class Expense_Group {
    private String name;
    private int id=0;
    private int color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Expense_Group() {
        this.name = "";
        this.color = Color.WHITE;
        this.id=0;
    }
    public Expense_Group(Expense_Group expense_group) {
        this.name = expense_group.name;
        this.color = expense_group.color;
        this.id=expense_group.id;
    }
    public Expense_Group(String name, int color) {
        this.name = name;
        this.color = color;

    }

    public String toString(){
        String s = "name : "+name+", color = "+color+", id = "+id+"\n";
        return s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
