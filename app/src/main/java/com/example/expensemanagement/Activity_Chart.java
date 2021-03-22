package com.example.expensemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.github.mikephil.charting.charts.HorizontalBarChart;
public class Activity_Chart extends AppCompatActivity {

    HorizontalBarChart horizontalBarChart;
    ArrayList<BarEntry> yvalues ;
    ArrayList<String> lablename ;
    int choosetype = Calendar.DATE;
    Calendar calendar_from,calendar_to;

    All_Data all_data;
    SharedPreferences sharedPreferences;
    ArrayList expense_Array_List;
    ArrayList expense_Group_List;
    TextView date,group,donvi;
    int id_array[];
    ArrayList<Calendar> calendarArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        add_actionbar_title(getResources().getString(R.string.bieu_do));
        horizontalBarChart = (HorizontalBarChart) findViewById(R.id.barchart2);

        date = (TextView) findViewById(R.id.date);
        group = (TextView) findViewById(R.id.group);
        donvi = (TextView) findViewById(R.id.donvi);
        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List= new ArrayList<Expense>();

        getdata();

        yvalues = new ArrayList<>();
        lablename = new ArrayList<>();


        calendar_from = Calendar.getInstance();
        calendar_from.add(choosetype,-5);
        calendar_to = Calendar.getInstance();


