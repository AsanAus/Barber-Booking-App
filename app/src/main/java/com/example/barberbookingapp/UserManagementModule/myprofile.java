package com.example.barberbookingapp.UserManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;

public class myprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        // Find the TextView by its ID
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);//For Back button return to previous page which is(HomePage)
        // Set an OnClickListener to navigate to HomePageActivity
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myprofile.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

      Button editProfileButton = findViewById(R.id.BTNeditprofile);
      editProfileButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent1 = new Intent(myprofile.this , editprofile.class);
              startActivity(intent1);
              finish();
          }
      });



    }



}