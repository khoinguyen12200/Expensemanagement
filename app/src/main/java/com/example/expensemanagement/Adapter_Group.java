package com.example.expensemanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_Group extends RecyclerView.Adapter<Adapter_Group.ViewHolder> {

    ArrayList<Expense_Group> expense_groups;
    Context context;
    private static ClickListener clickListener;





    public Adapter_Group(ArrayList<Expense_Group> expense_groups, Context context) {
        this.expense_groups = expense_groups;
        this.context = context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View itemView= layoutInflater.inflate(R.layout.layout_group_view,parent,false);

        return new ViewHolder(itemView);
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tvName.setText(expense_groups.get(holder.getLayoutPosition()).getName());

        setcolor(holder,expense_groups.get(holder.getLayoutPosition()).getColor());
    }


    public void setcolor(ViewHolder holder, int color){

        int[][] states = new int[][] {
                new int[] { android.R.attr.enabled},
                {- android.R.attr.enabled},
        };
        int[] colors = new int[] {
               color,
                color,
        };
        ColorStateList myList = new ColorStateList(states, colors);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.background.setBackgroundTintList(myList);
        }
    }


    @Override
    public int getItemCount() {
        return expense_groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tvName;
        ConstraintLayout background;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvName = (TextView) itemView.findViewById(R.id.group_name);
            background = (ConstraintLayout) itemView.findViewById(R.id.background_group);

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
    public void setOnItemClickListener(ClickListener clickListener) {
        Adapter_Group.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}
