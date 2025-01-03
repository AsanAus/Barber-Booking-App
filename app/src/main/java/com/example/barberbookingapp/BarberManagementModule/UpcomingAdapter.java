package com.example.barberbookingapp.BarberManagementModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    Context context;
    ArrayList<Upcoming> UpcomingBookingArrayList;

    public UpcomingAdapter(Context context, ArrayList<Upcoming> UpcomingBookingArrayList) {
        this.context = context;
        this.UpcomingBookingArrayList = UpcomingBookingArrayList;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.upcoming_card_view, parent, false);
        return new UpcomingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        Upcoming upcoming = UpcomingBookingArrayList.get(position);

        holder.Name.setText(upcoming.getName());
        holder.Date.setText(upcoming.getDate());
        holder.Time.setText(upcoming.getTime());
        holder.ServiceType.setText(upcoming.getServiceType());
        holder.Price.setText(upcoming.getPrice());
        holder.Contact.setText(upcoming.getContact());

        holder.Done.setOnClickListener(v -> {
            moveItemToCompleted(upcoming, position);
        });
    }

    private void moveItemToCompleted(Upcoming upcoming, int position) {
        // Reference to Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Get id of booking
        String id = (upcoming.UpcomingId);

        // Add the item to 'completed'
        databaseRef.child("completed").child(id).setValue(upcoming)
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from 'upcoming'
                    databaseRef.child("upcoming").child(id).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove from local list and notify adapter
                                UpcomingBookingArrayList.remove(position);
                                notifyItemRemoved(position);

                                // Display the key of the moved object
                                Toast.makeText(context, "Successfully moved Booking ID " + id + " to completed", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle removal error
                                Toast.makeText(context, "Failed to remove from upcoming", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle addition error
                    Toast.makeText(context, "Failed to move to completed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return UpcomingBookingArrayList.size();
    }

    public static class UpcomingViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Date;
        TextView Time;
        TextView ServiceType;
        TextView Price;
        TextView Contact;
        MaterialButton Done;

        public UpcomingViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.TVName);
            Date = itemView.findViewById(R.id.TVDate);
            Time = itemView.findViewById(R.id.TVTime);
            ServiceType = itemView.findViewById(R.id.TVServiceType);
            Price = itemView.findViewById(R.id.TVPrice);
            Contact = itemView.findViewById(R.id.TVContact);
            Done = itemView.findViewById(R.id.BTDone);
        }
    }
}
