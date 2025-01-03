package com.example.barberbookingapp.BarberManagementModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    Context context;
    ArrayList<Service> ServicesList;

    public ServiceAdapter(Context context, ArrayList<Service> ServicesList) {
        this.context = context;
        this.ServicesList = ServicesList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.my_services_card_view, parent, false);
        return new ServiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = ServicesList.get(position);

        holder.serviceName.setText(service.getServiceName());
        holder.duration.setText(service.getDuration());
        holder.price.setText(service.getPrice());

        holder.delete.setOnClickListener(v -> {
            deleteService(service, position);
        });
    }

    private void deleteService(Service service, int position) {
        // Reference to Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Barbers") // Adjust the path to your database structure
                .child("barber1") // Use the barber's ID from FirebaseAuth
                .child("service")
                .child(service.getServiceId()); // Use the service ID to target the correct item;

        // Remove service from local ArrayList
        ServicesList.remove(position);
        notifyItemRemoved(position);

        // Remove from Firebase
        databaseRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Toast.makeText(context, "Service deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to delete service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return ServicesList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView duration;
        TextView price;
        ImageButton delete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.TVServiceType);
            duration = itemView.findViewById(R.id.TVDuration);
            price = itemView.findViewById(R.id.TVPrice);
            delete = itemView.findViewById(R.id.IBDelete);
        }
    }
}
