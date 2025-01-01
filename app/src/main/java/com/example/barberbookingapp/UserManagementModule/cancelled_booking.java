package com.example.barberbookingapp.UserManagementModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class cancelled_booking extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CancelledBookingModel> CancelledBookingModelArrayList = new ArrayList<>();

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

        CancelledBookingModelArrayList.add(new CancelledBookingModel(R.drawable.usericon,"NADHEA","DEC 10 Jan","7.00PM","KLANG"));

        cancelledBookingAdapter cancelledBookingAdapter = new cancelledBookingAdapter(this,CancelledBookingModelArrayList);
        recyclerView.setAdapter(cancelledBookingAdapter);
    }



    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userProfileIcon) {
            // Redirect to MyProfileActivity
            Intent intent = new Intent(cancelled_booking.this, myprofile.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}