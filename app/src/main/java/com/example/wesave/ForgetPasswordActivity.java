package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button btn_reset_password;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.email);
        btn_reset_password = findViewById(R.id.btn_reset_password);
       // progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {

        String userEmail = email.getText().toString().trim();

        if (userEmail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ForgetPasswordActivity.this, "Try again! Something went wrong!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}