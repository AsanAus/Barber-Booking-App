package com.example.barberbookingapp.BarberListViewModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BarberListView extends AppCompatActivity {

    private ArrayList<Barber> barberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_list_view);

        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarberListView.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.barbersRecyclerView);

        BarberAdapter adapter = new BarberAdapter(this, barberList);

        // Set the GridLayoutManager with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setAdapter(adapter);

        // Load barbers from Firebase
        loadBarbersFromFirebase(adapter);


    }

    // Method to load barbers from Firebase
    private void loadBarbersFromFirebase(BarberAdapter adapter) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Barbers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barberList.clear();
                for (DataSnapshot barberSnapshot : snapshot.getChildren()) {

                    // Get the barberId (the unique key)
                    String barberId = barberSnapshot.getKey();
//                    Barber barber = barberSnapshot.getValue(Barber.class);

                    // Get the other fields from Firebase
                    String username = barberSnapshot.child("username").getValue(String.class);
                    String location = barberSnapshot.child("location").getValue(String.class);
                    double rating = barberSnapshot.child("rating").getValue(Double.class);
                    String profileImage = barberSnapshot.child("profileImage").getValue(String.class);

                    // Create a new Barber object with the barberId and other fields
                    Barber barber = new Barber(barberId, username, location, rating, profileImage);

                    if (barber != null && barber.getUsername() != null) {
                        Log.d("Firebase", "Username: " + barber.getUsername());
                    }

                    if (barber != null) {
                        barberList.add(barber);
                    }
                }
                adapter.notifyDataSetChanged();  // Update the RecyclerView when data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error loading barbers from firebase
                Log.e("FirebaseError", "Failed to read data", error.toException());
            }
        });
    }


}