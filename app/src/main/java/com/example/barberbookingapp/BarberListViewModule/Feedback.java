package com.example.barberbookingapp.BarberListViewModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.example.barberbookingapp.UserManagementModule.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Feedback extends AppCompatActivity {

    private TextView barbername;
    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<FeedbackModel> feedbackList = new ArrayList<>();
    String barberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);

        // Initialize barberID inside onCreate
        Intent intent = getIntent();
        if (intent != null) {
            barberID = intent.getStringExtra("barberID");
            if (barberID == null) {
                Log.e("FeedbackActivity", "BarberID is null!");
                Toast.makeText(this, "Failed to retrieve Barber ID", Toast.LENGTH_SHORT).show();
                finish(); // Exit activity if barberID is invalid
                return;
            }
            Log.d("FeedbackActivity", "BarberID: " + barberID);
        } else {
            Log.e("FeedbackActivity", "Intent is null!");
            Toast.makeText(this, "Intent not found!", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if Intent is invalid
            return;
        }

        TextView btnBack = findViewById(R.id.TVbacktoHomePage);
        btnBack.setOnClickListener(v -> finish()); // Close this activity and go back to the previous one

        Button btnWriteReview = findViewById(R.id.BtnWriteReview);
        btnWriteReview.setOnClickListener(v -> {
            Intent writeReviewIntent = new Intent(Feedback.this, WriteReview.class);
            writeReviewIntent.putExtra("barberID", barberID);
            startActivity(writeReviewIntent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        recyclerView.setAdapter(feedbackAdapter);
        barbername = findViewById(R.id.textView6);

        // Fetch and set barber's username
        fetchBarberName();
        // Fetch feedback
        loadFeedback();
    }

    private void fetchBarberName() {
        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("Barbers").child(barberID);
        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    if (username != null) {
                        barbername.setText(username);
                        Log.d("FeedbackActivity", "Barber username: " + username);
                    } else {
                        Log.e("FeedbackActivity", "Username not found for Barber ID: " + barberID);
                        barbername.setText("Unknown Barber");
                    }
                } else {
                    Log.e("FeedbackActivity", "Barber not found with ID: " + barberID);
                    barbername.setText("Unknown Barber");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FeedbackActivity", "Error fetching barber: " + error.getMessage());
                barbername.setText("Error loading Barber");
            }
        });
    }

    private void loadFeedback() {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks").child(barberID);
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                    FeedbackModel feedback = feedbackSnapshot.getValue(FeedbackModel.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("FeedbackActivity", "Error fetching feedback: " + error.getMessage());
            }
        });
    }
}
