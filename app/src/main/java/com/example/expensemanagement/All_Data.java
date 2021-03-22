package com.example.expensemanagement;

import java.util.ArrayList;
import java.util.Calendar;

public class All_Data {
    private ArrayList<Expense_Group> expense_Group;
    private ArrayList<Expense> expenseArrayList;
    private int title=TUAN;

    public static int TUAN= Calendar.WEEK_OF_YEAR;
    public static int THANG=Calendar.MONTH;

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public All_Data(ArrayList expense_Group, ArrayList expenseArrayList2) {
        this.expense_Group = expense_Group;
        this.expenseArrayList = expenseArrayList2;
    }
    public All_Data() {
        this.expense_Group = new ArrayList<Expense_Group>();
        this.expenseArrayList = new ArrayList<Expense>();
    }

    public ArrayList<Expense> getExpenseArrayList() {
        return expenseArrayList;
    }

    public void setExpenseArrayList(ArrayList<Expense> expenseArrayList) {
        this.expenseArrayList = expenseArrayList;
    }

    public ArrayList getExpense_Group() {
        return expense_Group;
    }

    public void setExpense_Group(ArrayList expense_Group) {
        this.expense_Group = expense_Group;
    }
}
