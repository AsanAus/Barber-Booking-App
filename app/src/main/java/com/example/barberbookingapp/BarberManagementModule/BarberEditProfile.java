package com.example.barberbookingapp.BarberManagementModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.EditProfile;
import com.example.barberbookingapp.UserManagementModule.MyProfile;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BarberEditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private EditText etUsername, etEmail, etPhoneNumber,etLocation, etDesc;
    private ImageView ivProfile;
    private Button btnSaveChanges;
    private Uri profileImageUri;  // URI for the selected image
    private String encodedImage; // Base64 encoded image string (class-level variable)

    public String username, email, phone, location, description, profilePicture;
    public static String role = "barber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barber_edit_profile);

        Button Back = findViewById(R.id.BTBack);
        Back.setOnClickListener(view -> {
            startActivity(new Intent(this, BarberProfile.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        etUsername = findViewById(R.id.ETUsername);
        etEmail = findViewById(R.id.ETEmail);
        etPhoneNumber = findViewById(R.id.ETPhone);
        etLocation = findViewById(R.id.ETLocation);
        etDesc = findViewById(R.id.ETDescription);

        btnSaveChanges = findViewById(R.id.BTNsaveChanges);
        ivProfile = findViewById(R.id.IVProfile);

        // Fetch and display Barber data
        fetchBarberProfile();

        // image upload button
        ivProfile.setOnClickListener(v -> openImageChooser());

        // Save Changes button
        btnSaveChanges.setOnClickListener(v -> updateBarberProfile());

    }

    private void fetchBarberProfile() {
        String barberId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Reference to the user's node in the database
        DatabaseReference userRef = mDatabase.getReference("Barbers").child(barberId);

        // Listen for data at the user's node
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get BarberProfile object from snapshot
                    BarberProfile barberProfile = snapshot.getValue(BarberProfile.class);

                    if (barberProfile != null) {
                        // Populate the UI elements
                        etUsername.setText(barberProfile.username);
                        etEmail.setText(barberProfile.email);
                        etPhoneNumber.setText(barberProfile.phone);
                        etLocation.setText(barberProfile.location);
                        etDesc.setText(barberProfile.description);


                        // If the profile picture exists, load it into the ImageView
                        if (barberProfile.profileImage != null) {
                            // Decode the Base64 string into a Bitmap
                            Bitmap profileBitmap = decodeBase64(barberProfile.profileImage);
                            ivProfile.setImageBitmap(profileBitmap);
                            Toast.makeText(BarberEditProfile.this, "Found profile picture", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(BarberEditProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BarberEditProfile.this, "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open image chooser for selecting a profile picture
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImageUri);

                // Resize image to prevent large payloads
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                // Display in ImageView
                ivProfile.setImageBitmap(resizedBitmap);

                // Encode resized image to Base64
                encodedImage = encodeToBase64(resizedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Update user profile in Firebase
    private void updateBarberProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDesc.getText().toString().trim();


        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(location) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current Barber ID from Firebase Authentication
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Prepare the updated values as a Map
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("email", email);
        updates.put("phone", phoneNumber);
        updates.put("location", location);
        updates.put("description", description);

        // Check if an image was selected and encode it to Base64
        if (profileImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImageUri);
                encodedImage = encodeToBase64(bitmap); // Use class-level encodedImage
                updates.put("profileImage", encodedImage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error encoding image", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        // Update user profile in Firebase Realtime Database
        mDatabase.getReference("Barbers").child(userId).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successfully updating the user profile, update the username in the Usernames node
                        DatabaseReference usernameReference = FirebaseDatabase.getInstance().getReference("Usernames");

                        // Remove the old username from the Usernames node (if it exists)
                        usernameReference.orderByValue().equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot usernameSnapshot : snapshot.getChildren()) {
                                    usernameSnapshot.getRef().removeValue();  // Remove the old username
                                }

                                // Add the new username to the Usernames node
                                usernameReference.child(username).setValue(email)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(BarberEditProfile.this, "Barber Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(BarberEditProfile.this, BarberProfile.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(BarberEditProfile.this, "Error updating username", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle any database errors
                                Toast.makeText(BarberEditProfile.this, "Error updating username", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(BarberEditProfile.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Encode image to Base64
    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // User profile model
    public static class BarberProfile {

        public String username, email, phone, location, description, role, profileImage;
        public Double rating;
        public BarberProfile(String username, String email, String phone, String location, String description, String role, String profileImage, double rating) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.description = description;
            this.role = role;
            this.rating = rating;
            this.profileImage = profileImage;
        }
        public BarberProfile(String username, String email, String phone, String location, String description, String role, String profileImage) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.description = description;
            this.role = role;
            this.profileImage = profileImage;
        }

        public BarberProfile(){

        }

    }
}