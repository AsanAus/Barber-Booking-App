package com.example.barberbookingapp.BarberListViewModule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.BarberViewHolder> {

    private ArrayList<Barber> barberList;
    private Context context;

    public BarberAdapter(Context context, ArrayList<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.barber_item, parent, false);
        return new BarberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        Barber barber = barberList.get(position);

        holder.barberNameTextView.setText(barber.getUsername());
        holder.barberLocationTextView.setText(barber.getLocation());
        holder.barberRatingBar.setRating(barber.getRating());
        // Load image if necessary

        holder.barberSelectButton.setOnClickListener(v -> {
            // Handle button click
            Toast.makeText(context, "Selected " + barber.getUsername(), Toast.LENGTH_SHORT).show();

            // Create an Intent to navigate to BarberDetails activity
            Intent intent = new Intent(context, BarberDetails.class);

            // Pass the entire Barber object to the BarberDetails activity
            intent.putExtra("selectedBarber", barber); // Pass the Barber object
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public static class BarberViewHolder extends RecyclerView.ViewHolder {
        ImageView barberImageView;
        TextView barberNameTextView, barberLocationTextView;
        RatingBar barberRatingBar;
        Button barberSelectButton;

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);

            barberImageView = itemView.findViewById(R.id.barberImageView);
            barberNameTextView = itemView.findViewById(R.id.barberNameTextView);
            barberLocationTextView = itemView.findViewById(R.id.barberLocationTextView);
            barberRatingBar = itemView.findViewById(R.id.barberRatingBar);
            barberSelectButton = itemView.findViewById(R.id.barberSelectButton);
        }
    }
}
