package com.example.untitled_project_2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.RegisterAdapter;
import com.example.untitled_project_2.networking.SSLRules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RegisterActivity extends AppCompatActivity {
    private SSLRules ssl = new SSLRules();
    private RecyclerView rvRegister;
    private Button registerButton;
    private Button cancelButton;
    private String URLline = "https://10.0.2.2:7277/api/account/register/";
    private String CityUrl = "https://10.0.2.2:7277/api/account/getAllCities/";
    private ArrayList<String> fieldsArray;
    private ArrayList<String> valuesArray;
    private ArrayList<String> citiesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fieldsArray = new ArrayList<String>();
        fieldsArray.add("Imie");
        fieldsArray.add("Nazwisko");
        fieldsArray.add("Telefon");
        fieldsArray.add("Pesel");
        fieldsArray.add("Adres e-mail");
        fieldsArray.add("Hasło");
        fieldsArray.add("Potwierdź hasło");
        fieldsArray.add("Kod pocztowy");
        fieldsArray.add("Ulica");
        fieldsArray.add("Numer mieszkania");
        fieldsArray.add("Numer budynku");
        fieldsArray.add("Miasto");

        valuesArray = new ArrayList<String>(12);

        rvRegister = (RecyclerView)findViewById(R.id.RegisterRv);
        registerButton = (Button)findViewById(R.id.Registerbutton);
        cancelButton = (Button)findViewById(R.id.RegisterCancelButton);
        rvRegister.getRecycledViewPool().setMaxRecycledViews(0,15);
        rvRegister.setItemViewCacheSize(15);

        //get cities
        citiesArray = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, CityUrl,null,
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

        queue.add(arrayRequest);

        RegisterAdapter registerAdapter = new RegisterAdapter(this, fieldsArray, valuesArray,citiesArray,registerButton);
        rvRegister.setAdapter(registerAdapter);
        rvRegister.setLayoutManager(new LinearLayoutManager(this));



        //ssl disable
        ssl.SSlDisable();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    JSONObject jsonBody = new JSONObject();
                    JSONObject jsonAddress = new JSONObject();
                    //filling json array with gathered data from RegisterAdapter
                    jsonBody.put("FirstName",valuesArray.get(0));
                    jsonBody.put("Surname",valuesArray.get(1));
                    jsonBody.put("PhoneNumber",valuesArray.get(2));
                    jsonBody.put("Pesel",valuesArray.get(3));
                    jsonBody.put("Email",valuesArray.get(4));
                    jsonBody.put("Password",valuesArray.get(5));
                    jsonBody.put("ConfirmPassword",valuesArray.get(6));
                    jsonAddress.put("PostalCode",valuesArray.get(7));
                    jsonAddress.put("StreetName",valuesArray.get(8));
                    jsonAddress.put("FlatNumber",valuesArray.get(9));
                    jsonAddress.put("BuildingNumber",valuesArray.get(10));
                    jsonAddress.put("CityId",valuesArray.get(11));
                    jsonBody.put("AddressDto",jsonAddress);

                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(RegisterActivity.this,"Konto zostało utworzone, teraz należy się zalogować",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse response = error.networkResponse;
                            String json = "";
                            if(response != null && response.data != null) {
                                switch (response.statusCode) {
                                    case 400:
                                        json = new String(response.data);
                                        try {
                                            JSONObject errorJson = new JSONObject(json);
                                            JSONObject errors = errorJson.getJSONObject("errors");
                                            ArrayList<JSONArray> errorMessages = new ArrayList<JSONArray>(12);
                                            String[] keys = {"Email","Pesel","Surname","Password","FirstName","PhoneNumber","AddressDto.FlatNumber","AddressDto.PostalCode","AddressDto.StreetName","AddressDto.BuildingNumber"};
                                            String alertMessage = "";
                                            for (String key:keys){
                                                if (errors.has(key)){
                                                    errorMessages.add(errors.getJSONArray(key));
                                                }
                                            }
                                            for(JSONArray message:errorMessages){
                                                Log.e("RegisterError",message.getString(0));
                                                alertMessage += message.getString(0) + "\n\n";


                                            }
                                            new AlertDialog.Builder(RegisterActivity.this).setTitle("Błąd rejestracji").setMessage(alertMessage)
                                                    .setPositiveButton("OK", null).setIcon(R.drawable.ic_stop_black).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        break;
                                }
                            }
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                                return null;
                            }
                        }
                    };

                    queue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}