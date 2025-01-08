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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class completedBookingAdapter extends RecyclerView.Adapter<CompletedBookingHolder> {

    Context context;
    ArrayList<CompletedBookingModel> CompletedBookingModelArrayList = new ArrayList<>();
    private completedBookingAdapter adapter;

    public completedBookingAdapter(Context context, ArrayList<CompletedBookingModel> completedBookingModelArrayList) {
        this.context = context;
        CompletedBookingModelArrayList = completedBookingModelArrayList;

    }

    @NonNull
    @Override
    public CompletedBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompletedBookingHolder(LayoutInflater.from(context).inflate(R.layout.completed_booking_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedBookingHolder holder, int position) {
        CompletedBookingModel model = CompletedBookingModelArrayList.get(position);

        holder.IVbarberProfilePicture.setImageResource(CompletedBookingModelArrayList.get(position).getImage());
        holder.TVbarberName.setText(CompletedBookingModelArrayList.get(position).getBarberName());
        holder.TVbookingDate.setText(CompletedBookingModelArrayList.get(position).getBookingDate());
        holder.TVbookingTime.setText(CompletedBookingModelArrayList.get(position).getBookingTime());
        holder.TVbookingLocation.setText(CompletedBookingModelArrayList.get(position).getBookingLocation());

        Log.d("BindViewHolder", "Binding data for position " + holder);

        holder.buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to cancelled_booking activity
                //Intent intent = new Intent(context, doneReview_booking.class);
              //  context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return CompletedBookingModelArrayList.size();
    }
}

class CompletedBookingHolder extends RecyclerView.ViewHolder{
    ImageView IVbarberProfilePicture;
    TextView TVbarberName,TVbookingDate,TVbookingTime,TVbookingLocation;
    Button buttonReview;

    public CompletedBookingHolder(@NonNull View itemView) {
        super(itemView);

        IVbarberProfilePicture = itemView.findViewById(R.id.IVBarberProfilePicture);
        TVbarberName = itemView.findViewById(R.id.TVbarberName);
        TVbookingDate = itemView.findViewById(R.id.TVdateOfBooking);
        TVbookingTime = itemView.findViewById(R.id.TVtimeOfBooking);
        TVbookingLocation = itemView.findViewById(R.id.TVlocationOfBooking);
        buttonReview = itemView.findViewById(R.id.buttonReview);
    }
}

class CompletedBookingModel{
    int image;
    String barberName, bookingDate, bookingTime , bookingLocation, appointmentID;

    public CompletedBookingModel(int image, String barberName, String bookingDate, String bookingTime, String bookingLocation, String appointmentID) {
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

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(String bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }
}
