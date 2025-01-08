package com.example.barberbookingapp.BookingModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.example.barberbookingapp.UserManagementModule.MyProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Booking extends AppCompatActivity {

    private Button Barber1, Barber2, Barber3, Barber4, Barber5;
    private TextView barber1NameTV, barber2NameTV, barber3NameTV, barber4NameTV, barber5NameTV;
    private  TextView barber1LocationTV,barber2LocationTV,barber3LocationTV,barber4LocationTV,barber5LocationTV;
    private RatingBar barber1RatingBar,barber2RatingBar,barber3RatingBar,barber4RatingBar,barber5RatingBar;

    private List<TextView> barberNameTVs = new ArrayList<>();
    private List<TextView> barberLocationTVs = new ArrayList<>();
    private List<RatingBar> barberRatingBars = new ArrayList<>();
    private List<Button> barberSelectButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.barber_list);

        // Find the TextView by its ID
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);//For Back button return to previous page which is(HomePage)
        // Set an OnClickListener to navigate to HomePageActivity
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Booking.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        // Initialize UI components
        barberNameTVs.add(findViewById(R.id.Barber1NameTV));
        barberNameTVs.add(findViewById(R.id.Barber2NameTV));
        barberNameTVs.add(findViewById(R.id.Barber3NameTV));
        barberNameTVs.add(findViewById(R.id.Barber4NameTV));
        barberNameTVs.add(findViewById(R.id.Barber5NameTV));

        barberLocationTVs.add(findViewById(R.id.Barber1LocationTV));
        barberLocationTVs.add(findViewById(R.id.Barber2LocationTV));
        barberLocationTVs.add(findViewById(R.id.Barber3LocationTV));
        barberLocationTVs.add(findViewById(R.id.Barber4LocationTV));
        barberLocationTVs.add(findViewById(R.id.Barber5LocationTV));

        barberRatingBars.add(findViewById(R.id.Barber1RatingBar));
        barberRatingBars.add(findViewById(R.id.Barber2RatingBar));
        barberRatingBars.add(findViewById(R.id.Barber3RatingBar));
        barberRatingBars.add(findViewById(R.id.Barber4RatingBar));
        barberRatingBars.add(findViewById(R.id.Barber5RatingBar));

        barberSelectButtons.add(findViewById(R.id.Barber1SelectBtn));
        barberSelectButtons.add(findViewById(R.id.Barber2SelectBtn));
        barberSelectButtons.add(findViewById(R.id.Barber3SelectBtn));
        barberSelectButtons.add(findViewById(R.id.Barber4SelectBtn));
        barberSelectButtons.add(findViewById(R.id.Barber5SelectBtn));


        // Fetch barbers from Firebase
        fetchBarbers();
    }

    private void fetchBarbers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Barbers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < barberNameTVs.size(); i++) {
                    String barberNameToMatch = barberNameTVs.get(i).getText().toString();

                    for (DataSnapshot barberSnapshot : dataSnapshot.getChildren()) {
                        String username = barberSnapshot.child("username").getValue(String.class);

                        if (username != null && username.equals(barberNameToMatch)) {
                            String barberID = barberSnapshot.getKey();
                            String location = barberSnapshot.child("location").getValue(String.class);
                            Float rating = barberSnapshot.child("rating").getValue(Float.class);

                            // Populate matched barber details
                            barberLocationTVs.get(i).setText(location != null ? location : "Unknown");
                            barberRatingBars.get(i).setRating(rating != null ? rating : 0);

                            // Set button click listener
                            int finalIndex = i; // For lambda use
                            barberSelectButtons.get(i).setOnClickListener(v -> {
                                Intent intent = new Intent(Booking.this, services.class);
                                intent.putExtra("barberID", barberID);
                                intent.putExtra("barberName", barberNameTVs.get(finalIndex).getText().toString());
                                intent.putExtra("barberLocation", barberLocationTVs.get(finalIndex).getText().toString());
                                startActivityForResult(intent, 1);
                                Log.d("BarberID", "BarberID selected " + barberID);

                            });

                            break; // Exit loop once matched
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}





