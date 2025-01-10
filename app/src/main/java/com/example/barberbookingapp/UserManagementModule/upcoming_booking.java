package com.example.barberbookingapp.UserManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.BookingModule.Booking;
import com.example.barberbookingapp.BookingModule.appointment;
import com.example.barberbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class upcoming_booking extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UpcomingBookingModel> UpcomingBookingModelArrayList = new ArrayList<>();
    upcomingBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_booking);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        // Find the TextView by its ID
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);//For Back button return to previous page which is(HomePage)
        // Set an OnClickListener to navigate to HomePageActivity
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcoming_booking.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        TextView cancelledBooking = findViewById(R.id.TVcancelled);
        cancelledBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcoming_booking.this, cancelled_booking.class);
                startActivity(intent);

            }
        });

        TextView completedBooking = findViewById(R.id.TVcompleted);
        completedBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcoming_booking.this, completed_booking.class);
                startActivity(intent);

            }
        });



        // Initialize adapter and set to RecyclerView
        adapter = new upcomingBookingAdapter(this, UpcomingBookingModelArrayList);
        recyclerView.setAdapter(adapter);

        String currentUserID = getCurrentUserID();
        fetchAppointmentData(currentUserID);

        //Handle "Book Now" button
        Button bookNowButton = findViewById(R.id.bookNowButton);
        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcoming_booking.this, appointment.class);
                startActivity(intent);
            }
        });

    }

    private String getCurrentUserID(){
        String currentUserID = null;
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("UserID", "Current User ID: " + currentUserID); // Log the barber ID
        return currentUserID;
    }

    private void fetchAppointmentData(String currentUserID) {
        if(currentUserID == null) return;

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("Barbers");




        //Fetch Appointments
        appointmentsRef.orderByChild("userID").equalTo(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasPendingAppointments = false;

                // Clear the list to avoid duplicates
                UpcomingBookingModelArrayList.clear();
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                        String appointmentID = appointmentSnapshot.getKey();
                        String barberID = appointmentSnapshot.child("barberID").getValue(String.class);
                        String date = appointmentSnapshot.child("date").getValue(String.class);
                        String time = appointmentSnapshot.child("time").getValue(String.class);
                        String status = appointmentSnapshot.child("status").getValue(String.class);

                        if ("upcoming".equals(status)) {
                            hasPendingAppointments = true;
                            //Fetch Barber Details
                            barberRef.child(barberID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot barbersnapshot) {
                                    String username = barbersnapshot.child("username").getValue(String.class);
                                    String location = barbersnapshot.child("location").getValue(String.class);
                                    String profilePicture = barbersnapshot.child("profileImage").getValue(String.class);


                                    // Add new booking to the list
                                    UpcomingBookingModelArrayList.add(new UpcomingBookingModel(profilePicture, username, date, time, location,appointmentID));

                                    // Notify adapter
                                    adapter.notifyDataSetChanged();

                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(upcoming_booking.this, "Failed to load barber details", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                // Update visibility based on data
                if (hasPendingAppointments) {
                    recyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.noAppointmentsCard).setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    findViewById(R.id.noAppointmentsCard).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(upcoming_booking.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userProfileIcon) {
            // Redirect to MyProfileActivity
            Intent intent = new Intent(this, MyProfile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}