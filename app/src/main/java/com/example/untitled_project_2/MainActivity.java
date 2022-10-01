package com.example.untitled_project_2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.untitled_project_2.R;
import com.example.untitled_project_2.activities.LoginActivity;
import com.example.untitled_project_2.activities.SettingsActivity;
import com.example.untitled_project_2.activities.SubscriptionActivity;
import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MenuActivityLauncher menuActivityLauncher;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Boolean loggedIn = false;
    String token;

    public static ActivityResultLauncher<Intent> mActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init menu
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuActivityLauncher = new MenuActivityLauncher(MainActivity.this);
        menuActivityLauncher.init(navigationView, drawerLayout,MainActivity.this, mActivityLauncher);

        //if i need to resppond to results
        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                //zmien na string w values
                String resultString = bundle.getString("ActivityResult");
                switch (resultString) {
                    case "loginOK":
                        token = bundle.getString("token");
                        loggedIn = true;

                        //Toast.makeText(this, "Odebrano wiadomość, Witaj "+userName,Toast.LENGTH_SHORT).show();
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
            menuActivityLauncher.startActivity(LoginActivity.class);
        }

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}