package com.example.expensemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_total_expense extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    All_Data all_data;
    ArrayList expense_Group_List;
    ArrayList expense_Array_List;
    Expense_Group mainGroup;
    TextView TVtong;

    TextView date;
    LinearLayout list;
    TextView bieudo;

    Calendar calendar;
    int chose_type = Expense.NGAY;

    PieChart pieChart;
    LinearLayout backpie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_expense);
        anhxa();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List = new ArrayList<Expense>();
        mainGroup = new Expense_Group();

        getdata();

        Intent intent = getIntent();
        calendar = Calendar.getInstance();
        long day = intent.getLongExtra("calendar",Calendar.getInstance().getTimeInMillis());
        calendar.setTimeInMillis(day);
        chose_type = intent.getIntExtra("chose_type",Expense.NGAY);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_picker_dialog();
            }
        });

        setchose();

        bieudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backpie.getVisibility()==View.VISIBLE){
                    collapse(backpie);
                    bieudo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_baseline_arrow_drop_down_24,0);
                }

                else{
                    expand(backpie);
                    bieudo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_up_24,0);
                }

            }
        });


        values = new ArrayList<>();
        colors = new ArrayList<>();

        show();



    }
    ArrayList <Integer> colors;
    ArrayList<PieEntry> values;
    private void setPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setHoleColor(getResources().getColor(R.color.none));
        pieChart.setTransparentCircleRadius(55f);
        pieChart.animateY(1000, Easing.EaseInOutCubic);


        PieDataSet dataSet = new PieDataSet(values,"");
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextColor(Color.BLACK);


        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);


    }



    private void date_picker_dialog(){
        final Dialog dialog = new Dialog(this,R.style.Theme_Design_Light_NoActionBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_picker);

        final RadioButton ngay = (RadioButton) dialog.findViewById(R.id.ngay);
        final RadioButton tuan = (RadioButton) dialog.findViewById(R.id.tuan);
        final RadioButton thang = (RadioButton) dialog.findViewById(R.id.thang);
        final  RadioButton nam = (RadioButton) dialog.findViewById(R.id.nam);
        final CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarView);
        ImageView back = (ImageView) dialog.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        switch (chose_type){
            case Calendar.DATE:
                ngay.setChecked(true);
                break;
            case Calendar.WEEK_OF_YEAR:
                tuan.setChecked(true);
                break;
            case Calendar.MONTH:
                thang.setChecked(true);
                break;
            case Calendar.YEAR:
                nam.setChecked(true);
                break;
        }

        calendarView.setDate(calendar.getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DATE,dayOfMonth);

            }
        });



        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(ngay.isChecked())
                    chose_type = Calendar.DATE;
                if(tuan.isChecked())
                    chose_type = Calendar.WEEK_OF_YEAR;
                if(thang.isChecked())
                    chose_type= Calendar.MONTH;
                if(nam.isChecked())
                    chose_type= Calendar.YEAR;
                setchose();

                show();
            }
        });


        dialog.show();
    }


    private void setchose(){
        switch (chose_type){
            case Calendar.DATE:
                date.setText(getResources().getString(R.string.ngay)+" "+calendar.get(Calendar.DATE));
                break;
            case Calendar.WEEK_OF_YEAR:
                date.setText(getResources().getString(R.string.tuan)+" "+calendar.get(Calendar.WEEK_OF_MONTH));
                break;
            case Calendar.MONTH:
                date.setText(getResources().getString(R.string.thang)+" "+(calendar.get(Calendar.MONTH)+1));
                break;
            case Calendar.YEAR:
                date.setText(getResources().getString(R.string.nam)+" "+calendar.get(Calendar.YEAR));
                break;
        }
    }

    private boolean isday(Expense expense){
            return expense.is_same(calendar,chose_type);
    }






    public void add_actionbar_title(String s){
        androidx.appcompat.app.ActionBar abar = getSupportActionBar();

        View viewActionBar = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
        androidx.appcompat.app.ActionBar.LayoutParams params =new androidx.appcompat.app.ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(s);
        abar.setCustomView(viewActionBar,params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
    }



    Animation popup;
    int tong=0;

    private int tinh_tong(){
        int sum=0;
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            for(int j=0;j<expense_Array_List.size();j++){
                Expense expense = (Expense) expense_Array_List.get(j);
                if(isday(expense))
                    if(expense.getId_group()==expense_group.getId())
                        sum+=expense.getMoney();
            }
        }
        return sum;

    }

    private void show(){
        add_actionbar_title(getResources().getString(R.string.tong_hop));
        list.removeAllViews();
        tong = tinh_tong();
        values = new ArrayList<>();
        colors = new ArrayList<>();
        for(int i=0;i<expense_Group_List.size();i++){
            final Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);

            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.layout_group_expense, null);

            LinearLayout back = (LinearLayout) v.findViewById(R.id.background);
            final TextView group = (TextView) v.findViewById(R.id.group);
            final LinearLayout listview = (LinearLayout) v.findViewById(R.id.liste);
            TextView tong = (TextView) v.findViewById(R.id.tong);



            group.setText(expense_group.getName());
            group.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(Activity_total_expense.this,Activity_List_Expense.class);
                    intent.putExtra("id",expense_group.getId());
                    intent.putExtra("calendar",calendar.getTimeInMillis());
                    intent.putExtra("chose_type",chose_type);
                    startActivityForResult(intent,0);
                    return false;
                }
            });
            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listview.getVisibility()==View.VISIBLE){
                        collapse(listview);
                        group.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_baseline_arrow_drop_down_24,0);

                    }else{
                        expand(listview);
                        group.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_up_24,0);
                    }
                }
            });
            int tong_nho = add_expense_views(expense_group.getId(),listview);

            tong.setText(getResources().getString(R.string.tong)+": "+layDauPhay(tong_nho));

            if(tong_nho!=0){
                PieEntry pieEntry = new PieEntry(tong_nho,expense_group.getName());
                values.add(pieEntry);
                colors.add(expense_group.getColor());
            }


            list.addView(v);

        }
        setPieChart();
        TVtong.setText(layDauPhay(tong));
    }

    private int lay_phan_tram(int nho, int lon){
        double pt =((double) nho/ (double)lon)*100;
        if(pt%1 < 0.5){
            return (int) Math.floor(pt);
        }else
            return (int) Math.round(pt);
    }


    private int add_expense_views(final int idgroup, LinearLayout linearLayout){
        int tong_nho =0;
        for(int i=0;i<expense_Array_List.size();i++){
            Expense expense = (Expense) expense_Array_List.get(i);
            if(expense.getId_group()==idgroup && isday(expense)){

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.expense_item, null);

                TextView time = (TextView) v.findViewById(R.id.ngay);
                TextView chitiet = (TextView) v.findViewById(R.id.chitiet);
                TextView money = (TextView) v.findViewById(R.id.money);

                time.setText(expense.getngay());
                chitiet.setText(expense.getDetail());
                money.setText(expense.getMoney_string());

                final int finalI = i;
                v.setAnimation(popup);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Activity_total_expense.this,Activity_Edit_Expense.class);
                        intent.putExtra("id",idgroup);
                        intent.putExtra("position",finalI);
                        startActivityForResult(intent,0);

                    }
                });

                linearLayout.addView(v);
                tong_nho+=expense.getMoney();
            }
        }
        return tong_nho;
    }

    public static void expand(final View v) {

        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public String layDauPhay(int a){
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String s=nf.format(a);
        return s;
    }



    public void anhxa(){
        backpie= (LinearLayout) findViewById(R.id.back_pie);
        bieudo = (TextView) findViewById(R.id.bieu_do);
        pieChart = (PieChart) findViewById(R.id.piechart);
        date = (TextView) findViewById(R.id.date);
        TVtong = (TextView) findViewById(R.id.tong);
        list = (LinearLayout) findViewById(R.id.list);
    }
    private void exit(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                exit();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getdata();
        show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getdata(){

        Gson gson1 = new Gson();
        String json1 = sharedPreferences.getString("all_data", "");
        if(!json1.equals("")){
            all_data=gson1.fromJson(json1,All_Data.class);
            expense_Group_List = all_data.getExpense_Group();
            expense_Array_List = all_data.getExpenseArrayList();
        }
    }
    private void savedata(){


        all_data.setExpense_Group(expense_Group_List);
        all_data.setExpenseArrayList(expense_Array_List);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(all_data);
        editor.putString("all_data",json1);
        editor.commit();
    }
}