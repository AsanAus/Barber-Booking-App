package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.upcoming_booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class lastConfirm extends AppCompatActivity {

    private TextView BarberNameTV, BarberLocationTV, ServiceNameTV, ServicePriceTV, DateAndTimeTimeDayTV, DateAndTimeDateYearTV;
    private Button BarberEditBtn, ServiceEditBtn, DateAndTimeEditBtn, lastconfirm;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_last_confirm);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        // Apply window insets for system bars (for UI padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        BarberNameTV = findViewById(R.id.Appointment2BarberNameTV);
        BarberLocationTV = findViewById(R.id.Appointment2BarberLocationTV);
        ServiceNameTV = findViewById(R.id.Appointment2ServiceNameTV);
        ServicePriceTV = findViewById(R.id.Appointment2ServicePriceTV);
        DateAndTimeTimeDayTV = findViewById(R.id.Appointment2DateAndTimeTimeDayTV);
        DateAndTimeDateYearTV = findViewById(R.id.Appointment2DateAndTimeDateYearTV);

        BarberEditBtn = findViewById(R.id.Appointment2BarberEditBtn);
        ServiceEditBtn = findViewById(R.id.Appointment2ServiceEditBtn);
        DateAndTimeEditBtn = findViewById(R.id.Appointment2DateAndTimeEditBtn);
        lastconfirm = findViewById(R.id.lastConfirmBtn);

        // Retrieve data passed from the previous activity (date_and_time)
        String barberID = getIntent().getStringExtra("barberID");
        String barberName = getIntent().getStringExtra("barberName");
        String barberLocation = getIntent().getStringExtra("barberLocation");
        String serviceName = getIntent().getStringExtra("serviceName");
        String servicesPrices = getIntent().getStringExtra("servicesPrices");
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedTime = getIntent().getStringExtra("selectedTime");

        // Process the price
        String totalPrice = calculateTotalPrice(servicesPrices);

        // Display data in TextViews
        BarberNameTV.setText(barberName);
        BarberLocationTV.setText(barberLocation);
        ServiceNameTV.setText(serviceName);
        ServicePriceTV.setText(totalPrice); // Display the processed price
        DateAndTimeTimeDayTV.setText(selectedTime); // Show the selected time
        DateAndTimeDateYearTV.setText(selectedDate); // Show the selected date



        BarberEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(lastConfirm.this, Booking.class);
            startActivity(intent); // Start the Booking activity
        });

        ServiceEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(lastConfirm.this, services.class);
            startActivity(intent); // Start the services activity
        });

        DateAndTimeEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(lastConfirm.this, date_and_time.class);
            startActivity(intent); // Start the date_and_time activity
        });

        // Handle the confirmation button click
        lastconfirm.setOnClickListener(v -> {
            // Write data to Firebase when Last Confirm button is clicked
            writeToFirebase(barberID, barberLocation, serviceName, totalPrice, selectedDate, selectedTime);
            Intent intent =  new Intent(lastConfirm.this, upcoming_booking.class);
            startActivity(intent);
        });
    }

    // Method to write the appointment data to Firebase
    private void writeToFirebase(String barberID, String barberLocation, String serviceName, String servicePrice, String date, String time) {
        // Create a new appointment object with status "pending"
        Apt appointment = new Apt(barberID, barberLocation, serviceName, servicePrice, date, time, "pending");

        // Write the appointment to Firebase
        String appointmentId = databaseReference.push().getKey();
        if (appointmentId != null) {
            databaseReference.child(appointmentId).setValue(appointment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(lastConfirm.this, "Appointment confirmed and saved with pending status!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(lastConfirm.this, "Failed to confirm appointment.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    // Method to process prices
    private String calculateTotalPrice(String servicesPrices) {
        if (servicesPrices == null || servicesPrices.isEmpty()) {
            return "RM0"; // Default value if prices are not provided
        }

        String[] prices = servicesPrices.split(","); // Split by commas
        if (prices.length == 1) {
            return prices[0].trim(); // Return the single price
        }

        double total = 0;
        for (String price : prices) {
            // Extract numeric value from "RMXX" format
            String numericValue = price.trim().replace("RM", "").trim();
            try {
                total += Double.parseDouble(numericValue);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Log error if parsing fails
            }
        }

        return "RM" + String.format("%.2f", total); // Return total with "RM" prefix
    }
}
