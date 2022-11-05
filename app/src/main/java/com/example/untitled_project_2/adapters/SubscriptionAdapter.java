package com.example.untitled_project_2.adapters;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionsAdapterViewHolder> {
    private final Activity mActivity;
    private final ArrayList<String> cities;
    private final ArrayList<String> vaccines;
    private final ArrayList<String> subIds;
    private final int length;
    private final String token;

    public SubscriptionAdapter(Activity activity, ArrayList<String> citiesArray, ArrayList<String> vaccinesArray, ArrayList<String> subIdsArray, int mlength, String mToken){
        mActivity = activity;
        cities = citiesArray;
        vaccines = vaccinesArray;
        subIds = subIdsArray;
        length = mlength;
        token = mToken;
    }

    //launches when we create Recyclerview
    @NonNull
    @Override
    public SubscriptionsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.sub_row,parent,false);
        return new SubscriptionsAdapterViewHolder(rowRootview);
    }
    //launches when we need to create new row
    @Override
    public void onBindViewHolder(@NonNull SubscriptionsAdapterViewHolder holder, int position) {
        holder.isBinding = true;
        holder.SubCancelButton.setTag(position);
        holder.SubVaccine.setText(vaccines.get(position));
        holder.SubCity.setText(cities.get(position));
        //holder.signupButton.setTag(position);

        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        return length;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class SubscriptionsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        //initialize fields

        boolean isBinding;
        Button SubCancelButton;
        TextView SubCity;
        TextView SubVaccine;

        public SubscriptionsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //init fields, set listeners
            SubCancelButton = itemView.findViewById(R.id.SubCancel);
            SubCity = itemView.findViewById(R.id.SubCity);
            SubVaccine = itemView.findViewById(R.id.subVaccine);

            SubCancelButton.setOnClickListener(view -> {
                int clickedPos = (Integer)SubCancelButton.getTag();
                String subId = subIds.get(clickedPos);
                deleteSub(subId);
            });
        }

        private void deleteSub(String subId) {
            RequestQueue queue = Volley.newRequestQueue(mActivity.getApplicationContext());
            @SuppressLint("NotifyDataSetChanged")
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE,"https://10.0.2.2:7277/api/subscriptions/"+subId+"/",
                    response -> notifyDataSetChanged(), error -> Log.i("Error", error.toString())) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            queue.add(stringRequest);
            mActivity.finish();
            mActivity.startActivity(mActivity.getIntent());
            Toast.makeText(mActivity.getApplicationContext(), "Usunięto subskrypcję",Toast.LENGTH_LONG).show();
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
