package com.example.bellvantage.smartTracking;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.example.bellvantage.smartTracking.DB.DbHelper;
import com.example.bellvantage.smartTracking.DB.MySingleton;
import com.example.bellvantage.smartTracking.SWF.Attendance;
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.Utils.DateManager;
import com.example.bellvantage.smartTracking.Utils.DateTimeFormatings;
import com.example.bellvantage.smartTracking.web.HTTPPaths;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_BATTERY_IN;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_BATTERY_OUT;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_DATE;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_DISTRIBUTR_ID;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_IS_SYNC_IN;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_IS_SYNC_OUT;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_LATITUDE_IN;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_LATITUDE_OUT;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_LONGITUDE_IN;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_LONGITUDE_OUT;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_SALESREP_ID;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_TIME_IN;
import static com.example.bellvantage.smartTracking.DB.DbHelper.COL_SYNC_ATTENDANCE_TIME_OUT;
import static com.example.bellvantage.smartTracking.DB.DbHelper.TABLE_SYNC_ATTENDANCE;


public class MarkAttendanceActivity  extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    //Toolbar
    private Toolbar mToolBar;
    EditText etLocation;

    //Navigation Drawer
    private AccountHeader headerResult = null;
    private Drawer result = null;

    //Spinner spLocation;

    //Product Status DialogsBox Spinner
    ArrayList<String> locationArray = new ArrayList<>();
    SpinnerDialog spdLocations;
    String location = "";

    //views
    TextView tvMobileTime,tvIn,tvOut,tvSelectLocation;
    java.util.Date noteTS;
    Button btnSubmit,btnMarkOut;

    LinearLayout llMarkIn,llMarOut,llCurrentLocation,llDataA;

    CardView cvLocationIn,cvHeadeer;

    //Geo Location
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private  double lat = 0 ;
    private  double lon = 0;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 5;

    double inLon,inlat,outLon,outlat;

    private String selectedLocation,current_location;
    private int venueCode,voltage;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 5 meters
    private int attendanceCount = 0;
    private DbHelper dbh;

    String enteredUser,status;
    DataLogin dataLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        if (getIntent().getSerializableExtra("DataLogin")!=null){
            dataLogin = (DataLogin) getIntent().getSerializableExtra("DataLogin");
        }


        dbh = new DbHelper(getApplicationContext());
        tvMobileTime = (TextView)findViewById(R.id.tvMobileTime);
        tvIn = (TextView)findViewById(R.id.tvIn);
        tvOut = (TextView)findViewById(R.id.tvOut);
        tvSelectLocation = (TextView)findViewById(R.id.tvSelectLocation);

        etLocation = findViewById(R.id.etLocation);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnMarkOut = (Button) findViewById(R.id.btnMarkOut);
        llMarkIn = (LinearLayout) findViewById(R.id.llMarkIn);
        llMarOut = (LinearLayout) findViewById(R.id.llMarkOut);
        llCurrentLocation = (LinearLayout) findViewById(R.id.llCurrentLocation);
        llDataA = (LinearLayout) findViewById(R.id.llDataA);
        cvLocationIn = (CardView) findViewById(R.id.cvLocationIn);
        cvHeadeer = (CardView) findViewById(R.id.cvHeadeer);
        //ToolBar
        mToolBar = (Toolbar) findViewById(R.id.tb_main);
        mToolBar.setTitle("Attendance");

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(700)
                    .playOn(llDataA);
        } catch (Exception e) {

        }

       /* Gson gson = new Gson();
        String getJson = SessionManager.pref.getString("user", "");
        LoginUser prefUser = gson.fromJson(getJson, LoginUser.class);
        enteredUser =prefUser.getUserName();*/
        //registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //Geo Location


        //Location DialogsBox Spinner
        loadLocations();
        tvSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spdLocations.showSpinerDialog();
            }
        });
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
        //time
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        setAttendanceType();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAttendance(0);
            }
        });

        btnMarkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attendanceCount==0){
                    displayStatusMessage("Do you want to mark OUT without mark IN?",3, 1);
                }else{

                    markAttendance(attendanceCount);
                }

            }
        });


        //View Geo Locations
        llMarkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inlat ==0 || inLon==0){
                    displayStatusMessage("Unable to get your IN time GPS location",3, 0);
                    return;
                }

                Intent intent = new Intent(getApplicationContext(),LocationViewrActivity.class);
                intent.putExtra("longitude",inLon);
                intent.putExtra("latitude",inlat);
                startActivity(intent);
            }
        });


        llMarOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outLon==0 || outlat==0){
                    displayStatusMessage("Unable to get your OUT time GPS location",3, 0);
                    return;
                }

                Intent intent = new Intent(getApplicationContext(),LocationViewrActivity.class);
                intent.putExtra("longitude",outLon);
                intent.putExtra("latitude",outlat);
                startActivity(intent);
            }
        });

    }//end onCreate

    private void markAttendance(int attendanceCount) {
        System.out.println("First Attendance count: "+ attendanceCount);
        selectedLocation = tvSelectLocation.getText().toString();
        current_location = etLocation.getText().toString();

        LocationManager manager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(statusOfGPS==false){
            Toast.makeText(getApplicationContext(),"GPS is disabled.. Please Check your GPS", Toast.LENGTH_LONG).show();
            return;
        }


        if(lat == 0.0 && lon == 0.0){
            Toast.makeText(MarkAttendanceActivity.this,getResources().getString(R.string.invalied_location), Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedLocation.equalsIgnoreCase("Select a location")){
            Toast.makeText(MarkAttendanceActivity.this, "You haven't selected Location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLocation.equalsIgnoreCase("OTHER")&& current_location.isEmpty()|| current_location==""){
            Toast.makeText(MarkAttendanceActivity.this, "Please enter your current location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedLocation.equalsIgnoreCase("OTHER")){
            location=current_location;
        }

        Attendance attendance = new Attendance();

        attendance.setIsSynced(0);
        attendance.setMarkedTime(new Date());
        attendance.setUserId(enteredUser);
        attendance.setLon(lon);
        attendance.setLat(lat);
        attendance.setType(attendanceCount);
        attendance.setVenue(selectedLocation);
        attendance.setVenueCode(venueCode);

        syncAttendance(attendance, attendanceCount);
        tvSelectLocation.setText("Select a location");
        selectedLocation = "";
        current_location = "";



        System.out.println("Attendance OnButtonClick Count: "+ attendanceCount);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"You haven't accepted the loaction permissions for this app. Please go to settings and enable permissions", Toast.LENGTH_LONG).show();
            //ActivityCompat.requestPermissions(this,new String[]);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

/*        Settings.Global.putInt(
                getContentResolver(),
                Settings.Global.AUTO_TIME_ZONE,
               1);*/

        try {
            int auto = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME);
            if (auto==0){
                /*Settings.Global.putInt(
                        getContentResolver(),
                        Settings.Global.AUTO_TIME_ZONE,
                        1);*/
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


       /* if (Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED).contains("1")) {
            if (Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED).contains("1")) {
                System.out.println("Putting the Value to Enable..");
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            } else {
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.TOUCH_EXPLORATION_ENABLED, 1);
                System.out.println("Putting ...");
            }
        }*/

        /*String timeSettings = android.provider.Settings.System.getString(
                this.getContentResolver(),
                android.provider.Settings.System.AUTO_TIME);
        if (timeSettings.contentEquals("0")) {
            android.provider.Settings.System.putString(
                    this.getContentResolver(),
                    android.provider.Settings.System.AUTO_TIME, "1");
        }
        timeSettings = android.provider.Settings.System.getString(
                this.getContentResolver(),
                android.provider.Settings.System.AUTO_TIME);*/

       // mLastLocation.setAccuracy(MIN_DISTANCE_CHANGE_FOR_UPDATES);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        float accuracy = mLastLocation.getAccuracy();
        System.out.println("Accuracy: "+accuracy);
        System.out.println("lon : "+ mLastLocation.getLongitude());
        System.out.println("lat : "+ mLastLocation.getLatitude());
        if (mLastLocation != null) {
//        if (mLastLocation != null && accuracy<= MIN_DISTANCE_CHANGE_FOR_UPDATES ) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();

            System.out.println("ACC Accuracy: "+accuracy);
            System.out.println("ACC lon : "+ mLastLocation.getLongitude());
            System.out.println("Acc lat : "+ mLastLocation.getLatitude());
            String curdate = new DateManager().getDateAccordingToMil(mLastLocation.getTime(),"yyyy-MM-dd HH:mm:ss");
            System.out.println("Location Time "+curdate);
            //Toast.makeText(this,lat+ "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public ArrayList<Attendance> getAttendaceCount(){
        Calendar calOn = Calendar.getInstance();
        Calendar calOff = Calendar.getInstance();

        calOn.set(Calendar.HOUR_OF_DAY, 00);
        calOn.set(Calendar.MINUTE, 01);

        calOff.set(Calendar.HOUR_OF_DAY, 23);
        calOff.set(Calendar.MINUTE, 59);
        return  getAttendance(calOn.getTimeInMillis(),calOff.getTimeInMillis());
    }

    public void displayStatusMessage(String s, int colorValue, final int i) {

        AlertDialog.Builder builder = null;
        View view = null;
        TextView tvOk,tvMessage,tvCancel;

        int defaultColor = R.color.textGray;
        int successColor = R.color.successColor; // 1
        int errorColor = R.color.errorColor; // 2
        int warningColor = R.color.warningColor; // 3
        //1,2,3

        int color = defaultColor;
        if(colorValue == 1){
            color = successColor;
        }else if(colorValue == 2){
            color = errorColor;
        }else if(colorValue == 3){
            color = warningColor;
        }

        builder = new AlertDialog.Builder(MarkAttendanceActivity.this);
        view = getLayoutInflater().inflate(R.layout.layout_for_custom_message_with_ok_cancel,null);

        tvOk = (TextView) view.findViewById(R.id.tvOk);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvMessage.setTextColor(getResources().getColor(color));
        tvMessage.setText(s);


        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (i==1){
                    markAttendance(1);
                }

            }
        });

    }

    private void setAttendanceType() {
        ArrayList<Attendance> attendaces = getAttendaceCount();
        attendanceCount =attendaces.size();
        if (attendanceCount == 0) {

            cvLocationIn.setVisibility(View.GONE);

        } else if (attendanceCount == 1) {

            int timeStatus = attendaces.get(0).getType();
            if (timeStatus==0){
                System.out.println(" attendance count 1 Status: "+ timeStatus);
                btnMarkOut.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                tvIn.setText(DateTimeFormatings.getDateTime(attendaces.get(0).getMarkedTime()));
                inlat = attendaces.get(0).getLat();
                inLon = attendaces.get(0).getLon();

                cvLocationIn.setVisibility(View.VISIBLE);
                llMarOut.setVisibility(View.GONE);
            }else{
                System.out.println(" attendance count 1 Status: "+ timeStatus);
                btnSubmit.setVisibility(View.GONE);
                btnMarkOut.setVisibility(View.GONE);
                cvHeadeer.setVisibility(View.GONE);
                tvOut.setText(DateTimeFormatings.getDateTime(attendaces.get(0).getMarkedTime()));
                outlat = attendaces.get(0).getLat();
                outLon = attendaces.get(0).getLon();

                cvLocationIn.setVisibility(View.VISIBLE);
                llMarkIn.setVisibility(View.GONE);
            }

        } else {
            System.out.println(" attendance count 2 Status: ");
            btnSubmit.setVisibility(View.VISIBLE);
            btnMarkOut.setVisibility(View.GONE);
            btnSubmit.setText("Already Marked both");
            int timeStatus = attendaces.get(0).getType();

            if (timeStatus==0){
                tvIn.setText(DateTimeFormatings.getDateTime(attendaces.get(0).getMarkedTime()));
                inlat = attendaces.get(0).getLat();
                inLon = attendaces.get(0).getLon();

                tvOut.setText(DateTimeFormatings.getDateTime(attendaces.get(1).getMarkedTime()));
                outlat= attendaces.get(1).getLat();
                outLon= attendaces.get(1).getLon();

            }else{
                tvIn.setText(DateTimeFormatings.getDateTime(attendaces.get(1).getMarkedTime()));
                inlat = attendaces.get(1).getLat();
                inLon = attendaces.get(1).getLon();

                tvOut.setText(DateTimeFormatings.getDateTime(attendaces.get(0).getMarkedTime()));
                outlat= attendaces.get(0).getLat();
                outLon= attendaces.get(0).getLon();
            }

            btnSubmit.setEnabled(false);
            cvLocationIn.setVisibility(View.VISIBLE);
            llMarOut.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<Attendance> getAttendance(long min,long max){
        String sql = "select * from attendance where attendace_date >= " + min +" and attendace_date <= " + max;
        SQLiteDatabase db  = dbh.getWritableDatabase();
        Cursor cursor = null;
        ArrayList<Attendance> attendances = new ArrayList<Attendance>();
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
        }
    }
    private void syncAttendance(final Attendance attendance, int attendanceCount) {

        System.out.println("Atendance Count: "+attendanceCount);
        final String attendanceDate = DateManager.getTodayDateString();
        String distributorID = "";
        String userId = dataLogin.getUserID()+"";
        StringRequest stringRequest;
        if (attendanceCount==0){
            String timeIn = DateManager.getDateWithTime();
            timeIn=timeIn.replace(" ","%20");
            location=location.replace(" ","%20");

            String url = HTTPPaths.SERVICE_URL+"SaveAttendance?attendanceDate=" +attendanceDate+
                    "&userID=" +userId+
                    "&timeIN=" +timeIn+
                    "&timeOut=" +timeIn+
                    "&longitudeIn=" +attendance.getLon()+
                    "&latitudeIn=" +attendance.getLat()+
                    "&longitudeOut=0.0" +
                    "&latitudeOut=0.0"+
                    "&locationIn="+location+
                    "&locationOut=Not%20available";

            /*String url= HTTPPaths.SERVICE_URL+
                    "SaveAttendance?attendanceDate=" +attendanceDate+
                    "&distributorID=" +distributorID+
                    "&salesRepID=" +userID+
                    "&timeIN=" +timeIn+
                    "&timeOut=" +timeIn+
                    "&batteryStatusIN=" +status+
                    "&batteryStatusOut=0" +
                    "&isSync=0" +
                    "&syncDate=" +attendanceDate+
                    "&longitudeIn=" +attendance.getLon()+
                    "&latitudeIn=" +attendance.getLat()+
                    "&longitudeOut=0.0" +
                    "&latitudeOut=0.0";*/

            System.out.println("Attendance Insertion URL: "+ url);

            //Insert MarkIn to db
            final ContentValues cv =new ContentValues();
            cv.put(COL_SYNC_ATTENDANCE_DATE,attendanceDate);
            cv.put(COL_SYNC_ATTENDANCE_DISTRIBUTR_ID,distributorID);
            cv.put(COL_SYNC_ATTENDANCE_SALESREP_ID,userId);
            cv.put(COL_SYNC_ATTENDANCE_TIME_IN,timeIn);
            cv.put(COL_SYNC_ATTENDANCE_BATTERY_IN,status);
            cv.put(COL_SYNC_ATTENDANCE_LONGITUDE_IN,attendance.getLon());
            cv.put(COL_SYNC_ATTENDANCE_LATITUDE_IN,attendance.getLat());
            cv.put(COL_SYNC_ATTENDANCE_IS_SYNC_IN,0);

            stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonObject object = Json.parse(response).asObject();
                            int id = object.get("ID").asInt();
                            if (id == 200) {
                                insertAttendanceToDb(attendance);
                                Toast.makeText(MarkAttendanceActivity.this,  "Successfully inserted your mark out time", Toast.LENGTH_SHORT).show();
                                System.out.println("Sync Successful at Attendance Activity");
                                MarkAttendanceActivity.this.finish();
                                cv.put(COL_SYNC_ATTENDANCE_IS_SYNC_IN,1);
                                insertSyncData(cv);

                            } else if(id==500){
                                insertAttendanceToDb(attendance);
                            }else{
                                Toast.makeText(MarkAttendanceActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                System.out.println("Sync Fail at Attendance Activity");
                                insertSyncData(cv);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MarkAttendanceActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                            System.out.println("Error at syncing Attendance at Attendance Activity");
                            insertSyncData(cv);
                        }
                    }
            );
        }else{
            String timeOut = DateManager.getDateWithTime();
            timeOut=timeOut.replace(" ","%20");
            location=location.replace(" ","%20");

            String url =  HTTPPaths.SERVICE_URL +"UpdateAttendance?attendanceDate=" +attendanceDate+
                    "&userID=" +userId+
                    "&timeOut=" +timeOut+
                    "&longitudeOut=" +attendance.getLon()+
                    "&latitudeOut="+attendance.getLat()+
                    "&locationOut="+location;

            /*String url =    HTTPPaths.SERVICE_URL +
                    "UpdateAttendance?attendanceDate=" +attendanceDate+
                    "&salesRepID=" +userId+
                    "&timeOut=" +timeOut+
                    "&batteryStatusOut=" +status+
                    "&longitudeOut=" +attendance.getLon()+
                    "&latitudeOut="+attendance.getLat();*/

            System.out.println("Attendance updated URL: "+ url);

            //Insert MarkOut to db
            final ContentValues cv =new ContentValues();

            cv.put(COL_SYNC_ATTENDANCE_TIME_OUT,timeOut);
            cv.put(COL_SYNC_ATTENDANCE_BATTERY_OUT,status);
            cv.put(COL_SYNC_ATTENDANCE_LONGITUDE_IN,attendance.getLon());
            cv.put(COL_SYNC_ATTENDANCE_LATITUDE_IN,attendance.getLat());
            cv.put(COL_SYNC_ATTENDANCE_LONGITUDE_OUT,attendance.getLon());
            cv.put(COL_SYNC_ATTENDANCE_LATITUDE_OUT,attendance.getLat());
            cv.put(COL_SYNC_ATTENDANCE_IS_SYNC_OUT,0);

            stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonObject object = Json.parse(response).asObject();
                            int id = object.get("ID").asInt();
                            System.out.println("++++++++++++ Attendance Response: "+ response.toString());

                            if(id == 200){

                                insertAttendanceToDb(attendance);
                                Toast.makeText(MarkAttendanceActivity.this, "Successfully inserted your mark out time", Toast.LENGTH_SHORT).show();
                                System.out.println("Successfully updated mark out time at Attendance SyncActivity");
                                cv.put(COL_SYNC_ATTENDANCE_IS_SYNC_OUT,1);
                                updateMarkOut(cv,attendanceDate);
                            }else if(id==500){
                                //insertAttendanceToDb(attendance);
                                Toast.makeText(MarkAttendanceActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                System.out.println("SYNC FAIL Attendance SyncActivity");
                                Toast.makeText(MarkAttendanceActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                updateMarkOut(cv,attendanceDate);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            displayStatusMessage("Please try again",1,0);
                            System.out.println("Error at update Attendance at Attendance Activity");
                            updateMarkOut(cv,attendanceDate);
                        }
                    }
            );
        }


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void insertAttendanceToDb(Attendance attendance) {
        ContentValues cv =attendance.getContentvalues();
        boolean success = dbh.insertData(cv);
        if (success){
            //Toast.makeText(MarkAttendanceActivity.this, "Your attendance is inserted", Toast.LENGTH_SHORT).show();
            setAttendanceType();
        }
        else {
           // Toast.makeText(MarkAttendanceActivity.this, "Your attendance is not inserted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMarkOut(ContentValues cv, String attendanceDate) {
        SQLiteDatabase database = dbh.getWritableDatabase();
        try{
            int afectedRows = database.update(TABLE_SYNC_ATTENDANCE,cv,COL_SYNC_ATTENDANCE_DATE +" = ? " ,new String[]{""+attendanceDate});
            if(afectedRows>0){
                System.out.println("Attendance is  updated at row "+attendanceDate);
            }
        }catch (SQLException e){
            System.out.println("Error at data update at SYNC_ATTENDANCE "+ e.getMessage());
        }finally {
            database.close();
        }

    }
    private void insertSyncData(ContentValues cv) {
        boolean success = dbh.insertDataAll(cv,TABLE_SYNC_ATTENDANCE);
        if (success){
            System.out.println("Data is inserted to "+ TABLE_SYNC_ATTENDANCE+" IsSyncIn: "+ cv.get(COL_SYNC_ATTENDANCE_IS_SYNC_IN));
        }
    }

    private void updateTextView() {
        noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm a"; // 12:00
        tvMobileTime.setText(DateFormat.format(time, noteTS));
    }

    private void loadLocations() {
        locationArray.clear();
        locationArray.add("DCSL");
        locationArray.add("SLIC");
        locationArray.add("CONTINENTAL");
        locationArray.add("SANASA");
        locationArray.add("OTHER");
        spdLocations = new SpinnerDialog(MarkAttendanceActivity.this, locationArray, "Select or search a location", R.style.DialogAnimations_SmileWindow);

        spdLocations.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                tvSelectLocation.setText(item);
                if (item.equalsIgnoreCase("OTHER")) {
                    llCurrentLocation.setVisibility(View.VISIBLE);
                }else{
                    llCurrentLocation.setVisibility(View.GONE);
                    location = item;
                }

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"You haven't accepted the location permissions for this app. Please go to settings and enable permissions", Toast.LENGTH_LONG).show();
            //ActivityCompat.requestPermissions(this,new String[]);
            return;
        }

        mLastLocation = location;
        float accuracy = mLastLocation.getAccuracy();
        System.out.println("Accuracy on  changed: "+accuracy);
        System.out.println("lon on changed: "+ mLastLocation.getLongitude());
        System.out.println("lat on changed: "+ mLastLocation.getLatitude());
        if (mLastLocation != null) {
//        if (mLastLocation != null && accuracy<= MIN_DISTANCE_CHANGE_FOR_UPDATES ) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();

            System.out.println("ACC Accuracy on Changed: "+accuracy);
            System.out.println("ACC lon on Changed: "+ mLastLocation.getLongitude());
            System.out.println("Acc lat on changed: "+ mLastLocation.getLatitude());
            String curdate = new DateManager().getDateAccordingToMil(mLastLocation.getTime(),"yyyy-MM-dd HH:mm:ss");

        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }
}
