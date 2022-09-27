package com.example.untitled_project_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;
import com.example.untitled_project_2.adapters.RegisterAdapter;
import com.example.untitled_project_2.networking.SSLRules;

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
    private String URLline = "https://10.0.2.2:7277/api/account/register/";
    private ArrayList<String> fieldsArray;
    private ArrayList<String> valuesArray;

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
        rvRegister.getRecycledViewPool().setMaxRecycledViews(0,15);
        rvRegister.setItemViewCacheSize(15);

        RegisterAdapter registerAdapter = new RegisterAdapter(this, fieldsArray, valuesArray);
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
                            //smthn smthn

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG_RESPONSE", error.toString());
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
//                            @Override
//                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                                String responseString = "";
//                                if (response != null) {
//                                    responseString = String.valueOf(response.statusCode);
//                                }
//                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                            }
                    };

                    queue.add(stringRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}