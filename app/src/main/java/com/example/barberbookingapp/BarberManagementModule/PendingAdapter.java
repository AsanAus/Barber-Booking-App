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
        String id = pending.pendingId;

        // Remove the item from 'pending'
        databaseRef.child("pending").child(id).removeValue()
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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Get id of booking
        String id = pending.pendingId;

        // Add the item to 'upcoming' with the same key
        databaseRef.child("upcoming").child(id).setValue(pending)
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from 'pending'
                    databaseRef.child("pending").child(id).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove from local list and notify adapter
                                PendingBookingArrayList.remove(position);
                                notifyItemRemoved(position);

                                // Display the key of the moved object
                                Toast.makeText(context, "Successfully moved Booking ID " + id + " to upcoming", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle removal error
                                Toast.makeText(context, "Failed to remove from pending", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle addition error
                    Toast.makeText(context, "Failed to move to upcoming", Toast.LENGTH_SHORT).show();
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
