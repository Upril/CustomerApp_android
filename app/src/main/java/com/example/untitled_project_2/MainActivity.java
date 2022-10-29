package com.example.untitled_project_2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.activities.LoginActivity;
import com.example.untitled_project_2.activities.SettingsActivity;
import com.example.untitled_project_2.activities.SubscriptionActivity;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.Vaccine;
import com.example.untitled_project_2.adapters.VaccinesAdapter;
import com.example.untitled_project_2.networking.JWTUtils;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MenuActivityLauncher menuActivityLauncher;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Boolean loggedIn = false;
    String token;
    SSLRules ssl = new SSLRules();
    ArrayList<Vaccine> vaccines = new ArrayList<>();

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Activity recovery
        if(savedInstanceState != null && savedInstanceState.containsKey("loggedIn")) {
            loggedIn = savedInstanceState.getBoolean("loggedIn");
            token = savedInstanceState.getString("token");
        }

        //ssl disable
        ssl.SSlDisable();

        //if i need to resppond to results
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                //zmien na string w values
                String resultString = bundle.getString("ActivityResult");
                switch (resultString) {
                    case "loginOK":
                        token = bundle.getString("token");
                        loggedIn = true;
                        menuActivityLauncher = new MenuActivityLauncher(MainActivity.this,mActivityLauncher,token);
                        menuActivityLauncher.init(navigationView, drawerLayout);
                        Log.i("Main JWT","Received");
                        try {
                            String[] data = JWTUtils.decode(token);
                            Log.e("Data from decode",data[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getDateRecycler();
                        break;
                }
            }
            if (result.getResultCode() == RESULT_CANCELED) {
                Toast.makeText(this, "Anulowano", Toast.LENGTH_SHORT).show();
            }
            //do something with data
        });

        //redirect to login page
        if(!loggedIn) {
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
        outstate.putBoolean("loggedIn",loggedIn);
        super.onSaveInstanceState(outstate);
    }
    public void getDateRecycler(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://10.0.2.2:7277/api/vaccination/getAllFreeDates/";
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null && response.length() > 0){
                            for (int i = 0; i<response.length();i++){
                                try{
                                    Vaccine appointmentObject = new Vaccine();
                                    JSONObject appointmentJson = response.getJSONObject(i);
                                    appointmentObject.setId(appointmentJson.getString("Id"));

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date d = sdf.parse(appointmentJson.getString("Date"));
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



                            RecyclerView rvVaccines = findViewById(R.id.rvVaccines);
                            //set do długości response
                            rvVaccines.getRecycledViewPool().setMaxRecycledViews(0,15);
                            rvVaccines.setItemViewCacheSize(15);
                            VaccinesAdapter vaccinesAdapter = new VaccinesAdapter(MainActivity.this, vaccines, response.length(), token);
                            rvVaccines.setAdapter(vaccinesAdapter);
                            rvVaccines.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError",error.toString());
            }
        });
        requestQueue.add(arrayRequest);
    }

}