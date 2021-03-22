package com.example.expensemanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    Adapter_Group adapter_group;
    Adapter_Expense adapter_expense;
    FloatingActionButton add_button;

    RecyclerView view_list_group;
    RecyclerView view_list_expense;

    All_Data all_data;
    SharedPreferences sharedPreferences;
    ArrayList expense_Array_List;
    ArrayList expense_Group_List;


    TextView tong_chi_tieu;

    TextView tien_tieu_de;
    TextView tieu_de;

    LinearLayout card1,card2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        anhxa();

        sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        all_data = new All_Data();
        expense_Group_List = new ArrayList<Expense_Group>();
        expense_Array_List= new ArrayList<Expense>();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_Add_Group.class);
                startActivityForResult(intent,0);
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_total_expense.class);
                startActivityForResult(intent,0);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_Chart.class);
                startActivityForResult(intent,0);
            }
        });

        getdata();


        adapter_group = new Adapter_Group(expense_Group_List,getApplicationContext());
        adapter_expense = new Adapter_Expense(expense_Array_List,getApplicationContext(),R.layout.expense_item_history);
        show_big_group();
        savedata();

    }


    int tong=0;
    public void logall(){

        tong=0;
        int tongtuan=0;

        String s1 =" \n\n";
        for(int i=0;i<expense_Group_List.size();i++){
            Expense_Group expense_group = (Expense_Group) expense_Group_List.get(i);
            s1+=expense_group.toString()+"\n";
        }

        String s2=" \n\n";
        for(int j=0;j<expense_Array_List.size();j++){
            Expense expense = (Expense) expense_Array_List.get(j);
            s2+=expense.toString()+"\n";
            if(expense.is_same(Calendar.getInstance(),Calendar.DATE))
                tong+=expense.getMoney();
            if(expense.is_same(Calendar.getInstance(),all_data.getTitle()))
                tongtuan+=expense.getMoney();
        }


        if(all_data.getTitle() == All_Data.TUAN){
            tieu_de.setText(getResources().getString(R.string.chi_tieu_trong_tuan));
        }
        else if(all_data.getTitle() == All_Data.THANG){
            tieu_de.setText(getResources().getString(R.string.chi_tieu_trong_thang));
        }
        tong_chi_tieu.setText(layDauPhay(tong));
        tien_tieu_de.setText(layDauPhay(tongtuan));

        Log.d("TAG_DATA", s1);
        Log.d("TAG_DATA", s2);
    }
    public String layDauPhay(int a){
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String s=nf.format(a);
        return s;
    }

    private void showRadioButtonDialog() {


        final Dialog dialog = new Dialog(this);

        dialog.setTitle(getResources().getString(R.string.muc_thong_ke_chi_tieu));
        dialog.setContentView(R.layout.dialog_chose);

        final RadioButton tuan = (RadioButton) dialog.findViewById(R.id.tuan);
        final RadioButton thang = (RadioButton) dialog.findViewById(R.id.thang);

        if(all_data.getTitle() == All_Data.TUAN)
            tuan.setChecked(true);
        else if(all_data.getTitle() == All_Data.THANG)
            thang.setChecked(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(tuan.isChecked())
                    all_data.setTitle(All_Data.TUAN);
                else if(thang.isChecked())
                    all_data.setTitle(All_Data.THANG);
                savedata();
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
    }

    private void savedata(){
        logall();

        all_data.setExpense_Group(expense_Group_List);
        all_data.setExpenseArrayList(expense_Array_List);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(all_data);
        editor.putString("all_data",json1);
        editor.commit();
    }

    @Override // Tạo hàm onActivityResult
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(popupWindow!= null && popupWindow.isShowing())
            popupWindow.dismiss();

        getdata();

        adapter_group.expense_groups = expense_Group_List;
        adapter_expense.expenses = expense_Array_List;
        savedata();
        adapter_expense.notifyDataSetChanged();
        adapter_group.notifyDataSetChanged();



        super.onActivityResult(requestCode, resultCode, data);
    }
    private void show_big_group(){

        adapter_group = new Adapter_Group(expense_Group_List,this);
        view_list_group.clearOnChildAttachStateChangeListeners();
        view_list_group.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        view_list_group.setLayoutManager(linearLayoutManager);
        view_list_group.setAdapter(adapter_group);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromP = viewHolder.getAdapterPosition();
                int toP = target.getAdapterPosition();

                if (fromP < toP) {
                    for (int i = fromP; i < toP; i++) {
                        swap_group(i, i + 1);
                    }
                } else {
                    for (int i = fromP; i > toP; i--) {
                        swap_group(i, i - 1);
                    }
                }

                adapter_group.notifyItemMoved(fromP,toP);
                savedata();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.LEFT){
                    Expense_Group expense_group = (Expense_Group) expense_Group_List.get(viewHolder.getLayoutPosition());

                    Intent intent = new Intent(MainActivity.this, Activity_Add_Expense.class);
                    intent.putExtra("id",expense_group.getId());
                    startActivityForResult(intent,0);
                }else if(direction == ItemTouchHelper.RIGHT){
                    Intent intent = new Intent(MainActivity.this, Activity_List_Expense.class);
                    Expense_Group expense_group = (Expense_Group) expense_Group_List.get(viewHolder.getLayoutPosition());
                    intent.putExtra("id",expense_group.getId());
                    startActivityForResult(intent,0);
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.icon_shopping_add_white)
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.blue))
                        .addSwipeRightActionIcon(R.drawable.icon_history_white)
                        .addSwipeRightBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(view_list_group);

        adapter_group.setOnItemClickListener(new Adapter_Group.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                menu_window(v,position);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Expense_Group expense_group = (Expense_Group) expense_Group_List.get(position);
                Intent intent = new Intent(MainActivity.this,Activity_Edit_Group.class);
                intent.putExtra("id",expense_group.getId());
                startActivityForResult(intent,0);
            }
        });

        adapter_expense = new Adapter_Expense(expense_Array_List,this,R.layout.expense_item_history);
        view_list_expense.clearOnChildAttachStateChangeListeners();
        view_list_expense.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        view_list_expense.setLayoutManager(linearLayoutManager2);
        view_list_expense.setAdapter(adapter_expense);

        adapter_expense.setOnItemClickListener(new Adapter_Expense.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Expense expense = (Expense) expense_Array_List.get(position);
                Intent intent = new Intent(MainActivity.this,Activity_Edit_Expense.class);
                intent.putExtra("id",expense.getId_group());
                intent.putExtra("position",position);
                startActivityForResult(intent,0);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        savedata();
    }


    private void menupopu(View v, final int position){

        PopupMenu popupMenu =  new PopupMenu(this,v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupMenu.setGravity(Gravity.CENTER);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Expense_Group expense_group = (Expense_Group) expense_Group_List.get(position);
                switch (item.getItemId()){
                    case R.id.add:
                        Intent intent1 =new Intent(MainActivity.this, Activity_Add_Expense.class).
                                putExtra("id",expense_group.getId());
                        startActivityForResult(intent1,0);
                        break;
                    case R.id.history:
                        Intent intent2 =new Intent(MainActivity.this, Activity_List_Expense.class).
                                putExtra("id",expense_group.getId());
                        startActivityForResult(intent2,0);
                        break;
                    case R.id.edit:
                        Intent intent3 =new Intent(MainActivity.this, Activity_Edit_Group.class).
                                putExtra("id",expense_group.getId());
                        startActivityForResult(intent3,0);
                        break;
                }
                return false;
            }
        });
        popupMenu.getMenuInflater().inflate(R.menu.menu_group_item, popupMenu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.show();
    }

    PopupWindow popupWindow;
    private void menu_window(final View view, int position){
        final Animation out = AnimationUtils.loadAnimation(this,R.anim.up_out);
        final Animation in = AnimationUtils.loadAnimation(this,R.anim.down_in);

        view.startAnimation(out);
        view.setVisibility(View.INVISIBLE);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = vi.inflate(R.layout.dialog_group_click, null);
        Rect location = locateView(view);

        int width1 = LinearLayout.LayoutParams.MATCH_PARENT;
        int height1 = LinearLayout.LayoutParams.WRAP_CONTENT;
        final boolean focusable = true;

        popupWindow = new PopupWindow(v,width1,height1,focusable);
        popupWindow.setAnimationStyle(R.style.Popup_window_anim);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.nothing));
        popupWindow.showAtLocation(this.findViewById(R.id.main), Gravity.TOP|Gravity.CENTER_HORIZONTAL, location.left, location.top);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CountDownTimer countDownTimer = new CountDownTimer(600,600) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        view.setVisibility(View.VISIBLE);
                        view.startAnimation(in);
                    }
                }.start();
            }
        });

        ImageView history = (ImageView) v.findViewById(R.id.history);
        ImageView edit = (ImageView) v.findViewById(R.id.edit);
        ImageView add = (ImageView) v.findViewById(R.id.add);


        final Expense_Group expense_group = (Expense_Group) expense_Group_List.get(position);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_List_Expense.class);
                intent.putExtra("id",expense_group.getId());
                startActivityForResult(intent,0);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_Edit_Group.class);
                intent.putExtra("id",expense_group.getId());
                startActivityForResult(intent,0);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Activity_Add_Expense.class);
                intent.putExtra("id",expense_group.getId());
                startActivityForResult(intent,0);
            }
        });

    }

    public static Rect locateView(View v)
    {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }


    private void swap_group(int from, int to){
        Expense_Group a = (Expense_Group) expense_Group_List.get(from);
        Expense_Group b = (Expense_Group) expense_Group_List.get(to);
        expense_Group_List.remove(from);
        expense_Group_List.add(from,b);
        expense_Group_List.remove(to);
        expense_Group_List.add(to,a);
    }



    private void anhxa(){
        tieu_de = (TextView) findViewById(R.id.title) ;

        tieu_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog();
            }
        });

        tien_tieu_de = (TextView) findViewById(R.id.title_money);

        tong_chi_tieu= (TextView) findViewById(R.id.tong_chi_tieu);

        view_list_group = (RecyclerView) findViewById(R.id.recycler_list);
        view_list_expense = (RecyclerView) findViewById(R.id.list_expense);
        add_button = (FloatingActionButton) findViewById(R.id.add);
        card1 = (LinearLayout) findViewById(R.id.card1);
        card2 = (LinearLayout) findViewById(R.id.card2);
    }

    boolean exit = false;
    @Override
    public void onBackPressed() {
        if(exit){
            finish();
        }else{
            exit=true;
            Toast.makeText(this, getResources().getString(R.string.an_lai_lan_nua), Toast.LENGTH_SHORT).show();
            CountDownTimer cd = new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    exit=false;
                }
            }.start();
        }
    }
}