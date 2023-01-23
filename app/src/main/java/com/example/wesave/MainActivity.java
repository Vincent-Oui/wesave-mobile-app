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
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CardView home_asset_liability_CardView,home_budget_CardView,home_search_CardView,home_currency_CardView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home_asset_liability_CardView = findViewById(R.id.home_asset_liability_CardView);
        home_budget_CardView = findViewById(R.id.home_budget_CardView);
        home_search_CardView = findViewById(R.id.home_search_CardView);
        home_currency_CardView = findViewById(R.id.home_currency_CardView);

        toolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WeSave");

        home_asset_liability_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,NetworthHome.class);
                startActivity(intent);
            }
        });

        home_budget_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,Budget.class);
                startActivity(intent);
            }
        });

        home_currency_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,GoldActivity.class);
                startActivity(intent);
            }
        });

        home_search_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
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
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}