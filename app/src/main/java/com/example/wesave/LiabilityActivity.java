package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import org.joda.time.MutableDateTime;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class LiabilityActivity extends AppCompatActivity {

    private Toolbar my_Feed_Toolbar;
    private TextView liabilitytotalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView liabilityRecyclerView;

    private LiabilityAdapter liabilityAdapter;
    private List<LiabilityData> myLiabilityList;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference liabilityRef, networthRef;
    private FloatingActionButton fab;

    private ProgressDialog loader;

    private String liabilitytype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liability);

        my_Feed_Toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(my_Feed_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Liability");

        liabilitytotalAmountSpentOn = findViewById(R.id.liabiltytotalAmountSpentOn);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        liabilityRef = FirebaseDatabase.getInstance().getReference("Liability").child(onlineUserID);
        networthRef = FirebaseDatabase.getInstance().getReference("Networth").child(onlineUserID);

        liabilityRecyclerView = findViewById(R.id.liabilityRecyclerView);
        LinearLayoutManager liabilitylinearLayoutManager = new LinearLayoutManager(this);
        liabilitylinearLayoutManager.setStackFromEnd(true);
        liabilitylinearLayoutManager.setReverseLayout(true);
        liabilityRecyclerView.setHasFixedSize(true);
        liabilityRecyclerView.setLayoutManager(liabilitylinearLayoutManager);

        myLiabilityList = new ArrayList<>();

        liabilityAdapter = new LiabilityAdapter(LiabilityActivity.this,myLiabilityList);
        liabilityRecyclerView.setAdapter(liabilityAdapter);

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
        String liabilityDate = dateFormat.format(calendar.getTime());

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Years years = Years.yearsBetween(epoch, now);

        liabilityRef = FirebaseDatabase.getInstance().getReference("Liability").child(onlineUserID);
        Query query = liabilityRef.orderByChild("liabilityYears").equalTo(years.getYears());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myLiabilityList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LiabilityData liabilityData = dataSnapshot.getValue(LiabilityData.class);

                    myLiabilityList.add(liabilityData);
                }
                liabilityAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("liabilityAmount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    liabilitytotalAmountSpentOn.setText("Total Liability Value: RM" + totalAmount);
                }
                networthRef.child("liabilityPersonal").setValue(totalAmount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View liabilityView = inflater.inflate(R.layout.liability_layout, null);
        myDialog.setView(liabilityView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Spinner liabilitySpinner = liabilityView.findViewById(R.id.liabilitySpinner);
        EditText liabilityAmount = liabilityView.findViewById(R.id.liabilityAmount);
        EditText liabilityNote = liabilityView.findViewById(R.id.liabilityNote);
        //EditText assetYearsOwn =assetView.findViewById(R.id.assetYearsOwn);
        Button liabilityCancel = liabilityView.findViewById(R.id.liabilityCancel);
        Button liabilitySave = liabilityView.findViewById(R.id.liabilitySave);

        liabilityNote.setVisibility(View.VISIBLE);

        liabilitySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String LiabilityAmount = liabilityAmount.getText().toString();
                String LiabilityItem = liabilitySpinner.getSelectedItem().toString();
                String LiabilityNote = liabilityNote.getText().toString();

                if (TextUtils.isEmpty(LiabilityAmount)) {

                    liabilityAmount.setError("Amount is required");
                    return;
                }

                if (LiabilityItem.equals("Select Item")){

                    Toast.makeText(LiabilityActivity.this, "Select a valid liability",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(LiabilityNote)){
                    liabilityNote.setError("Details is required");
                    return;
                }

                else {

                    loader.setMessage("Adding an asset");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String liabilityId = liabilityRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String liabilityDate = dateFormat.format(calendar.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Years years = Years.yearsBetween(epoch, now);


                    String liabilityItemNdays = LiabilityItem+liabilityDate;
                    String liabilityItemNyears = LiabilityItem+years.getYears();



                    LiabilityData liabilityData = new LiabilityData(LiabilityItem, liabilityDate, liabilityId, liabilityItemNdays, liabilityItemNyears
                            ,Integer.parseInt(LiabilityAmount), years.getYears(), LiabilityNote);

                    liabilityRef.child(liabilityId).setValue(liabilityData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(LiabilityActivity.this,"Added successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LiabilityActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }

                dialog.dismiss();
            }
        });

        liabilityCancel.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(LiabilityActivity.this, NetworthHome.class);
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