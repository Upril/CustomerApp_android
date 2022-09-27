package com.example.untitled_project_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {
    EditText EmailText;
    EditText PasswordText;
    Button LoginButton;
    Button GotoRegisterButton;
    Boolean emailValid = false,passwordValid = false;
    String URLline = "https://10.0.2.2:7277/api/account/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailText = findViewById(R.id.LoginEmail);
        PasswordText = findViewById(R.id.TextPassword);
        LoginButton = findViewById(R.id.LoginButton);
        GotoRegisterButton = findViewById(R.id.GotoRegisterButton);
        String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regexPattern);

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

        EmailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    emailValid = false;
                    String value = EmailText.getText().toString();
                    Matcher matcher = pattern.matcher(value);
                    if(!matcher.matches()){
                        EmailText.setError("Podany email jest nieprawiłdowy");
                    }
                    else{
                        emailValid = true;
                    }
                }
            }
        });
        PasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    passwordValid = false;
                    String value = PasswordText.getText().toString();
                    if (value.length() <= 3) {
                        PasswordText.setError("Podane hasło jest zbyt krotkie");
                    }
                    else{
                        passwordValid = true;
                    }
                }
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(emailValid && passwordValid) {
                    //api login tutaj

                    try {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = URLline;
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", EmailText.getText().toString());
                        jsonBody.put("password", PasswordText.getText().toString());
                        final String mRequestBody = jsonBody.toString();
                        EmailText.setText(mRequestBody);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

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
                else {
                    Toast.makeText(getApplicationContext(),"Niepoprawny Email lub haslo",Toast.LENGTH_LONG).show();

                    Toast.makeText(getApplicationContext(),emailValid+" "+passwordValid,Toast.LENGTH_LONG).show();
                }
            }


        });
        GotoRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });
    }
}

