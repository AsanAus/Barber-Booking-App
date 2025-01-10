package com.example.barberbookingapp.BarberManagementModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.BarberListViewModule.BarberDetails;
import com.example.barberbookingapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class BarberGallery extends AppCompatActivity {
    private Uri selectedImageUri;
    private String encodedImage;
    private ImageView ivUploadGallery,IVgallery;
    Button btnUploadGallery;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private LinearLayout galleryContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button Back = findViewById(R.id.BTBack);
        Back.setOnClickListener(v -> {
            startActivity(new Intent(this, BarberProfile.class));
        });

        IVgallery = findViewById(R.id.IVgallery);
        ivUploadGallery =  findViewById(R.id.IVuploadGallery);
        btnUploadGallery = findViewById(R.id.BTNuploadGallery);

        // Replace "currentBarberId" with the actual ID of the logged-in barber
        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID

        databaseReference = FirebaseDatabase.getInstance().getReference("Gallery");

        ivUploadGallery.setOnClickListener(v -> openImageChooser());

        btnUploadGallery.setOnClickListener(v -> {
            if(encodedImage != null){
                uploadImageToFirebase(currentBarberId );
            }else{
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });

        galleryContainer = findViewById(R.id.galleryContainer);
        // Fetch images from Firebase
        fetchImagesFromDatabase(currentBarberId);


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
                    Toast.makeText(BarberGallery.this, "No images found for this barber", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarberGallery.this, "Failed to load images", Toast.LENGTH_SHORT).show();
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

    private void uploadImageToFirebase(String currentBarberId) {
        HashMap<String, String> imageData = new HashMap<>();
        imageData.put("image",encodedImage);
        imageData.put("BarberId",currentBarberId);

        databaseReference.push().setValue(imageData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BarberGallery.this, BarberProfile.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            IVgallery.setImageURI(selectedImageUri);
            ivUploadGallery.setVisibility(View.INVISIBLE);

            // Encode the image to Base64
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                encodedImage = encodeToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

    private String getCurrentBarberId() {
        String currentBarberId = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentBarberId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        Log.d("BarberID", "Current Barber ID: " + currentBarberId); // Log the barber ID
        return currentBarberId;
    }



}


