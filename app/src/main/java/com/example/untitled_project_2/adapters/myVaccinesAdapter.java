package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.networking.JWTUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myVaccinesAdapter extends RecyclerView.Adapter<myVaccinesAdapter.myVaccinesAdapterViewHolder> {
    private final Activity mActivity;
    private final ArrayList<Vaccine> vaccines;
    private final Integer length;
    private final String token;
    private String[] data;

    public myVaccinesAdapter(Activity activity, ArrayList<Vaccine> vaccines1, Integer length1, String token1){
        mActivity = activity;
        vaccines=vaccines1;
        length=length1;
        token=token1;
        try {
            data = JWTUtils.decode(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //launches when we create Recyclerview
    @NonNull
    @Override
    public myVaccinesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.myvaccines_row,parent,false);
        return new myVaccinesAdapterViewHolder(rowRootview);
    }
    //launches when we need to create new row
    @Override
    public void onBindViewHolder(@NonNull myVaccinesAdapterViewHolder holder, int position) {
        holder.isBinding = true;

        Vaccine vaccine = vaccines.get(position);
        holder.signupButton.setTag(vaccine.getId());
        Log.i("ButtonForVaccine", (String) holder.signupButton.getTag());
        holder.vaccineName.setText(vaccine.getVaccineName());
        holder.vaccineDate.setText(vaccine.getDate());
        holder.medicalFacility.setText(vaccine.getFacilityName());
        holder.facilityAddress.setText(vaccine.getAddress());

        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        return length;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class myVaccinesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        public TextView medicalFacility;
        public TextView vaccineName;
        public TextView vaccineDate;
        public TextView facilityAddress;
        public Button signupButton;
        boolean isBinding;

        public myVaccinesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            medicalFacility = itemView.findViewById(R.id.myFacilityName);
            facilityAddress = itemView.findViewById(R.id.myFacilityAddress);
            vaccineName = itemView.findViewById(R.id.myVaccineName);
            vaccineDate = itemView.findViewById(R.id.myVaccineDate);
            signupButton = itemView.findViewById(R.id.myVaccineButton);
            signupButton.setOnClickListener(view -> {
                if (!isBinding) {
                    RequestQueue queue1 = Volley.newRequestQueue(mActivity.getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT,"https://10.0.2.2:7277/api/vaccination/unassignUserFromDate?dateId="+signupButton.getTag()+"&userId="+data[0], response -> {
                        Log.e("Vaccination","Removed");
                        mActivity.finish();
                        mActivity.startActivity(mActivity.getIntent());
                        Toast.makeText(mActivity.getApplicationContext(), "Anulowano termin!", Toast.LENGTH_LONG).show();

                    }, error -> Log.i("Error", error.toString())) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };
                    queue1.add(stringRequest);
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
