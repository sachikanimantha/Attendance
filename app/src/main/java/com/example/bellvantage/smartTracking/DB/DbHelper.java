package com.example.bellvantage.smartTracking.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sachika on 28/11/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tracking.db";

    //meta
    public static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAIL = 0;



    public static final String TABLE_DRIVER_TRACKING = "DRIVER_TRACKING";
    public static final String TABLE_NAME = "attendance";
    public static final String TABLE_SYNC_ATTENDANCE = "SYNC_ATTENDANCE";


    //Columns of Driver Trackimg
    public static final String COL_DRIVER_TRACKING_ID = "Id";
    public static final String COL_DRIVER_TRACKING_RIDER_ID = "RiderId";
    public static final String COL_DRIVER_TRACKING_GPS_DATE = "GpsDate";
    public static final String COL_DRIVER_TRACKING_LATITUDE = "Latitude";
    public static final String COL_DRIVER_TRACKING_LONGITUDE = "Longitude";
    public static final String COL_DRIVER_TRACKING_SPEED = "Spedd";
    public static final String COL_DRIVER_TRACKING_ACCURACY = "Accuracy";
    public static final String COL_DRIVER_TRACKING_IMEI_NUMBER = "ImeiNumber";
    public static final String COL_DRIVER_TRACKING_SYNC_DATE = "SyncDate";
    public static final String COL_DRIVER_TRACKING_ISSYNC = "IsSync";



    //Columns of TABLE SYNC_ATTENDANCE
    public static final String COL_SYNC_ATTENDANCE_ID = "id";
    public static final String COL_SYNC_ATTENDANCE_DATE = "attendance_date";
    public static final String COL_SYNC_ATTENDANCE_DISTRIBUTR_ID = "distributer_id";
    public static final String COL_SYNC_ATTENDANCE_SALESREP_ID = "salesrep_id";
    public static final String COL_SYNC_ATTENDANCE_TIME_IN= "time_in";
    public static final String COL_SYNC_ATTENDANCE_TIME_OUT= "time_out";
    public static final String COL_SYNC_ATTENDANCE_BATTERY_IN= "battery_in";
    public static final String COL_SYNC_ATTENDANCE_BATTERY_OUT= "battery_out";
    public static final String COL_SYNC_ATTENDANCE_LONGITUDE_IN= "longitude_in";
    public static final String COL_SYNC_ATTENDANCE_LATITUDE_IN= "latitude_in";
    public static final String COL_SYNC_ATTENDANCE_LONGITUDE_OUT= "longitude_out";
    public static final String COL_SYNC_ATTENDANCE_LATITUDE_OUT= "latitude_out";
    public static final String COL_SYNC_ATTENDANCE_IS_SYNC_IN= "is_sync_in";
    public static final String COL_SYNC_ATTENDANCE_IS_SYNC_OUT= "is_sync_out";




    public DbHelper(Context context) {
        super( context, DATABASE_NAME, null, 1 );
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql_user_attendace = "create table attendance(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT," +
                "attendace_date INTEGER," +
                "type INTEGER," +
                "sequence_id INTEGER," +
                "lat TEXT," +
                "lon TEXT," +
                "is_synced INTEGER," +
                "created_at INTEGER," +
                "venue TEXT," +
                "venueCode INTEGER)";
        System.out.println("sql_user_attendace - "+sql_user_attendace);
        db.execSQL(sql_user_attendace);



        //create DRIVER_TRACKING table
        String sql_driverTracking = "CREATE TABLE " + TABLE_DRIVER_TRACKING + " ( " +
                COL_DRIVER_TRACKING_ID + " INTEGER  NOT NULL PRIMARY KEY    AUTOINCREMENT," +
                COL_DRIVER_TRACKING_RIDER_ID + " INTEGER, " +
                COL_DRIVER_TRACKING_GPS_DATE + " TEXT, " +
                COL_DRIVER_TRACKING_LATITUDE + " REAL, " +
                COL_DRIVER_TRACKING_LONGITUDE + " REAL, " +
                COL_DRIVER_TRACKING_SPEED + " REAL, " +
                COL_DRIVER_TRACKING_ACCURACY + " INTEGER, " +
                COL_DRIVER_TRACKING_IMEI_NUMBER + " TEXT, " +
                COL_DRIVER_TRACKING_SYNC_DATE + " TEXT, " +
                COL_DRIVER_TRACKING_ISSYNC + " INTEGER );";
        System.out.println( "sql_driverTracking - " + sql_driverTracking );
        db.execSQL( sql_driverTracking );

        String sql_sync_attendace = "CREATE TABLE "+TABLE_SYNC_ATTENDANCE+"(" +
                COL_SYNC_ATTENDANCE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SYNC_ATTENDANCE_DATE+" TEXT," +
                COL_SYNC_ATTENDANCE_DISTRIBUTR_ID+" TEXT," +
                COL_SYNC_ATTENDANCE_SALESREP_ID+" TEXT," +
                COL_SYNC_ATTENDANCE_TIME_IN+" TEXT," +
                COL_SYNC_ATTENDANCE_TIME_OUT+" TEXT," +
                COL_SYNC_ATTENDANCE_BATTERY_IN+" TEXT," +
                COL_SYNC_ATTENDANCE_BATTERY_OUT+" TEXT," +
                COL_SYNC_ATTENDANCE_LONGITUDE_IN+" REAL," +
                COL_SYNC_ATTENDANCE_LATITUDE_IN+" REAL," +
                COL_SYNC_ATTENDANCE_LONGITUDE_OUT+" REAL," +
                COL_SYNC_ATTENDANCE_LATITUDE_OUT+" REAL," +
                COL_SYNC_ATTENDANCE_IS_SYNC_IN+" INTEGER," +
                COL_SYNC_ATTENDANCE_IS_SYNC_OUT+" INTEGER)";
        System.out.println("sql_sync_attendace - "+sql_sync_attendace);
        db.execSQL(sql_sync_attendace);



    }

    public boolean insertData(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_NAME,null,cv);

        if (result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*String sql_upgrade = "drop table "+TABLE_NAME+ " if exists;" +
                "drop table "+TBL25+" if exists"+
        "drop table "+TABLE_CATEGORY+ " if exists;" +
                "drop table "+TBL25+" if exists;" ;
        db.execSQL(sql_upgrade);*/
    }

    public boolean insertDataAll(ContentValues cv, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert( tableName, null, cv );

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getAllData(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + tableName, null );
        return cursor;
    }

    public Cursor getSelectedData(String tableName, String colName, String colName2) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " + colName + " = 1 " + " ORDER BY " + colName2 + " ASC ", null );
        return cursor;
    }

    public Cursor getOngoingJobsData(String tableName, String colName, String colName2) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " + colName + " = 2 " + " ORDER BY " + colName2 + " ASC ", null );
        return cursor;
    }

    public Cursor getHistoryJobsData(String tableName, String colName, String colName2) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " + colName + " = 3 " + " ORDER BY " + colName2 + " ASC ", null );
        return cursor;
    }


    public Cursor getSingleData(String url) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( url, null );
        return cursor;
    }

    /*db.update(TABLE_NAME, contentValues, NAME + " = ? AND " + LASTNAME + " = ?", new String[]{"Manas", "Bajaj"});*/

    public boolean updateTable(String id, ContentValues cv, String whereClause, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isUpdated = false;
        int afectedRows = db.update( tableName, cv, whereClause, new String[]{id} );
        if (afectedRows > 0) {
            isUpdated = true;
            System.out.println( "======== Updated Row Count: " + afectedRows + " ================= " );
        }
        return isUpdated;
    }

    public boolean deleteAllData(String tableName) {
        boolean success = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.execSQL( "DELETE FROM " + tableName );
            success = true;

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return success;
    }


    public int totalROWS(String tablename) {
        int value = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
//        if(tablename.equalsIgnoreCase(All)){
//            cursor.getCount() = 0;
//        }else{
//
//        }
        cursor = db.rawQuery( "SELECT COUNT (*) FROM " + tablename, null );
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                value = cursor.getInt( 0 );
            }
        } else {
            value = 0;
        }

        return value;
    }


    public void deleteFromAnyTable(String tablename, String wherePart) {

        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery("delete from "+tablename+" where "+wherePart,null);

        String sql = "delete from " + tablename + " where " + wherePart;
        db.execSQL( sql );
    }

}
