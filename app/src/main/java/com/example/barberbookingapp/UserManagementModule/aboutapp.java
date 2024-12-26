package com.example.barberbookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.example.barberbookingapp.UserManagementModule.editprofile;
import com.example.barberbookingapp.UserManagementModule.myprofile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class aboutapp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aboutapp);

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);


        // Back button functionality
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(aboutapp.this, HomePage.class);
            startActivity(intent);
            finish();
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set listener for item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Desthome) {
                    startActivity(new Intent(aboutapp.this, HomePage.class));
                    return true;
                } else if (item.getItemId() == R.id.DestBooking) {
                    //  startActivity(new Intent(HomePage.this, BookingActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.DestAboutApp) {
                    startActivity(new Intent(aboutapp.this, aboutapp.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userProfileIcon) {
            // Redirect to MyProfileActivity
            Intent intent = new Intent(aboutapp.this, myprofile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}