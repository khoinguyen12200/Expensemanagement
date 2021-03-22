package com.example.expensemanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Activity_Edit_Group extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    All_Data all_data;
    ArrayList expense_Group_List;
    ArrayList expense_Array_List;
    Expense_Group mainGroup;

    EditText editName;
    FlexboxLayout flexboxLayout;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_check_whiet);
        add_actionbar_title(getResources().getString(R.string.nhom_chi_tieu));

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

        boolean flag = false;
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId()==id) {
                flag = true;
                mainGroup = new Expense_Group(expense_group);
            }
        }
        if(!flag)
            finish();


        editName = (EditText) findViewById(R.id.group_name);
        flexboxLayout = (FlexboxLayout) findViewById(R.id.flexbox);


        editName.setText(mainGroup.getName());
        editName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!editName.getText().toString().trim().equals(""))
                    mainGroup.setName(editName.getText().toString().trim());
            }
        });
        color_add(flexboxLayout,mainGroup.getColor());

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
    public void onBackPressed() {
        end_activity();
    }

    private void end_activity(){

        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId()==id){
               expense_Group_List.remove(i);
               if(mainGroup!=null)
                    expense_Group_List.add(i,mainGroup);
               else{
                   for(int j=0;j<expense_Array_List.size();j++){
                       Expense expense = (Expense) expense_Array_List.get(j);
                       if(expense.getId_group()==id){
                           expense_Array_List.remove(j);
                           j--;
                       }
                   }
               }
               savedata();
            }
        }

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_group, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                end_activity();
                return true;
            case R.id.delete:
                alert_dialog();
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void alert_dialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.icon_warning_red);

        alertDialog.setTitle(getResources().getString(R.string.ban_co_chac_xoa));

        alertDialog.setMessage(getResources().getString(R.string.sau_khi_xoa));

        alertDialog.setPositiveButton(getResources().getString(R.string.co), new DialogInterface.OnClickListener() { // Tạo nút Có và hành động xóa phần tử trong array
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainGroup=null;
                end_activity();

            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.khong), new DialogInterface.OnClickListener() { // Tạo nút không
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    int check;
    private void color_add(final FlexboxLayout flexboxLayout,int color){
        final int [] color_int = getResources().getIntArray(R.array.color_list);
        final ImageView[] oval = new ImageView[color_int.length];

        Resources r = getResources();
        int img = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,r.getDisplayMetrics());
        int item;
        item=0;
        check=-1;
        while(item<color_int.length){
            Log.d("group_activity", "item: "+item);
            oval[item] = new ImageView(this);
            GradientDrawable gdtv = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {color_int[item],color_int[item]});
            gdtv.setCornerRadii(new float[]{img/2, img/2, img/2, img/2, img/2, img/2, img/2, img/2});
            gdtv.setStroke(3, Color.BLACK);

            oval[item].setBackgroundColor(color_int[item]);
            oval[item].setMinimumWidth(img);
            oval[item].setMinimumHeight(img);
            oval[item].setBackground(gdtv);

            final int finalItem = item;
            oval[item].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(check != finalItem){
                        if(check==-1){
                            oval[finalItem].setImageResource(R.drawable.icon_check_black);
                            check= finalItem;
                        }
                        else{
                            oval[check].setImageResource(R.drawable.nothing);
                            oval[finalItem].setImageResource(R.drawable.icon_check_black);
                            check= finalItem;
                        }
                        mainGroup .setColor(color_int[check]);
                        savedata();

                    }
                }
            });

            if(color_int[item]==color){
                oval[item].setImageResource(R.drawable.icon_check_black);
                check=item;
            }

            Animation pop_up = AnimationUtils.loadAnimation(this,R.anim.popup2);
            pop_up.setStartOffset(item*30);
            oval[item].setAnimation(pop_up);

            LinearLayout paddinglayout = new LinearLayout(this);
            paddinglayout.setPadding(img/4,img/4,img/4,img/4);
            paddinglayout.addView(oval[item]);
            flexboxLayout.addView(paddinglayout);
            item++;

        }

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