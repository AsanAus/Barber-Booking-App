package com.example.barberbookingapp.UserManagementModule;




import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.barberbookingapp.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.text.TextUtils;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayOutputStream;
import java.util.Objects;


public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private EditText etUsername, etEmail, etPhoneNumber,etPassword;
    private ImageView ivUploadPicture;
    private Button btnSaveChanges;
    private Uri profileImageUri;  // URI for the selected image
    private String encodedImage; // Base64 encoded image string (class-level variable)

    public String username, email, password, phoneNumber, profilePicture;
    public static String role = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Back button
        TextView backToHome = findViewById(R.id.TVBacktoMyProfilePage);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, MyProfile.class);
            startActivity(intent);
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        etUsername = findViewById(R.id.ETUsername);
        etEmail = findViewById(R.id.ETEmail);
        etPhoneNumber = findViewById(R.id.ETphoneNumber);
        etPassword =findViewById(R.id.ETNewPassword);
        ivUploadPicture = findViewById(R.id.IVUploadPicture);
        btnSaveChanges = findViewById(R.id.BTNsaveChanges);

        // Fetch and display user data
        fetchUserProfile();


        // image upload button
        ivUploadPicture.setOnClickListener(v -> openImageChooser());

        // Save Changes button
        btnSaveChanges.setOnClickListener(v -> updateUserProfile());


    }

    private void fetchUserProfile() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Reference to the user's node in the database
        DatabaseReference userRef = mDatabase.getReference("Users").child(userId);

        // Listen for data at the user's node
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get UserProfile object from snapshot
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        // Populate the UI elements
                        etUsername.setText(userProfile.username);
                        etEmail.setText(userProfile.email);
                        etPhoneNumber.setText(userProfile.phoneNumber);


                        // If the profile picture exists, load it into the ImageView
                        if (userProfile.profilePicture != null) {
                            // Decode the Base64 string into a Bitmap
                            Bitmap profileBitmap = decodeBase64(userProfile.profilePicture);

                            // Apply Glide with circular cropping to display the image
                            Glide.with(EditProfile.this)
                                    .load(profileBitmap)
                                    .circleCrop()
                                    .placeholder(R.drawable.usericon) // Replace with your placeholder drawable
                                    .into(ivUploadPicture);
                        }

                    }
                } else {
                    Toast.makeText(EditProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            // Use Glide to load the selected image with circular cropping
            Glide.with(this)
                    .load(profileImageUri)
                    .circleCrop()
                    .placeholder(R.drawable.usericon) // Replace with your placeholder drawable
                    .into(ivUploadPicture);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImageUri);

                // Resize image to prevent large payloads
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                // Display in ImageView
                ivUploadPicture.setImageBitmap(resizedBitmap);

                // Encode resized image to Base64
                encodedImage = encodeToBase64(resizedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Update user profile in Firebase
    private void updateUserProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user ID from Firebase Authentication
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserProfile userProfile = new UserProfile(username, email, password, role, phoneNumber, encodedImage);

        // Check if an image was selected and encode it to Base64
        if (profileImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImageUri);
                encodedImage = encodeToBase64(bitmap); // Use class-level encodedImage
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error encoding image", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        // Update user profile in Firebase Realtime Database
        mDatabase.getReference("Users").child(userId).setValue(userProfile)
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
                                                // Optionally, update password if changed
                                                if (!password.isEmpty()) {
                                                    mAuth.getCurrentUser().updatePassword(password)
                                                            .addOnCompleteListener(task2 -> {
                                                                if (task2.isSuccessful()) {
                                                                    Toast.makeText(EditProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(EditProfile.this, MyProfile.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Toast.makeText(EditProfile.this, "Error updating password", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(EditProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(EditProfile.this, "Error updating username", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle any database errors
                                Toast.makeText(EditProfile.this, "Error updating username", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(EditProfile.this, "Error updating profile", Toast.LENGTH_SHORT).show();
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
    public static class UserProfile {

        public String username, email, password, role, phoneNumber, profilePicture;
        public UserProfile(String username, String email,String password,String role, String phoneNumber, String profilePicture) {
            this.username = username;
            this.email = email;
            this.password=password;
            this.role = role;
            this.phoneNumber = phoneNumber;
            this.profilePicture = profilePicture;;
        }
        public UserProfile(){

        }

    }
}
