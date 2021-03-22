package com.example.expensemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Activity_Edit_Expense extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    All_Data all_data;
    ArrayList expense_Group_List;
    ArrayList expense_Array_List;
    Expense_Group mainGroup;

    int id;

    EditText money;
    EditText detail;
    TextView time;
    Calendar calendar;
    CalendarView calendarView;
    FloatingActionButton delete;
    Expense expense;
    int position;
    FlexboxLayout flexboxLayout2,flexboxLayout1;
    Context context;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__edit__expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close_white);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar);

        add_actionbar_title(getResources().getString(R.string.sua_chi_tieu));

        context = this;

        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List = new ArrayList<Expense>();
        mainGroup = new Expense_Group();
        flexboxLayout2 = (FlexboxLayout) findViewById(R.id.flexbox2);
        flexboxLayout1 = (FlexboxLayout) findViewById(R.id.flexbox1);

        money = (EditText) findViewById(R.id.money);
        detail = (EditText) findViewById(R.id.detail);
        time = (TextView) findViewById(R.id.time);




        Intent intent = getIntent();
        id=intent.getIntExtra("id",-1);
        if(id==-1)
            finish();
        position = intent.getIntExtra("position",-1);
        if(position==-1)
            finish();


        getdata();

        boolean flag = false;
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId()==id){
                flag = true;
                mainGroup=new Expense_Group(expense_group);
            }

        }
        if(!flag)
            finish();

        expense = (Expense) expense_Array_List.get(position);

        money.addTextChangedListener(new NumberTextWatcherForThousand(money));


        money.setText(expense.getMoney()+"");
        detail.setText(expense.getDetail());

        NumberTextWatcherForThousand numberTextWatcherForThousand = new NumberTextWatcherForThousand(money);
        calendar = expense.getCalendar();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String ngaythangnam="";
        if(!expense.is_Day(Calendar.getInstance()))
            ngaythangnam = simpleDateFormat.format(calendar.getTime());
        else
            ngaythangnam = getResources().getString(R.string.hom_nay);
        time.setText(ngaythangnam);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                calendar.set(year,month,dayOfMonth);
                Expense expense = new Expense();
                expense.setCalendar(calendar);
                String ngaythangnam="";
                if(!expense.is_Day(Calendar.getInstance()))
                    ngaythangnam = simpleDateFormat.format(calendar.getTime());
                else
                    ngaythangnam = getResources().getString(R.string.hom_nay);
                time.setText(ngaythangnam);
            }
        });
        delete = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        collapse(calendarView);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.clearFocus();
                money.clearFocus();
                if(calendarView.getVisibility() == View.GONE)
                    expand(calendarView);
                else
                    collapse(calendarView);
            }
        });

        set_detail_hint();
        flexboxLayout2.setVisibility(View.GONE);
        detail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(flexboxLayout2.getVisibility() == View.VISIBLE)
                    collapse(flexboxLayout2);
                else
                    expand(flexboxLayout2);
            }
        });

        set_money_hint();

        flexboxLayout1.setVisibility(View.GONE);
        money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(flexboxLayout1.getVisibility()==View.VISIBLE)
                    collapse(flexboxLayout1);
                else
                    expand(flexboxLayout1);
            }
        });

    }

    private void set_money_hint(){
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for(int i=expense_Array_List.size()-1;i>=0;i--){
            Expense expense = (Expense) expense_Array_List.get(i);
            if(expense.getId_group() == id){
                boolean flag = true;
                for(int j=0;j<arrayList.size();j++){
                    if(expense.getMoney()==arrayList.get(j))
                        flag=false;
                }
                if(flag){
                    add_item_money_hint(expense.getMoney());
                    arrayList.add(expense.getMoney());
                }
                if(arrayList.size()==10)
                    break;
            }

        }

    }

    private void set_detail_hint(){
        ArrayList<String> strings = new ArrayList<String>();
        for(int i=expense_Array_List.size()-1;i>=0;i--){
            Expense expense = (Expense) expense_Array_List.get(i);
            if(expense.getId_group() == id){
                boolean flag = true;
                for(int j=0;j<strings.size();j++){
                    if(expense.getDetail().equals(strings.get(j)))
                        flag=false;
                }
                if(flag){
                    add_item_detail_hint(expense.getDetail());
                    strings.add(expense.getDetail());
                }
                if(strings.size()==10)
                    break;
            }

        }
    }


    private void add_item_money_hint(final int tien){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.layout_detail_hint, null);

        final TextView textView = (TextView) v.findViewById(R.id.item);

        textView.setText(layDauPhay(tien));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money.setText(layDauPhay(tien));

            }
        });

        flexboxLayout1.addView(v);
    }

    private void add_item_detail_hint( final String text){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.layout_detail_hint, null);

        final TextView textView = (TextView) v.findViewById(R.id.item);

        textView.setText(text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.setText(text);

            }
        });

        flexboxLayout2.addView(v);
    }

    public String layDauPhay(int a){
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String s=nf.format(a);
        return s;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                exit_click();
                return true;
            case R.id.add:
                add_click();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    public void add_click(){
        if(money.getText().toString().trim().equals("")){
            Toast.makeText(this, getResources().getString(R.string.ban_chua_nhap_so_tien), Toast.LENGTH_SHORT).show();
        }else{
            expense.setMoney(Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(money.getText().toString()).trim()));

            if(detail.getText().toString().trim().equals(""))
                expense.setDetail(mainGroup.getName());
            else
                expense.setDetail(detail.getText().toString().trim());

            expense.setId_group(id);
            expense.setCalendar(calendar);

            expense_Array_List.remove(position);
            add_expense_tolist(expense);
            savedata();
            exit_click();
        }

    }

    private void delete(){
        expense_Array_List.remove(position);
        savedata();
        exit_click();
    }

    private void add_expense_tolist(Expense expense){
        int i = 0;
        while(true){
            if(i>=0 && i < expense_Array_List.size()){
                Expense e = (Expense) expense_Array_List.get(i);
                if(expense.getCalendar().getTimeInMillis() >= e.getCalendar().getTimeInMillis())
                    break;
            }
            else break;
            i++;
        }
        expense_Array_List.add(i,expense);
    }
    public void exit_click(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        finish();
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