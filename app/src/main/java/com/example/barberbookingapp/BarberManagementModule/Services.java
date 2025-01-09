package com.example.barberbookingapp.BarberManagementModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Services extends AppCompatActivity {

    private EditText serviceNameEditText;
    private EditText priceEditText;
    private NumberPicker hourPicker;
    private Button submitButton;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView recyclerView;
    private DatabaseReference serviceReference;
    private DatabaseReference databaseReference;
    private ArrayList<Service> ServicesList;
    ServiceAdapter adapter;
    private String barberId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_services);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button Back = findViewById(R.id.BTBack);
        Back.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberProfile.class));
        });

        // Get references to NumberPickers
        hourPicker = findViewById(R.id.HourPicker);

        // Set the range of the NumberPickers
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);

        // Create an array of displayed values
        String[] displayedHour = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

        // Set the displayed values to the HourPicker and MinPicker
        hourPicker.setDisplayedValues(displayedHour);

        // Disable wrapping if needed
        hourPicker.setWrapSelectorWheel(false);

        // Initialize UI elements
        serviceNameEditText = findViewById(R.id.ETNameOfService);
        priceEditText = findViewById(R.id.TILETPrice);
        submitButton = findViewById(R.id.BTAddService);

        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID

        // Initialize Firebase components
        FirebaseAuth auth = FirebaseAuth.getInstance();
        barberId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        firebaseDatabase = FirebaseDatabase.getInstance();
        serviceReference = firebaseDatabase.getReference("Barbers").child(currentBarberId).child("service");  // Adjust the reference path if necessary



        //display created services
        loadMyServices();
        
        // Button click listener to upload data to Firebase
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from UI elements
                String ServiceName = serviceNameEditText.getText().toString();
                String Price = String.valueOf(priceEditText.getText());
                String Duration = String.valueOf(hourPicker.getValue());


                // Upload to Firebase
                String serviceId = serviceReference.push().getKey();  // Generates a unique key for each service
                // Create a service object
                Service service = new Service(serviceId, barberId, ServiceName, Duration, Price);
                if (serviceId != null) {
                    serviceReference.child(serviceId).setValue(service)
                            .addOnSuccessListener(aVoid -> {
                                // Handle success
                                Toast.makeText(Services.this, "Service added successfully", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(Services.this, "Failed to add service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
                loadMyServices();
            }
        });
    }

    private void loadMyServices() {
        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID
        recyclerView = findViewById(R.id.rvServices);
        databaseReference = FirebaseDatabase.getInstance().getReference("Barbers").child(currentBarberId).child("service");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ServicesList = new ArrayList<>();
        adapter = new ServiceAdapter(this, ServicesList);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ServicesList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Service service = dataSnapshot.getValue(Service.class);
                    ServicesList.add(service);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getCurrentBarberId() {
        String currentBarberId = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentBarberId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("BarberID", "Current Barber ID: " + currentBarberId); // Log the barber ID
        return currentBarberId;
    }
}