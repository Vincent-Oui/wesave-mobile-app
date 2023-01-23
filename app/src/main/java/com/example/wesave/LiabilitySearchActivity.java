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

public class LiabilitySearchActivity extends AppCompatActivity {

    DatabaseReference searchRef;
    private ListView liabilitysearch_listData;
    private AutoCompleteTextView laibilitytextSearch;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private Toolbar my_Feed_Toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liability_search);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();

        searchRef = FirebaseDatabase.getInstance().getReference("Liability").child(onlineUserID);
        liabilitysearch_listData = findViewById(R.id.liabilitysearch_listData);
        laibilitytextSearch = (AutoCompleteTextView) findViewById(R.id.laibilitytextSearch);

        my_Feed_Toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(my_Feed_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Liability Search");

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
                String description = ds.child("liabilityNotes").getValue(String.class);
                descriptions.add(description);
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, descriptions);
            laibilitytextSearch.setAdapter(adapter);
            laibilitytextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String description = laibilitytextSearch.getText().toString();
                    searchDescription(description);
                }
            });

        }else {
            Log.d("Liability", "Search no found");
        }
    }

    private void searchDescription(String description) {

        Query query = searchRef.orderByChild("liabilityNotes").equalTo(description);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    ArrayList<String> liabilityList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        LiabilityData liabilityData = ds.getValue(LiabilityData.class);
                        liabilityList.add(
                                "Liability Type : " + liabilityData.getLiabilityItem() + "\n" +
                                        "Amount : RM" + liabilityData.getLiabilityAmount() + "\n" +
                                        "Date : " + liabilityData.getLiabilityDate() + "\n" +
                                        "Description : " + liabilityData.getLiabilityNotes());
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, liabilityList);
                    liabilitysearch_listData.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(LiabilitySearchActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(LiabilitySearchActivity.this, SearchActivity.class);
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