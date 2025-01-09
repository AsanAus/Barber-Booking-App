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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.UserManagementModule.HomePage;

import com.example.barberbookingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private TextView signupText, forgotPasswordText, loginAsBarberText;
    private EditText EtUsername, EtPass;
    private FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    if (signInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Switch to the home activity
                                    Intent intent = new Intent(Login.this, HomePage.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Sign-in account is null", Toast.LENGTH_SHORT).show();
                    }
                } catch (ApiException e) {
                    Toast.makeText(Login.this, "Sign-in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        // --------------- click text to go forgot password activity --------------------
        forgotPasswordText = findViewById(R.id.TVForgot);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });


        // --------------- click text to go Sign Up activity ----------------------------
        signupText = findViewById(R.id.TVSignUp);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });


        // --------------- click text to go Barber Login activity -----------------------

        loginAsBarberText = findViewById(R.id.TVBarber);
        loginAsBarberText.setText(Html.fromHtml("<u>Login as Barber</u>"));
        loginAsBarberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, BarberLogin.class);
                startActivity(intent);
            }
        });

        // --------------- click button LOG IN  ------------------------------------------
        
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
                    Toast.makeText(Login.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    EtUsername.setError("Username is required");
                    EtUsername.requestFocus();
                } else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    EtPass.setError("Password is required");
                    EtPass.requestFocus();
                } else{
                    loginUser(username, pass);
                }
            }
        });

        // --------------- click button GOOGLE  ------------------------------------------

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Login.this, options);

        auth = FirebaseAuth.getInstance();

        Button google = findViewById(R.id.BtnGG);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Force sign out before showing the account selection
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    // Launch Google Sign-In Intent
                    Intent intent = googleSignInClient.getSignInIntent();
                    activityResultLauncher.launch(intent);
                });
            }
        });


        // --------------- click button Facebook  ------------------------------------------

        Button fb = findViewById(R.id.BtnFB);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser(String username, String pass) {
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
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Authentication successful, now check the role
                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
                            String userId = mAuth.getCurrentUser().getUid();

                            userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        // Retrieve the user's role
                                        String role = userSnapshot.child("role").getValue(String.class);

                                        if ("user".equals(role)) {
                                            // Role matches, grant access
                                            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Login.this, HomePage.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
                                            startActivity(intent);
                                        } else {
                                            // Role does not match, deny access
                                            Toast.makeText(Login.this, "Access denied. You are not authorized.", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut(); // Sign out the unauthorized user
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "Access denied. You are not authorized.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Login.this, "User not found.", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            // Login failed
                            Toast.makeText(Login.this, "Invalid username or password. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Username does not exist
                    Toast.makeText(Login.this, "Username does not exist. Please check and try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                Toast.makeText(Login.this, "Error during login. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }


}