package com.example.barberbookingapp.UserManagementModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barberbookingapp.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.text.TextUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;


public class editprofile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private EditText etUsername, etEmail, etPhoneNumber, etPassword;
    private ImageView ivUploadPicture;
    private Button btnSaveChanges;
    private Uri profileImageUri;  // URI for the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);


        // Back button functionality
        TextView backToHome = findViewById(R.id.TVBacktoMyProfilePage);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(editprofile.this, myprofile.class);
            startActivity(intent);
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        etUsername = findViewById(R.id.ETUsername);
        etEmail = findViewById(R.id.ETEmail);
        etPhoneNumber = findViewById(R.id.ETphoneNumber);
        etPassword = findViewById(R.id.ETuserPassword);
        ivUploadPicture = findViewById(R.id.IVUploadPicture);
        btnSaveChanges = findViewById(R.id.BTNsaveChanges);

        // Click listener for the image upload button
        ivUploadPicture.setOnClickListener(v -> openImageChooser());

        // Click listener for the Save Changes button
        btnSaveChanges.setOnClickListener(v -> updateUserProfile());

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
            ivUploadPicture.setImageURI(profileImageUri);
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
        // Upload image to Firebase Storage
        if (profileImageUri != null) {
            final StorageReference profileImageRef = mStorageRef.child("profile_pictures/" + UUID.randomUUID().toString());
            //StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + userId + ".jpg");

            profileImageRef.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profileImageUrl = uri.toString();

                            // Get the current user ID from Firebase Authentication
                            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                            // Update the user's profile information in Firebase Realtime Database
                            UserProfile userProfile = new UserProfile(username, email, phoneNumber, profileImageUrl);

                            // Updating the user details in the Realtime Database
                            mDatabase.getReference("users").child(userId).setValue(userProfile)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Update password if changed
                                            if (!password.isEmpty()) {
                                                mAuth.getCurrentUser().updatePassword(password)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(editprofile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(editprofile.this, "Error updating password", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(editprofile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(editprofile.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UploadError", "Error uploading image: " + e.getMessage(), e);
                        Toast.makeText(this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // User profile model
    public static class UserProfile {
        public String username;
        public String email;
        public String phoneNumber;
        public String profilePicture;

        public UserProfile(String username, String email, String phoneNumber, String profilePicture) {
            this.username = username;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.profilePicture = profilePicture;


        }

    }
}
