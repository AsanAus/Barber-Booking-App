package com.example.barberbookingapp.GeneralModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.example.barberbookingapp.R;

public class ForgotPassword extends AppCompatActivity {


    //Declaration
    Button btnReset;
    EditText etEmail;
    FirebaseAuth mAuth;
    String strEmail;
    TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        //initialization
        btnReset = findViewById(R.id.BtnFP);
        etEmail = findViewById(R.id.ETEmailFP);

        mAuth = FirebaseAuth.getInstance();

        //Reset button listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = etEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(strEmail)){
                    ResetPassword();
                } else{
                    etEmail.setError("Email field can't be empty");
                }
            }
        });



        backButton = findViewById(R.id.TVSignInFP);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Return to the previous activity
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        // Custom behavior before going back
        super.onBackPressed(); // Navigate back to the previous activity
    }

    private void ResetPassword(){
        btnReset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Reset Password link has been sent to your registered Email.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, "Error :-", Toast.LENGTH_SHORT).show();
                btnReset.setVisibility(View.VISIBLE);
            }
        });
    }

}