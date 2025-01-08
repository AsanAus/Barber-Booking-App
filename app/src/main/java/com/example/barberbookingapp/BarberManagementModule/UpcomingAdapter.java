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

        holder.Name.setText(upcoming.getName()); // Assuming barberID is mapped to barber's name
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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Get id of booking using the getter method
        String id = upcoming.getUpcomingId();  

        // Update the 'status' field to 'completed'
        databaseRef.child(id).child("status").setValue("completed")
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from the local list and notify the adapter
                    UpcomingBookingArrayList.remove(position);
                    notifyItemRemoved(position);

                    // Show success message
                    Toast.makeText(context, "Booking ID " + id + " status updated to completed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle update error
                    Toast.makeText(context, "Failed to update status to completed", Toast.LENGTH_SHORT).show();
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
