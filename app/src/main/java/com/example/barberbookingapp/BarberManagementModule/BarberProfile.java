package com.example.barberbookingapp.BarberManagementModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.barberbookingapp.GeneralModule.BarberLogin;
import com.example.barberbookingapp.GeneralModule.Login;
import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.MyProfile;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;

public class BarberProfile extends AppCompatActivity {


    private ImageView ivMyProfilePicture;
    private TextView tvBarberName;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_profile);

        tvBarberName = findViewById(R.id.TVName);
        ivMyProfilePicture = findViewById(R.id.IVProfile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Barbers");

        //Load user profile data
        loadBarberProfile();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back
        Button Back = findViewById(R.id.BTBack);
        Back.setOnClickListener(view -> {
            startActivity(new Intent(this, pendingBarber.class));
        });

        //Edit Profile
        Button EditProfile = findViewById(R.id.BTEditProfile);
        EditProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberEditProfile.class));
        });

        // My Services
        Button MyServices = findViewById(R.id.BTMyService);
        MyServices.setOnClickListener(view -> {
            startActivity(new Intent(this, Services.class));
        });

        //Gallery
        Button Gallery = findViewById(R.id.BTGallery);
        Gallery.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberGallery.class));
        });

        //Logout
        Button Logout = findViewById(R.id.BTLogOut);
        Logout.setOnClickListener(view -> {
            showLogOutConfirmation();
        });

    }

    private void showLogOutConfirmation() {
        // Create an AlertDialog for log out confirmation
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOutAndRedirect();
                    }
                })
                .setNegativeButton("Cancel", null)  // Cancel button does nothing
                .show();
    }
    private void logOutAndRedirect() {
        // Sign out from Firebase
        mAuth.signOut();

        // Redirect to Login Activity
        Intent intent = new Intent(BarberProfile.this, BarberLogin.class);
        startActivity(intent);
        finish();  // Close the current activity
    }

    private void loadBarberProfile() {
        String userID = mAuth.getCurrentUser().getUid();

        //retrive Barber data from firebase
        mDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Fetch user details
                    String username = snapshot.child("username").getValue(String.class);
                    String profileImage = snapshot.child("profileImage").getValue(String.class);

                    //set TextViews
                    tvBarberName.setText(username);

                    // Decode and set profile image
                    if (profileImage != null && !profileImage.isEmpty()) {
                        Bitmap profileBitmap = decodeBase64(profileImage);
                        ivMyProfilePicture.setImageBitmap(profileBitmap);
                    } else {
                        ivMyProfilePicture.setImageResource(R.drawable.usericon); // Placeholder image
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Decode Base64 string to Bitmap
    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }

}