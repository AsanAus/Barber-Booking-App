package com.example.barberbookingapp.UserManagementModule;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class upcomingBookingAdapter extends RecyclerView.Adapter<UpcomingBookingHolder> {
    Context context;
    ArrayList<UpcomingBookingModel> UpcomingBookingModelArrayList = new ArrayList<>();
    private upcomingBookingAdapter adapter;

    public upcomingBookingAdapter(Context context, ArrayList<UpcomingBookingModel> upcomingBookingModelArrayList) {
        this.context = context;
        UpcomingBookingModelArrayList = upcomingBookingModelArrayList;
    }

    @NonNull
    @Override
    public UpcomingBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UpcomingBookingHolder(LayoutInflater.from(context).inflate(R.layout.upcoming_booking_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingBookingHolder holder, int position) {
        UpcomingBookingModel model = UpcomingBookingModelArrayList.get(position);

        holder.IVbarberProfilePicture.setImageResource(UpcomingBookingModelArrayList.get(position).getImage());
        holder.TVbarberName.setText(UpcomingBookingModelArrayList.get(position).getBarberName());
        holder.TVbookingDate.setText(UpcomingBookingModelArrayList.get(position).getBookingDate());
        holder.TVbookingTime.setText(UpcomingBookingModelArrayList.get(position).getBookingTime());
        holder.TVbookingLocation.setText(UpcomingBookingModelArrayList.get(position).getBookingLocation());

        holder.BTNcancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model != null && model.getAppointmentID() != null){
                    cancelledBookingStatus(model.getAppointmentID());
                    Intent intent = new Intent(context, cancelled_booking.class);
                    intent.putExtra("appointmentId",model.getAppointmentID());
                    context.startActivity(intent);
                }else{

                }


            }
        });

    }

    private void cancelledBookingStatus(String appointmentID) {
        if(appointmentID == null){
            Log.e("CancelledBookingStatus", "Booking ID is null");
            return;
        }
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.child(appointmentID).child("status").setValue("cancelled")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Firebase Error", "Failed to update booking status to 'upcoming'");
                        Toast.makeText(context, "Failed to accept booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {

        return UpcomingBookingModelArrayList.size();
    }
}

/////////////////////////////UpcomingBookingHolder//////////////////////
class UpcomingBookingHolder extends RecyclerView.ViewHolder{

    ImageView IVbarberProfilePicture;
    TextView TVbarberName,TVbookingDate,TVbookingTime,TVbookingLocation;
    Button BTNcancelled;
    public UpcomingBookingHolder(@NonNull View itemView) {
        super(itemView);

        IVbarberProfilePicture = itemView.findViewById(R.id.IVBarberProfilePicture);
        TVbarberName = itemView.findViewById(R.id.TVbarberName);
        TVbookingDate = itemView.findViewById(R.id.TVdateOfBooking);
        TVbookingTime = itemView.findViewById(R.id.TVtimeOfBooking);
        TVbookingLocation = itemView.findViewById(R.id.TVlocationOfBooking);
        BTNcancelled = itemView.findViewById(R.id.BTNcancelBooking);

    }
}

///////////////////////////////////UpcomingBookingModel//////////////////////////////
class UpcomingBookingModel{
    int image;
    String barberName, bookingDate, bookingTime , bookingLocation, appointmentID;

    public UpcomingBookingModel(int image,String barberName, String bookingDate, String bookingTime, String bookingLocation,String appointmentID){
        this.image = image;
        this.barberName = barberName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingLocation = bookingLocation;
        this.appointmentID = appointmentID;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(String bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
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

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }
}
