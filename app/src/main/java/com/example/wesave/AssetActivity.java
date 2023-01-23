package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
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
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AssetActivity extends AppCompatActivity {

    private Toolbar my_Feed_Toolbar;
    private TextView assettotalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView assetRecyclerView;

    private AssetAdapter assetAdapter;
    private List<AssetData>  myAssetList;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference assetRef, networthRef;
    private FloatingActionButton fab;

    private ProgressDialog loader;

    private String assettype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        my_Feed_Toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(my_Feed_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Asset");

        assettotalAmountSpentOn = findViewById(R.id.assettotalAmountSpentOn);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        assetRef = FirebaseDatabase.getInstance().getReference("Asset").child(onlineUserID);
        networthRef = FirebaseDatabase.getInstance().getReference("Networth").child(onlineUserID);

        assetRecyclerView = findViewById(R.id.assetRecyclerView);
        LinearLayoutManager assetlinearLayoutManager = new LinearLayoutManager(this);
        assetlinearLayoutManager.setStackFromEnd(true);
        assetlinearLayoutManager.setReverseLayout(true);
        assetRecyclerView.setHasFixedSize(true);
        assetRecyclerView.setLayoutManager(assetlinearLayoutManager);

        myAssetList = new ArrayList<>();

        assetAdapter = new AssetAdapter(AssetActivity.this,myAssetList);
        assetRecyclerView.setAdapter(assetAdapter);

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
        String assetDate = dateFormat.format(calendar.getTime());

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Years years = Years.yearsBetween(epoch, now);

        assetRef = FirebaseDatabase.getInstance().getReference("Asset").child(onlineUserID);
        Query query = assetRef.orderByChild("assetYears").equalTo(years.getYears());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAssetList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    AssetData assetData = dataSnapshot.getValue(AssetData.class);

                    myAssetList.add(assetData);
                }
                assetAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("assetAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    assettotalAmountSpentOn.setText("Total Asset Value: RM" + totalAmount);
                }
                networthRef.child("assetPersonal").setValue(totalAmount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View assetView = inflater.inflate(R.layout.asset_layout, null);
        myDialog.setView(assetView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Spinner assetSpinner = assetView.findViewById(R.id.assetSpinner);
        EditText assetAmount = assetView.findViewById(R.id.assetAmount);
        EditText assetNote = assetView.findViewById(R.id.assetNote);
        //EditText assetYearsOwn =assetView.findViewById(R.id.assetYearsOwn);
        Button assetCancel = assetView.findViewById(R.id.assetCancel);
        Button assetSave = assetView.findViewById(R.id.assetSave);

        assetNote.setVisibility(View.VISIBLE);

        assetSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String AssetAmount = assetAmount.getText().toString();
                String AssetItem = assetSpinner.getSelectedItem().toString();
                String AssetNote = assetNote.getText().toString();

                if (TextUtils.isEmpty(AssetAmount)) {

                    assetAmount.setError("Amount is required");
                    return;
                }

                if (AssetItem.equals("Select Item")){

                    Toast.makeText(AssetActivity.this, "Select a valid asset",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(AssetNote)){
                    assetNote.setError("Details is required");
                    return;
                }

                else {

                    loader.setMessage("Adding an asset");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String assetId = assetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String assetDate = dateFormat.format(calendar.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Years years = Years.yearsBetween(epoch, now);


                    String assetItemNdays = AssetItem+assetDate;
                    String assetItemNyears = AssetItem+years.getYears();



                    AssetData assetData = new AssetData(AssetItem, assetDate, assetId, assetItemNdays, assetItemNyears,Integer.parseInt(AssetAmount),
                            years.getYears(), AssetNote);

                    assetRef.child(assetId).setValue(assetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(AssetActivity.this,"Added successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AssetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }

                dialog.dismiss();
            }
        });

        assetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(AssetActivity.this, NetworthHome.class);
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
