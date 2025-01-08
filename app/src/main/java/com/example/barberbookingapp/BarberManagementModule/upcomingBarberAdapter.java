package com.example.barberbookingapp.BarberManagementModule;

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

public class upcomingBarberAdapter extends RecyclerView.Adapter<UpcomingBarberHolder> {

    Context context;
    ArrayList<UpcomingBarberModel> UpcomingBookingModelArrayList = new ArrayList<>();

    public upcomingBarberAdapter(Context context, ArrayList<UpcomingBarberModel> UpcomingBookingModelArrayList) {
        this.context = context;
        this.UpcomingBookingModelArrayList = UpcomingBookingModelArrayList;
    }

    @NonNull
    @Override
    public UpcomingBarberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UpcomingBarberHolder(LayoutInflater.from(context).inflate(R.layout.upcoming_card_view,parent,false));


    }


    @Override
    public void onBindViewHolder(@NonNull UpcomingBarberHolder holder, int position) {
      UpcomingBarberModel model = UpcomingBookingModelArrayList.get(position);


        // Debugging: Check if the holder views are null
        if (holder.TVName == null) {
            Log.e("UpcomingBarberAdapter", "TVName is null");
        }
        if (holder.TVDate == null) {
            Log.e("UpcomingBarberAdapter", "TVDate is null");
        }
        if (holder.TVTime == null) {
            Log.e("UpcomingBarberAdapter", "TVTime is null");
        }
        if (holder.TVContact == null) {
            Log.e("UpcomingBarberAdapter", "TVContact is null");
        }
        if (holder.TVServiceType == null) {
            Log.e("UpcomingBarberAdapter", "TVServiceName is null");
        }
        if (holder.TVPrice == null) {
            Log.e("UpcomingBarberAdapter", "TVPrice is null");
        }


        // Populate views with data
        holder.TVName.setText(model.getUserName());
        holder.TVDate.setText(model.getBookingDate());
        holder.TVTime.setText(model.getBookingTime());
        holder.TVContact.setText(model.getUserContact());
        holder.TVServiceType.setText(model.getBookingServiceName());
        holder.TVPrice.setText(model.getBookingPrice());

        holder.BTDone.setOnClickListener(v -> {
            Log.d("UpcomingBarberAdapter", "Done button clicked for appointment ID: " + model.getAppointmentID());
            if (model != null && model.getAppointmentID() != null) {
                CompletedBookingStatus(model.getAppointmentID()); // Update Firebase status
                Intent intent = new Intent(context, completedBarber.class);
                intent.putExtra("bookingId", model.getAppointmentID()); // Pass booking ID to next activity
                context.startActivity(intent);
            } else {
                Log.e("BTAccept Error", "Model or AppointmentID is null for position: " + position);
                Toast.makeText(context, "Unable to accept booking, data is missing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CompletedBookingStatus(String appointmentID) {
        if (appointmentID == null) {
            Log.e("completedBookingStatus", "Booking ID is null");
            return;
        }

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.child(appointmentID).child("status").setValue("completed")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Booking completed", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Firebase Error", "Failed to update booking status to 'completed'");
                        Toast.makeText(context, "Failed to complete booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return UpcomingBookingModelArrayList .size();
    }
}

class UpcomingBarberHolder extends RecyclerView.ViewHolder{
    TextView TVName,TVDate, TVTime, TVContact, TVServiceType,TVPrice;
    Button BTDone;

    public UpcomingBarberHolder(@NonNull View itemView) {
        super(itemView);
        TVName = itemView.findViewById(R.id.TVName);
        TVDate = itemView.findViewById(R.id.TVDate);
        TVTime = itemView.findViewById(R.id.TVTime);
        TVContact = itemView.findViewById(R.id.TVContact);
        TVServiceType = itemView.findViewById(R.id.TVServiceType);
        TVPrice = itemView.findViewById(R.id.TVPrice);
        BTDone = itemView.findViewById(R.id.BTDone);
    }


}

class UpcomingBarberModel{
    String userName, bookingDate, bookingTime, userContact, bookingServiceName, bookingPrice,appointmentID;

    public UpcomingBarberModel(String userName, String bookingDate, String bookingTime, String userContact, String bookingServiceName, String bookingPrice, String appointmentID) {
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

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }
}
