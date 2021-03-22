package com.example.expensemanagement;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_Expense extends RecyclerView.Adapter<Adapter_Expense.ViewHolder> {

    ArrayList<Expense> expenses;
    Context context;
    int layout_id;
    private static Adapter_Expense.ClickListener clickListener;


    public Adapter_Expense(ArrayList<Expense> expenses, Context context, int layout_id) {
        this.expenses = expenses;
        this.context = context;
        this.layout_id = layout_id;

    }


    @NonNull
    @Override
    public Adapter_Expense.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View itemView= layoutInflater.inflate(layout_id,parent,false);

        return new Adapter_Expense.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.tien.setText(expense.getMoney_string());
        holder.chi_tiet.setText(expense.getDetail());
        holder.ngay.setText(expense.getngay());


    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }





    @Override
    public int getItemCount() {
        return min(expenses.size(),10);
    }
    private int min(int a, int b){
        if(a>b)
            return b;
        return a;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tien, chi_tiet, ngay ;


        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tien = (TextView) itemView.findViewById(R.id.money);
            chi_tiet = (TextView) itemView.findViewById(R.id.chitiet);
            ngay = (TextView) itemView.findViewById(R.id.ngay);

        }


        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getLayoutPosition(), v);
        }



        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getLayoutPosition(), v);
            return false;
        }
    }
    public void setOnItemClickListener(Adapter_Expense.ClickListener clickListener) {
        Adapter_Expense.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}
