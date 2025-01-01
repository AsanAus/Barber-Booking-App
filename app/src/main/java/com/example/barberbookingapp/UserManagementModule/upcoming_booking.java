package com.example.barberbookingapp.UserManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class upcoming_booking extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UpcomingBookingModel> UpcomingBookingModelArrayList = new ArrayList<>();

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


        UpcomingBookingModelArrayList.add(new UpcomingBookingModel(R.drawable.usericon,"Nadhea","DEC 10 Jan","9.00 PM","Kelana Jaya"));
        UpcomingBookingModelArrayList.add(new UpcomingBookingModel(R.drawable.homepage,"Asan","DEC 11 Jan","10.00 PM","Subang Jaya"));
        UpcomingBookingModelArrayList.add(new UpcomingBookingModel(R.drawable.homepage,"Ali","DEC 12 Jan","11.00 PM","Subang Ria"));


        upcomingBookingAdapter upcomingBookingAdapter = new upcomingBookingAdapter(this,UpcomingBookingModelArrayList);
        recyclerView.setAdapter(upcomingBookingAdapter);


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