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

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.PendingViewHolder> {

    Context context;
    ArrayList<Pending> PendingBookingArrayList;

    public PendingAdapter(Context context, ArrayList<Pending> PendingBookingArrayList) {
        this.context = context;
        this.PendingBookingArrayList = PendingBookingArrayList;
    }

    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.pending_card_view, parent, false);
        return new PendingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingViewHolder holder, int position) {
        Pending pending = PendingBookingArrayList.get(position);

        holder.Name.setText(pending.getName());
        holder.Date.setText(pending.getDate());
        holder.Time.setText(pending.getTime());
        holder.ServiceType.setText(pending.getServiceType());
        holder.Price.setText(pending.getPrice());
        holder.Contact.setText(pending.getContact());

        holder.Accept.setOnClickListener(v -> {
            moveItemToUpcoming(pending, position);
        });
        holder.Decline.setOnClickListener(v -> {
            removeItem(pending, position);
        });
    }


    private void removeItem(Pending pending, int position) {
        // Reference to Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Get id of booking
        String id = pending.getPendingId();

        // Remove the item from 'pending'
        databaseRef.child(id).child("status").setValue("cancelled")
                .addOnSuccessListener(aVoid1 -> {
                    // Remove from local list and notify adapter
                    PendingBookingArrayList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    // Handle removal error
                    Toast.makeText(context, "Failed to remove from upcoming", Toast.LENGTH_SHORT).show();
                });
    }

    private void moveItemToUpcoming(Pending pending, int position) {
        // Reference to Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Get id of booking
      //  String id = pending.pendingId;
        String id = pending.getPendingId();

        // Update the 'status' field to "upcoming"
        databaseRef.child(id).child("status").setValue("upcoming")
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from local list and notify adapter
                    PendingBookingArrayList.remove(position);
                    notifyItemRemoved(position);

                    // Notify the user of success
                    Toast.makeText(context, "Successfully updated status to 'upcoming' for Booking ID " + id, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle update error
                    Toast.makeText(context, "Failed to update status to 'upcoming'", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return PendingBookingArrayList.size();
    }

    public static class PendingViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Date;
        TextView Time;
        TextView ServiceType;
        TextView Price;
        TextView Contact;
        MaterialButton Accept;
        MaterialButton Decline;

        public PendingViewHolder(@NonNull View itemView){
            super (itemView);
            Name = itemView.findViewById(R.id.TVName);
            Date = itemView.findViewById(R.id.TVDate);
            Time = itemView.findViewById(R.id.TVTime);
            ServiceType = itemView.findViewById(R.id.TVServiceType);
            Price = itemView.findViewById(R.id.TVPrice);
            Contact = itemView.findViewById(R.id.TVContact);
            Accept = itemView.findViewById(R.id.BTAccept);
            Decline = itemView.findViewById(R.id.BTDecline);
        }
    }
}
