package com.example.barberbookingapp.UserManagementModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

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
    public UpcomingBookingHolder(@NonNull View itemView) {
        super(itemView);

        IVbarberProfilePicture = itemView.findViewById(R.id.IVBarberProfilePicture);
        TVbarberName = itemView.findViewById(R.id.TVbarberName);
        TVbookingDate = itemView.findViewById(R.id.TVdateOfBooking);
        TVbookingTime = itemView.findViewById(R.id.TVtimeOfBooking);
        TVbookingLocation = itemView.findViewById(R.id.TVlocationOfBooking);

    }
}

///////////////////////////////////UpcomingBookingModel//////////////////////////////
class UpcomingBookingModel{
    int image;
    String barberName, bookingDate, bookingTime , bookingLocation;

    public UpcomingBookingModel(int image,String barberName, String bookingDate, String bookingTime, String bookingLocation){
        this.image = image;
        this.barberName = barberName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingLocation = bookingLocation;
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

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }
}
