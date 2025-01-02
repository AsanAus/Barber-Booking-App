package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class appointment extends AppCompatActivity {

    private Button chooseBarberBtn, chooseServiceBtn, chooseDateAndTimeBtn;
    private CardView barberCardView, serviceCardView, dateAndTimeCardView;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment); // Use a distinct layout for this activity

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appointmentRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        chooseBarberBtn = findViewById(R.id.Appointment1ChooseBarberBtn);
        chooseServiceBtn = findViewById(R.id.Appointment1ChooseServiceBtn);
        chooseDateAndTimeBtn = findViewById(R.id.Appointment1ChooseDateAndTimeBtn);

        barberCardView = findViewById(R.id.Appointment2BarberCardView);
        serviceCardView = findViewById(R.id.Appointment2ServiceCardView);
        dateAndTimeCardView = findViewById(R.id.Appointment2DateAndTimeCardView);

        // Initially hide all card views
        barberCardView.setVisibility(View.GONE);
        serviceCardView.setVisibility(View.GONE);
        dateAndTimeCardView.setVisibility(View.GONE);

        // Handle ChooseBarberBtn click
        chooseBarberBtn.setOnClickListener(v -> {
            Intent intent = new Intent(appointment.this, Booking.class);
            startActivityForResult(intent, 1); // Start Booking activity with request code 1
        });

        // Handle ChooseServiceBtn click
        chooseServiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(appointment.this, services.class);
            startActivity(intent);
        });

        // Handle ChooseDateAndTimeBtn click
        chooseDateAndTimeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(appointment.this, date_and_time.class);
            startActivity(intent);
        });


    }





}