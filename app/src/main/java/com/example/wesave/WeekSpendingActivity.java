package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalWeekAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<BudgetData>  myDateList;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference expenseRef;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Week Spending");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalWeekAmountSpentOn = findViewById(R.id.totalWeekAmountTextView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.budgetRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);

        myDateList = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpendingActivity.this, myDateList);
        recyclerView.setAdapter(weekSpendingAdapter);

        if (getIntent().getExtras()!=null){

            type = getIntent().getStringExtra("type");

            if (type.equals("budgetWeek")){
                readWeekSpendingItems();
            }else if (type.equals("budgetMonth")){
                readMonthSpendingItems();
            }
        }

        //readWeekSpendingItems();

    }

    private void readMonthSpendingItems() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = expenseRef.orderByChild("budgetMonth").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDateList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BudgetData budgetData = dataSnapshot.getValue(BudgetData.class);
                    myDateList.add(budgetData);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountSpentOn.setText("Total Month's Spending: RM" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readWeekSpendingItems() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = expenseRef.orderByChild("budgetWeek").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDateList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BudgetData budgetData = dataSnapshot.getValue(BudgetData.class);
                    myDateList.add(budgetData);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountSpentOn.setText("Total Week's Spending: RM" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(WeekSpendingActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(WeekSpendingActivity.this, Budget.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }
}