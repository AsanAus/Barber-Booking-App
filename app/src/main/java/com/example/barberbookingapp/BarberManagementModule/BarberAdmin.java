package com.example.barberbookingapp.BarberManagementModule;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barberbookingapp.R;

import java.util.concurrent.atomic.AtomicReference;

public class BarberAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button Pending = findViewById(R.id.BTPending);
        Button Upcoming = findViewById(R.id.BTUpcoming);
        Button Completed = findViewById(R.id.BTCompleted);
        AtomicReference<Button> lastPressedButton = new AtomicReference<>(Pending);
        setButtonPressed(Pending);

        // Get the NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentBookingView);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Set OnClickListener for ImageButton
        ImageButton Profile = findViewById(R.id.IBProfile);
        Profile.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberProfile.class));
        });

        // Set OnClickListener for Pending
        Pending.setOnClickListener(view -> {
            resetButtonState(lastPressedButton.get());

            setButtonPressed(Pending);
            lastPressedButton.set(Pending);
            navController.navigate(R.id.Pending);
        });

        // Set OnClickListener for Upcoming
        Upcoming.setOnClickListener(view -> {
            resetButtonState(lastPressedButton.get());

            setButtonPressed(Upcoming);
            lastPressedButton.set(Upcoming);
            navController.navigate(R.id.Upcoming);
        });

        // Set OnClickListener for Completed
        Completed.setOnClickListener(view -> {
            resetButtonState(lastPressedButton.get());

            setButtonPressed(Completed);
            lastPressedButton.set(Completed);
            navController.navigate(R.id.Completed);
        });
    }

    // Method to handle resetting the button state
    private void resetButtonState(Button button) {
        // Reset background and text color for the button (un-pressed state)
        button.setBackgroundResource(R.drawable.purple_accent_background);
        button.setTextColor(getResources().getColor(R.color.black));
        Typeface interMedium = ResourcesCompat.getFont(this, R.font.inter_medium);
        button.setTypeface(interMedium);
    }

    // Method to set the button as pressed
    private void setButtonPressed(Button button) {
        // Set the background and text color for the pressed button
        button.setBackgroundResource(R.drawable.gradient_vertical);
        button.setTextColor(getResources().getColor(R.color.white));
        Typeface interBold = ResourcesCompat.getFont(this, R.font.inter_bold);
        button.setTypeface(interBold);
    }
}