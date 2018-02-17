package com.example.bellvantage.smartTracking;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.SWF.DriverTracking;
import com.example.bellvantage.smartTracking.Services.LocationTracker;
import com.example.bellvantage.smartTracking.Services.NetworkMonitor;
import com.example.bellvantage.smartTracking.Services.ScreenStateReceiver;
import com.example.bellvantage.smartTracking.Utils.NetworkConnection;
import com.example.bellvantage.smartTracking.web.HTTPPaths;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import static com.example.bellvantage.smartTracking.DB.DbHelper.TABLE_DRIVER_TRACKING;
public class UserAreaActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    CardView cvMarkAttendance,cvHistory,cvResetPassword,cvInquery;
    LinearLayout llMenu1,llMenu2;


    BroadcastReceiver networkMonitorBroadCastReciver;//broadcastReceiver
    public static ScreenStateReceiver mScreenStateReceiver;

    //For GPS Tracking
    int gpsStatus;
    double lat, lon;
    float accuracy, speed;

    DbHelper db;

    //Navigation Drawer
    AccountHeader headerResult = null;
    Drawer result = null;
    static final int PROFILE_SETTING = 1;

    DataLogin dataLogin;


/*

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                    DateManager dateManager = new DateManager();
                    String gpsDate = dateManager.getDateWithTime();
                    System.out.println("GPS Date: " + gpsDate);

                    gpsDate = gpsDate.replace(" ", "%20");
                    String syncDate = gpsDate;
                    String imeiNumber = "0";

                    //Get Imei Number
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (ActivityCompat.checkSelfPermission(UserAreaActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        imeiNumber = tm.getImei(1);
                    }
                    System.out.println( imeiNumber );


                    //Get Data form GPS Tracker Service
                    lat = (double) intent.getExtras().get( "lat" );
                    lon = (double) intent.getExtras().get( "lon" );
                    accuracy = (float) intent.getExtras().get( "accuracy" );
                    gpsStatus = (int) intent.getExtras().get( "gpsStatus" );
                    try {

                        speed = (float) intent.getExtras().get( "speed" );

                    } catch (Exception e) {
                        System.out.println( "Error at casting speed" );
                    } finally {
                        speed = 0;
                    }

                    DriverTracking driverTracking = new DriverTracking(
                            getRiderId(),
                            gpsDate,
                            lat,
                            lon,
                            speed,
                            accuracy,
                            imeiNumber,
                            syncDate,
                            0
                    );

                    insertData( driverTracking );

                }
            };
        }
        registerReceiver( broadcastReceiver, new IntentFilter( "location_update" ) );
        startServices();
    }
*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //keep this for tracking
       /* if (broadcastReceiver != null) {
            unregisterReceiver( broadcastReceiver );
//            unregisterReceiver( mScreenStateReceiver );
            unregisterReceiver( networkMonitorBroadCastReciver );
        }*/
        if (networkMonitorBroadCastReciver != null) {
            unregisterReceiver( networkMonitorBroadCastReciver );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );
        mToolBar = (Toolbar) findViewById( R.id.tb_main );
        cvMarkAttendance = findViewById( R.id.cvMarkAttendance );
        cvHistory = findViewById( R.id.cvHistory );
        cvResetPassword = findViewById( R.id.cvResetPassword );
        cvInquery = findViewById( R.id.cvInquery );
        llMenu1 = findViewById( R.id.llMenu1 );
        llMenu2 = findViewById( R.id.llMenu2 );
        mToolBar.setTitle( "Attendance" );



        db = new DbHelper( UserAreaActivity.this );
        if (getIntent().getSerializableExtra("DataLogin")!=null){
            dataLogin = (DataLogin) getIntent().getSerializableExtra("DataLogin");
        }

        try {
            YoYo.with(Techniques.BounceInDown)
                    .duration(500)
                    .playOn(llMenu1);
        } catch (Exception e) {

        }

        try {
            YoYo.with(Techniques.BounceInDown)
                    .duration(700)
                    .playOn(llMenu2);
        } catch (Exception e) {

        }



        //=========Navigation Drawer===========================================
        insertNavigationDraer( savedInstanceState );

        /*//Netowrk Monitor===============================================
        networkMonitorBroadCastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println( "NetworkMonitor Service start..." );
            }
        };*/

        //startServices();

        if (!runTimePermisions()) {
            enableService();
        }

        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAreaActivity.this,HistoryActivity.class);
                intent.putExtra("DataLogin",dataLogin);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cvMarkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAreaActivity.this,MarkAttendanceActivity.class);
                intent.putExtra("DataLogin",dataLogin);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAreaActivity.this,PasswordResetActivity.class);
                intent.putExtra("DataLogin",dataLogin);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        cvInquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInquiry();
            }
        });
    }


    //Insert and sync tracking data to server
    private void insertData(final DriverTracking driverTracking) {
        String url = HTTPPaths.seriveUrlTrack + "saveDriverTracking.php?RiderId=" + driverTracking.getRiderId() +
                "&GpsDate=" + driverTracking.getGpsDate() +
                "&Latitude=" + driverTracking.getLatitude() +
                "&Longitude=" + driverTracking.getLongitude() +
                "&Speed=" + driverTracking.getSpeed() +
                "&Accuracy=" + driverTracking.getAccuracy() +
                "&ImeiNumber=" + driverTracking.getImeiNumber() +
                "&SyncDate=" + driverTracking.getSyncDate();

        System.out.println( "Driver Tracking URL: " + url );

        if (NetworkConnection.checkNetworkConnection( getApplicationContext() )) {
            StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonObject object = Json.parse( response ).asObject();
                            int id = object.get( "ID" ).asInt();
                            if (id == 200) {
                                System.out.println( "Tracking and Sync to server" );
                                insertTrackingDetailsTOSqlite( driverTracking, 1 );
                            } else {
                                System.out.println( "Tracking fail" );
                                insertTrackingDetailsTOSqlite( driverTracking, 0 );
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println( "Tracking fail " + error.getMessage() );
                            insertTrackingDetailsTOSqlite( driverTracking, 0 );
                        }
                    } );
            MySingleton.getInstance( getApplicationContext() ).addToRequestQueue( stringRequest );
        } else {
            System.out.println( "Tracking fail.. No network Coverage " );
            insertTrackingDetailsTOSqlite( driverTracking, 0 );
        }
    }

    private void insertTrackingDetailsTOSqlite(DriverTracking driverTracking, int i) {
        if (i == 1) {
            driverTracking.setIsSync( 1 );
        }
        ContentValues cv = driverTracking.getDriverTrackingCV();
        boolean success = db.insertDataAll( cv, TABLE_DRIVER_TRACKING );
        if (success) {
            System.out.println( "Tracking Data is inserted to " + TABLE_DRIVER_TRACKING + " Sync Status: " + driverTracking.getIsSync() );
        }
    }


    //Services
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver( networkMonitorBroadCastReciver, new IntentFilter( HTTPPaths.UI_UPDATE_BROADCAST ) );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void enableService() {
        Intent intent = new Intent( getApplicationContext(), LocationTracker.class );
        startService( intent );
    }

    boolean runTimePermisions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.
                checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ContextCompat.
                checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100 );
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableService();
            } else {
                runTimePermisions();
            }
        }
    }

    public void screenOnReceicer() {
        if (mScreenStateReceiver == null) {
            mScreenStateReceiver = new ScreenStateReceiver();

            IntentFilter screenStateFilter = new IntentFilter();
            screenStateFilter.addAction( Intent.ACTION_SCREEN_ON );
            screenStateFilter.addAction( Intent.ACTION_SCREEN_OFF );

            try {
                registerReceiver( mScreenStateReceiver, screenStateFilter );
            } catch (Exception e) {
                System.out.println( "Error at screen on reciver " + e.getMessage() );
            }
        }
    }

    private void startServices() {
        try {
            startService( new Intent( this, NetworkMonitor.class ) );
            screenOnReceicer();
            System.out.println( "Start service..." );
        } catch (Exception e) {
            System.out.println( "Error at Service start " + e.getMessage() );
        }

    }

    private void insertNavigationDraer(Bundle savedInstanceState) {
        headerResult = new AccountHeaderBuilder()
                .withActivity( this )
                .withHeaderBackground( R.drawable.header )
                .withTranslucentStatusBar( true )

                .withOnAccountHeaderListener( new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        return false;
                    }
                } )
                .withSavedInstance( savedInstanceState )
                .build();

        //Drawer
        result = new DrawerBuilder()
                .withActivity( this )
                .withToolbar( mToolBar )
                .withDisplayBelowStatusBar( false )
                .withActionBarDrawerToggleAnimated( true )
                .withDrawerGravity( Gravity.LEFT )
                .withSavedInstance( savedInstanceState )
                .withSelectedItem( 0 )
                .withTranslucentStatusBar( false )
                .withAccountHeader( headerResult ) //set the AccountHeader  created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName( "Home" ).withIcon( GoogleMaterial.Icon.gmd_home ).withIdentifier( 1 ).withSelectedColor( 3 ),
