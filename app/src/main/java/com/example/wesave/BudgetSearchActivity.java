package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BudgetSearchActivity extends AppCompatActivity {

    DatabaseReference searchRef;
    private ListView budgetsearch_listData;
    private AutoCompleteTextView budgettextSearch;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private Toolbar my_Feed_Toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_search);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();

        searchRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        budgetsearch_listData = findViewById(R.id.budgetsearch_listData);
        budgettextSearch = (AutoCompleteTextView) findViewById(R.id.budgettextSearch);

        my_Feed_Toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(my_Feed_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Budget Search");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        searchRef.addListenerForSingleValueEvent(eventListener);
    }

    private void populateSearch(DataSnapshot snapshot) {

        ArrayList <String> descriptions = new ArrayList<>();
        if (snapshot.exists()){
            for (DataSnapshot ds : snapshot.getChildren()){
                String description = ds.child("budgetNotes").getValue(String.class);
                descriptions.add(description);
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, descriptions);
            budgettextSearch.setAdapter(adapter);
            budgettextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String description = budgettextSearch.getText().toString();
                    searchDescription(description);
                }
            });

        }else {
            Log.d("Expenses", "Search no found");
        }
    }

    private void searchDescription(String description) {

        Query query = searchRef.orderByChild("budgetNotes").equalTo(description);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    ArrayList<String> budgetList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        BudgetData budgetData = ds.getValue(BudgetData.class);
                        budgetList.add(
                                "Budget Type : " + budgetData.getBudgetItem() + "\n" +
                                        "Amount : RM" + budgetData.getBudgetAmount() + "\n" +
                                        "Date : " + budgetData.getBudgetDate() + "\n" +
                                        "Description : " + budgetData.getBudgetNotes());
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, budgetList);
                    budgetsearch_listData.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(BudgetSearchActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(BudgetSearchActivity.this, SearchActivity.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}