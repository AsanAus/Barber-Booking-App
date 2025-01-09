package com.example.barberbookingapp.GeneralModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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

import com.example.barberbookingapp.BarberManagementModule.BarberAdmin;
import com.example.barberbookingapp.BarberManagementModule.pendingBarber;
import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BarberLogin extends AppCompatActivity {

    private TextView signupText, forgotPasswordText, loginAsUserText;
    private EditText EtUsername, EtPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_login);

        //click text to go forgot password activity
        forgotPasswordText = findViewById(R.id.TVForgot);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarberLogin.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        //click text to go Sign Up activity
        signupText = findViewById(R.id.TVSignUp);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarberLogin.this, BarberSignUp.class);
                startActivity(intent);
            }
        });

        //click text to go User Login activity
        loginAsUserText = findViewById(R.id.TVBarber);
        loginAsUserText.setText(Html.fromHtml("<u>Login as User</u>"));
        loginAsUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarberLogin.this, Login.class);
                startActivity(intent);
            }
        });

        EtUsername = findViewById(R.id.ETUsername);
        EtPass = findViewById(R.id.ETPassword);

        auth = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.BtnSignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = EtUsername.getText().toString();
                String pass = EtPass.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(BarberLogin.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    EtUsername.setError("Username is required");
                    EtUsername.requestFocus();
                } else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(BarberLogin.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    EtPass.setError("Password is required");
                    EtPass.requestFocus();
                } else{
                    loginBarber(username, pass);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginBarber(String username, String pass) {
        // Reference to the Usernames node in Firebase
        DatabaseReference usernameReference = FirebaseDatabase.getInstance().getReference("Usernames");

        // Check if the username exists
        usernameReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the email associated with the username
                    String email = snapshot.getValue(String.class);

                    // Proceed to authenticate with email and password
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Authentication successful, now check the role
                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Barbers");
                            String userId = auth.getCurrentUser().getUid();

                            userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        // Retrieve the user's role
                                        String role = userSnapshot.child("role").getValue(String.class);

                                        if ("barber".equals(role)) {
                                            // Role matches, grant access
                                            Toast.makeText(BarberLogin.this, "Barber Login successful!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(BarberLogin.this, pendingBarber.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
                                            startActivity(intent);
                                        } else {
                                            // Role does not match, deny access
                                            Toast.makeText(BarberLogin.this, "Access denied. You are not authorized.", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut(); // Sign out the unauthorized user
                                        }
                                    } else {
                                        Toast.makeText(BarberLogin.this, "Access denied. You are not authorized.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(BarberLogin.this, "Barber not found.", Toast.LENGTH_LONG).show();
                                }
                            });


                        } else {
                            // Login failed
                            Toast.makeText(BarberLogin.this, "Invalid username or password. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Username does not exist
                    Toast.makeText(BarberLogin.this, "Username does not exist. Please check and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                Toast.makeText(BarberLogin.this, "Database error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}