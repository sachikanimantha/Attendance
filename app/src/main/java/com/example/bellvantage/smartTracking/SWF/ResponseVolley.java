package com.example.bellvantage.smartTracking.SWF;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachika on 11/01/2018.
 */

public class ResponseVolley {

    /**
     * Data : {"Address":"Mount Lavinia","ContactNumber":"0776926321","Name":"Mohamed Shamaal Usuph","UserType":"N","UserPassword":"123456","UserName":"Shamaal","UserID":213050}
     */

    private DataLogin Data;

    public static ResponseVolley objectFromData(String str) {

        return new Gson().fromJson(str, ResponseVolley.class);
    }

    public static ResponseVolley objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), ResponseVolley.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ResponseVolley> arrayResponseVolleyFromData(String str) {

        Type listType = new TypeToken<ArrayList<ResponseVolley>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<ResponseVolley> arrayResponseVolleyFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ResponseVolley>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public DataLogin getData() {
        return Data;
    }

    public void setData(DataLogin Data) {
        this.Data = Data;
    }
}
