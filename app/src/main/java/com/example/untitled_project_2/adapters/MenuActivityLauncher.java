package com.example.untitled_project_2.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.untitled_project_2.MainActivity;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.activities.AccountActivity;
import com.example.untitled_project_2.activities.MyVaccinesActivity;
import com.example.untitled_project_2.activities.SubscriptionActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuActivityLauncher {
    private final Context mContext;
    private final ActivityResultLauncher mActivityLauncher;
    private String mToken;
    public MenuActivityLauncher(Context context, ActivityResultLauncher activityResultLauncher, String token){
        mContext = context;
        mActivityLauncher = activityResultLauncher;
        mToken = token;
    }
    public MenuActivityLauncher(Context context, ActivityResultLauncher activityResultLauncher){
        mContext = context;
        mActivityLauncher = activityResultLauncher;
    }
    public void init(NavigationView navigationView, DrawerLayout drawerLayout){
        if (mToken == null) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.menuSubscriptions).setVisible(false);
            menu.findItem(R.id.menuMyVaccines).setVisible(false);
            menu.findItem(R.id.menuHome).setVisible(false);
            menu.setGroupVisible(R.id.menuAccountGroup, false);

            menu.findItem(R.id.menuNoUserLogin).setVisible(true);
            menu.findItem(R.id.menuNoUserRegister).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuAccount:
                    Log.i("Account clicked", "Account was clicked");
                    startActivity(AccountActivity.class);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.menuSubscriptions:
                    Log.i("Vaccines clicked", "Vaccines was clicked");
                    startActivity(SubscriptionActivity.class);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.menuMyVaccines:
                    Log.i("MyVaccines clicked", "MyVaccines was clicked");
                    startActivity(MyVaccinesActivity.class);
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
        });
    }
    public void startActivity(Class c) {
        Intent intent = new Intent(mContext, c);
        intent.putExtra("token",mToken);
        mActivityLauncher.launch(intent);
    }
    public void returnHome() {
        Intent intent = new Intent(mContext , MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle(2);
        bundle.putString("token",mToken);
        bundle.putString("ActivityResult","loginOK");
        mContext.startActivity(intent);
    }
}
