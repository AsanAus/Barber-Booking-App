package com.example.barberbookingapp.UserManagementModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.barberbookingapp.GeneralModule.Login;
import com.example.barberbookingapp.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MyProfile extends AppCompatActivity {

    private ImageView ivMyProfilePicture;
    private TextView tvUsername, tvEmail;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this); // Initialize Fresco
        setContentView(R.layout.activity_my_profile);

        // Back to HomePage button
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, HomePage.class);
            startActivity(intent);
            finish();
        });

        // Edit Profile button
        Button editProfileButton = findViewById(R.id.BTNeditprofile);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfile.this, EditProfile.class);
            startActivity(intent);
            finish();
        });

        // Initialize views
        ivMyProfilePicture = findViewById(R.id.IVMyProfilePicture);
        tvUsername = findViewById(R.id.TVusername);
        tvEmail = findViewById(R.id.TVemail);
        btnLogout = findViewById(R.id.BTNLogOut);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Log Out button
        btnLogout.setOnClickListener(v -> showLogOutConfirmation());

        // Load user profile data
        loadUserProfile();
    }

    private void showLogOutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logOutAndRedirect())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logOutAndRedirect() {
        mAuth.signOut();
        Intent intent = new Intent(MyProfile.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void loadUserProfile() {

        String userID = mAuth.getCurrentUser().getUid();

        //retrive user data from firebase
        mDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Fetch user details
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String encodedImage = snapshot.child("profilePicture").getValue(String.class);

                    //set TextViews
                    tvUsername.setText(username);
                    tvEmail.setText(email);

                    // Decode and set profile image
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        Bitmap profileBitmap = decodeBase64(encodedImage);
                        ivMyProfilePicture.setImageBitmap(profileBitmap);

                        // Apply Glide with circular cropping to display the image
                        Glide.with(MyProfile.this)
                                .load(profileBitmap)
                                .circleCrop()
                                .placeholder(R.drawable.usericon) // Replace with your placeholder drawable
                                .into(ivMyProfilePicture);

                    } else {
                        ivMyProfilePicture.setImageResource(R.drawable.usericon); // Placeholder image
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching profile", error.toException());

            }
        });
    }

    // Decode Base64 string to Bitmap
    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }
}

