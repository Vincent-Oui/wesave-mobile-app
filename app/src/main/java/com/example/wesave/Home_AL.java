package com.example.wesave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home_AL extends AppCompatActivity {

    Button albtncreate,albtnchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_al);

        albtncreate = findViewById(R.id.al_btn_create);
        albtnchart = findViewById(R.id.al_btn_chart);

        albtncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Home_AL.this,NetworthHome.class);
                startActivity(intent);
            }
        });

    }
}