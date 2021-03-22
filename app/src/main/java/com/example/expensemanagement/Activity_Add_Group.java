package com.example.expensemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Activity_Add_Group extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    All_Data all_data;
    ArrayList expense_Group_List;
    ArrayList expense_Array_List;
    Expense_Group mainGroup;

    int [] color_int;
    EditText newName;
    FlexboxLayout flexboxLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close_white);
        add_actionbar_title(getResources().getString(R.string.them_nhom_chi_tieu));

        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List = new ArrayList<Expense>();
        mainGroup = new Expense_Group();

        getdata();

        color_int  = getResources().getIntArray(R.array.color_list);


        newName = findViewById(R.id.newname);
        flexboxLayout =findViewById(R.id.flexbox);


        color_add(flexboxLayout,getResources().getColor(R.color.white));





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
    private int getnextid(){
        int [] array = new int[expense_Group_List.size()+1];
        for(int i=0;i<array.length;i++)
            array[i]=0;
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            if(expense_group.getId() >=0 && expense_group.getId()<array.length)
                array[expense_group.getId()]=1;
        }
        for(int i=0;i<array.length;i++)
            if(array[i]==0)
                return i;
        return expense_Group_List.size();

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
    public void add_click(){
        if(newName.getText().toString().trim().equals("")){
            Toast.makeText(Activity_Add_Group.this, getResources().getString(R.string.ban_chua_nhap_ten), Toast.LENGTH_SHORT).show();
        }
        else{
            Expense_Group group = new Expense_Group();
            group.setName(newName.getText().toString().trim());
            group.setColor(color_int[check]);
            group.setId(getnextid());
            expense_Group_List.add(group);
            savedata();
            finish();
        }
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
                finish();
                return true;
            case R.id.add:
                add_click();
                break;

        }

        return super.onOptionsItemSelected(item);
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