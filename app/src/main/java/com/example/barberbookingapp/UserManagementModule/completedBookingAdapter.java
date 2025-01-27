package com.example.barberbookingapp.UserManagementModule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

import com.example.barberbookingapp.BarberListViewModule.Feedback;
import com.example.barberbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        holder.TVbarberName.setText(CompletedBookingModelArrayList.get(position).getBarberName());
        holder.TVbookingDate.setText(CompletedBookingModelArrayList.get(position).getBookingDate());
        holder.TVbookingTime.setText(CompletedBookingModelArrayList.get(position).getBookingTime());
        holder.TVbookingLocation.setText(CompletedBookingModelArrayList.get(position).getBookingLocation());

        String profileImageBase64 = model.getImage();
        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
            holder.IVbarberProfilePicture.setImageBitmap(bitmap);
        } else {
            // Set a placeholder image if no profileImage is available
            holder.IVbarberProfilePicture.setImageResource(R.drawable.usericon);
        }

        Log.d("BindViewHolder", "Binding data for position " + holder);

        holder.buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarberID(model.getAppointmentID(), new BarberIDCallback() {
                    @Override
                    public void onSuccess(String barberID) {
                        Log.d("BarberID", "Retrieved Barber ID: " + barberID);

                        // Start the Feedback activity with the retrieved barberID
                        Intent intent = new Intent(context, Feedback.class);
                        intent.putExtra("barberID", barberID);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("BarberID", "Error: " + errorMessage);
                        // Show an error message to the user
                        Toast.makeText(context, "Failed to retrieve Barber ID: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });

    }

    private Bitmap decodeBase64ToBitmap(String profileImageBase64) {
        byte[] decodedBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void getBarberID(String appointmentID, BarberIDCallback callback) {
        if (appointmentID == null || appointmentID.isEmpty()) {
            callback.onError("Invalid appointment ID");
            return;
        }

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentsRef.child(appointmentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String barberID = snapshot.child("barberID").getValue(String.class);
                    if (barberID != null) {
                        callback.onSuccess(barberID);
                    } else {
                        callback.onError("Barber ID not found for this appointment.");
                    }
                } else {
                    callback.onError("Appointment not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Failed to fetch data: " + error.getMessage());
            }
        });
    }
    public interface BarberIDCallback {
        void onSuccess(String barberID);
        void onError(String errorMessage);
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
    String image;
    String barberName, bookingDate, bookingTime , bookingLocation, appointmentID;

    public CompletedBookingModel(String image, String barberName, String bookingDate, String bookingTime, String bookingLocation, String appointmentID) {
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
