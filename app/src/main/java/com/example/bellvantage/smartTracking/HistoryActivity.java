package com.example.bellvantage.smartTracking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.bellvantage.smartTracking.Adapters.AttendanceAdapter;
import com.example.bellvantage.smartTracking.DB.MySingleton;
import com.example.bellvantage.smartTracking.SWF.AttendanceResponse;
import com.example.bellvantage.smartTracking.SWF.AttendaneDataBean;
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.Utils.DateManager;
import com.example.bellvantage.smartTracking.Utils.NetworkConnection;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.bellvantage.smartTracking.web.HTTPPaths.SERVICE_URL;

public class HistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rvAttendance;
    TextView tvDateTo,tvDateFrom,tvMessage;
    Button tvSearchButon;
    CardView cvHeadeers;
    ProgressDialog progressDialog;
    LinearLayout llTitles,llDataHistory;

    //date
    Calendar calendar;
    int year, month, day,id,mHour, mMinute;
    long dateFrom, dateTo;
    String startDate = "" , endDate="";
    DateManager dateManager;
    DataLogin dataLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        initializeViews();

        if (getIntent().getSerializableExtra("DataLogin")!=null){
            dataLogin = (DataLogin) getIntent().getSerializableExtra("DataLogin");
        }

        dateManager = new DateManager();
        llTitles.setVisibility(View.GONE);
        toolbar.setTitle(R.string.history);

        //Date
        tvDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(67);
            }
        });

        tvDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(63);
            }
        });

        //Search
        tvSearchButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateFrom = tvDateFrom.getText().toString();
                String dateTo = tvDateTo.getText().toString();

                if (dateFrom.equalsIgnoreCase("Select a date")){
                    displayStatusMessage("Please select start date ", 2);
                    return;
                }

                if (dateTo.equalsIgnoreCase("Select a date")){
                    displayStatusMessage("Please select end date ", 2);
                    return;
                }

                if (!NetworkConnection.checkNetworkConnection(HistoryActivity.this)){
                    displayStatusMessage("Mobile network is not available. Please enable", 2);
                    return;
                }

                progressDialog = new ProgressDialog(HistoryActivity.this);
                progressDialog.setTitle("Loading...");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                getAttendanceList();
            }
        });

        try {
            YoYo.with(Techniques.BounceInDown)
                    .duration(700)
                    .playOn(tvMessage);
        } catch (Exception e) {

        }

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(700)
                    .playOn(cvHeadeers);
        } catch (Exception e) {

        }
    }

    private void initializeViews() {
        //ToolBar
        toolbar = findViewById(R.id.tb_main);

        //RecyclerView
        rvAttendance = findViewById(R.id.rvAttandance);

        //TextViews
        tvDateTo = findViewById(R.id.tvDateTo);
        tvDateFrom = findViewById(R.id.tvDateFrom);
        tvMessage = findViewById(R.id.tvMessage);

        //Button
        tvSearchButon = findViewById(R.id.tvSearchButon);

        //Layouts
        llTitles = findViewById(R.id.llTitles);
        llDataHistory = findViewById(R.id.llDataHistory);

        //CardViews
        cvHeadeers = findViewById(R.id.cvHeadeers);

        //Calender

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void getAttendanceList() {
        String url = SERVICE_URL+"GetAttendanceListByDateAndSalesRep?" +
                "startDate="+startDate +
                "&endDate="+endDate +
                "&userID="+dataLogin.getUserID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject object = Json.parse(response).asObject();
                        int id = object.get("ID").asInt();
                        if (id == 200) {
                            String newUrl = response.replace("{\"Data\":\"", "{\"Data\": ");
                            String new2Url = newUrl.replace("\",\"ID\":200}", "}");
                            String new3Url = new2Url.replace("\\", "");
                            new3Url = new3Url.trim();
                            System.out.println("History Response new3Url : " + new3Url);

                            Gson gson = new Gson();
                            AttendanceResponse attendanceResponse = gson.fromJson(new3Url, AttendanceResponse.class);
                            ArrayList<AttendaneDataBean> attendaneArrayList = attendanceResponse.getData();
                            System.out.println("Attendance ArrayList Size "+ attendaneArrayList.size());

                            //setRecyclerView
                            if (attendaneArrayList.size()>0){
                                tvMessage.setVisibility(View.GONE);
                                llTitles.setVisibility(View.VISIBLE);
                                rvAttendance.setVisibility(View.VISIBLE);
                                loadRecyclerView(attendaneArrayList);
                            }else {
                                progressDialog.dismiss();
                                tvMessage.setVisibility(View.VISIBLE);
                                llTitles.setVisibility(View.GONE);
                                rvAttendance.setVisibility(View.GONE);
                            }
                        } else {
                            System.out.println("No Data");
                            progressDialog.dismiss();
                            tvMessage.setVisibility(View.VISIBLE);
                            llTitles.setVisibility(View.GONE);
                            rvAttendance.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        llTitles.setVisibility(View.GONE);
                        System.out.println("Volley Error at History");
                        error.printStackTrace();
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void loadRecyclerView(ArrayList<AttendaneDataBean> attendaneArrayList) {
        rvAttendance.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                true
        );
        rvAttendance.setLayoutManager(layoutManager);
        AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getApplicationContext(),attendaneArrayList,dataLogin);
        rvAttendance.setAdapter(attendanceAdapter);
        progressDialog.dismiss();
    }

    @Override
    protected Dialog onCreateDialog(final int id) {

        if(id == 67) {
            return new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            showDate(year, month+1, dayOfMonth,id);
                        }
                    }, year, month, day);

        }

        if(id == 63) {
            return new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            showDate(year, month+1, dayOfMonth,id);
                        }
                    }, year, month, day);
        }
        return null;
    }

    private void showDate(int Year, int month, int day,int id) {
        String mnt= null;
        String dy = null;
        if(month < 10){
            mnt = "0"+month;
        }else if(month >= 10){
            mnt = ""+month;
        }
        if(day < 10){
            dy = "0"+day;
        }else if(day >= 10){
            dy = ""+day;
        }

        String  dateString = Year +"-"+mnt+"-"+dy;


        //Date Foremat for compare
        //Fri 28 July 2017 10:47:03
        if (id==67){
            tvDateFrom.setText(dateString);
            startDate = dateString;
            dateFrom = dateManager.getMilSecAccordingToDate( dy+"."+mnt+"."+Year);
            System.out.println("==== Date From Milliseconds =====" + dateFrom);
            System.out.println("==== Date From  =====" + dy+"."+mnt+"."+Year);
        }
        if (id==63){
            tvDateTo.setText(dateString);
            endDate = dateString;
            dateTo = dateManager.getMilSecAccordingToDate(dy+"."+mnt+"."+Year);
            System.out.println("==== Date To Milliseconds =====" + dateTo);
            System.out.println("==== Date To  =====" + dy+"."+mnt+"."+Year);
        }

    }

    private void displayStatusMessage(String s, int colorValue) {

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

        builder = new AlertDialog.Builder(HistoryActivity.this);
        view = getLayoutInflater().inflate(R.layout.layout_for_custom_message, null);

        tvOk = (TextView) view.findViewById(R.id.tvOk);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        imageView = (ImageView) view.findViewById(R.id.iv_status);

        tvMessage.setTextColor(getResources().getColor(color));
        tvMessage.setText(s);
        imageView.setImageResource(img);


        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
}
