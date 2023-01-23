package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private TextView userEmail;
    private Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getActionBar().setTitle("My Account");

        logoutBtn = findViewById(R.id.logoutBtn);
        userEmail = findViewById(R.id.userEmail);

        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("WeSave")
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"),(dialog, id) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), null).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}