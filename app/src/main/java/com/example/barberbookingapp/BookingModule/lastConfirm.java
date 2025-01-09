package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.MyProfile;
import com.example.barberbookingapp.UserManagementModule.upcoming_booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;


public class lastConfirm extends AppCompatActivity {

    private TextView BarberNameTV, BarberLocationTV, ServiceNameTV, ServicePriceTV, DateAndTimeTimeDayTV, DateAndTimeDateYearTV;
    private Button BarberEditBtn, ServiceEditBtn, DateAndTimeEditBtn, lastconfirm;

    private ImageView BarberIV;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_last_confirm);

        TextView backToHome = findViewById(R.id.TVback);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(lastConfirm.this, date_and_time.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

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
        BarberIV = findViewById(R.id.Appointment2BarberIV);

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

        // Set the barber image based on the barberID
        databaseReference = FirebaseDatabase.getInstance().getReference("Barbers");
        databaseReference.child(barberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Fetch user details
                    String encodedImage = snapshot.child("profileImage").getValue(String.class);

                    // Decode and set profile image
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        Bitmap profileBitmap = decodeBase64(encodedImage);
                        BarberIV.setImageBitmap(profileBitmap);

                    } else {
                        BarberIV.setImageResource(R.drawable.usericon); // Placeholder image
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching profile", error.toException());
            }
        });



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

        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(lastConfirm.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid(); // Retrieve the user's ID

        // Create a new appointment object with status "pending"
        Apt appointment = new Apt(barberID, barberLocation, serviceName, servicePrice, date, time, "upcoming", userID);

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

    // Decode Base64 string to Bitmap
    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }
}
