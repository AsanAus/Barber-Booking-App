package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class date_and_time extends AppCompatActivity {

    CalendarView calendarView;
    TimePicker timePicker;
    Button selectButton;

    private String selectedDate;
    private String selectedTime;

    // Firebase Realtime Database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_and_time);

        TextView backToHome = findViewById(R.id.TVback);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(date_and_time.this, services.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Appointment2DateAndTimeDateYearTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        selectButton = findViewById(R.id.SelectDateAndTimeSelectBtn);

        // Set up CalendarView to capture selected date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Note: Month is zero-based, so add 1
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        // Set up TimePicker to capture selected time in 12-hour format
        timePicker.setIs24HourView(false); // Use 12-hour format
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            // Determine AM or PM
            String period = (hourOfDay >= 12) ? "PM" : "AM";
            int hour = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12; // Convert 24-hour to 12-hour format
            selectedTime = String.format("%02d:%02d %s", hour, minute, period);
        });

        // Set up button click listener
        selectButton.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTime == null) {
                Toast.makeText(this, "Please select a time.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get data from previous activity
            String barberID = getIntent().getStringExtra("barberID");
            String barberName = getIntent().getStringExtra("barberName");
            String barberLocation = getIntent().getStringExtra("barberLocation");
            String serviceName = getIntent().getStringExtra("selectedServices");
            String servicesPrices = getIntent().getStringExtra("servicesPrices");

            // Pass data to the next activity (lastConfirm)
            Intent intent = new Intent(date_and_time.this, lastConfirm.class);
            intent.putExtra("barberID", barberID);
            intent.putExtra("barberName", barberName);
            intent.putExtra("barberLocation", barberLocation);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("servicesPrices", servicesPrices);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("selectedTime", selectedTime);

            startActivity(intent);
            finish(); // Close the current activity
        });
    }
}
