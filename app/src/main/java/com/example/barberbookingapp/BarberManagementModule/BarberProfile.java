package com.example.barberbookingapp.BarberManagementModule;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.android.material.button.MaterialButton;

public class BarberProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set OnClickListener for Back MaterialButton
        MaterialButton Back = findViewById(R.id.BTBack);
        Back.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberAdmin.class));
        });

        // Set OnClickListener for Edit Profile MaterialButton
        MaterialButton EditProfile = findViewById(R.id.BTEditProfile);
        EditProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberEditProfile.class));
        });

        // Set OnClickListener for My Services MaterialButton
        MaterialButton MyServices = findViewById(R.id.BTMyService);
        MyServices.setOnClickListener(view -> {
            startActivity(new Intent(this, Services.class));
        });

        // Set OnClickListener for Gallery MaterialButton
        MaterialButton Gallery = findViewById(R.id.BTGallery);
        Gallery.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberGallery.class));
        });

    }
}