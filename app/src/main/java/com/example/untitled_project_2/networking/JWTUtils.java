package com.example.untitled_project_2.networking;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class JWTUtils {
    public static void decode(String JWT) throws Exception{
        try {
            String[] split = JWT.split("\\.");
            Log.d("JWT","Header: "+getJson(split[0]));
            Log.d("JWT","Body: "+getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            Log.e("JWT","Decoding error");
        }
    }

    public static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
