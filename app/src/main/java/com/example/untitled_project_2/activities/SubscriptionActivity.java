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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import java.util.ArrayList;

public class SubscriptionActivity extends AppCompatActivity {

    private final SSLRules ssl = new SSLRules();
    //decode JWT
    private Integer userId;
    private ArrayList<String> cities;
    private ArrayList<String> vaccines;
    private ArrayList<String> subIds;
    private RecyclerView rvSubs;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MenuActivityLauncher menuActivityLauncher;
    public static ActivityResultLauncher<Intent> mActivityLauncher;
    private int length;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        //init menu
        setTitle("Twoje Subskrypcje");
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        subIds = new ArrayList<String>();
        vaccines = new ArrayList<String>();
        cities = new ArrayList<String>();

        //intent get user id
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        //wydobadz userid z tokena
        try {
            String[] data = JWTUtils.decode(token);
            userId = Integer.parseInt(data[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                //zmien na string w values
                String resultString = bundle.getString("ActivityResult");
                switch (resultString) {

                }
            }
            if (result.getResultCode() == RESULT_CANCELED) {
                Toast.makeText(this, "Anulowano", Toast.LENGTH_SHORT).show();
            }
        });
        menuActivityLauncher = new MenuActivityLauncher(SubscriptionActivity.this,mActivityLauncher,token);
        menuActivityLauncher.init(navigationView,drawerLayout);

        //ssl disable
        ssl.SSlDisable();

        //request user subs
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://10.0.2.2:7277/api/subscriptions/byUser/"+userId+"/";
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null && response.length() > 0){
                            length = response.length();

                            for(int i = 0; i<response.length();i++){
                                try {
                                    JSONObject sub = response.getJSONObject(i);
                                    subIds.add(sub.getString("Id"));

                                    JSONObject city = sub.getJSONObject("City");
                                    cities.add(city.getString("Name"));

                                    JSONObject vaccine = sub.getJSONObject("Vaccine");
                                    vaccines.add(vaccine.getString("Name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            rvSubs = (RecyclerView)findViewById(R.id.subsListRv);
                            rvSubs.getRecycledViewPool().setMaxRecycledViews(0, response.length());
                            rvSubs.setItemViewCacheSize(response.length());

                            SubscriptionAdapter subAdapter = new SubscriptionAdapter(SubscriptionActivity.this, cities, vaccines, subIds, length);
                            rvSubs.setAdapter(subAdapter);
                            rvSubs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        });
        queue.add(arrayRequest);

        Button addSubButton = findViewById(R.id.AddSubsButton);
        addSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SubscriptionActivity.this, AddSubActivity.class);
                SubscriptionActivity.this.startActivity(myIntent);
            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}