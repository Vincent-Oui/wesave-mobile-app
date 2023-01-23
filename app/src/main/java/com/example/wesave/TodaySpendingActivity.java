package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference expenseRef;

    private TodayItemsAdapter todayItemsAdapter;
    private List<BudgetData> myDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today Spending");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);

        recyclerView = findViewById(R.id.budgetRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();

        todayItemsAdapter = new TodayItemsAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        readItem();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });
    }

    private void readItem() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String budgetDate = dateFormat.format(calendar.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        Query query = reference.orderByChild("budgetDate").equalTo(budgetDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BudgetData budgetData = dataSnapshot.getValue(BudgetData.class);

                    myDataList.add(budgetData);
                }
                todayItemsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("budgetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountSpentOn.setText("Total Day's Spending: RM" + totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Spinner itemSpinner = myView.findViewById(R.id.budgetSpinner);
        EditText budgetAmount = myView.findViewById(R.id.budgetAmount);
        EditText budgetNote = myView.findViewById(R.id.budgetNote);
        Button budgetCancel = myView.findViewById(R.id.budgetCancel);
        Button budgetSave = myView.findViewById(R.id.budgetSave);

        budgetNote.setVisibility(View.VISIBLE);

        budgetSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String BudgetAmount = budgetAmount.getText().toString();
                String BudgetItem = itemSpinner.getSelectedItem().toString();
                String BudgetNote = budgetNote.getText().toString();

                if (TextUtils.isEmpty(BudgetAmount)) {

                    budgetAmount.setError("Amount is required");
                    return;
                }

                if (BudgetItem.equals("Select Item")){

                    Toast.makeText(TodaySpendingActivity.this, "Select a valid item",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(BudgetNote)){
                    budgetNote.setError("Note is required");
                    return;
                }

                else {

                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String budgetId = expenseRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String budgetDate = dateFormat.format(calendar.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch,now);

                    String budgetItemNdays = BudgetItem+budgetDate;
                    String budgetItemNweeks = BudgetItem+weeks.getWeeks();
                    String budgetItemNmonths = BudgetItem+months.getMonths();

                    BudgetData budgetData = new BudgetData(BudgetItem, budgetDate, budgetId,BudgetNote, budgetItemNdays, budgetItemNweeks, budgetItemNmonths, Integer.parseInt(BudgetAmount),
                            months.getMonths(), weeks.getWeeks());

                    expenseRef.child(budgetId).setValue(budgetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this,"Added successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TodaySpendingActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }

                dialog.dismiss();
            }
        });

        budgetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(TodaySpendingActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(TodaySpendingActivity.this, Budget.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }
}