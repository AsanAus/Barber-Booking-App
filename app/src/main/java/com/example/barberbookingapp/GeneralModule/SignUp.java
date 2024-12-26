package com.example.barberbookingapp.GeneralModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.example.barberbookingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SignUp extends AppCompatActivity {

    TextView signinText;
    private EditText Email, Username, Password, ConPassword;
    private static final String TAG = "SignUp";
    String role = "user";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // --------------- click text to go Login activity -------------------------//

        signinText = findViewById(R.id.TVSignUp);
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


        // --------------- click Button to go Register/SignUp -----------------------//

        Email = findViewById(R.id.ETEmail);
        Username = findViewById(R.id.ETUsername);
        Password = findViewById(R.id.ETPassword);
        ConPassword = findViewById(R.id.ETConfirmPassword);

        Button btnSignUp = findViewById(R.id.BtnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtain entered data
                String email = Email.getText().toString();
                String username = Username.getText().toString();
                String pass = Password.getText().toString();
                String conPass = ConPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    Email.setError("Email is required");
                    Email.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(SignUp.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    Email.setError("Valid email is required");
                    Email.requestFocus();
                } else if(TextUtils.isEmpty(username)){
                    Toast.makeText(SignUp.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    Username.setError("Username is required");
                    Username.requestFocus();
                } else if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    Password.setError("Password is required");
                    Password.requestFocus();
                } else if(pass.length()<6) {
                    Toast.makeText(SignUp.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    Password.setError("Password too weak");
                    Password.requestFocus();
                } else if(TextUtils.isEmpty(conPass)) {
                    Toast.makeText(SignUp.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    ConPassword.setError("Password Confirmation is required");
                    ConPassword.requestFocus();
                } else if(!pass.equals(conPass)) {
                    Toast.makeText(SignUp.this, "Please same same password", Toast.LENGTH_SHORT).show();
                    ConPassword.setError("Password Confirmation is required");
                    ConPassword.requestFocus();
                    //clear the entered password
                    Password.clearComposingText();
                    ConPassword.clearComposingText();
                } else{
                    registerUser(email, username, pass);
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Register User using the credentials given
//    private void registerUser(String email, String username, String pass) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        // create user
//        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUp.this,
//                new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//
//                            FirebaseUser firebaseUser = auth.getCurrentUser();
//
//                            //Update Display Name of User
//                            UserProfileChangeRequest UPCR = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
//                            firebaseUser.updateProfile(UPCR);
//
//                            //Enter User Data into the Firebase Realtime database
//                            HelperClass writeUserDetails = new HelperClass(email, username, pass);
//
//                            //Extracting user reference from database for "Registered Users"
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                            reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    if(task.isSuccessful()){
//                                        Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_LONG).show();
//
//                                        Intent intent = new Intent(SignUp.this, Login.class);
//                                        startActivity(intent);
//                                        finish(); //to close register activity
//                                    } else{
//                                        Toast.makeText(SignUp.this, "User registered failed. Please try again", Toast.LENGTH_LONG).show();
//                                    }
//
//                                }
//                            });
//
//
//                        } else {
//                            try{
//                                throw task.getException();
//                            } catch (FirebaseAuthWeakPasswordException e){
//                                Password.setError("Your password is to weak. Kindly use a mix of number and alphabets");
//                                Password.requestFocus();
//                            } catch (FirebaseAuthInvalidCredentialsException e){
//                                Password.setError("Your email is invalid or already use. Kindly re-enter.");
//                                Password.requestFocus();
//                            } catch (FirebaseAuthUserCollisionException e){
//                                Password.setError("User is already registered with this email. use another one");
//                                Password.requestFocus();
//                            } catch (Exception e){
//                                Log.e(TAG, e.getMessage());
//                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    }
//                });
//
//    }

    // Register User using the credentials given
    private void registerUser(String email, String username, String pass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Check if the username already exists
        DatabaseReference usernameReference = FirebaseDatabase.getInstance().getReference("Usernames");
        usernameReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    Toast.makeText(SignUp.this, "Username is already taken. Please choose another one.", Toast.LENGTH_LONG).show();
                    Username.setError("Username already exists");
                    Username.requestFocus();
                } else {
                    // Proceed with user creation
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUp.this,
                            task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();

                                    // Update Display Name of User
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();
                                    firebaseUser.updateProfile(profileUpdate);

                                    // Enter User Data into the Firebase Realtime database
                                    HelperClass writeUserDetails = new HelperClass(email, username, pass, role);

                                    // Add user details and username mapping to the database
                                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
                                    String userId = firebaseUser.getUid();

                                    // Save user details
                                    userReference.child(userId).setValue(writeUserDetails).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Save username mapping
                                            usernameReference.child(username).setValue(email).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(SignUp.this, Login.class));
                                                    finish(); // Close the registration activity
                                                } else {
                                                    Toast.makeText(SignUp.this, "Failed to save username mapping. Please try again.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SignUp.this, "User registration failed. Please try again.", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } else {
                                    // Handle Firebase exceptions
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        Password.setError("Your password is too weak. Kindly use a mix of numbers and letters.");
                                        Password.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Email.setError("Your email is invalid or already in use. Please re-enter.");
                                        Email.requestFocus();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Email.setError("This email is already registered. Use another one.");
                                        Email.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUp.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
