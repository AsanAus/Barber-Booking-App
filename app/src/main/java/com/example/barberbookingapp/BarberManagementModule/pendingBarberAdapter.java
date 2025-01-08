package com.example.barberbookingapp.BarberManagementModule;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class pendingBarberAdapter extends RecyclerView.Adapter<PendingBookingHolder> {

    Context context;
    ArrayList<PendingBookingModel> PendingBookingModelArrayList = new ArrayList<>();

    public pendingBarberAdapter(Context context, ArrayList<PendingBookingModel> pendingBookingModelArrayList) {
        this.context = context;
        PendingBookingModelArrayList = pendingBookingModelArrayList;
    }

    @NonNull
    @Override
    public PendingBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PendingBookingHolder(LayoutInflater.from(context).inflate(R.layout.pending_card_view,parent,false));


    }

    @Override
    public void onBindViewHolder(@NonNull PendingBookingHolder holder, int position) {
        PendingBookingModel model = PendingBookingModelArrayList.get(position);

        // Populate views with data
        holder.TVName.setText(model.getUserName());
        holder.TVDate.setText(model.getBookingDate());
        holder.TVTime.setText(model.getBookingTime());
        holder.TVContact.setText(model.getUserContact());
        holder.TVServiceName.setText(model.getBookingServiceName());
        holder.TVPrice.setText(model.getBookingPrice());

        Log.d("BindViewHolder", "Binding data for position " + position);

        // Handle BTAccept
        holder.BTAccept.setOnClickListener(v -> {
            if (model != null && model.getAppointmentID() != null) {
                acceptBookingStatus(model.getAppointmentID()); // Update Firebase status
                Intent intent = new Intent(context, upcomingBarber.class);
                intent.putExtra("bookingId", model.getAppointmentID()); // Pass booking ID to next activity
                context.startActivity(intent);
            } else {
                Log.e("BTAccept Error", "Model or AppointmentID is null for position: " + position);
                Toast.makeText(context, "Unable to accept booking, data is missing.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle BTDecline
        holder.BTDecline.setOnClickListener(v -> {
            if (model != null && model.getAppointmentID() != null) {
                declineBookingStatus(model.getAppointmentID()); // Update Firebase status
                PendingBookingModelArrayList.remove(position); // Remove from list
                notifyItemRemoved(position); // Update RecyclerView
            } else {
                Log.e("BTDecline Error", "Model or AppointmentID is null for position: " + position);
                Toast.makeText(context, "Unable to decline booking, data is missing.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void acceptBookingStatus(String bookingId) {
        if (bookingId == null) {
            Log.e("acceptBookingStatus", "Booking ID is null");
            return;
        }

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.child(bookingId).child("status").setValue("upcomingAccepted")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Booking accepted", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Firebase Error", "Failed to update booking status to 'upcoming'");
                        Toast.makeText(context, "Failed to accept booking", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void declineBookingStatus(String bookingId) {
        if (bookingId == null) {
            Log.e("declineBookingStatus", "Booking ID is null");
            return;
        }

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.child(bookingId).child("status").setValue("decline")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Booking declined", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Firebase Error", "Failed to update booking status to 'decline'");
                        Toast.makeText(context, "Failed to decline booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return PendingBookingModelArrayList.size();
    }
}

class PendingBookingHolder extends RecyclerView.ViewHolder{

    TextView TVName,TVDate, TVTime, TVContact, TVServiceName,TVPrice;
    Button BTAccept,BTDecline;

    public PendingBookingHolder(@NonNull View itemView) {
        super(itemView);

        TVName = itemView.findViewById(R.id.TVName);
        TVDate = itemView.findViewById(R.id.TVDate);
        TVTime = itemView.findViewById(R.id.TVTime);
        TVContact = itemView.findViewById(R.id.TVContact);
        TVServiceName = itemView.findViewById(R.id.TVServiceName);
        TVPrice = itemView.findViewById(R.id.TVPrice);
        BTAccept = itemView.findViewById(R.id.BTAccept);
        BTDecline =itemView.findViewById(R.id.BTDecline);
    }
}

class PendingBookingModel{

    String userName, bookingDate, bookingTime, userContact, bookingServiceName, bookingPrice,appointmentID;

    public PendingBookingModel(String userName, String bookingDate, String bookingTime, String userContact, String bookingServiceName, String bookingPrice, String appointmentID) {
        this.userName = userName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.userContact = userContact;
        this.bookingServiceName = bookingServiceName;
        this.bookingPrice = bookingPrice;
        this.appointmentID = appointmentID;
    }

    public String getUserName() {
        return userName;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getBookingServiceName() {
        return bookingServiceName;
    }

    public void setBookingServiceName(String bookingServiceName) {
        this.bookingServiceName = bookingServiceName;
    }

    public String getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(String bookingPrice) {
        this.bookingPrice = bookingPrice;
    }
}
