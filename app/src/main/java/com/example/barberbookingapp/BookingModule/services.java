package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class services extends AppCompatActivity {

    CheckBox CB1, CB2, CB3, CB4;
    TextView service1NameTV, service2NameTV, service3NameTV, service4NameTV;
    TextView service1TimeTV, service2TimeTV, service3TimeTV, service4TimeTV;
    TextView service1PriceTV, service2PriceTV, service3PriceTV, service4PriceTV;
    Button ConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);

        TextView backToHome = findViewById(R.id.TVback);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(services.this, Booking.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        // Initialize UI components
        CB1 = findViewById(R.id.Service1CheckBox);
        CB2 = findViewById(R.id.Service2CheckBox);
        CB3 = findViewById(R.id.Service3CheckBox);
        CB4 = findViewById(R.id.Service4CheckBox);

        service1NameTV = findViewById(R.id.Service1NameTV);
        service2NameTV = findViewById(R.id.Service2NameTV);
        service3NameTV = findViewById(R.id.Service3NameTV);
        service4NameTV = findViewById(R.id.Service4NameTV);

        service1TimeTV = findViewById(R.id.Service1TimeTV);
        service2TimeTV = findViewById(R.id.Service2TimeTV);
        service3TimeTV = findViewById(R.id.Service3TimeTV);
        service4TimeTV = findViewById(R.id.Service4TimeTV);

        service1PriceTV = findViewById(R.id.Service1PriceTV);
        service2PriceTV = findViewById(R.id.Service2PriceTV);
        service3PriceTV = findViewById(R.id.Service3PriceTV);
        service4PriceTV = findViewById(R.id.Service4PriceTV);

        ConfirmBtn = findViewById(R.id.ServiceConfirmBtn);

        // Get barberID from Intent
        String barberID = getIntent().getStringExtra("barberID");
        String barberLocation = getIntent().getStringExtra("barberLocation");
        String barberName = getIntent().getStringExtra("barberName");

        // Reference Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Service");

        // Retrieve and display services for the barber
        databaseReference.child(barberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                    String serviceName = serviceSnapshot.child("serviceName").getValue(String.class);
                    String duration = serviceSnapshot.child("duration").getValue(String.class);
                    String price = serviceSnapshot.child("price").getValue(String.class);

                    if (serviceName != null && duration != null && price != null) {
                        switch (count) {
                            case 0:
                                service1NameTV.setText(serviceName);
                                service1TimeTV.setText(duration);
                                service1PriceTV.setText(price);
                                break;
                            case 1:
                                service2NameTV.setText(serviceName);
                                service2TimeTV.setText(duration);
                                service2PriceTV.setText(price);
                                break;
                            case 2:
                                service3NameTV.setText(serviceName);
                                service3TimeTV.setText(duration);
                                service3PriceTV.setText(price);
                                break;
                            case 3:
                                service4NameTV.setText(serviceName);
                                service4TimeTV.setText(duration);
                                service4PriceTV.setText(price);
                                break;
                        }
                        count++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to read data", databaseError.toException());
            }
        });

        // Set click listener for Confirm button
        ConfirmBtn.setOnClickListener(v -> {
            String selectedServices = "";
            String servicesPrices ="";

            if (CB1.isChecked()) {
                selectedServices += service1NameTV.getText().toString() + ", ";
                servicesPrices += service1PriceTV.getText().toString()+", ";
            }
            if (CB2.isChecked()) {
                selectedServices += service2NameTV.getText().toString() + ", ";
                servicesPrices += service2PriceTV.getText().toString()+", ";
            }
            if (CB3.isChecked()) {
                selectedServices += service3NameTV.getText().toString() + ", ";
                servicesPrices += service3PriceTV.getText().toString()+", ";
            }
            if (CB4.isChecked()) {
                selectedServices += service4NameTV.getText().toString() + ", ";
                servicesPrices += service4PriceTV.getText().toString()+", ";
            }

            if (!selectedServices.isEmpty()) {
                selectedServices = selectedServices.substring(0, selectedServices.length() - 2); // Remove trailing comma and space
                servicesPrices = servicesPrices.substring(0,servicesPrices.length()-2);
                Toast.makeText(services.this, "Services selected: " + selectedServices, Toast.LENGTH_SHORT).show();

                // Navigate to date_and_time activity
                Intent intent = new Intent(services.this, date_and_time.class);
                intent.putExtra("barberID", barberID);
                intent.putExtra("selectedServices", selectedServices);
                intent.putExtra("servicesPrices",servicesPrices);
                intent.putExtra("barberName",barberName);
                intent.putExtra("barberLocation",barberLocation);
                startActivity(intent);
            } else {
                Toast.makeText(services.this, "Please select at least one service", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
