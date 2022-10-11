package com.example.untitled_project_2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.RegisterAdapter;
import com.example.untitled_project_2.networking.SSLRules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private final SSLRules ssl = new SSLRules();
    private final String URLline = "https://10.0.2.2:7277/api/account/register/";
    private ArrayList<String> valuesArray;
    private ArrayList<String> citiesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ArrayList<String> fieldsArray = new ArrayList<>();
        fieldsArray.add("Imie");
        fieldsArray.add("Nazwisko");
        fieldsArray.add("Telefon");
        fieldsArray.add("Pesel");
        fieldsArray.add("Adres e-mail");
        fieldsArray.add("Hasło");
        fieldsArray.add("Potwierdź hasło");
        fieldsArray.add("Kod pocztowy");
        fieldsArray.add("Ulica");
        fieldsArray.add("Numer mieszkania");
        fieldsArray.add("Numer budynku");
        fieldsArray.add("Miasto");

        valuesArray = new ArrayList<>(12);

        RecyclerView rvRegister = (RecyclerView) findViewById(R.id.RegisterRv);
        Button registerButton = (Button) findViewById(R.id.Registerbutton);
        Button cancelButton = (Button) findViewById(R.id.RegisterCancelButton);
        rvRegister.getRecycledViewPool().setMaxRecycledViews(0, 15);
        rvRegister.setItemViewCacheSize(15);

        //get cities
        citiesArray = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String cityUrl = "https://10.0.2.2:7277/api/account/getAllCities/";
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, cityUrl, null,
                response -> {
                    if (response != null && response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject sub = response.getJSONObject(i);
                                citiesArray.add(sub.getString("Name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, error -> Log.i("Error", error.toString()));

        queue.add(arrayRequest);

        RegisterAdapter registerAdapter = new RegisterAdapter(this, fieldsArray, valuesArray, citiesArray, registerButton);
        rvRegister.setAdapter(registerAdapter);
        rvRegister.setLayoutManager(new LinearLayoutManager(this));


        //ssl disable
        ssl.SSlDisable();

        registerButton.setOnClickListener(view -> {
            try {
                RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                JSONObject jsonAddress = new JSONObject();
                //filling json array with gathered data from RegisterAdapter
                jsonBody.put("FirstName", valuesArray.get(0));
                jsonBody.put("Surname", valuesArray.get(1));
                jsonBody.put("PhoneNumber", valuesArray.get(2));
                jsonBody.put("Pesel", valuesArray.get(3));
                jsonBody.put("Email", valuesArray.get(4));
                jsonBody.put("Password", valuesArray.get(5));
                jsonBody.put("ConfirmPassword", valuesArray.get(6));
                jsonAddress.put("PostalCode", valuesArray.get(7));
                jsonAddress.put("StreetName", valuesArray.get(8));
                jsonAddress.put("FlatNumber", valuesArray.get(9));
                jsonAddress.put("BuildingNumber", valuesArray.get(10));
                jsonAddress.put("CityId", valuesArray.get(11));
                jsonBody.put("AddressDto", jsonAddress);

                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline, response -> {
                    Toast.makeText(RegisterActivity.this, "Konto zostało utworzone, teraz należy się zalogować", Toast.LENGTH_LONG).show();
                    finish();
                }, error -> {
                    NetworkResponse response = error.networkResponse;
                    String json;
                    if (response != null && response.data != null) {
                        if (response.statusCode == 400) {
                            json = new String(response.data);
                            try {
                                JSONObject errorJson = new JSONObject(json);
                                JSONObject errors = errorJson.getJSONObject("errors");
                                ArrayList<JSONArray> errorMessages = new ArrayList<>(12);
                                String[] keys = {"Email", "Pesel", "Surname", "Password", "FirstName", "PhoneNumber",
                                        "AddressDto.FlatNumber", "AddressDto.PostalCode", "AddressDto.StreetName", "AddressDto.BuildingNumber"};
                                StringBuilder alertMessage = new StringBuilder();
                                for (String key : keys) {
                                    if (errors.has(key)) {
                                        errorMessages.add(errors.getJSONArray(key));
                                    }
                                }
                                for (JSONArray message : errorMessages) {
                                    Log.e("RegisterError", message.getString(0));
                                    alertMessage.append(message.getString(0)).append("\n\n");
                                }
                                new AlertDialog.Builder(RegisterActivity.this).setTitle("Błąd rejestracji").setMessage(alertMessage.toString())
                                        .setPositiveButton("OK", null).setIcon(R.drawable.ic_stop_black).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                    @Override
                    public byte[] getBody() {
                        return mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }
                };
                queue1.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnClickListener(view -> finish());

    }
}