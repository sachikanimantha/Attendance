package com.example.bellvantage.smartTracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.example.bellvantage.smartTracking.DB.MySingleton;
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.SWF.ResponseVolley;
import com.example.bellvantage.smartTracking.Utils.DateManager;
import com.example.bellvantage.smartTracking.Utils.NetworkConnection;
import com.google.gson.Gson;

import dmax.dialog.SpotsDialog;

import static com.example.bellvantage.smartTracking.web.HTTPPaths.SERVICE_URL;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    //btnSignUp;
    ConstraintLayout rootLayout;


    final static int PERMISIONS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkConnection.checkNetworkConnection(MainActivity.this)){
                    displayStatusMessage("Mobile network is not available. Please enable", 2);
                    return;
                }
                showLoginDialog();
            }
        });

       /* btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });*/
    }

    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Login");
        dialog.setMessage("Please Use Username & Password to Sign In");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View loginLayout = layoutInflater.inflate(R.layout.layout_for_login, null);
        final TextInputEditText email = loginLayout.findViewById(R.id.etEmail);
        final TextInputEditText password = loginLayout.findViewById(R.id.etPassword);
        dialog.setView(loginLayout);

        //Set Buttons
        dialog.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                String el = email.getText().toString();
                String pas = password.getText().toString();

                String currentDate = new DateManager().getDateWithTime().trim();
                currentDate = currentDate.replace(" ","%20");
                
             


                if (el.isEmpty() || pas.isEmpty()) {
                    waitingDialog.dismiss();
                    //Toast.makeText(MainActivity.this, "Please fill valid username and password", Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Please fill valid username and password", 2);


                } else {
                    String url = SERVICE_URL + "GetDefLogin?userName=" + el + "&userPassword=" + pas + "&lastLoginDate="+ currentDate;
                    System.out.println("Login URL: " + url);

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
                                        System.out.println("Login Response new3Url : " + new3Url);


                                        Gson gson = new Gson();
                                        ResponseVolley defaultResponse = gson.fromJson(new3Url, ResponseVolley.class);
                                        DataLogin dataLogin = defaultResponse.getData();

                                        String lastLoginDate = dataLogin.getLastLoginDate();

                                        int start = lastLoginDate.indexOf("(");
                                        int end = lastLoginDate.indexOf(")");
                                        lastLoginDate = lastLoginDate.substring(start+1,end);
                                       // long milCurrentDate = new DateManager().todayMillsec();
                                        long milCurrentDate = 946665000000L;
                                        long milLastLoginDate = 0;

                                        try{
                                            if (!lastLoginDate.isEmpty() || !lastLoginDate.equals("")){
                                                milLastLoginDate = Long.parseLong(lastLoginDate);
                                            }
                                        }catch (Exception e){
                                            System.out.println("error at convert last login date into milliseconds");
                                            e.printStackTrace();
                                        }
                                        System.out.println("Mill Current: "+ milCurrentDate + " MilLastDate: "+ milLastLoginDate);
                                        //holder.tvInTime.setText(new DateManager().getDateAccordingToMil(Long.parseLong(inTime), "HH:mm:ss"));



                                        waitingDialog.dismiss();
                                        if (milCurrentDate>=milLastLoginDate){
                                            Intent intent = new Intent(MainActivity.this, PasswordResetActivity.class);
                                            intent.putExtra("DataLogin",dataLogin);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                                            intent.putExtra("DataLogin",dataLogin);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                    } else {
                                        waitingDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    waitingDialog.dismiss();
                                    System.out.println("Login Volley Error");
                                    displayStatusMessage("Something went wrong.. Please try again", 2);
                                    error.printStackTrace();
                                }
                            });
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                    /*if (el.equals("admin")&& pas.equals("admin")){
                        Intent intent = new Intent(MainActivity.this,UserAreaActivity.class);
                        startActivity(intent);
                    }else {
                        displayStatusMessage("Invalid username or password",2);
                       // Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }*/
                }

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Please Use Email to Register");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View registerLayout = layoutInflater.inflate(R.layout.layout_for_register, null);
        final TextInputEditText email = registerLayout.findViewById(R.id.etEmail);
        final TextInputEditText password = registerLayout.findViewById(R.id.etPassword);
        final TextInputEditText name = registerLayout.findViewById(R.id.etName);
        final TextInputEditText phone = registerLayout.findViewById(R.id.etPhone);
        dialog.setView(registerLayout);

        //Set Buttons
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Snackbar.make(rootLayout, "Please Enter Email Address", Snackbar.LENGTH_SHORT);

                //Waiting Dialog
                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                        name.getText().toString().isEmpty() || phone.getText().toString().isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Please fill all the details", 2);
                    waitingDialog.dismiss();
                    return;
                }
                // Toast.makeText(MainActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                displayStatusMessage("Registration is successful", 2);
                waitingDialog.dismiss();


            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        dialog.show();

    }

    private void initializeViews() {

        //ConstraintLayout
        rootLayout = findViewById(R.id.rootLayout);

        //Buttons
        btnLogin = findViewById(R.id.btnLogin);
        // btnSignUp = findViewById(R.id.btnSignUp);
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

        builder = new AlertDialog.Builder(MainActivity.this);
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
