package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.activities.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.RegisterAdapterViewHolder> {
    private Activity mActivity;
    private ArrayList<String> mfieldsArray;
    private ArrayList<String> mresultsArray;
    private ArrayList<String> mCitiesArray;
    private Button mSubmitButton;

    public RegisterAdapter(Activity activity, ArrayList<String> fieldsArray, ArrayList<String> resultsArray, ArrayList<String> citiesArray, Button submitButton){
        mActivity = activity;
        mfieldsArray = fieldsArray;
        mresultsArray = resultsArray;
        mCitiesArray = citiesArray;
        mSubmitButton = submitButton;

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
        switch (registerField){
            case "Potwierdź hasło":
                holder.RegisterEdit.setHint("Potwierdź Hasło");
                break;
            case "Ulica":
                holder.RegisterEdit.setHint("Podaj Ulicę");
                break;
            case "Telefon":
                holder.RegisterEdit.setHint("Podaj Numer Telefonu");
                break;
            default:
                holder.RegisterEdit.setHint("Podaj "+registerField);
        }
        if(position == mfieldsArray.size() -1){
            holder.RegisterEdit.setVisibility(View.GONE);
            holder.RegisterSpinner.setVisibility(View.VISIBLE);
            holder.RegisterSpinner.setTag(position);
            ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(holder.RegisterSpinner.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mCitiesArray);
            holder.RegisterSpinner.setAdapter(adapterCity);
        }

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
        public Spinner RegisterSpinner;
        boolean isBinding;

        public RegisterAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //init fields, set listeners
            RegisterEdit = (EditText)itemView.findViewById(R.id.RegisterEditText);
            RegisterText = (TextView) itemView.findViewById(R.id.RegisterTextview);
            RegisterSpinner = (Spinner) itemView.findViewById(R.id.RegisterSpinner);

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
                        if(RegisterEdit.getText().toString().length() < 1)
                        {
                            RegisterEdit.setError("Pole "+mfieldsArray.get(clickedPos)+" nie może być puste");
                        }
                        else{
                            mresultsArray.set(clickedPos,RegisterEdit.getText().toString());
                            validate();
                        }

                    }
                }
            });
            RegisterEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b && !isBinding){
                        int clickedPos = (Integer)RegisterEdit.getTag();
                        if(RegisterEdit.getText().toString().length() < 1)
                        {
                            RegisterEdit.setError("Pole "+mfieldsArray.get(clickedPos)+" nie może być puste");
                            validate();
                        }
                    }
                }
            });
            RegisterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!isBinding){
                        int clickedPos = (Integer)RegisterSpinner.getTag();
                        int choice = RegisterSpinner.getSelectedItemPosition();
                        mresultsArray.set(clickedPos,Integer.toString(choice));
                        validate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

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
        private void validate()
        {
            boolean everythingChecked = true;
            for (int i=0;i<getItemCount();i++)
            {
                if (mresultsArray.get(i).length() < 1)
                {
                    //commented for testing
                    //everythingChecked = false;
                    break;
                }
            }
            if (everythingChecked)
            {
                mSubmitButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
