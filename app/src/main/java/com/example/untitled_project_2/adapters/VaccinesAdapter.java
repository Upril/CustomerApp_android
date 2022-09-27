package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untitled_project_2.R;

public class VaccinesAdapter extends RecyclerView.Adapter<VaccinesAdapter.VaccinesAdapterViewHolder> {
    private Activity mActivity;

    public VaccinesAdapter(Activity activity){
        mActivity = activity;
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

        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        //zmien potem jak sie bedziesz laczy z baza
        return 9;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class VaccinesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        public TextView medicalFacility;
        public TextView vaccineName;
        public TextView vaccineDate;
        public TextView vaccineHour;
        public Button signupButton;
        //public TextView doseNumber;
        //public TextView vaccinationStatus;
        boolean isBinding;

        public VaccinesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            medicalFacility = (TextView) itemView.findViewById(R.id.facilityName);
            vaccineName = (TextView) itemView.findViewById(R.id.vaccineName);
            vaccineHour = (TextView) itemView.findViewById(R.id.vaccineHour);
            vaccineDate = (TextView) itemView.findViewById(R.id.vaccineDate);
            signupButton = (Button) itemView.findViewById(R.id.vaccineButton);

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isBinding) {
                        int clickedPos = (Integer) signupButton.getTag();
                        medicalFacility.setText("kliknieto: "+clickedPos);
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
