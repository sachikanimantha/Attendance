package com.example.bellvantage.smartTracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.bellvantage.smartTracking.DB.MySingleton;
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.SWF.ResponsePasswordChange;
import com.example.bellvantage.smartTracking.Utils.NetworkConnection;
import com.google.gson.Gson;

import static com.example.bellvantage.smartTracking.web.HTTPPaths.SERVICE_URL;

public class PasswordResetActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextInputLayout tilOldPassword, tilNewPassword, tilConfirmPassword;
    Button btnChangePassword;

    ProgressDialog progressDialog;
    LinearLayout llData;

    DataLogin dataLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        initialzeViews();

        try {
            YoYo.with(Techniques.ZoomIn)
                    .duration(700)
                    .playOn(llData);
        } catch (Exception e) {

        }

        if (getIntent().getSerializableExtra("DataLogin") != null) {
            dataLogin = (DataLogin) getIntent().getSerializableExtra("DataLogin");
        }

        progressDialog = new ProgressDialog(this);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = tilOldPassword.getEditText().getText().toString();
                String newPassword = tilNewPassword.getEditText().getText().toString();
                String confirmPassword = tilConfirmPassword.getEditText().getText().toString();

                System.out.println(newPassword + " : "+ newPassword.length());

                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                   // Toast.makeText(PasswordResetActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Enter all fields",2);
                    return;
                }


                if (!oldPassword.equals(dataLogin.getUserPassword())) {
                   // Toast.makeText(PasswordResetActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Old password is incorrect",2);
                    return;
                }
                if (newPassword.length()>10){
                    displayStatusMessage("Password must be 10 characters or less in length",2);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    //Toast.makeText(PasswordResetActivity.this, "Password confirmation is fail", Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Password confirmation is fail",2);
                    return;
                }
                if (!NetworkConnection.checkNetworkConnection(PasswordResetActivity.this)){
                    displayStatusMessage("Mobile network is not available. Please enable", 2);
                    return;
                }
                progressDialog.setTitle("Loading...");
                progressDialog.setMessage("Please wait while we change your password..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                changePassword(dataLogin.getUserID(), newPassword);

            }
        });

        toolbar.setTitle("Change Password");
    }

    private void changePassword(int userid, final String newPassword) {
        String url = SERVICE_URL + "UpdatePassword?" +
                "userID=" + userid +
                "&newUserName=" + dataLogin.getUserName() +
                "&newUserPassword=" + newPassword;

        System.out.println("change password url: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                System.out.println("Password change response: "+response.toString());
                Gson gson = new Gson();
                ResponsePasswordChange responsePasswordChange =gson.fromJson(response,ResponsePasswordChange.class);
                int id = responsePasswordChange.getID();
                String data = responsePasswordChange.getData();
                if (id==200){
                    //Toast.makeText(PasswordResetActivity.this, data, Toast.LENGTH_SHORT).show();
                    dataLogin.setUserPassword(newPassword);
                    //displayStatusMessage(data,1);
                    Toast.makeText(PasswordResetActivity.this, data ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordResetActivity.this,UserAreaActivity.class);
                    intent.putExtra("DataLogin",dataLogin);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    //Toast.makeText(PasswordResetActivity.this, "Password change fail..." ,Toast.LENGTH_SHORT).show();
                    displayStatusMessage("Password change fail...",2);
                }

            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                System.out.println("======== Error at password change ===============");
                displayStatusMessage("Password change fail...",2);
                error.printStackTrace();
            }
        }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initialzeViews() {
        toolbar = findViewById(R.id.tb_main);

        //TextInputLayouts
        tilOldPassword = findViewById(R.id.tilOldPassword);
        tilNewPassword = findViewById(R.id.tilnewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        //Button
        btnChangePassword = findViewById(R.id.btnChangePassword);

        //Layouts
        llData = findViewById(R.id.llData);
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

        builder = new AlertDialog.Builder(PasswordResetActivity.this);
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
