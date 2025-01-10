package com.example.barberbookingapp.BarberListViewModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BarberDetails extends AppCompatActivity {

    private TextView barberListTV;
    private ImageView barberImageView;
    private TextView detailsTextView;
    private TextView locationTextView;
    private TextView phoneNumberTextView;

    private LinearLayout galleryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_details);


        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarberDetails.this, BarberListView.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        String barberId = getIntent().getStringExtra("barberId");
        if (barberId != null) {
            // Fetch barber details using the barberId
            fetchBarberDetails(barberId);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        galleryContainer = findViewById(R.id.galleryContainer);
        // Fetch images from Firebase
        fetchImagesFromDatabase(barberId);


    }

    private void fetchImagesFromDatabase(String barberId) {
        if (barberId == null || barberId.isEmpty()) {
            Log.e("fetchImagesFromDatabase", "barberId is null or empty");
            Toast.makeText(this, "Error: Barber ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Gallery");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean imageFound = false;

                for (DataSnapshot gallerySnapshot : snapshot.getChildren()) {
                    // Retrieve the BarberId for the current gallery entry
                    String galleryBarberId = gallerySnapshot.child("BarberId").getValue(String.class);

                    if (barberId.equals(galleryBarberId)) {
                        // Retrieve the image for the matching BarberId
                        String base64Image = gallerySnapshot.child("image").getValue(String.class);
                        if (base64Image != null) {
                            Log.d("GalleryImage", "Base64 Image: " + base64Image);
                            addImageToGallery(base64Image);
                            imageFound = true;
                        } else {
                            Log.w("GalleryImage", "Image is null for key: " + gallerySnapshot.getKey());
                        }
                    }
                }

                if (!imageFound) {
                    Toast.makeText(BarberDetails.this, "No images found for this barber", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarberDetails.this, "Failed to load images", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });

    }
    private void addImageToGallery(String base64Image) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.gallery_card, galleryContainer, false);

        // Decode Base64 to Bitmap
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // Set the image in the ImageView
        ImageView imageView = cardView.findViewById(R.id.galleryImageView);
        imageView.setImageBitmap(decodedBitmap);

        // Add the CardView to the gallery container
        galleryContainer.addView(cardView);
    }

    private Bitmap decodeBase64ToBitmap(String profileImageBase64) {
        byte[] decodedBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }

    private void fetchBarberDetails(String barberId) {
        // Replace this with your database fetching logic
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("Barbers").child(barberId);
        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Barber barber = snapshot.getValue(Barber.class);
                if (barber != null) {
                    displayBarberDetails(barber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarberDetails", "Error fetching barber details: " + error.getMessage());
            }
        });
    }

    private void displayBarberDetails(Barber barber) {
        barberListTV = findViewById(R.id.BarberListTV);
        barberImageView = findViewById(R.id.imageView2);
        detailsTextView = findViewById(R.id.details);
        locationTextView = findViewById(R.id.location);
        phoneNumberTextView = findViewById(R.id.phone);


        barberListTV.setText(barber.getUsername());
        detailsTextView.setText(barber.getDescription());
        locationTextView.setText(barber.getLocation());
        phoneNumberTextView.setText(barber.getPhone());

        String profileImageBase64 = barber.getProfileImage();
        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
            barberImageView.setImageBitmap(bitmap);
        } else {
            barberImageView.setImageResource(R.drawable.usericon);
        }
    }
}