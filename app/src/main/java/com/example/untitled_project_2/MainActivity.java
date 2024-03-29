package com.example.untitled_project_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import com.example.untitled_project_2.activities.LoginActivity;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.Vaccine;
import com.example.untitled_project_2.adapters.VaccinesAdapter;
import com.example.untitled_project_2.networking.JWTUtils;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MenuActivityLauncher menuActivityLauncher;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Boolean wentThroughLogin = false;
    String token;
    SSLRules ssl = new SSLRules();
    ArrayList<Vaccine> vaccines = new ArrayList<>();
    private ArrayList<String> citiesArray;
    private ArrayList<String> vaccinesArray;
    private final String VaccineUrl = "https://10.0.2.2:7277/api/vaccine/all/";
    private final String VaccinationUrl = "https://10.0.2.2:7277/api/vaccination/getAllFreeDates/";
    private Spinner citySpinner;
    private Spinner vaccineSpinner;
    private Integer citySelected;
    private Integer vaccineSelected;
    private RecyclerView rvVaccines;
    private VaccinesAdapter vaccinesAdapter;


    public static ActivityResultLauncher<Intent> mActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Dostępne Terminy Szczepień");

        //init menu
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Activity recovery
        if(savedInstanceState != null && savedInstanceState.containsKey("loggedIn")) {
            wentThroughLogin = savedInstanceState.getBoolean("loggedIn");
            token = savedInstanceState.getString("token");
        }

        //ssl disable
        ssl.SSlDisable();

        Intent intent1 = getIntent();
        Bundle ex = intent1.getExtras();
        if (ex != null)
        {
            String res = ex.getString("ActivityResult");
            if ("noLogin".equals(res)) {
                wentThroughLogin = true;
                token = null;
                menuActivityLauncher = new MenuActivityLauncher(MainActivity.this, mActivityLauncher);
                menuActivityLauncher.init(navigationView, drawerLayout);
                getDateRecycler(VaccinationUrl);
            } else if("loginOK".equals(res)){
                token = ex.getString("token");
                wentThroughLogin = true;
                menuActivityLauncher = new MenuActivityLauncher(MainActivity.this, mActivityLauncher, token);
                menuActivityLauncher.init(navigationView,drawerLayout);
                Log.i("Main JWT", "Received");
                try {
                    String[] data = JWTUtils.decode(token);
                    Log.e("Data from decode", data[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDateRecycler(VaccinationUrl);
            }
        }

        //if i need to resppond to results
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});

        //redirect to login page
        if(!wentThroughLogin) {
            //Intent intent = new Intent(this, AddSubActivity.class);
            Intent intent = new Intent(this,LoginActivity.class);
            Log.e("Intent",intent.toString());
            mActivityLauncher.launch(intent);
        }



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle outstate){
        outstate.putString("token",token);
        outstate.putBoolean("loggedIn", wentThroughLogin);
        super.onSaveInstanceState(outstate);
    }
    public void getDateRecycler(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response != null && response.length() > 0){
                        for (int i = 0; i<response.length();i++){
                            try{
                                Vaccine appointmentObject = new Vaccine();
                                JSONObject appointmentJson = response.getJSONObject(i);
                                appointmentObject.setId(appointmentJson.getString("Id"));

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = sdf.parse(appointmentJson.getString("Date"));
                                assert d != null;
                                appointmentObject.setDate(output.format(d));

                                JSONObject facility = appointmentJson.getJSONObject("MedicalFacility");
                                appointmentObject.setFacilityName(facility.getString("Name"));

                                JSONObject address = facility.getJSONObject("Address");
                                JSONObject addressCity = address.getJSONObject("City");
                                appointmentObject.setAddress(address.getString("StreetName")+" "+address.getString("BuildingNumber")+", "+addressCity.getString("Name"));

                                JSONObject vaccine = appointmentJson.getJSONObject("Vaccine");
                                appointmentObject.setVaccineName(vaccine.getString("Name"));

                                vaccines.add(appointmentObject);

                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        dataInit();

                        rvVaccines = findViewById(R.id.rvVaccines);
                        //set do długości response
                        rvVaccines.getRecycledViewPool().setMaxRecycledViews(0,15);
                        rvVaccines.setItemViewCacheSize(15);
                        if (token == null){
                            vaccinesAdapter = new VaccinesAdapter(MainActivity.this, vaccines, response.length());
                        } else {
                            vaccinesAdapter = new VaccinesAdapter(MainActivity.this,menuActivityLauncher, vaccines, response.length(), token);
                        }
                        rvVaccines.setAdapter(vaccinesAdapter);
                        rvVaccines.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }, error -> Log.e("VolleyError",error.toString()));
        requestQueue.add(arrayRequest);
    }
    private void dataInit(){
        setVaccinesArray();
    }
    private void setVaccinesArray(){
        //get Vaccines
        vaccinesArray = new ArrayList<>();
        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest1 = new JsonArrayRequest(Request.Method.GET, VaccineUrl,null,
                response -> {
                    if (response != null && response.length() > 0){
                        for(int i = 0; i<response.length();i++){
                            try {
                                JSONObject sub = response.getJSONObject(i);
                                vaccinesArray.add(sub.getString("Name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    setCitiesArray();
                }, error -> Log.i("Error",error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue1.add(arrayRequest1);
    }
    private void setCitiesArray(){
        //get cities
        citiesArray = new ArrayList<>();
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        String cityUrl = "https://10.0.2.2:7277/api/account/getAllCities/";
        JsonArrayRequest arrayRequest2 = new JsonArrayRequest(Request.Method.GET, cityUrl,null,
                response -> {
                    if (response != null && response.length() > 0){
                        for(int i = 0; i<response.length();i++){
                            try {
                                JSONObject sub = response.getJSONObject(i);
                                citiesArray.add(sub.getString("Name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    ControlsInit();
                }, error -> Log.i("Error",error.toString()));
        queue2.add(arrayRequest2);
    }
    private void ControlsInit(){
        vaccineSpinner = findViewById(R.id.vaccinationVaccineSpinner);
        citySpinner = findViewById(R.id.vaccinationCitySpinner);
        Button clearFilterButton = findViewById(R.id.ClearFiltersButton);


        ArrayAdapter<String> adapterVaccine = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, vaccinesArray);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, citiesArray);

        vaccineSpinner.setAdapter(adapterVaccine);
        citySpinner.setAdapter(adapterCity);

        vaccineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vaccineSelected = vaccineSpinner.getSelectedItemPosition();
                Log.e("Spinner","VacChanged");
                updateRecycler("https://10.0.2.2:7277/api/vaccination/getAllFreeDatesByVaccine?vaccineId="+(vaccineSelected+1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                citySelected = citySpinner.getSelectedItemPosition();
                Log.e("Spinner","CityChanged");
                updateRecycler("https://10.0.2.2:7277/api/vaccination/getAllFreeDatesByCity?cityId="+(citySelected+1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        clearFilterButton.setOnClickListener(view -> updateRecycler(VaccinationUrl));
    }
    private void updateRecycler(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null,
                response -> {
                    if (response != null && response.length() > 0){
                        vaccines.clear();
                        for (int i = 0; i<response.length();i++){
                            try{
                                Vaccine vaccine = loadVaccineFromJsonObject(response, i);
                                vaccines.add(vaccine);
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    vaccinesAdapter.setItemCount(vaccines.size());
                    vaccinesAdapter.notifyDataSetChanged();
                }, error -> Log.e("VolleyError",error.toString()));
        requestQueue.add(arrayRequest);
    }
    private Vaccine loadVaccineFromJsonObject(JSONArray response, int i) throws JSONException, ParseException {
        Vaccine appointmentObject = new Vaccine();
        JSONObject appointmentJson = response.getJSONObject(i);
        appointmentObject.setId(appointmentJson.getString("Id"));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse(appointmentJson.getString("Date"));
        assert d != null;
        appointmentObject.setDate(output.format(d));

        JSONObject facility = appointmentJson.getJSONObject("MedicalFacility");
        appointmentObject.setFacilityName(facility.getString("Name"));

        JSONObject address = facility.getJSONObject("Address");
        JSONObject addressCity = address.getJSONObject("City");
        appointmentObject.setAddress(address.getString("StreetName")+
                " "+address.getString("BuildingNumber")+", "
                +addressCity.getString("Name"));

        JSONObject vaccine = appointmentJson.getJSONObject("Vaccine");
        appointmentObject.setVaccineName(vaccine.getString("Name"));

        return appointmentObject;
    }
    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }
}