        setBarChart();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_picker_dialog();
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_picker_dialog();
            }
        });
        setDonvi();

    }

    TextView tu,den;
    private void date_picker_dialog(){
        final Dialog dialog = new Dialog(this,R.style.Theme_Design_Light_NoActionBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_activity_chart);

        final RadioButton ngay = (RadioButton) dialog.findViewById(R.id.ngay);
        final RadioButton tuan = (RadioButton) dialog.findViewById(R.id.tuan);
        final RadioButton thang = (RadioButton) dialog.findViewById(R.id.thang);
        final  RadioButton nam = (RadioButton) dialog.findViewById(R.id.nam);
        final CalendarView calendarView1 = (CalendarView) dialog.findViewById(R.id.calendarView1);
        final CalendarView calendarView2 = (CalendarView) dialog.findViewById(R.id.calendarView2) ;
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);

        tu = (TextView) dialog.findViewById(R.id.tu_ngay);
        den = (TextView) dialog.findViewById(R.id.den_ngay);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.ngay:
                        choosetype = Calendar.DATE;
                        setradiotext();
                        break;
                    case R.id.tuan:
                        choosetype = Calendar.WEEK_OF_YEAR;
                        setradiotext();
                        break;
                    case R.id.thang:
                        choosetype = Calendar.MONTH;
                        setradiotext();
                        break;
                    case R.id.nam:
                        choosetype = Calendar.YEAR;
                        setradiotext();
                        break;

                }
            }
        });
        setradiotext();



        ImageView back = (ImageView) dialog.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        switch (choosetype){
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


        calendarView1.setDate(calendar_from.getTimeInMillis());
        calendarView2.setDate(calendar_to.getTimeInMillis());

        calendarView1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar_from = Calendar.getInstance();
                calendar_from.set(Calendar.YEAR,year);
                calendar_from.set(Calendar.MONTH,month);
                calendar_from.set(Calendar.DATE,dayOfMonth);

                setradiotext();
            }
        });
        calendarView2.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar_to = Calendar.getInstance();
                calendar_to.set(Calendar.YEAR,year);
                calendar_to.set(Calendar.MONTH,month);
                calendar_to.set(Calendar.DATE,dayOfMonth);

                setradiotext();
            }
        });
        

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(ngay.isChecked())
                    choosetype = Calendar.DATE;
                if(tuan.isChecked())
                    choosetype = Calendar.WEEK_OF_YEAR;
                if(thang.isChecked())
                    choosetype= Calendar.MONTH;
                if(nam.isChecked())
                    choosetype= Calendar.YEAR;
                setBarChart();


            }
        });
        dialog.show();
    }
    private void setradiotext(){
        tu.setText(getResources().getString(R.string.tu_ngay)+" "+getngay2(calendar_from));
        den.setText(getResources().getString(R.string.den_ngay)+" "+getngay2(calendar_to));
    }
    private String getngay2(Calendar calendar){
        String s="";
        switch (choosetype){
            case Calendar.DATE:
                s=getResources().getString(R.string.ngay)+" "+calendar.get(Calendar.DATE);
                break;
            case Calendar.WEEK_OF_YEAR:
                s=getResources().getString(R.string.tuan)+" "+calendar.get(Calendar.WEEK_OF_MONTH);
                break;
            case Calendar.MONTH:
                s=getResources().getString(R.string.thang)+" "+(calendar.get(Calendar.MONTH)+1);
                break;
            case Calendar.YEAR:
                s=getResources().getString(R.string.nam)+" "+calendar.get(Calendar.YEAR);
                break;
        }
        return s;
    }

    private void group_picker_dialog(){
        final Dialog dialog = new Dialog(this,R.style.Theme_Design_Light_NoActionBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_group_chose);
        ImageView back = (ImageView) dialog.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.layout);
        for(int i=0;i<expense_Group_List.size();i++){
            final Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            CheckBox checkBox = new CheckBox(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            checkBox.setLayoutParams(layoutParams);
            checkBox.setPadding(50,50,50,50);
            checkBox.setTextSize(18);
            checkBox.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            checkBox.setText(expense_group.getName());
            if(id_array[i]==expense_group.getId())
                checkBox.setChecked(true);
            else checkBox.setChecked(false);

            final int finalI = i;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        id_array[finalI]=expense_group.getId();
                    else id_array[finalI]=-1;
                }
            });
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3);
            imageView.setLayoutParams(layoutParams2);
            imageView.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(checkBox);
            linearLayout.addView(imageView);
        }

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                setBarChart();
            }
        });
        dialog.show();
    }



    private void getdata(){
        Gson gson1 = new Gson();
        String json1 = sharedPreferences.getString("all_data", "");
        if(!json1.equals("")){
            all_data=gson1.fromJson(json1,All_Data.class);
            expense_Group_List = all_data.getExpense_Group();
            expense_Array_List = all_data.getExpenseArrayList();
        }
        id_array = new int[expense_Group_List.size()];
        for(int i=0;i<id_array.length;i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            id_array[i]=expense_group.getId();
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

    private void setdata(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar_from.getTimeInMillis());

        calendar.set(calendar_from.get(Calendar.YEAR),
                calendar_from.get(Calendar.MONTH),
                calendar_from.get(Calendar.DATE),0,0,0);
        calendar.set(Calendar.MILLISECOND,0);

        calendar_to.set(calendar_to.get(Calendar.YEAR),
                calendar_to.get(Calendar.MONTH),
                calendar_to.get(Calendar.DATE),0,0,0);
        calendar_to.set(Calendar.MILLISECOND,0);

        int i=0;
        yvalues = new ArrayList<>();
        lablename = new ArrayList<>();
        calendarArrayList = new ArrayList<>();
        while(calendar.getTimeInMillis() <= calendar_to.getTimeInMillis()){
            yvalues.add(new BarEntry(i,laytong(calendar)));
            lablename.add(getngay(calendar));
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(calendar.getTimeInMillis());
            calendarArrayList.add(calendar1);
            i++;
            calendar.add(choosetype,1);
        }
    }
    private int laytong(Calendar calendar){
        int tong = 0;
        for(int i=0;i<expense_Array_List.size();i++){
            Expense expense = (Expense) expense_Array_List.get(i);
            if(expense.is_same(calendar,choosetype) && trong_mang(expense.getId_group())){
                tong+=expense.getMoney();
            }
            setDonvi();
        }
        return tong;
    }
    private boolean trong_mang(int id){
        boolean flag = false;
        for(int i=0;i<id_array.length;i++){
            if(id == id_array[i])
                flag=true;
        }
        return flag;
    }
    private void setDonvi(){
        switch (choosetype){
            case Calendar.DATE:
                donvi.setText(getResources().getString(R.string.ngay));
                break;
            case Calendar.WEEK_OF_YEAR:
                donvi.setText(getResources().getString(R.string.tuan));
                break;
            case Calendar.MONTH:
                donvi.setText(getResources().getString(R.string.thang));
                break;
            case Calendar.YEAR:
                donvi.setText(getResources().getString(R.string.nam));
                break;
        }
    }
    private String getngay(Calendar calendar){
        String s="";
        switch (choosetype){
            case Calendar.DATE:
                s=""+calendar.get(Calendar.DATE);
                break;
            case Calendar.WEEK_OF_YEAR:
                s=""+calendar.get(Calendar.WEEK_OF_MONTH);
                break;
            case Calendar.MONTH:
                s=""+(calendar.get(Calendar.MONTH)+1);
                break;
            case Calendar.YEAR:
                s=""+calendar.get(Calendar.YEAR);
                break;
        }
        return s;
    }
    private void setBarChart(){




        setdata();

        BarDataSet barDataSet =new BarDataSet(yvalues,"Thong ke");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(14);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int a = (int) value;
                return Expense.getMoney_string(a);
            }
        });



        BarData barData = new BarData(barDataSet);



        horizontalBarChart.setFitBars(true);
        horizontalBarChart.setData(barData);
        horizontalBarChart.animateXY(0,2000);
        horizontalBarChart.invalidate();
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(chose_x==e.getX()){
                    Intent intent = new Intent(Activity_Chart.this,Activity_total_expense.class);
                    Calendar calendar = calendarArrayList.get((int) e.getX());

                    intent.putExtra("chose_type",choosetype);
                    intent.putExtra("calendar",calendar.getTimeInMillis());
                    startActivityForResult(intent,0);
                }else{
                    chose_x= (int) e.getX();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        XAxis xAxis1 = horizontalBarChart.getXAxis();
        xAxis1.setTextSize(14);
        xAxis1.setValueFormatter(new IndexAxisValueFormatter(lablename));
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setDrawGridLines(false);
        xAxis1.setDrawAxisLine(false);
        xAxis1.setGranularity(1f);
        xAxis1.setLabelCount(lablename.size());
        xAxis1.setLabelRotationAngle(0);

        YAxis yAxiz_right = horizontalBarChart.getAxisRight();
        yAxiz_right.setDrawLabels(false);
    }
    int chose_x=-1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
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
}