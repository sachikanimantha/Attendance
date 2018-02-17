package com.example.bellvantage.smartTracking.SWF;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_ACCURACY;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_GPS_DATE;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_IMEI_NUMBER;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_ISSYNC;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_LATITUDE;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_LONGITUDE;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_RIDER_ID;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_SPEED;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_SYNC_DATE;

/**
 * Created by Sachika on 28/11/2017.
 */

public class DriverTracking implements Serializable {

    private int Id;
    private int RiderId;
    private String GpsDate;
    private double Latitude;
    private double Longitude;
    private float Speed;
    private float Accuracy;
    private String ImeiNumber;
    private String SyncDate;
    private int IsSync;

    public DriverTracking() {
    }

    public DriverTracking(int riderId, String gpsDate, double latitude,
                          double longitude, float speed, float accuracy,
                          String imeiNumber, String syncDate, int isSync) {
        this.setRiderId( riderId );
        this.setGpsDate( gpsDate );
        this.setLatitude( latitude );
        this.setLongitude( longitude );
        this.setSpeed( speed );
        this.setAccuracy( accuracy );
        this.setImeiNumber( imeiNumber );
        this.setSyncDate( syncDate );
        this.setIsSync( isSync );
    }

    public int getIsSync() {
        return IsSync;
    }

    public void setIsSync(int isSync) {
        IsSync = isSync;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getRiderId() {
        return RiderId;
    }

    public void setRiderId(int riderId) {
        RiderId = riderId;
    }

    public String getGpsDate() {
        return GpsDate;
    }

    public void setGpsDate(String gpsDate) {
        GpsDate = gpsDate;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public float getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(float accuracy) {
        Accuracy = accuracy;
    }

    public String getImeiNumber() {
        return ImeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        ImeiNumber = imeiNumber;
    }

    public String getSyncDate() {
        return SyncDate;
    }

    public void setSyncDate(String syncDate) {
        SyncDate = syncDate;
    }

    public ContentValues getDriverTrackingCV() {
        ContentValues cv = new ContentValues();
        cv.put( COL_DRIVER_TRACKING_RIDER_ID, getRiderId() );
        cv.put( COL_DRIVER_TRACKING_GPS_DATE, getGpsDate() );
        cv.put( COL_DRIVER_TRACKING_LATITUDE, getLatitude() );
        cv.put( COL_DRIVER_TRACKING_LONGITUDE, getLongitude() );
        cv.put( COL_DRIVER_TRACKING_SPEED, getSpeed() );
        cv.put( COL_DRIVER_TRACKING_ACCURACY, getAccuracy() );
        cv.put( COL_DRIVER_TRACKING_IMEI_NUMBER, getImeiNumber() );
        cv.put( COL_DRIVER_TRACKING_SYNC_DATE, getSyncDate() );
        cv.put( COL_DRIVER_TRACKING_ISSYNC, getIsSync() );
        return cv;
    }


    public static DriverTracking getDbInstance(Cursor cursor) {
        DriverTracking driverTracking = new DriverTracking();

        driverTracking.setId( cursor.getInt( 0 ) );
        driverTracking.setRiderId( cursor.getInt( 1 ) );
        driverTracking.setGpsDate( cursor.getString( 2 ) );
        driverTracking.setLatitude( cursor.getDouble( 3 ) );
        driverTracking.setLongitude( cursor.getDouble( 4 ) );
        driverTracking.setSpeed( cursor.getFloat( 5 ) );
        driverTracking.setAccuracy( cursor.getFloat( 6 ) );
        driverTracking.setImeiNumber( cursor.getString( 7 ) );
        driverTracking.setSyncDate( cursor.getString( 8 ) );
        driverTracking.setIsSync( cursor.getInt( 9 ) );

        return driverTracking;
    }


    /*
    try {
        cursor =  db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                Attendance attendance = Attendance.getDBInstance(cursor);
                attendances.add(attendance);
            }while(cursor.moveToNext());
        }
        return attendances;

    }catch (Exception e){
        throw  e;
    }finally {
        if(cursor != null){
            cursor.close();
        }
        db.close();
    }*/

}
