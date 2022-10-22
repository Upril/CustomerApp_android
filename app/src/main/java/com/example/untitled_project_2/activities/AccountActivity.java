package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.AccountAdapter;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final SSLRules ssl = new SSLRules();
    public static ActivityResultLauncher<Intent> mActivityLauncher;
    public ArrayList<String> citiesArray;
    private ArrayList<String> valuesArray;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ArrayList<String> fieldsArray = new ArrayList<>();
        fieldsArray.add("Imie");
        fieldsArray.add("Nazwisko");
        fieldsArray.add("Telefon");
        fieldsArray.add("Pesel");
        fieldsArray.add("Adres e-mail");
        fieldsArray.add("Kod pocztowy");
        fieldsArray.add("Ulica");
        fieldsArray.add("Numer mieszkania");
        fieldsArray.add("Numer budynku");
        fieldsArray.add("Miasto");

        valuesArray = new ArrayList<>(10);

        //intent get user id
        Intent intent = getIntent();
        token = intent.getStringExtra("token");


        int userId = 1002;
        //wydobadz userid z tokena

        //init menu
        setTitle("Profil");
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });
        MenuActivityLauncher menuActivityLauncher = new MenuActivityLauncher(AccountActivity.this, mActivityLauncher, token);
        menuActivityLauncher.init(navigationView, drawerLayout);

        //ssl disable
        ssl.SSlDisable();

        RecyclerView rvAccount = findViewById(R.id.AccountRv);
        Button accountEditButton = findViewById(R.id.AccountEditButton);
        rvAccount.getRecycledViewPool().setMaxRecycledViews(0, 15);
        rvAccount.setItemViewCacheSize(15);

        //get account info
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String infoUrl = "https://10.0.2.2:7277/api/account/getInfo?userId=" + userId;
        JsonObjectRequest arrayRequest1 = new JsonObjectRequest(Request.Method.GET, infoUrl, null,
                response -> {
                    if (response != null && response.length() > 0) {
                        try {
                            valuesArray.add(response.getString("FirstName"));
                            valuesArray.add(response.getString("Surname"));
                            valuesArray.add(response.getString("PhoneNumber"));
                            valuesArray.add(response.getString("Pesel"));
                            valuesArray.add(response.getString("Email"));
                            JSONObject address = response.getJSONObject("Address");
                            valuesArray.add(address.getString("PostalCode"));
                            valuesArray.add(address.getString("StreetName"));
                            valuesArray.add(address.getString("FlatNumber"));
                            valuesArray.add(address.getString("BuildingNumber"));
                            valuesArray.add(address.getString("CityId"));

                            AccountAdapter accountAdapter = new AccountAdapter(this, fieldsArray, valuesArray, citiesArray, accountEditButton);
                            rvAccount.setAdapter(accountAdapter);
                            rvAccount.setLayoutManager(new LinearLayoutManager(this));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> Log.i("Error", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(arrayRequest1);

        //get cities
        citiesArray = new ArrayList<>();
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

        accountEditButton.setOnClickListener(view -> POSTRequest());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void POSTRequest() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonBody = new JSONObject();
        JSONObject jsonAddress = new JSONObject();

        //filling json array with gathered data from RegisterAdapter
        try {
            jsonBody.put("Id", 1002);
            jsonBody.put("FirstName", valuesArray.get(0));
            jsonBody.put("Surname", valuesArray.get(1));
            jsonBody.put("PhoneNumber", valuesArray.get(2));
//            jsonBody.put("Pesel", valuesArray.get(3));
            jsonBody.put("Email", valuesArray.get(4));
            jsonAddress.put("PostalCode", valuesArray.get(5));
            jsonAddress.put("StreetName", valuesArray.get(6));
            jsonAddress.put("FlatNumber", valuesArray.get(7));
            jsonAddress.put("BuildingNumber", valuesArray.get(8));
            jsonAddress.put("CityId", valuesArray.get(9));
            jsonBody.put("AddressDto", jsonAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "https://10.0.2.2:7277/api/account/edit/", response -> Toast.makeText(AccountActivity.this, "Konto zostało zaktualizowane", Toast.LENGTH_LONG).show(), error -> {
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
                        new AlertDialog.Builder(AccountActivity.this).setTitle("Błąd aktualizacji").setMessage(alertMessage.toString())
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

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

        };
        queue.add(stringRequest);
    }
}