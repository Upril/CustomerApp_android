package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.SubscriptionAdapter;
import com.example.untitled_project_2.networking.JWTUtils;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSubActivity extends AppCompatActivity {
    private SSLRules ssl = new SSLRules();
    private ArrayList<String> citiesArray;
    private ArrayList<String> vaccinesArray;
    private String VaccineUrl = "https://10.0.2.2:7277/api/vaccine/all/";
    private String CityUrl = "https://10.0.2.2:7277/api/account/getAllCities/";
    private Spinner vaccineSpinner;
    private Spinner citySpinner;
    private Integer citySelected;
    private Integer vaccineSelected;

    private String token;
    private Button signupButton;
    private Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        signupButton = findViewById(R.id.ConfirmSubButton);
        cancelButton = findViewById(R.id.CancelSubButton);

        //init menu
        setTitle("Dodaj Subskrypcję");

        //ssl disable
        ssl.SSlDisable();

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        dataInit();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSub();
            }
        });
    }


    private void dataInit(){
        setVaccinesArray();
    }
    private void setVaccinesArray(){
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
                        setCitiesArray();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        }) {
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
                        spinnerInit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        });
        queue2.add(arrayRequest2);
    }
    private void spinnerInit(){
        vaccineSpinner = findViewById(R.id.vaccineSpinner);
        citySpinner = findViewById(R.id.CitySpinner);

        ArrayAdapter<String> adapterVaccine = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, vaccinesArray);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,citiesArray);

        vaccineSpinner.setAdapter(adapterVaccine);
        citySpinner.setAdapter(adapterCity);

        vaccineSpinner.setSelection(0);
        citySpinner.setSelection(0);
        vaccineSelected = vaccineSpinner.getSelectedItemPosition();
        citySelected = citySpinner.getSelectedItemPosition();

        vaccineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vaccineSelected = vaccineSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                citySelected = citySpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void sendSub(){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cityId",citySelected+1);

            String[] data = new String[]{};
            try {
                data = JWTUtils.decode(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonBody.put("userId",data[0]);

            jsonBody.put("vaccineId",vaccineSelected+1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = jsonBody.toString();

        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://10.0.2.2:7277/api/subscriptions", response -> {
            Log.e("Vaccination","Added");
            Toast.makeText(getApplicationContext(), "Subskrypcja aktywowana!", Toast.LENGTH_LONG).show();
            finish();
        }, error -> Log.i("Error", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
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
    }
}