package com.example.barberbookingapp.UserManagementModule;




import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barberbookingapp.GeneralModule.Login;
import com.example.barberbookingapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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



import java.util.Objects;
import java.util.UUID;


public class editprofile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private EditText etUsername, etEmail, etPhoneNumber,etCurrentPassword, etNewPassword;
    private ImageView ivUploadPicture;
    private Button btnSaveChanges;
    private Uri profileImageUri;  // URI for the selected image
    private String encodedImage; // Base64 encoded image string (class-level variable)

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
       // mStorage = FirebaseStorage.getInstance();
      //  mStorageRef = mStorage.getReference();

        etUsername = findViewById(R.id.ETUsername);
        etEmail = findViewById(R.id.ETEmail);
        etPhoneNumber = findViewById(R.id.ETphoneNumber);
        etCurrentPassword = findViewById(R.id.ETCurrentPassword); // Current password field
        etNewPassword = findViewById(R.id.ETNewPassword); // New password field
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

            // Convert the selected image to Base64
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImageUri);
                encodedImage = encodeToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }

        }
    }


    // Update user profile in Firebase
    // Update user profile in Firebase
    private void updateUserProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user ID from Firebase Authentication
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user with the current password
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If re-authentication is successful, update the email (username) and password
                currentUser.updateEmail(email).addOnCompleteListener(emailUpdateTask -> {
                    if (emailUpdateTask.isSuccessful()) {
                        // If email update is successful, update the password
                        currentUser.updatePassword(newPassword).addOnCompleteListener(passwordUpdateTask -> {
                            if (passwordUpdateTask.isSuccessful()) {
                                Toast.makeText(editprofile.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

                                // Now update the user profile in the database
                                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Retrieve old username from the user's profile
                                            String oldUsername = snapshot.child("username").getValue(String.class);

                                            // Now update the user profile with the new details
                                            if (profileImageUri != null) {
                                                try {
                                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImageUri);
                                                    encodedImage = encodeToBase64(bitmap); // Use class-level encodedImage
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(editprofile.this, "Error encoding image", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }else {
                                                encodedImage = null; // If no image was selected, set encodedImage to null
                                            }

                                            // Create the updated UserProfile object
                                            UserProfile userProfile = new UserProfile(username, email, newPassword, "user", phoneNumber, encodedImage);

                                            // Update user profile in Firebase Realtime Database
                                            usersReference.setValue(userProfile).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    // Optionally, log out after updating the profile
                                                    mAuth.signOut();
                                                    Intent intent = new Intent(editprofile.this, Login.class);
                                                    startActivity(intent);
                                                    finish(); // Close the current activity
                                                } else {
                                                    Toast.makeText(editprofile.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(editprofile.this, "Error fetching old username", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(editprofile.this, "Error updating password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(editprofile.this, "Error updating email", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(editprofile.this, "Re-authentication failed. Please check your current password.", Toast.LENGTH_SHORT).show();
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

    // User profile model
    public static class UserProfile {
        public String username;
        public String email;
        public String password;
        public String role;
        public String phoneNumber;
        public String profilePicture; // Base64 encoded image string

        public UserProfile(String username, String email,String password,String role, String phoneNumber, String profilePicture) {
            this.username = username;
            this.email = email;
            this.password=password;
            this.role = role;
            this.phoneNumber = phoneNumber;
            this.profilePicture = profilePicture;;


        }

    }
}
