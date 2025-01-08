package com.example.barberbookingapp.BarberListViewModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;

public class BarberDetails extends AppCompatActivity {

    private TextView barberListTV;
    private ImageView barberImageView;
    private TextView detailsTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_details);


        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarberDetails.this, BarberListView.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        // Retrieve the barberId from the Intent
        String barberId = getIntent().getStringExtra("barberId");
        if (barberId != null) {
            // Use the barberId to load the barber details (e.g., from Firebase or a database)
            Log.d("BarberDetails", "Selected Barber ID: " + barberId);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Retrieve the Barber object passed via intent
        Barber selectedBarber = getIntent().getParcelableExtra("selectedBarber");

        if (selectedBarber != null) {
            // Set the data to the views
            barberListTV = findViewById(R.id.BarberListTV); // Barber name TextView
            barberImageView = findViewById(R.id.imageView2); // Barber image ImageView
            detailsTextView = findViewById(R.id.details); // Barber details TextView
            locationTextView = findViewById(R.id.location); // Location TextView

            barberListTV.setText(selectedBarber.getUsername());
            detailsTextView.setText(selectedBarber.getDetails());
            locationTextView.setText(selectedBarber.getLocation());
            barberImageView.setImageResource(selectedBarber.getImageResourceId());
        }


    }
}