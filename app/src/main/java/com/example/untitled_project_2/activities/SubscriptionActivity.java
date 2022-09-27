package com.example.untitled_project_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.SubscriptionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SubscriptionActivity extends AppCompatActivity {
    private String userId;
    private ArrayList<String> cities;
    private ArrayList<String> vaccines;
    private ArrayList<String> subIds;
    private RecyclerView rvSubs;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        TextView test = (TextView)findViewById(R.id.testView);

        subIds = new ArrayList<String>();
        vaccines = new ArrayList<String>();
        cities = new ArrayList<String>();

        //intent get user id
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        //ssl disable
        try {
            TrustManager[] victimizedManager = new TrustManager[]{

                    new X509TrustManager() {

                        public X509Certificate[] getAcceptedIssuers() {

                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];

                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, victimizedManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }




        //request user subs
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://10.0.2.2:7277/api/subscriptions/byUser/2/";
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
                            rvSubs.getRecycledViewPool().setMaxRecycledViews(0, 10);
                            rvSubs.setItemViewCacheSize(10);

                            SubscriptionAdapter subAdapter = new SubscriptionAdapter(SubscriptionActivity.this, cities, vaccines, subIds, length);
                            rvSubs.setAdapter(subAdapter);
                            rvSubs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                            test.setText(cities.toString());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error",error.toString());
            }
        });
        queue.add(arrayRequest);




    }
}