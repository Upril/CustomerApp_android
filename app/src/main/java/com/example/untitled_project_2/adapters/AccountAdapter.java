package com.example.untitled_project_2.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.example.untitled_project_2.R;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountAdapterViewHolder> {
    private final Activity mActivity;
    private final ArrayList<String> mfieldsArray;
    private final ArrayList<String> mValuesArray;
    private final ArrayList<String> mCitiesArray;
    private ArrayList<String> initialValuesArray;
    private final Button mSubmitButton;

    public AccountAdapter(Activity activity, ArrayList<String> fieldsArray, ArrayList<String> resultsArray, ArrayList<String> citiesArray, Button submitButton){
        mActivity = activity;
        mfieldsArray = fieldsArray;
        mValuesArray = resultsArray;
        mCitiesArray = citiesArray;
        mSubmitButton = submitButton;
    }

    //launches when we create Recyclerview
    @NonNull
    @Override
    public AccountAdapter.AccountAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        initialValuesArray = new ArrayList<>(mValuesArray.size());
        initialValuesArray.addAll(mValuesArray);
        View rowRootview = mActivity.getLayoutInflater().inflate(R.layout.account_row,parent,false);
        return new AccountAdapter.AccountAdapterViewHolder(rowRootview);
    }
    //launches when we need to create new row
    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.AccountAdapterViewHolder holder, int position) {
        holder.isBinding = true;
        String registerField = mfieldsArray.get(position);
        String placeholder = mValuesArray.get(position);
        holder.RegisterText.setText(registerField);
        holder.RegisterEdit.setText(placeholder);
        holder.RegisterEdit.setTag(position);
        switch (registerField){
            case "Hasło":
                holder.RegisterEdit.setHint("Hasło");
                holder.RegisterEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case "Potwierdź hasło":
                holder.RegisterEdit.setHint("Potwierdź Hasło");
                holder.RegisterEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
            ArrayAdapter<String> adapterCity = new ArrayAdapter<>(holder.RegisterSpinner.getContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mCitiesArray);
            holder.RegisterSpinner.setAdapter(adapterCity);
            holder.RegisterSpinner.setSelection(Integer.parseInt(mValuesArray.get(mValuesArray.size()-1))-1);
        }


        holder.isBinding = false;

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    //view holder zarządza pojedynczym wierszem listy
    //to dobre miejsce na zaimplementowanie słuchaczy
    class AccountAdapterViewHolder extends RecyclerView.ViewHolder{
        //initialize fields
        public TextView RegisterText;
        public EditText RegisterEdit;
        public Spinner RegisterSpinner;
        boolean isBinding;

        public AccountAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //init fields, set listeners
            RegisterEdit = itemView.findViewById(R.id.AccountEditText);
            RegisterText = itemView.findViewById(R.id.AccountTextview);
            RegisterSpinner = itemView.findViewById(R.id.AccountSpinner);

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
                            mValuesArray.set(clickedPos,RegisterEdit.getText().toString());
                            validate();
                        }

                    }
                }
            });
            RegisterEdit.setOnFocusChangeListener((view, b) -> {
                if (!b && !isBinding){
                    int clickedPos = (Integer)RegisterEdit.getTag();
                    if(RegisterEdit.getText().toString().length() < 1)
                    {
                        RegisterEdit.setError("Pole "+mfieldsArray.get(clickedPos)+" nie może być puste");
                        validate();
                    }
                }
            });
            RegisterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!isBinding){
                        int clickedPos = (Integer)RegisterSpinner.getTag();
                        int choice = RegisterSpinner.getSelectedItemPosition();
                        mValuesArray.set(clickedPos,Integer.toString(choice+1));
                        validate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        private void validate()
        {
            boolean everythingChecked = true;
            boolean dataChanged = !initialValuesArray.equals(mValuesArray);
            for (int i=0;i<getItemCount();i++)
            {
                if (mValuesArray.get(i).length() < 1)
                {
                    //commented for testing
                    everythingChecked = false;
                    break;

                }
            }
            if (everythingChecked && dataChanged)
            {
                mSubmitButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
