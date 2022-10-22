package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
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
import android.widget.Toolbar;

import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.VaccinesAdapter;
import com.example.untitled_project_2.networking.SSLRules;
import com.google.android.material.navigation.NavigationView;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Vaccines extends AppCompatActivity {

    private SSLRules ssl = new SSLRules();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private RecyclerView rvVaccines;
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.menu_open,R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        //wez stuff z intentu getStringExtra("tag")

        //ssl disable
        ssl.SSlDisable();

        rvVaccines = (RecyclerView) findViewById(R.id.vaccinesListRv);
        //check czy z długimi listami sie nie pierdoli
        rvVaccines.getRecycledViewPool().setMaxRecycledViews(0,15);
        rvVaccines.setItemViewCacheSize(15);

//        VaccinesAdapter vaccinesAdapter = new VaccinesAdapter(this);
//        rvVaccines.setAdapter(vaccinesAdapter);
//        rvVaccines.setLayoutManager(new LinearLayoutManager(this));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.menuAccount:
                        Log.i("Account clicked","Account was clicked");
                        //mActivityLauncher.launch();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuSubscriptions:
                        Log.i("Vaccines clicked","Vaccines was clicked");
                        //startVaccines();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuMyVaccines:
                        Log.i("MyVaccines clicked","MyVaccines was clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuSettings:
                        Log.i("Settings clicked","Settings was clicked");
                        //startSettings();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuLogout:
                        Log.i("Logout clicked","Logout was clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return false;
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