//                        new PrimaryDrawerItem().withName("Sync Manual").withIcon(GoogleMaterial.Icon.gmd_sync).withIdentifier(2).withSelectedColor(2),
                        // new PrimaryDrawerItem().withName("Sales").withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(3).withBadge(getSalesOrders()).withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)),
                        new PrimaryDrawerItem().withName( "Attendance" ).withIcon( GoogleMaterial.Icon.gmd_access_time ).withIdentifier( 5 ).withBadgeStyle( new BadgeStyle( Color.RED, Color.RED ) ),
                        new PrimaryDrawerItem().withName( "History" ).withIcon( GoogleMaterial.Icon.gmd_history ).withIdentifier( 5 ).withBadgeStyle( new BadgeStyle( Color.RED, Color.RED ) ),
                        new PrimaryDrawerItem().withName( "Change Password" ).withIcon( GoogleMaterial.Icon.gmd_lock ).withIdentifier( 5 ).withBadgeStyle( new BadgeStyle( Color.RED, Color.RED ) ),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withDescription( "Bellvantage PVT LTD" ).withName( "Attendance App" ).withIdentifier( 6 ),
                        new SecondaryDrawerItem().withName( "Settings" ).withIcon( GoogleMaterial.Icon.gmd_settings ),
                        new SecondaryDrawerItem().withName( "About Us" ).withIcon( GoogleMaterial.Icon.gmd_apps ).withTag( "Bullhorn" ),
                        new SecondaryDrawerItem().withName( "Logout" ).withIcon( GoogleMaterial.Icon.gmd_exit_to_app )
                        // new SwitchDrawerItem().withName("Switch").withIcon(GoogleMaterial.Icon.gmd_ac_unit).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
                        // new ToggleDrawerItem().withName("Toggle").withIcon(GoogleMaterial.Icon.gmd_adjust).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener( new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "Home") {
                            Intent intent = new Intent(UserAreaActivity.this, UserAreaActivity.class);
                            intent.putExtra("DataLogin",dataLogin);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity( intent );
                            finish();
                        }


                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "About Us") {
                            about();
                        }
                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "History") {
                            Intent intent = new Intent(UserAreaActivity.this,HistoryActivity.class);
                            intent.putExtra("DataLogin",dataLogin);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "Change Password") {
                            Intent intent = new Intent(UserAreaActivity.this,PasswordResetActivity.class);
                            intent.putExtra("DataLogin",dataLogin);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "Attendance") {
                            Intent intent = new Intent(UserAreaActivity.this,MarkAttendanceActivity.class);
                            intent.putExtra("DataLogin",dataLogin);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "Settings") {


                        }
                        if (((Nameable) drawerItem).getName().getText( UserAreaActivity.this ) == "Logout") {
                            displayStatusMessage("Are you sure want to  logout?",3,0);
                        }

                        return false;
                    }
                } )
                .build();
    }


    @Override
    public void onBackPressed() {
        displayStatusMessage("Are you sure want to  exit?",3,0);
    }
    private void displayStatusMessage(String s, int colorValue, final int id) {

        AlertDialog.Builder builder = null;
        View view = null;
        TextView tvOk, tvMessage;
        ImageView imageView;
        int defaultColor = R.color.textGray;
        int successColor = R.color.successColor; // 1
        int errorColor = R.color.errorColor; // 2
        int warningColor = R.color.warningColor; // 3

        int success = R.drawable.ic_success;
        int error_image = R.drawable.ic_error;
        int warning_image = R.drawable.ic_warning;
        //1,2,3

        int color = defaultColor;
        int img = success;
        if (colorValue == 1) {
            color = successColor;
            img = success;

        } else if (colorValue == 2) {
            color = errorColor;
            img = error_image;

        } else if (colorValue == 3) {
            color = warningColor;
            img = warning_image;
        }

        builder = new AlertDialog.Builder(UserAreaActivity.this);
        view = getLayoutInflater().inflate(R.layout.layout_for_custom_message, null);

        tvOk = (TextView) view.findViewById(R.id.tvOk);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        imageView = (ImageView)view.findViewById(R.id.iv_status);

        tvMessage.setTextColor(getResources().getColor(color));
        tvMessage.setText(s);
        imageView.setImageResource(img);


        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id==0){
                    Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity( intent );
                    finish();
                    alertDialog.dismiss();
                }else{
                    alertDialog.dismiss();
                }


            }
        });

    }
    private void about() {
        AlertDialog.Builder builder = null;
        View view = null;

        builder = new AlertDialog.Builder(UserAreaActivity.this);
        view = getLayoutInflater().inflate(R.layout.layout_for_about, null);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void displayInquiry() {

        AlertDialog.Builder builder = null;
        View view = null;
        TextView tvOk, tvCancel;
        ImageView imageView;
        final EditText etInquiry;

        builder = new AlertDialog.Builder(UserAreaActivity.this);
        view = getLayoutInflater().inflate(R.layout.layout_for_inquery, null);

        tvOk = (TextView) view.findViewById(R.id.tvOk);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        imageView = (ImageView)view.findViewById(R.id.iv_status);
        etInquiry = view.findViewById(R.id.etInquiry);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inquiry = etInquiry.getText().toString();
                if (TextUtils.isEmpty(inquiry)){
                    displayStatusMessage("Please Enter your inquiry",2,1);
                    return;
                }
                    alertDialog.dismiss();
                    displayStatusMessage("Your inquiry is submitted...",1,1);

            }
        });

    }
}
