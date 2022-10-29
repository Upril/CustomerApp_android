package com.example.untitled_project_2.networking;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JWTUtils {
    public static String[] decode(String JWT) throws Exception{
        String[] values = new String[2];
        String[] split = JWT.split("\\.");
        Log.d("JWT","Header: "+getJson(split[0]));
        Log.d("JWT","Body: "+getJson(split[1]));
        JSONObject body = new JSONObject(getJson(split[1]));
        values[0] = body.getString("NameIdentifier");
        values[1] = body.getString("Name");
        return values;
    }

    public static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
