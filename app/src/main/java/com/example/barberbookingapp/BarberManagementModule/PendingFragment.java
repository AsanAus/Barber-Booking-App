package com.example.barberbookingapp.BarberManagementModule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberbookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Pending> PendingArrayList;
    private RecyclerView recyclerView;
    DatabaseReference databaseReference;
    PendingAdapter adapter;

    public PendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_upcoming.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingFragment newInstance(String param1, String param2) {
        PendingFragment fragment = new PendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.pending_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Replace "currentBarberId" with the actual ID of the logged-in barber
        String currentBarberId = getCurrentBarberId(); // Implement this method to fetch the current barber's ID

        loadPendingBooking(view,currentBarberId);
    }

    private void loadPendingBooking(View view,String barberID) {
        recyclerView = view.findViewById(R.id.rvPending);
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        PendingArrayList = new ArrayList<>();
        adapter = new PendingAdapter(this.getContext(), PendingArrayList);
        recyclerView.setAdapter(adapter);

        databaseReference.orderByChild("barberID").equalTo(barberID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PendingArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if("pending".equals(status)){
                        Pending pending = new Pending();
                        pending.setPendingId(dataSnapshot.getKey());
                        pending.setDate(dataSnapshot.child("date").getValue(String.class));
                        pending.setTime(dataSnapshot.child("time").getValue(String.class));
                        pending.setServiceType(dataSnapshot.child("serviceName").getValue(String.class));
                        pending.setPrice(dataSnapshot.child("servicePrice").getValue(String.class));

                        String userId = dataSnapshot.child("userID").getValue(String.class);
                        fetchUserDetails(userId, pending);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void fetchUserDetails(String userId, Pending pending) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.setName(snapshot.child("username").getValue(String.class));
                pending.setContact(snapshot.child("phoneNumber").getValue(String.class));
                PendingArrayList.add(pending);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCurrentBarberId() {
        // Retrieve the current barber ID from shared preferences, authentication session, or other methods
        return FirebaseAuth.getInstance().getCurrentUser().getUid(); // Example using Firebase Authentication

    }
}
