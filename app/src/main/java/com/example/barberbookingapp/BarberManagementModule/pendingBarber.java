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
import com.example.barberbookingapp.UserManagementModule.cancelled_booking;
import com.example.barberbookingapp.UserManagementModule.upcoming_booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pendingBarber extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PendingBookingModel> PendingBookingModelArrayList = new ArrayList<>();
    pendingBarberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_barber);

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
                Intent intent = new Intent(pendingBarber.this, upcomingBarber.class);
                startActivity(intent);

            }
        });

        TextView CompletedBooking = findViewById(R.id.TVcompleted);
        CompletedBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pendingBarber.this, completedBarber.class);
                startActivity(intent);

            }
        });



        adapter = new pendingBarberAdapter(this,PendingBookingModelArrayList);
        recyclerView.setAdapter(adapter);

        // Replace "currentBarberId" with the actual ID of the logged-in barber
        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID


        fetchPendingBooking(currentBarberId);
    }

    private String getCurrentBarberId() {
        // Retrieve the current barber ID from shared preferences, authentication session, or other methods
        //return FirebaseAuth.getInstance().getCurrentUser().getUid(); // Example using Firebase Authentication

        String currentBarberId = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentBarberId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("BarberID", "Current Barber ID: " + currentBarberId); // Log the barber ID
        return currentBarberId;
    }

    private void fetchPendingBooking(String currentBarberID) {
        if (currentBarberID == null) return; // No action if barberID is null

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        appointmentsRef.orderByChild("barberID").equalTo(currentBarberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PendingBookingModelArrayList.clear();
                ArrayList<PendingBookingModel> tempList = new ArrayList<>();
                Log.d("FirebaseSnapshot", "Number of appointments: " + snapshot.getChildrenCount());
                for (DataSnapshot pendingSnapshot : snapshot.getChildren()) {
                    String date = pendingSnapshot.child("date").getValue(String.class);
                    String time = pendingSnapshot.child("time").getValue(String.class);
                    String serviceName = pendingSnapshot.child("serviceName").getValue(String.class);
                    String price = pendingSnapshot.child("servicePrice").getValue(String.class);
                    String status = pendingSnapshot.child("status").getValue(String.class);
                    String userID = pendingSnapshot.child("userID").getValue(String.class);
                    String appointmentID = pendingSnapshot.getKey();
                    Log.d("AppointmentStatusPendingBooking", "Status: " + status);
                    Log.d("UserIDPendingBooking", "Fetched User ID: " + userID);
                    if ("upcoming".equals(status)) {
                        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot usersnapshot) {
                                String username = usersnapshot.child("username").getValue(String.class);
                                String contact = usersnapshot.child("phoneNumber").getValue(String.class);

                                tempList.add(new PendingBookingModel(username, date, time, contact, serviceName, price,appointmentID));
                                Log.d("tempListPending", "templist : "+tempList);
                                Log.d("tempListPendingSize", "templistSize : "+tempList.size());
                                Log.d("snapshotSize", "snapshotSize : "+snapshot.getChildrenCount());


                                PendingBookingModelArrayList.clear();
                                PendingBookingModelArrayList.addAll(tempList);
                                Log.d("RecyclerViewPendingBooking", "Updated PendingBookingModelArrayList size: " + PendingBookingModelArrayList.size());
                                adapter.notifyDataSetChanged();
                                Log.d("AdapterUpdate", "RecyclerView adapter notified of data change.");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(pendingBarber.this, "Failed to load pending booking details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(pendingBarber.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });

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