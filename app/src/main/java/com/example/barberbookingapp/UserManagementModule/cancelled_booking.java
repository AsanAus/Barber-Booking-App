package com.example.barberbookingapp.UserManagementModule;

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

public class cancelled_booking extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CancelledBookingModel> CancelledBookingModelArrayList = new ArrayList<>();

    cancelledBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cancelled_booking);

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
                Intent intent = new Intent(cancelled_booking.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        TextView upcomingBooking = findViewById(R.id.TVupcoming);
        upcomingBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cancelled_booking.this, upcoming_booking.class);
                startActivity(intent);

            }
        });

        TextView completedBooking = findViewById(R.id.TVcompleted);
        completedBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cancelled_booking.this, completed_booking.class);
                startActivity(intent);

            }
        });


        //Initialize adapter and set to RecyclerView
        adapter = new cancelledBookingAdapter(this,CancelledBookingModelArrayList);
        recyclerView.setAdapter(adapter);

        // Get the appointmentId passed from the previous activity
        String appointmentId = getIntent().getStringExtra("appointmentId");
        if(appointmentId != null){
            Log.d("AppointmentsID", "Received appointmentId: " + appointmentId);
            // Handle the received bookingId (if needed)
        }else {
            Log.d("AppointmentID", "No appointmentId passed to this activity.");
        }

        String currentUserID = getCurrentUserId();


        fetchAppointmentsData(currentUserID, appointmentId);
    }

    private String getCurrentUserId() {
        String currentUserId = null;
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("UserID", "Current user ID: " + currentUserId); // Log the barber ID
        return currentUserId;
    }

    private void fetchAppointmentsData(String currentUserId, String appointmentId ) {
            if(currentUserId == null) return;

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("Barbers");



        //Fetch Appointments
        appointmentsRef.orderByChild("userID").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear the list to avoid duplicates
                CancelledBookingModelArrayList.clear();
                for(DataSnapshot appoinmentSnapshot : snapshot.getChildren()){
                    String appointmentID = appoinmentSnapshot.getKey();
                    String barberID = appoinmentSnapshot.child("barberID").getValue(String.class);
                    String date = appoinmentSnapshot.child("date").getValue(String.class);
                    String time = appoinmentSnapshot.child("time").getValue(String.class);
                    String status = appoinmentSnapshot.child("status").getValue(String.class);

                    if("cancelled".equals(status)){
                        //fetch Barber Details
                        barberRef.child(barberID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot barbersnapshot) {
                                String username = barbersnapshot.child("username").getValue(String.class);
                                String location = barbersnapshot.child("location").getValue(String.class);
                                int profilePicture = R.drawable.usericon;

                                //Add cancelled Booking to the list
                                CancelledBookingModelArrayList.add(new CancelledBookingModel(profilePicture, username, date, time, location,appointmentID));

                                //notify adapter
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(cancelled_booking.this, "Failed to load barber details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(cancelled_booking.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();

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