package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.AccountAdapter;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.adapters.RegisterAdapter;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
        MenuActivityLauncher menuActivityLauncher = new MenuActivityLauncher(AccountActivity.this,mActivityLauncher,token);
        menuActivityLauncher.init(navigationView,drawerLayout);

        //ssl disable
        ssl.SSlDisable();

        RecyclerView rvAccount = (RecyclerView) findViewById(R.id.AccountRv);
        Button accountEditButton = (Button) findViewById(R.id.AccountEditButton);
        Button cancelButton = (Button) findViewById(R.id.AccountEditCancelButton);
        rvAccount.getRecycledViewPool().setMaxRecycledViews(0, 15);
        rvAccount.setItemViewCacheSize(15);

        //get account info
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String infoUrl = "https://10.0.2.2:7277/api/account/getInfo?userId="+userId;
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}