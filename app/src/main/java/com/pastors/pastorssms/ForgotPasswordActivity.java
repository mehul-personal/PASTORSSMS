package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MeHuL on 12-04-2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    static String callStatus = "FORGOT";
    EditText emailaddress, edtVerifyCode, edtNewPassword, edtConfirmPassword;
    Button submit;
    LinearLayout llChangePassword, llForgotPassword, llPastorLogo;
    RelativeLayout llVerificationCode;
    TextView txvEnterPSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailaddress = (EditText) findViewById(R.id.edtUserName);
        submit = (Button) findViewById(R.id.btnSubmit);
        edtVerifyCode = (EditText) findViewById(R.id.edtVerifyCode);
        edtNewPassword = (EditText) findViewById(R.id.edtNewPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        llVerificationCode = (RelativeLayout) findViewById(R.id.llVerificationCode);
        llChangePassword = (LinearLayout) findViewById(R.id.llChangePassword);
        llForgotPassword = (LinearLayout) findViewById(R.id.llForgotEmail);
        llPastorLogo = (LinearLayout) findViewById(R.id.llPastorLogo);
        txvEnterPSMS = (TextView) findViewById(R.id.txvEnterPSMS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");

        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
        callStatus = preferences.getString("CALLSTATUS", "FORGOT");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callStatus.equalsIgnoreCase("FORGOT")) {
                    if (emailaddress.getText().toString().isEmpty()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter email address", Toast.LENGTH_LONG).show();
                    } else if (!isValidEmail(emailaddress.getText().toString())) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                    } else {
                        setForgotPassword();
                    }
                } else if (callStatus.equalsIgnoreCase("CHANGEPASSWORD")) {
                    if (edtNewPassword.getText().toString().isEmpty()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter new password", Toast.LENGTH_LONG).show();
                    } else if (edtConfirmPassword.getText().toString().isEmpty()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter confirm password", Toast.LENGTH_LONG).show();
                    } else if (!edtNewPassword.getText().toString().equalsIgnoreCase(edtConfirmPassword.getText().toString())) {
                        Toast.makeText(ForgotPasswordActivity.this, "Your password doesn't match", Toast.LENGTH_LONG).show();
                    } else {
                        setChangePassword();
                    }
                }
            }
        });
        txvEnterPSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callStatus.equalsIgnoreCase("VERIFYCODE")) {
                    if (edtVerifyCode.getText().toString().isEmpty()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter verification code", Toast.LENGTH_LONG).show();
                    } else {
                        setVerifyCode();
                    }
                }
            }
        });
        if (callStatus.equalsIgnoreCase("FORGOT")) {
            llForgotPassword.setVisibility(View.VISIBLE);
            llVerificationCode.setVisibility(View.GONE);
            llChangePassword.setVisibility(View.GONE);
            llPastorLogo.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        } else if (callStatus.equalsIgnoreCase("VERIFYCODE")) {
            llPastorLogo.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            llForgotPassword.setVisibility(View.GONE);
            llVerificationCode.setVisibility(View.VISIBLE);
            llChangePassword.setVisibility(View.GONE);
        } else if (callStatus.equalsIgnoreCase("CHANGEPASSWORD")) {
            llForgotPassword.setVisibility(View.GONE);
            llVerificationCode.setVisibility(View.GONE);
            llChangePassword.setVisibility(View.VISIBLE);
            llPastorLogo.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        System.out.println(email);
        String emailExpression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

    public void setForgotPassword() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/forgotpassword";
        final ProgressDialog mProgressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");
                                String login_id = dataOb.getString("lgn_id");
                                String user_id = dataOb.getString("usr_id");
                                String verification_code = dataOb.getString("Verification Code");

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                edit.putString("VERIFICATION_CODE", dataOb.getString("Verification Code"));
                                edit.putString("ROLE", "FORGOT");
                                edit.putString("CALLSTATUS", "VERIFYCODE");
                                edit.commit();
                                callStatus = "VERIFYCODE";
                                if (callStatus.equalsIgnoreCase("VERIFYCODE")) {
                                    llForgotPassword.setVisibility(View.GONE);
                                    llVerificationCode.setVisibility(View.VISIBLE);
                                    llChangePassword.setVisibility(View.GONE);
                                    llPastorLogo.setVisibility(View.GONE);
                                    submit.setVisibility(View.GONE);
                                }
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Please enter verification code",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Sorry! Your verification code sending failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Sorry! Your verification code sending failed \n" +
                                            " Please try again!", Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("login Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_email", "" + emailaddress.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void setVerifyCode() {
        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
        if (edtVerifyCode.getText().toString().equalsIgnoreCase(preferences.getString("VERIFICATION_CODE", ""))) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("ROLE", "FORGOT");
            edit.putString("CALLSTATUS", "CHANGEPASSWORD");
            edit.commit();
            callStatus = "CHANGEPASSWORD";
            if (callStatus.equalsIgnoreCase("CHANGEPASSWORD")) {
                llForgotPassword.setVisibility(View.GONE);
                llVerificationCode.setVisibility(View.GONE);
                llChangePassword.setVisibility(View.VISIBLE);
                llPastorLogo.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
            Toast.makeText(ForgotPasswordActivity.this,
                    "Your verification code verified successfully \n Please enter new Password",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ForgotPasswordActivity.this,
                    "Sorry! Your verification code sending failed \n" +
                            " Please try again!",
                    Toast.LENGTH_SHORT).show();
        }
       /* String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "forgotpassword";
        final ProgressDialog mProgressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");
                                String login_id = dataOb.getString("lgn_id");

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                edit.putString("ROLE", "FORGOT");
                                edit.putString("CALLSTATUS", "CHANGEPASSWORD");
                                edit.commit();
                                callStatus = "CHANGEPASSWORD";
                                if (callStatus.equalsIgnoreCase("CHANGEPASSWORD")) {
                                    llForgotPassword.setVisibility(View.GONE);
                                    llVerificationCode.setVisibility(View.GONE);
                                    llChangePassword.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Your verification code verified successfully \n Please enter new Password",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Sorry! Your verification code sending failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Sorry! Your verification code sending failed \n" +
                                            " Please try again!", Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("login Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_id", "" + preferences.getString("ID", ""));
                params.put("lgn_fgtpass_verifycode", "" + edtVerifyCode.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);*/
    }

    public void setChangePassword() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/newpassword";
        final ProgressDialog mProgressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");
                                String login_id = dataOb.getString("lgn_id");

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                edit.putString("ROLE", "LOGIN");
                                edit.putString("CALLSTATUS", "");
                                edit.commit();
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Your new password updated successfully \nPlease Login",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Sorry! Your new password updating failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Sorry! Your new password updating failed \n" +
                                            " Please try again!", Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("login Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_id", "" + preferences.getString("ID", ""));
                params.put("new_password", "" + edtNewPassword.getText().toString());
                params.put("cnfm_password", "" + edtConfirmPassword.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
