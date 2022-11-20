package com.example.untitled_project_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.untitled_project_2.MainActivity;
import com.example.untitled_project_2.R;

import com.example.untitled_project_2.adapters.MenuActivityLauncher;
import com.example.untitled_project_2.networking.SSLRules;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private final SSLRules ssl = new SSLRules();
    private EditText EmailText;
    private EditText PasswordText;
    private TextView continueText;
    private Boolean emailValid = false, passwordValid = false;
    private final String URLline = "https://10.0.2.2:7277/api/account/login/";
    public static ActivityResultLauncher<Intent> mActivityLauncher;
    public static MenuActivityLauncher menuActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailText = findViewById(R.id.LoginEmail);
        PasswordText = findViewById(R.id.TextPassword);

        Button loginButton = findViewById(R.id.LoginButton);
        Button gotoRegisterButton = findViewById(R.id.GotoRegisterButton);
        String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regexPattern);

        //ssl disable
        ssl.SSlDisable();

        mActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
        menuActivityLauncher = new MenuActivityLauncher(LoginActivity.this, mActivityLauncher);

        EmailText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                emailValid = false;
                String value = EmailText.getText().toString();
                Matcher matcher = pattern.matcher(value);
                if (!matcher.matches()) {
                    EmailText.setError("Podany email jest nieprawiłdowy");
                } else {
                    emailValid = true;
                }
            }
        });
        PasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordValid = false;
                String value = PasswordText.getText().toString();
                if (value.length() < 6) {
                    PasswordText.setError("Podane hasło jest zbyt krotkie");
                } else {
                    passwordValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loginButton.setOnClickListener(view -> {
            if (emailValid && passwordValid) {
                login();
            } else {
                Toast.makeText(getApplicationContext(), "Niepoprawny Email lub haslo", Toast.LENGTH_LONG).show();
            }
        });

        gotoRegisterButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(myIntent);
        });
    }

    private void saveJWT(String JWT){
        Bundle bundle = new Bundle(2);
        bundle.putString("token", JWT);
        bundle.putString("ActivityResult", "loginOK");
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        mActivityLauncher.launch(intent);
    }
    private void login(){
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", EmailText.getText().toString());
            jsonBody.put("password", PasswordText.getText().toString());
            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    URLline, LoginActivity.this::saveJWT, error -> {
                try {
                    String errorResponse = new String(error.networkResponse.data,
                            HttpHeaderParser.parseCharset(error.networkResponse.headers, "utf-8"));
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Błąd logowania").setMessage(errorResponse)
                            .setPositiveButton("OK", null).
                            setIcon(R.drawable.ic_stop_black).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() {
                    return mRequestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void noLogin(View v){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle(1);
        bundle.putString("ActivityResult", "noLogin");
        intent.putExtras(bundle);
        setResult(RESULT_FIRST_USER, intent);
        mActivityLauncher.launch(intent);
    }
}

