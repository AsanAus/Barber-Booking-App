package com.example.barberbookingapp.GeneralModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.example.barberbookingapp.UserManagementModule.home;
import com.example.barberbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private TextView signupText, forgotPasswordText, loginAsBarberText;
    EditText loginUsername, loginPassword;
    Button loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        //click text to go forgot password activity
        forgotPasswordText = findViewById(R.id.TVForgot);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        //click text to go Sign Up activity
        signupText = findViewById(R.id.TVSignUp);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        //click text to go Barber Login activity
        loginAsBarberText = findViewById(R.id.TVBarber);
        loginAsBarberText.setText(Html.fromHtml("<u>Login as Barber</u>"));
        loginAsBarberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, BarberLogin.class);
                startActivity(intent);
            }
        });


        loginUsername = findViewById(R.id.ETUsername);
        loginPassword = findViewById(R.id.ETPassword);
        loginBtn = findViewById(R.id.BtnSignUp);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUsername() | !validatePassword()){

                } else{
                    checkUser();
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Boolean validateUsername(){
        String val = loginUsername.getText().toString();
        if(val.isEmpty()){
            loginUsername.setError("Username cannot be empty");
            return false;
        } else{
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Password cannot be empty");
            return false;
        } else{
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if(Objects.equals(passwordFromDB, userPassword)){
                        loginUsername.setError(null);
                        Intent intent = new Intent(Login.this, HomePage.class);
                        startActivity(intent);
                        Toast.makeText(Login.this, "successfully login", Toast.LENGTH_SHORT).show();

                    }else{
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                }else{
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}