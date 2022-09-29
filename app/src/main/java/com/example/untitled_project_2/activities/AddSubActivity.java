package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.SubscriptionAdapter;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddSubActivity extends AppCompatActivity {
    private SSLRules ssl = new SSLRules();
    private ArrayList<String> citiesArray;
    private ArrayList<String> vaccinesArray;
    private String VaccineUrl = "https://10.0.2.2:7277/api/vaccine/all/";
    private String CityUrl = "https://10.0.2.2:7277/api/account/getAllCities/";
    private Spinner vaccineSpinner;
    private Spinner citySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        //init menu
        setTitle("Dodaj Subskrypcję");

        //ssl disable
        ssl.SSlDisable();

        //get Vaccines
        vaccinesArray = new ArrayList<String>();
        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest1 = new JsonArrayRequest(Request.Method.GET, VaccineUrl,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        });
        //get cities
        citiesArray = new ArrayList<String>();
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest2 = new JsonArrayRequest(Request.Method.GET, CityUrl,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        });

        queue1.add(arrayRequest1);
        queue2.add(arrayRequest2);

        vaccineSpinner = findViewById(R.id.vaccineSpinner);
        citySpinner = findViewById(R.id.CitySpinner);

        ArrayAdapter<String> adapterVaccine = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, vaccinesArray);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,citiesArray);

        vaccineSpinner.setAdapter(adapterVaccine);
        citySpinner.setAdapter(adapterCity);

        vaccineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}