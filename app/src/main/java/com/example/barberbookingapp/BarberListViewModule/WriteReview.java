package com.example.barberbookingapp.BarberListViewModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.barberbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WriteReview extends AppCompatActivity {

    private EditText feedbackInput;
    private RatingBar ratingBar;
    private Button publishFeedbackButton;

    private DatabaseReference feedbackRef;
    private FirebaseAuth auth;

    private String currentUserId;
    private String currentBarberId; // Barber ID to whom the feedback belongs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_write_review);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back to Feedback Activity
        TextView backToHome = findViewById(R.id.TVbacktoHomePage);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(WriteReview.this, Feedback.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity
        });

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");

        // Fetch current user ID
        currentUserId = auth.getCurrentUser().getUid();

        // Fetch barber ID passed from the previous activity
        currentBarberId = getIntent().getStringExtra("barberID");
        if (TextUtils.isEmpty(currentBarberId)) {
            Toast.makeText(this, "Error: Barber ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        feedbackInput = findViewById(R.id.feedback_input);
        ratingBar = findViewById(R.id.rating_bar);
        publishFeedbackButton = findViewById(R.id.BtnPublishFeedback);

        // Set button click listener
        publishFeedbackButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String comment = feedbackInput.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique feedback ID
        String feedbackId = feedbackRef.child(currentBarberId).push().getKey();
        if (feedbackId == null) {
            Toast.makeText(this, "Error: Could not generate feedback ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a feedback object
        Map<String, Object> feedbackData = new HashMap<>();
//        feedbackData.put("feedbackID", feedbackId);
        feedbackData.put("userID", currentUserId);
        feedbackData.put("barberID", currentBarberId);
        feedbackData.put("rating", rating);
        feedbackData.put("comment", comment);

        // Write feedback to database under the barber's node
        feedbackRef.child(currentBarberId).child(feedbackId).setValue(feedbackData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WriteReview.this, "Feedback published successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(WriteReview.this, "Failed to publish feedback", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("WriteReview", "Error writing feedback: ", e);
                    Toast.makeText(WriteReview.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
