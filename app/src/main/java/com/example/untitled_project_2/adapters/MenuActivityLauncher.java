package com.example.untitled_project_2.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.untitled_project_2.MainActivity;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.activities.SettingsActivity;
import com.example.untitled_project_2.activities.SubscriptionActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuActivityLauncher {
    private Context mContext;
    private ActivityResultLauncher<Intent> mActivityLauncher;
    public MenuActivityLauncher(Context context){
        mContext = context;
    }
    public void init(NavigationView navigationView, DrawerLayout drawerLayout, Context context, ActivityResultLauncher<Intent> mActivityLauncher){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuAccount:
                        Log.i("Account clicked", "Account was clicked");
                        //mActivityLauncher.launch();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuSubscriptions:
                        Log.i("Vaccines clicked", "Vaccines was clicked");
                        startActivity(SubscriptionActivity.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuMyVaccines:
                        Log.i("MyVaccines clicked", "MyVaccines was clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuSettings:
                        Log.i("Settings clicked", "Settings was clicked");
                        startActivity(SettingsActivity.class);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuLogout:
                        Log.i("Logout clicked", "Logout was clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.menuHome:
                        Log.i("Home clicked", "Home was clicked");
                        returnHome();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return false;
            }
        });
    }
    public void startActivity(Class c) {
        Intent intent = new Intent(mContext, c);
        mActivityLauncher.launch(intent);
    }
    public void startActivity(Class c, String JWT){
        Intent intent = new Intent(mContext, c);
        intent.putExtra("token",JWT);
        mActivityLauncher.launch(intent);
    }
    public void returnHome() {
        Intent intent = new Intent(mContext , MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
    public void returnHome(String JWT) {
        Intent intent = new Intent(mContext , MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("token",JWT);
        mContext.startActivity(intent);
    }

}
