package com.example.bellvantage.smartTracking.Services;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.example.bellvantage.smartTracking.DB.DbHelper;
import com.example.bellvantage.smartTracking.DB.MySingleton;
import com.example.bellvantage.smartTracking.SWF.DriverTracking;
import com.example.bellvantage.smartTracking.Utils.DateManager;
import com.example.bellvantage.smartTracking.Utils.NetworkConnection;
import com.example.bellvantage.smartTracking.web.HTTPPaths;

import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_ID;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_DRIVER_TRACKING_ISSYNC;
import static com.example.bellvantage.smartTracking.DB.DbHelper.TABLE_DRIVER_TRACKING;

/**
 * Created by Sachika on 28/11/2017.
 */

public class NetworkMonitor extends BroadcastReceiver {

    DbHelper db;
    SQLiteDatabase database;

    @Override
    public void onReceive(final Context context, Intent intent) {
        db = new DbHelper( context );
        database = db.getWritableDatabase();
        context.sendBroadcast( new Intent( HTTPPaths.UI_UPDATE_BROADCAST ) );


        if (NetworkConnection.checkNetworkConnection( context )) {

            //Sync unsync driver tracking data to server
            syncDriverTrackingDetails( context );
        }

    }

    private void syncDriverTrackingDetails(Context context) {

        SQLiteDatabase database = db.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_DRIVER_TRACKING + " WHERE " + COL_DRIVER_TRACKING_ISSYNC + " = 0";
        Cursor cursor = database.rawQuery( sql, null );

        if (cursor.getCount() == 0) {
            System.out.println( "syncDriverTrackingDetails == No data in " + TABLE_DRIVER_TRACKING + " AT NETWORK MONITOR" );
        } else {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        DriverTracking driverTracking = DriverTracking.getDbInstance( cursor );
                        syncToServer( driverTracking, context );
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                System.out.println( "Error while get db instnce at  NETWORK MONITOR " + e.getMessage() );
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }
        }
    }

    private void syncToServer(final DriverTracking driverTracking, final Context context) {
        //Get Sync Date
        DateManager dateManager = new DateManager();
        String gpsDate = dateManager.getDateWithTime();
        System.out.println( "GPS Date: " + gpsDate );

        gpsDate = gpsDate.replace( " ", "%20" );
        String syncDate = gpsDate;

        String url = HTTPPaths.seriveUrlTrack + "saveDriverTracking.php?RiderId=" + driverTracking.getRiderId() +
                "&GpsDate=" + driverTracking.getGpsDate() +
                "&Latitude=" + driverTracking.getLatitude() +
                "&Longitude=" + driverTracking.getLongitude() +
                "&Speed=" + driverTracking.getSpeed() +
                "&Accuracy=" + driverTracking.getAccuracy() +
                "&ImeiNumber=" + driverTracking.getImeiNumber() +
                "&SyncDate=" + syncDate;

        if (NetworkConnection.checkNetworkConnection( context )) {
            StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonObject object = Json.parse( response ).asObject();
                            int id = object.get( "ID" ).asInt();
                            if (id == 200) {
                                context.sendBroadcast( new Intent( HTTPPaths.UI_UPDATE_BROADCAST ) );
                                System.out.println( "Tracking and Sync to server at NETWORK MONITOR SYNC_TRACKING DETAILS " );
                                ContentValues cv = new ContentValues();
                                cv.put( COL_DRIVER_TRACKING_ISSYNC, 1 );
                                int afectedRows = database.update( TABLE_DRIVER_TRACKING, cv, COL_DRIVER_TRACKING_ID + " = ? ", new String[]{"" + driverTracking.getId()} );
                                if (afectedRows > 0) {
                                    System.out.println( afectedRows + "row(s) updated in " + TABLE_DRIVER_TRACKING );
                                }
                            } else {
                                System.out.println( "Tracking fail at NETWORK MONITOR SYNC_TRACKING DETAILS " );
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println( "Tracking fail at NETWORK MONITOR SYNC_TRACKING DETAILS " + error.getMessage() );
                        }
                    } );
            MySingleton.getInstance( context ).addToRequestQueue( stringRequest );
        } else {
            System.out.println( "Tracking fail.. No network Coverage at NETWORK MONITOR SYNC_TRACKING DETAILS " );
        }
    }


}
