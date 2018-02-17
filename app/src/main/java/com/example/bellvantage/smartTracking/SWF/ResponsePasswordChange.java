package com.example.bellvantage.smartTracking.SWF;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachika on 13/01/2018.
 */

public class ResponsePasswordChange  {

    /**
     * Data : Successfully Updated
     * ID : 200
     */

    private String Data;
    private int ID;

    public static ResponsePasswordChange objectFromData(String str) {

        return new Gson().fromJson(str, ResponsePasswordChange.class);
    }

    public static ResponsePasswordChange objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), ResponsePasswordChange.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ResponsePasswordChange> arrayResponsePasswordChangeFromData(String str) {

        Type listType = new TypeToken<ArrayList<ResponsePasswordChange>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<ResponsePasswordChange> arrayResponsePasswordChangeFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ResponsePasswordChange>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
