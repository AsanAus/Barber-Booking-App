package com.example.barberbookingapp.GeneralModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText signupEmail, signupUsername, signupPassword;
    TextView signinText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);



        //click text to go Login activity
        signinText = findViewById(R.id.TVSignUp);
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });



        signupEmail = findViewById(R.id.ETEmail);
        signupUsername = findViewById(R.id.ETUsername);
        signupPassword = findViewById(R.id.ETPassword);
        signupButton = findViewById(R.id.BtnSignUp);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String Email = signupEmail.getText().toString();
                String Username = signupUsername.getText().toString();
                String Password = signupPassword.getText().toString();

                HelperClass helperClass = new HelperClass(Email, Username, Password);
                reference.child(Username).setValue(helperClass);

                Toast.makeText(SignUp.this, "You have sign up successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
