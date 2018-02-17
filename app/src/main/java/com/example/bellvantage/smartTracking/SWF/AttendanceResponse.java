package com.example.bellvantage.smartTracking.SWF;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Sachika on 11/01/2018.
 */

public class AttendanceResponse {

    private ArrayList<AttendaneDataBean> Data;

    public static AttendanceResponse objectFromData(String str) {

        return new Gson().fromJson(str, AttendanceResponse.class);
    }

    public static AttendanceResponse objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), AttendanceResponse.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<AttendanceResponse> arrayAttendanceResponseFromData(String str) {

        Type listType = new TypeToken<ArrayList<AttendanceResponse>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static ArrayList<AttendanceResponse> arrayAttendanceResponseFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<AttendanceResponse>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public ArrayList<AttendaneDataBean> getData() {
        return Data;
    }

    public void setData(ArrayList<AttendaneDataBean> Data) {
        this.Data = Data;
    }
}
