package com.example.untitled_project_2.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.Vaccine;
import com.example.untitled_project_2.adapters.myVaccinesAdapter;
import com.example.untitled_project_2.networking.JWTUtils;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyVaccinesActivity extends AppCompatActivity {
    ArrayList<Vaccine> vaccines = new ArrayList<>();
    private String token;
    private Integer userId;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MenuActivityLauncher menuActivityLauncher;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SSLRules ssl = new SSLRules();
    public static ActivityResultLauncher<Intent> mActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vaccines);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        //wydobadz userid z tokena
        try {
            String[] data = JWTUtils.decode(token);
            userId = Integer.parseInt(data[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //init menu
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });
        menuActivityLauncher = new MenuActivityLauncher(MyVaccinesActivity.this, mActivityLauncher, token);
        menuActivityLauncher.init(navigationView, drawerLayout);

        //ssl disable
        ssl.SSlDisable();

        getVaccinesRecycler();


    }

    public void getVaccinesRecycler() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, "https://10.0.2.2:7277/api/vaccination/getDatesByUserId?userId=" + userId, null,
                response -> {
                    Log.e("Response", response.toString());
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Vaccine appointmentObject = new Vaccine();
                                JSONObject appointmentJson = response.getJSONObject(i);
                                appointmentObject.setId(appointmentJson.getString("Id"));

                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date d = sdf.parse(appointmentJson.getString("Date"));
                                assert d != null;
                                appointmentObject.setDate(output.format(d));

                                JSONObject facility = appointmentJson.getJSONObject("MedicalFacility");
                                appointmentObject.setFacilityName(facility.getString("Name"));

                                JSONObject address = facility.getJSONObject("Address");
                                JSONObject addressCity = address.getJSONObject("City");
                                appointmentObject.setAddress(address.getString("StreetName") + " " + address.getString("BuildingNumber") + ", " + addressCity.getString("Name"));

                                JSONObject vaccine = appointmentJson.getJSONObject("Vaccine");
                                appointmentObject.setVaccineName(vaccine.getString("Name"));

                                vaccines.add(appointmentObject);

                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        RecyclerView rvVaccinations = findViewById(R.id.myVaccinesListRv);
                        //set do długości response
                        rvVaccinations.getRecycledViewPool().setMaxRecycledViews(0, 15);
                        rvVaccinations.setItemViewCacheSize(15);
                        myVaccinesAdapter myVaccinesAdapter = new myVaccinesAdapter(MyVaccinesActivity.this, vaccines, response.length(), token);
                        rvVaccinations.setAdapter(myVaccinesAdapter);
                        rvVaccinations.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };


        requestQueue.add(arrayRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}