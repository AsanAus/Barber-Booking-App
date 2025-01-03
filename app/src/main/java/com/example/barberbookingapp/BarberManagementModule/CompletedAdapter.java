package com.example.barberbookingapp.BarberManagementModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.CompletedViewHolder> {

    Context context;
    ArrayList<Completed> CompletedBookingArrayList;

    public CompletedAdapter(Context context, ArrayList<Completed> CompletedBookingArrayList) {
        this.context = context;
        this.CompletedBookingArrayList = CompletedBookingArrayList;
    }

    @NonNull
    @Override
    public CompletedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.completed_card_view, parent, false);
        return new CompletedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedViewHolder holder, int position) {
        Completed completed = CompletedBookingArrayList.get(position);

        holder.Name.setText(completed.getName());
        holder.Date.setText(completed.getDate());
        holder.Time.setText(completed.getTime());
        holder.ServiceType.setText(completed.getServiceType());
        holder.Price.setText(completed.getPrice());
        holder.Contact.setText(completed.getContact());
    }

    @Override
    public int getItemCount() {
        return CompletedBookingArrayList.size();
    }

    public static class CompletedViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Date;
        TextView Time;
        TextView ServiceType;
        TextView Price;
        TextView Contact;

        public CompletedViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.TVName);
            Date = itemView.findViewById(R.id.TVDate);
            Time = itemView.findViewById(R.id.TVTime);
            ServiceType = itemView.findViewById(R.id.TVServiceType);
            Price = itemView.findViewById(R.id.TVPrice);
            Contact = itemView.findViewById(R.id.TVContact);
        }
    }
}