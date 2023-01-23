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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NetworthHome extends AppCompatActivity {

    private Toolbar toolbar;

    private CardView networthCardView, assetCardView, liabilityCardView;
    private TextView tv_networth_value;

    private DatabaseReference networthRef;
    private FirebaseAuth mAuth;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networth_home);

        toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        assetCardView = findViewById(R.id.assetCardView);
        liabilityCardView = findViewById(R.id.liabilityCardView);
        tv_networth_value = findViewById(R.id.tv_networth_value);

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        networthRef = FirebaseDatabase.getInstance().getReference("Networth").child(onlineUserID);

        assetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NetworthHome.this, AssetActivity.class);
                startActivity(intent);
            }
        });

        liabilityCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NetworthHome.this, LiabilityActivity.class);
                startActivity(intent);
            }
        });

        getNetworth();

    }

    private void getNetworth() {

        networthRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int asset;

                    if (snapshot.hasChild("assetPersonal")){
                        asset = Integer.parseInt(snapshot.child("assetPersonal").getValue().toString());
                    }else {
                        asset = 0;
                    }
                    int liability;
                    if (snapshot.hasChild("liabilityPersonal")){
                        liability = Integer.parseInt(snapshot.child("liabilityPersonal").getValue().toString());
                    }else {
                        liability = 0;
                    }
                    int networth = asset - liability;
                    tv_networth_value.setText("Networth Value : RM " + networth);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NetworthHome.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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
            Intent intent = new Intent(NetworthHome.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(NetworthHome.this, MainActivity.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }


}