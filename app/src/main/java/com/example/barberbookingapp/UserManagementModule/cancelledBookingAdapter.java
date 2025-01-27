package com.example.barberbookingapp.UserManagementModule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class cancelledBookingAdapter extends RecyclerView.Adapter<CancelledBookingHolder>{

    Context context;
    ArrayList<CancelledBookingModel> CancelledBookingModelArrayList = new ArrayList<>();

    public cancelledBookingAdapter(Context context, ArrayList<CancelledBookingModel> cancelledBookingModelArrayList) {
        this.context = context;
        CancelledBookingModelArrayList = cancelledBookingModelArrayList;
    }

    @NonNull
    @Override
    public CancelledBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CancelledBookingHolder(LayoutInflater.from(context).inflate(R.layout.cancelled_booking_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull CancelledBookingHolder holder, int position) {
        CancelledBookingModel model = CancelledBookingModelArrayList.get(position);

        holder.TVbarberName.setText(CancelledBookingModelArrayList.get(position).getBarberName());
        holder.TVbookingDate.setText(CancelledBookingModelArrayList.get(position).getBookingDate());
        holder.TVbookingTime.setText(CancelledBookingModelArrayList.get(position).getBookingTime());
        holder.TVbookingLocation.setText(CancelledBookingModelArrayList.get(position).getBookingLocation());

        String profileImageBase64 = model.getImage();
        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
            holder.IVbarberProfilePicture.setImageBitmap(bitmap);
        } else {
            // Set a placeholder image if no profileImage is available
            holder.IVbarberProfilePicture.setImageResource(R.drawable.usericon);
        }
    }

    private Bitmap decodeBase64ToBitmap(String profileImageBase64) {
        byte[] decodedBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }

    @Override
    public int getItemCount() {

        return CancelledBookingModelArrayList.size();
    }
}

/////////////////////////////CancelledBookingHolder//////////////////////
class CancelledBookingHolder extends RecyclerView.ViewHolder{

    ImageView IVbarberProfilePicture;
    TextView TVbarberName,TVbookingDate,TVbookingTime,TVbookingLocation;
    public CancelledBookingHolder(@NonNull View itemView) {
        super(itemView);

        IVbarberProfilePicture = itemView.findViewById(R.id.IVBarberProfilePicture);
        TVbarberName = itemView.findViewById(R.id.TVbarberName);
        TVbookingDate = itemView.findViewById(R.id.TVdateOfBooking);
        TVbookingTime = itemView.findViewById(R.id.TVtimeOfBooking);
        TVbookingLocation = itemView.findViewById(R.id.TVlocationOfBooking);
    }
}


///////////////////////////////////CancelledBookingModel//////////////////////////////
class CancelledBookingModel{
    String image;
    String barberName, bookingDate, bookingTime , bookingLocation,appointmentID;

    public CancelledBookingModel(String image,String barberName, String bookingDate, String bookingTime, String bookingLocation, String appointmentID) {
        this.image = image;
        this.barberName = barberName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingLocation = bookingLocation;
        this.appointmentID = appointmentID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBookingLocation() {
        return bookingLocation;
    }

    public void setBookingLocation(String bookingLocation) {
        this.bookingLocation = bookingLocation;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }
}
