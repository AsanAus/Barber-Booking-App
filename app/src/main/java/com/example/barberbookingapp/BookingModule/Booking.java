package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Booking extends AppCompatActivity {

    private Button Barber1, Barber2, Barber3, Barber4, Barber5;
    private TextView barber1NameTV, barber2NameTV, barber3NameTV, barber4NameTV, barber5NameTV;
    private  TextView barber1LocationTV,barber2LocationTV,barber3LocationTV,barber4LocationTV,barber5LocationTV;
    private RatingBar barber1RatingBar,barber2RatingBar,barber3RatingBar,barber4RatingBar,barber5RatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.barber_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Appointment2DateAndTimeDateYearTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to TextViews
        barber1NameTV = findViewById(R.id.Barber1NameTV);
        barber2NameTV = findViewById(R.id.Barber2NameTV);
        barber3NameTV = findViewById(R.id.Barber3NameTV);
        barber4NameTV = findViewById(R.id.Barber4NameTV);
        barber5NameTV = findViewById(R.id.Barber5NameTV);

        barber1LocationTV = findViewById(R.id.Barber1LocationTV);
        barber2LocationTV = findViewById(R.id.Barber2LocationTV);
        barber3LocationTV = findViewById(R.id.Barber3LocationTV);
        barber4LocationTV = findViewById(R.id.Barber4LocationTV);
        barber5LocationTV = findViewById(R.id.Barber5LocationTV);

        barber1RatingBar = findViewById(R.id.Barber1RatingBar);
        barber2RatingBar = findViewById(R.id.Barber2RatingBar);
        barber3RatingBar = findViewById(R.id.Barber3RatingBar);
        barber4RatingBar = findViewById(R.id.Barber4RatingBar);
        barber5RatingBar = findViewById(R.id.Barber5RatingBar);



        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Read barber names dynamically
        readBarberNames(databaseReference, "barber1", barber1NameTV);
        readBarberNames(databaseReference, "barber2", barber2NameTV);
        readBarberNames(databaseReference, "barber3", barber3NameTV);
        readBarberNames(databaseReference, "barber4", barber4NameTV);
        readBarberNames(databaseReference, "barber5", barber5NameTV);

        readBarberLocation(databaseReference,"barber1",barber1LocationTV);
        readBarberLocation(databaseReference,"barber2",barber2LocationTV);
        readBarberLocation(databaseReference,"barber3",barber3LocationTV);
        readBarberLocation(databaseReference,"barber4",barber4LocationTV);
        readBarberLocation(databaseReference,"barber5",barber5LocationTV);

        readBarberRating(databaseReference,"barber1",barber1RatingBar);
        readBarberRating(databaseReference,"barber2",barber2RatingBar);
        readBarberRating(databaseReference,"barber3",barber3RatingBar);
        readBarberRating(databaseReference,"barber4",barber4RatingBar);
        readBarberRating(databaseReference,"barber5",barber5RatingBar);


        // Get references to buttons
        Barber1 = findViewById(R.id.Barber1SelectBtn);
        Barber2 = findViewById(R.id.Barber2SelectBtn);
        Barber3 = findViewById(R.id.Barber3SelectBtn);
        Barber4 = findViewById(R.id.Barber4SelectBtn);
        Barber5 = findViewById(R.id.Barber5SelectBtn);

        // Set click listeners for buttons
        Barber1.setOnClickListener(v -> {
            Intent intent = new Intent(Booking.this, services.class);
            intent.putExtra("barberID", "barber1"); // Pass the barber ID
            intent.putExtra("barberName", barber1NameTV.getText().toString()); // Pass the barber name
            intent.putExtra("barberLocation", barber1LocationTV.getText().toString()); // Pass the barber location
            startActivityForResult(intent, 1);
        });

        Barber2.setOnClickListener(v -> {
            Intent intent = new Intent(Booking.this, services.class);
            intent.putExtra("barberID", "barber2"); // Pass the barber ID
            intent.putExtra("barberName", barber2NameTV.getText().toString()); // Pass the barber name
            intent.putExtra("barberLocation", barber2LocationTV.getText().toString()); // Pass the barber location
            startActivityForResult(intent, 1);
        });

        Barber3.setOnClickListener(v -> {
            Intent intent = new Intent(Booking.this, services.class);
            intent.putExtra("barberID", "barber3"); // Pass the barber ID
            intent.putExtra("barberName", barber3NameTV.getText().toString()); // Pass the barber name
            intent.putExtra("barberLocation", barber3LocationTV.getText().toString()); // Pass the barber location
            startActivityForResult(intent, 1);
        });

        Barber4.setOnClickListener(v -> {
            Intent intent = new Intent(Booking.this, services.class);
            intent.putExtra("barberID", "barber4"); // Pass the barber ID
            intent.putExtra("barberName", barber4NameTV.getText().toString()); // Pass the barber name
            intent.putExtra("barberLocation", barber4LocationTV.getText().toString()); // Pass the barber location
            startActivityForResult(intent, 1);
        });

        Barber5.setOnClickListener(v -> {
            Intent intent = new Intent(Booking.this, services.class);
            intent.putExtra("barberID", "barber5"); // Pass the barber ID
            intent.putExtra("barberName", barber5NameTV.getText().toString()); // Pass the barber name
            intent.putExtra("barberLocation", barber5LocationTV.getText().toString()); // Pass the barber location
            startActivityForResult(intent, 1);
        });
    }

    // Method to read barber names dynamically
    private void readBarberNames(DatabaseReference databaseReference, String barberID, TextView textView) {
        databaseReference.child("Barbers").child(barberID).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String barberName = dataSnapshot.getValue(String.class);
                textView.setText(barberName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void readBarberLocation(DatabaseReference databaseReference, String barberID, TextView textView) {
        databaseReference.child("Barbers").child(barberID).child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String barberLocation = dataSnapshot.getValue(String.class);
                textView.setText(barberLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void readBarberRating(DatabaseReference databaseReference, String barberID, RatingBar ratingBar) {
        databaseReference.child("Barbers").child(barberID).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float barberRating = dataSnapshot.getValue(Float.class);
                if (barberRating != null) {
                    ratingBar.setRating(barberRating);
                } else {
                    ratingBar.setRating(0); // Default to 0 if no rating is found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


}