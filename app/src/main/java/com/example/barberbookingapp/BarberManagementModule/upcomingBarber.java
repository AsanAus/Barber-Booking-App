package com.example.barberbookingapp.BarberManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.MyProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class upcomingBarber extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UpcomingBarberModel> UpcomingBarberModelArrayList = new ArrayList<>();
    
    upcomingBarberAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_barber);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);


        TextView pendingBooking = findViewById(R.id.TVpending);
        pendingBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcomingBarber.this, pendingBarber.class);
                startActivity(intent);

            }
        });

        TextView CompletedBooking = findViewById(R.id.TVcompleted);
       CompletedBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(upcomingBarber.this, completedBarber.class);
                startActivity(intent);

            }
        });




        adapter = new upcomingBarberAdapter(this,UpcomingBarberModelArrayList);
        recyclerView.setAdapter(adapter);

        // Get the bookingId passed from the previous activity
        String bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId != null) {
            Log.d("BookingID", "Received bookingId: " + bookingId);
            // Handle the received bookingId (if needed)
        } else {
            Log.d("BookingID", "No bookingId passed to this activity.");
        }

        // Replace "currentBarberId" with the actual ID of the logged-in barber
        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID
        
        fetchUpcomingBooking(currentBarberId,bookingId);


    }

    private void fetchUpcomingBooking(String currentBarberId, String bookingId) {
        if (currentBarberId == null) return; // No action if barberID is null

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        appointmentsRef.orderByChild("barberID").equalTo(currentBarberId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UpcomingBarberModelArrayList.clear();
                ArrayList<UpcomingBarberModel> tempList = new ArrayList<>();
                Log.d("FirebaseSnapshot", "Number of appointments: " + snapshot.getChildrenCount());

                for (DataSnapshot upcomingSnapshot : snapshot.getChildren()) {
                    String appointmentID =  upcomingSnapshot.getKey();
                    String date =  upcomingSnapshot.child("date").getValue(String.class);
                    String time =  upcomingSnapshot.child("time").getValue(String.class);
                    String serviceName =  upcomingSnapshot.child("serviceName").getValue(String.class);
                    String price =  upcomingSnapshot.child("servicePrice").getValue(String.class);
                    String status =  upcomingSnapshot.child("status").getValue(String.class);
                    String userID =  upcomingSnapshot.child("userID").getValue(String.class);

                    // Filter by bookingId if provided
                    if (bookingId != null && !bookingId.equals(appointmentID)) {
                        continue; // Skip appointments that don't match the bookingId
                    }
                    Log.d("FirebaseData", "Fetched data: " +  upcomingSnapshot.getValue());
                    Log.d("AppointmentStatus", "Status: " + status);
                    Log.d("UserID", "Fetched User ID: " + userID);

                    if ("upcomingAccepted".equals(status)) {
                        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot usersnapshot) {
                                String username = usersnapshot.child("username").getValue(String.class);
                                String contact = usersnapshot.child("phoneNumber").getValue(String.class);

                                tempList.add(new UpcomingBarberModel(username, date, time, contact, serviceName, price, appointmentID));

                                // Update the list and adapter after all data is loaded
                                if (tempList.size() == snapshot.getChildrenCount() || bookingId != null) {
                                    UpcomingBarberModelArrayList.clear();
                                    UpcomingBarberModelArrayList.addAll(tempList);
                                    Log.d("RecyclerView", "Updated PendingBookingModelArrayList size: " + UpcomingBarberModelArrayList.size());
                                    adapter.notifyDataSetChanged();
                                    Log.d("AdapterUpdate", "RecyclerView adapter notified of data change.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(upcomingBarber.this, "Failed to load pending booking details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(upcomingBarber.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userProfileIcon) {
            // Redirect to MyProfileActivity
            Intent intent = new Intent(this, BarberProfile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}