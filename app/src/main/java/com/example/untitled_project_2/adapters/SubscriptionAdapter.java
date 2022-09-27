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

import java.util.ArrayList;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionsAdapterViewHolder> {
    private Activity mActivity;
    private ArrayList<String> cities;
    private ArrayList<String> vaccines;
    private ArrayList<String> subIds;
    private int length;

    public SubscriptionAdapter(Activity activity, ArrayList<String> citiesArray, ArrayList<String> vaccinesArray, ArrayList<String> subIdsArray, int mlength){
        mActivity = activity;
        cities = citiesArray;
        vaccines = vaccinesArray;
        subIds = subIdsArray;
        length = mlength;
    }

    //launches when we create Recyclerview
    @NonNull
    @Override
    public SubscriptionsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.sub_row,parent,false);
        SubscriptionsAdapterViewHolder subscriptionsAdapterViewHolder = new SubscriptionsAdapterViewHolder(rowRootview);
        return subscriptionsAdapterViewHolder;
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
            SubCancelButton = (Button)itemView.findViewById(R.id.SubCancel);
            SubCity = (TextView)itemView.findViewById(R.id.SubCity);
            SubVaccine = (TextView)itemView.findViewById(R.id.subVaccine);

            SubCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPos = (Integer)SubCancelButton.getTag();
                    //delete tutaj
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
