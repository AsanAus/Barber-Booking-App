package com.example.barberbookingapp.UserManagementModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.barberbookingapp.GeneralModule.Login;
import com.example.barberbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;

public class myprofile extends AppCompatActivity {

    private ImageView ivMyProfilePicture;
    private TextView tvUsername, tvEmail;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_myprofile);

        // Find the TextView by its ID
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);//For Back button return to previous page which is(HomePage)
        // Set an OnClickListener to navigate to HomePageActivity
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myprofile.this, HomePage.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

      Button editProfileButton = findViewById(R.id.BTNeditprofile);
      editProfileButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent1 = new Intent(myprofile.this , editprofile.class);
              startActivity(intent1);
              finish();
          }
      });

      //Initialize views
      ivMyProfilePicture = findViewById(R.id.IVMyProfilePicture);
      tvUsername = findViewById(R.id.TVusername);
      tvEmail = findViewById(R.id.TVemail);
      btnLogout = findViewById(R.id.BTNLogOut);

      //Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutConfirmation();
            }
        });

        //Load user profile data
        loadUserProfile();

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
        Intent intent = new Intent(myprofile.this, Login.class);
        startActivity(intent);
        finish();  // Close the current activity
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
                    String profilePicture = snapshot.child("profilePicture").getValue(String.class);

                    //set TextViews
                    tvUsername.setText(username);
                    tvEmail.setText(email);

                    // Decode and set profile image
                    if (profilePicture != null && !profilePicture.isEmpty()) {
                        try {
                            byte[] decodedBytes = Base64.decode(profilePicture, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            Uri bitmapUri = saveBitmapToCache(bitmap); // Save Bitmap to cache and get URI
                            if (bitmapUri != null) {
                                ivMyProfilePicture.setImageURI(bitmapUri); // Set image URI in Fresco
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Fallback image if no profile picture exists
                        Uri fallbackUri = Uri.parse("res:///" + R.drawable.usericon);
                        ivMyProfilePicture.setImageURI(fallbackUri);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {

        try {
            File cacheDir = getCacheDir();
            File tempFile = new File(cacheDir, "profile_picture.jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


}