package com.example.barberbookingapp.UserManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

       // CancelledBookingModelArrayList.add(new CancelledBookingModel(R.drawable.usericon,"NADHEA","DEC 10 Jan","7.00PM","KLANG"));

       // cancelledBookingAdapter cancelledBookingAdapter = new cancelledBookingAdapter(this,CancelledBookingModelArrayList);
      //  recyclerView.setAdapter(cancelledBookingAdapter);

        //Initialize adapter and set to RecyclerView
        adapter = new cancelledBookingAdapter(this,CancelledBookingModelArrayList);
        recyclerView.setAdapter(adapter);

        fetchAppointmentsData();
    }

    private void fetchAppointmentsData() {

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("Barbers");

        //Clear the list to avoid duplicates
        CancelledBookingModelArrayList.clear();

        //Fetch Appointments
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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