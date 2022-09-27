package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.untitled_project_2.R;

import java.util.ArrayList;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.RegisterAdapterViewHolder> {
    private Activity mActivity;
    private ArrayList<String> mfieldsArray;
    private ArrayList<String> mresultsArray;

    public RegisterAdapter(Activity activity, ArrayList<String> fieldsArray, ArrayList<String> resultsArray){
        mActivity = activity;
        mfieldsArray = fieldsArray;
        mresultsArray = resultsArray;


    }

    //launches when we create Recyclerview
    @NonNull
    @Override
    public RegisterAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        for(int i = 0;i<getItemCount();i++) {
            mresultsArray.add("");
        }

        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.register_row,parent,false);
        RegisterAdapterViewHolder registerAdapterViewHolder = new RegisterAdapterViewHolder(rowRootview);
        return registerAdapterViewHolder;
    }
    //launches when we need to create new row
    @Override
    public void onBindViewHolder(@NonNull RegisterAdapterViewHolder holder, int position) {
        holder.isBinding = true;
        String registerField = mfieldsArray.get(position);
        holder.RegisterText.setText(registerField);
        holder.RegisterEdit.setTag(position);

        //holder.signupButton.setTag(position);

        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class RegisterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        //initialize fields
        public TextView RegisterText;
        public EditText RegisterEdit;

        boolean isBinding;

        public RegisterAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //init fields, set listeners
            RegisterEdit = (EditText)itemView.findViewById(R.id.RegisterEditText);
            RegisterText = (TextView) itemView.findViewById(R.id.RegisterTextview);

            RegisterEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!isBinding){
                        int clickedPos = (Integer)RegisterEdit.getTag();
                        //tu walidacja
                        Log.i("Position: ",Integer.toString(clickedPos));
                        mresultsArray.set(clickedPos,RegisterEdit.getText().toString());
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
