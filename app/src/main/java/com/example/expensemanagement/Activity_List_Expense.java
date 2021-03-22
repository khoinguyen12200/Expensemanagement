package com.example.expensemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;



public class Activity_List_Expense<Piechart> extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    All_Data all_data;
    ArrayList expense_Group_List;
    ArrayList expense_Array_List;
    Expense_Group mainGroup;
    TextView TVtong;
    FloatingActionButton floatingActionButton;
    TextView date;
    LinearLayout list;
    int id;


    Calendar calendar;
    int chose_type = Expense.THANG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        anhxa();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List = new ArrayList<Expense>();
        mainGroup = new Expense_Group();



        Intent intent = getIntent();
        id=intent.getIntExtra("id",-1);
        if(id==-1)
            finish();


        getdata();

        calendar = Calendar.getInstance();
        long day = intent.getLongExtra("calendar",Calendar.getInstance().getTimeInMillis());
        calendar.setTimeInMillis(day);
        chose_type = intent.getIntExtra("chose_type",Expense.NGAY);


        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId()==id)
                mainGroup=new Expense_Group(expense_group);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_itent= new Intent(Activity_List_Expense.this, Activity_Add_Expense.class);
                add_itent.putExtra("id",mainGroup.getId());
                startActivityForResult(add_itent,0);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_picker_dialog();
            }
        });





        setchose();


        show();
    }





    private void date_picker_dialog(){
        final Dialog  dialog = new Dialog(this,R.style.Theme_Design_Light_NoActionBar);
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




    int tong=0;
    private void show(){
        tong=0;
        add_actionbar_title(mainGroup.getName());
        list.removeAllViews();

        for(int i=0;i<expense_Array_List.size();i++){
            Expense expense = (Expense) expense_Array_List.get(i);

            if(expense.getId_group() == id  && expense.is_same(calendar,chose_type)){


                tong+=expense.getMoney();
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.expense_item, null);


                TextView time = (TextView) v.findViewById(R.id.ngay);
                TextView chitiet = (TextView) v.findViewById(R.id.chitiet);
                TextView money = (TextView) v.findViewById(R.id.money);

                time.setText(expense.getngay());
                chitiet.setText(expense.getDetail());
                money.setText(expense.getMoney_string());

                final int finalI = i;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Activity_List_Expense.this,Activity_Edit_Expense.class);
                        intent.putExtra("id",id);
                        intent.putExtra("position",finalI);
                        startActivityForResult(intent,0);

                    }
                });
                list.addView(v);
            }

        }
        TVtong.setText(layDauPhay(tong));
    }
    public String layDauPhay(int a){
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String s=nf.format(a);
        return s;
    }



    public void anhxa(){
        date = (TextView) findViewById(R.id.date);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.add) ;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                exit();
                return true;
            case R.id.edit:
                Intent intent =new Intent(Activity_List_Expense.this, Activity_Edit_Group.class).
                        putExtra("id",id);
                startActivityForResult(intent,0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getdata();
        boolean flag = false;
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId()==id) {
                mainGroup = new Expense_Group(expense_group);
                flag = true;
            }
        }
        if(!flag) exit();
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