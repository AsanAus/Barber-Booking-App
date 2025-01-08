package com.example.barberbookingapp.BarberManagementModule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;

import java.util.ArrayList;

public class completedBarberAdapter extends RecyclerView.Adapter<CompletedBookingHolder> {

    Context context;
    ArrayList<CompletedBookingModel> CompletedBookingModelArrayList = new ArrayList<>();

    public completedBarberAdapter(Context context, ArrayList<CompletedBookingModel> completedBookingModelArrayList) {
        this.context = context;
        CompletedBookingModelArrayList = completedBookingModelArrayList;
    }

    @NonNull
    @Override
    public CompletedBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompletedBookingHolder(LayoutInflater.from(context).inflate(R.layout.newcompleted_card_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedBookingHolder holder, int position) {
        CompletedBookingModel model = CompletedBookingModelArrayList.get(position);

        // Populate views with data
        holder.TVName.setText(model.getUserName());
        holder.TVPrice.setText(model.getBookingPrice());

        Log.d("BindViewHolder", "Binding data for position " + holder);


    }

    @Override
    public int getItemCount() {
        return CompletedBookingModelArrayList.size();
    }
}

class CompletedBookingHolder extends RecyclerView.ViewHolder{
    TextView TVName,TVPrice;


    public CompletedBookingHolder(@NonNull View itemView) {
        super(itemView);
        TVName = itemView.findViewById(R.id.TVName);
        TVPrice = itemView.findViewById(R.id.TVPrice);
    }
}

class CompletedBookingModel{
    String userName, bookingPrice,appointmentID;

    public CompletedBookingModel(String userName,  String bookingPrice, String appointmentID) {
        this.userName = userName;
        this.bookingPrice = bookingPrice;
        this.appointmentID = appointmentID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
