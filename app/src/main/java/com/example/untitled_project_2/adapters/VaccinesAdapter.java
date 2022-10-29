package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untitled_project_2.R;
import com.example.untitled_project_2.networking.JWTUtils;

import java.util.ArrayList;

public class VaccinesAdapter extends RecyclerView.Adapter<VaccinesAdapter.VaccinesAdapterViewHolder> {
    private final Activity mActivity;
    private final ArrayList<Vaccine> vaccines;
    private final Integer length;
    private final String token;
    private String[] data;

    public VaccinesAdapter(Activity activity, ArrayList<Vaccine> vaccines1, Integer length1, String token1){
        mActivity = activity;
        vaccines=vaccines1;
        length=length1;
        token=token1;

    }
    //launches when we create Recyclerview
    @NonNull
    @Override
    public VaccinesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.vaccines_row,parent,false);
        VaccinesAdapterViewHolder vaccinesAdapterViewHolder = new VaccinesAdapterViewHolder(rowRootview);
        return vaccinesAdapterViewHolder;
    }
    //launches when we need to create new row
    @Override
    public void onBindViewHolder(@NonNull VaccinesAdapterViewHolder holder, int position) {
        holder.isBinding = true;

        holder.signupButton.setTag(position);
        Vaccine vaccine = vaccines.get(position);
        holder.vaccineName.setText(vaccine.getVaccineName());
        holder.vaccineDate.setText(vaccine.getDate());
        holder.medicalFacility.setText(vaccine.getFacilityName());
        holder.facilityAddress.setText(vaccine.getAddress());

        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        //zmien potem jak sie bedziesz laczy z baza
        return length;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class VaccinesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        public TextView medicalFacility;
        public TextView vaccineName;
        public TextView vaccineDate;
        public TextView facilityAddress;
        public Button signupButton;
        boolean isBinding;

        public VaccinesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            medicalFacility = (TextView) itemView.findViewById(R.id.facilityName);
            facilityAddress = (TextView) itemView.findViewById(R.id.facilityAddress);
            vaccineName = (TextView) itemView.findViewById(R.id.vaccineName);
            vaccineDate = (TextView) itemView.findViewById(R.id.vaccineDate);
            signupButton = (Button) itemView.findViewById(R.id.vaccineButton);

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isBinding) {
                        //signup tutaj
                    }
                }
            });
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void onClick(View view) {

        }
    }
}
