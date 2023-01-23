package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class Budget extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView budgetTv, todayTv, weekTv, monthTv,savingTv;

    private CardView budgetCardView, todayCardView, weekCardView, monthCardView;
    //private ImageView monthBtnImageView, weekBtnImageView, todayBtnImageView, budgetBtnImageView, analyticsImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, expenseRef, personalRef;
    private String onlineUserID = "";

    private int totalAmountMonth = 0;
    private int totalAmountBudget = 0;
    private int totalAmountBudgetB = 0;
    private int totalAmountBudgetC = 0;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        budgetCardView = findViewById(R.id.budgetCardView);
        todayCardView = findViewById(R.id.todayCardView);
        weekCardView = findViewById(R.id.weekCardView);
        monthCardView = findViewById(R.id.monthCardView);
        //analyticsImageView = findViewById(R.id.analyticsImageView);

        budgetTv = findViewById(R.id.budgetTv);
        todayTv = findViewById(R.id.todayTv);
        weekTv = findViewById(R.id.weekTv);
        monthTv = findViewById(R.id.monthTv);
        savingTv = findViewById(R.id.savingTv);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Budget").child(onlineUserID);
        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("Personal").child(onlineUserID);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WeSave");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Budget.this,BudgetActivity.class);
                startActivity(intent);
            }
        });
        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Budget.this,TodaySpendingActivity.class);
                startActivity(intent);

            }
        });
        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Budget.this, WeekSpendingActivity.class);
                intent.putExtra("type", "budgetWeek");
                startActivity(intent);

            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Budget.this, WeekSpendingActivity.class);
                intent.putExtra("type", "budgetMonth");
                startActivity(intent);

            }
        });
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Budget.this, TodaySpendingActivity.class);
                startActivity(intent);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object>  map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("budgetAmount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB+=pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                }else {
                    personalRef.child("Budget").setValue(0);
                    Toast.makeText(Budget.this,"Please Set A Budget", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSavings();




    }

    private void getSavings() {

        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int budget;

                    if (snapshot.hasChild("budgetPersonal")){
                        budget = Integer.parseInt(snapshot.child("budgetPersonal").getValue().toString());
                    }else {
                        budget = 0;
                    }
                    int monthSpending;
                    if (snapshot.hasChild("budgetMonth")){
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("budgetMonth").getValue().toString()));
                    }else {
                        monthSpending = 0;
                    }
                    int savings = budget - monthSpending;
                    savingTv.setText("RM " + savings);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Budget.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getWeekSpentAmount() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = reference.orderByChild("budgetWeek").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    weekTv.setText("RM " + String.valueOf(totalAmount));

                }
                personalRef.child("budgetWeek").setValue(totalAmount);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Budget.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getMonthSpentAmount() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch,now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = reference.orderByChild("budgetMonth").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    monthTv.setText("RM " + String.valueOf(totalAmount));

                }
                personalRef.child("budgetMonth").setValue(totalAmount);
                totalAmountMonth = totalAmount;
                

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Budget.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTodaySpentAmount() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = reference.orderByChild("budgetDate").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    todayTv.setText("RM " + String.valueOf(totalAmount));

                }
                personalRef.child("budgetToday").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Budget.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getBudgetAmount() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object>  map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("budgetAmount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget+=pTotal;
                        budgetTv.setText("RM " + String.valueOf(totalAmountBudget));
                    }
                    personalRef.child("budgetPersonal").setValue(totalAmountBudget);
                }else {
                    totalAmountBudget=0;
                    budgetTv.setText("RM " + String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Budget.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(Budget.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(Budget.this, MainActivity.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }

}