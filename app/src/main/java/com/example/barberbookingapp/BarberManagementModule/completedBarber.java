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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class completedBarber extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CompletedBookingModel> CompletedBookingModelArrayList = new ArrayList<>();
    completedBarberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_completed_barber);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);


        TextView UpcomingBooking = findViewById(R.id.TVupcoming);
        UpcomingBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(completedBarber.this, upcomingBarber.class);
                startActivity(intent);

            }
        });

        TextView pendingBooking = findViewById(R.id.TVpending);
        pendingBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(completedBarber.this, pendingBarber.class);
                startActivity(intent);

            }
        });

        adapter = new completedBarberAdapter(this,CompletedBookingModelArrayList);
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

        fetchCompletedBooking(currentBarberId,bookingId);

    }

    private void fetchCompletedBooking(String currentBarberId, String bookingId) {
        if (currentBarberId == null) return; // No action if barberID is null

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        appointmentsRef.orderByChild("barberID").equalTo(currentBarberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CompletedBookingModelArrayList.clear();
                ArrayList<CompletedBookingModel> templist = new ArrayList<>();
                Log.d("FirebaseSnapshot", "Number of appointments: " + snapshot.getChildrenCount());

                // Use a single-element array to hold the total income
                final double[] totalIncome = {0.0};

                for(DataSnapshot completedSnapshot : snapshot.getChildren()){
                    String appointmentID =  completedSnapshot.getKey();
                    String price =  completedSnapshot.child("servicePrice").getValue(String.class);
                    String status =  completedSnapshot.child("status").getValue(String.class);
                    String userID =  completedSnapshot.child("userID").getValue(String.class);

                    // Filter by bookingId if provided
                    //  if (bookingId != null && !bookingId.equals(appointmentID)) {
                    //       continue; // Skip appointments that don't match the bookingId
                    //   }
                    Log.d("FirebaseData", "Fetched data: " +  completedSnapshot.getValue());
                    Log.d("AppointmentStatus", "Status: " + status);
                    Log.d("UserID", "Fetched User ID: " + userID);

                    if ("completed".equals(status)) {

                        try {
                            String servicePrice = price.replace("RM","").trim(); //clean price remove RM
                            totalIncome[0] += Double.parseDouble(servicePrice); // Accumulate total income
                        } catch (NumberFormatException e) {
                            Log.e("IncomeCalculation", "Invalid price format: " + price);
                        }



                        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot usersnapshot) {
                                String username = usersnapshot.child("username").getValue(String.class);


                                templist.add(new CompletedBookingModel(username,  price, appointmentID));
                                Log.d("tempListUpcoming", "templist : "+ templist);
                                Log.d("tempListUpcomingSize", "templistSize : "+ templist.size());
                                Log.d("snapshotSize", "snapshotSize : "+snapshot.getChildrenCount());

                                // Update the list and adapter after all data is loaded

                                CompletedBookingModelArrayList.clear();
                                CompletedBookingModelArrayList.addAll(templist);
                                Log.d("RecyclerViewCompletedBooking", "Updated CompletedBookingModelArrayList size: " + CompletedBookingModelArrayList.size());
                                adapter.notifyDataSetChanged();
                                Log.d("AdapterUpdate", "RecyclerView adapter notified of data change.");




                                // Update total income in the TextView
                                TextView totalIncomeTextView = findViewById(R.id.TVtotalIncome);
                                totalIncomeTextView.setText("RM" + String.format("%.2f", totalIncome[0]));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(completedBarber.this, "Failed to load pending booking details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

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