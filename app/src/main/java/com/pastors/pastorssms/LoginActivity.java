package com.pastors.pastorssms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    ImageButton btnTwitter;
    private ProfileTracker profileTracker;
    List<String> permision = Arrays.asList("user_friends", "email");
    String fbemail, fbid, fbfirstname, fblastname, fbuserImage;
    public static String twitterFullname, twitter_image, twitter_id;
    public static Twitter twitter;
    private OAuthProvider provider;
    private CommonsHttpOAuthConsumer consumer;
    Button btnLogin, btnSignUp;
    EditText edtUserName, edtPassword;
    TextView txvForgotPassword;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            getUserProfile(accessToken);
            Log.e("facebook ", "callback" + profile);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_login);
        GenerateKeyhash();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.e("facebook ", "access tocke" + AccessToken.getCurrentAccessToken());
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e("facebook ", "Profile tracker" + newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        btnTwitter = (ImageButton) findViewById(R.id.imbTwitterLogin);
        loginButton = (LoginButton) findViewById(R.id.btn_FacebookLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txvForgotPassword = (TextView) findViewById(R.id.txvForgotPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        loginButton.setReadPermissions(permision);
        loginButton.registerCallback(callbackManager, callback);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setBackgroundResource(R.drawable.ic_fb);
        loginButton.setText("");

        getConsumerProvider();
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askOAuth();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtUserName.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the email address", Toast.LENGTH_LONG).show();
                } else if (edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the password", Toast.LENGTH_LONG).show();
                } else {
                    checkAuthentication();
                }
            }
        });
        txvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
        if (!preferences.getString("ID", "").isEmpty()) {
            if (preferences.getString("ROLE", "").equalsIgnoreCase("LOGIN")) {
                Intent i = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                startActivity(i);
                finish();
            } else if (preferences.getString("ROLE", "").equalsIgnoreCase("FORGOT")) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        }

    }

    public void checkAuthentication() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/login";
        final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
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

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ROLE", "LOGIN");
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                edit.putString("EMAIL", dataOb.getString("lgn_email"));
                                edit.putString("NAME", dataOb.getString("usr_name"));
                                edit.putString("PHONE", dataOb.getString("usr_phone"));
                                edit.putString("IMAGE", dataOb.getString("usr_image"));
                                edit.putString("GENDER", dataOb.getString("usr_gender"));
                                edit.commit();

                                Intent i = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                JSONArray msgArr=object.getJSONArray("MESSAGES");
                                Toast.makeText(LoginActivity.this, msgArr.toString().replace("\"", "").replace("[", "").replace("]", ""),
                                Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_email", "" + edtUserName.getText().toString());
                params.put("lgn_password", "" + edtPassword.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void twitterAuthentication() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/twitterlogin";
        final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
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

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ROLE", "LOGIN");
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                //edit.putString("EMAIL", dataOb.getString("lgn_email"));
                                edit.putString("NAME", dataOb.getString("usr_name"));
                                //edit.putString("PHONE", dataOb.getString("usr_phone"));
                                edit.putString("IMAGE", dataOb.getString("usr_image"));
                                //edit.putString("GENDER", dataOb.getString("usr_gender"));
                                edit.commit();

                                Intent i = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Sorry! Your Authentication Failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_twitter_auth_id", "" + twitter_id);
                params.put("lgn_device_imei_no", "434");
                params.put("lgn_gcm_no", "4345");
                params.put("usr_name", "" + twitterFullname);
                params.put("usr_image", "" + twitter_image);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void facebookAuthentication(final String fbid, final String email, final String name, final String image) {
        final String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/facebooklogin";
        final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        final StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
//{"STATUS":"SUCCESS","MESSAGES":"User signedup successfully.","DATA":{"lgn_id":"40","usr_id":"35","lgn_fb_auth_id":"1691430211137125"}}

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");

                                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("ROLE", "LOGIN");
                                edit.putString("ID", dataOb.getString("lgn_id"));
                                edit.putString("EMAIL", email);
                                edit.putString("NAME", name);
                                //edit.putString("PHONE", dataOb.getString("usr_phone"));
                                edit.putString("IMAGE", image);
                                //edit.putString("GENDER", dataOb.getString("usr_gender"));
                                edit.commit();

                                Intent i = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Sorry! Your Authentication Failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                JSONObject dataob = null;
                try {
                    dataob = new JSONObject();
                    dataob.put("email", "" + email);
                    dataob.put("id", "" + fbid);
                    dataob.put("name", "" + name);

                    JSONObject pictureob = new JSONObject();
                    JSONObject picturesubob = new JSONObject();
                    picturesubob.put("is_silhouette", "0");
                    picturesubob.put("url", "" + image);
                    pictureob.put("data", picturesubob);

                    dataob.put("picture", pictureob);
                    Log.e("fb response", "" + dataob.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_fb_auth_id", "" + fbid);
                params.put("lgn_device_imei_no", "434");
                params.put("lgn_gcm_no", "4345");
                params.put("fr_response", "" + dataob.toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }


    private void askOAuth() {
        try {
            consumer = new CommonsHttpOAuthConsumer(
                    ApplicationData.CONSUMER_KEY,
                    ApplicationData.CONSUMER_SECRET);
            provider = new DefaultOAuthProvider(
                    "https://api.twitter.com/oauth/request_token",
                    "https://api.twitter.com/oauth/access_token",
                    "https://api.twitter.com/oauth/authorize");
            String authUrl = provider.retrieveRequestToken(consumer,
                    ApplicationData.CALLBACK_URL);
            setConsumerProvider();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getConsumerProvider() {
        OAuthProvider p = ((ApplicationData) getApplication()).getProvider();
        if (p != null) {
            provider = p;
        }
        CommonsHttpOAuthConsumer c = ((ApplicationData) getApplication())
                .getConsumer();
        if (c != null) {
            consumer = c;
        }
    }

    private void setConsumerProvider() {
        if (provider != null) {
            ((ApplicationData) getApplication()).setProvider(provider);
        }
        if (consumer != null) {
            ((ApplicationData) getApplication()).setConsumer(consumer);
        }
    }

    public void GenerateKeyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.pastors.pastorssms", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private void storeAccessToken(twitter4j.auth.AccessToken a) {
        try {
            SharedPreferences settings = getSharedPreferences("LOGIN_DETAIL",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("access_token", a.getToken());
            editor.putString("accessTokenSecret", a.getTokenSecret());
            editor.putString("user_id", a.getUserId() + "");
            Log.e("tweet user_id", a.getUserId() + "");

            editor.putString("Twitter_id", String.valueOf(a.getUserId()));

            long userID = a.getUserId();
            User user = twitter.showUser(userID);
            editor.putString("name", user.getName());
            editor.commit();
            Log.e("twitter name", "" + user.getName());
            twitterFullname = user.getName();
            twitter_id = a.getUserId() + "";
            twitter_image = user.getOriginalProfileImageURL();
        } catch (Exception e) {
            Log.e("twitter get info error", e + "");
        }
    }

    private void startFirstActivity() {

        System.out.println("STARTING FIRST ACTIVITY!");
        twitterAuthentication();
        // Intent i = new Intent(AllRegisterActivity.this,
        // NavigationMainDrawerActivity.class);
        // startActivity(i);
        // checkFbTwitter("TWITTER", twitter_id, twitterFullname, "");
        /*SharedPreferences sharedPreferences = getSharedPreferences(
                "LOGIN_DETAIL", 0);
		Editor editor = sharedPreferences.edit();
		editor.putString("REGISTER", "TWITTER");
		editor.putString("ID", twitter_id);
		editor.putString("FULL_NAME", twitterFullname);
		// editor.putString("EMAIL", email);
		// Log.e("Twitter Email", email);
		editor.putString("IMAGE_URL", twitter_image);
		editor.commit();
		RegisterApiForFBandTwitter();*/

        //CheckTwitterIDExist(twitter_id);

//		Intent i=new Intent(LoginActivity.this,SignUp.class);
//		i.putExtra("TYPE", "TWITTER");
//		i.putExtra("IMAGE_URL", twitter_image);
//		i.putExtra("TWITTER_ID",twitter_id);
//		i.putExtra("FULL_NAME",twitterFullname);
//		startActivity(i);
    }

    public void getUserProfile(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("FacebookData", "Response : " + response.toString());
                        JSONObject datajson = response.getJSONObject();
                        Log.e("datajson", "Response : " + datajson);
                        fbemail = "";
                        try {
                            fbid = datajson.getString("id");

                            fbfirstname = datajson.getString("first_name");
                            fblastname = datajson.getString("last_name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            fbemail = datajson.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fbuserImage = ("https://graph.facebook.com/" + fbid + "/picture?type=large");
                        Log.e("userImage", "Response : " + fbuserImage);
                        //checkFacebookId();
                        facebookAuthentication(fbid, fbemail, fbfirstname + " " + fblastname, fbuserImage);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("#########", "Login  : " + data);

    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        if (this.getIntent() != null && this.getIntent().getData() != null) {
            Uri uri = this.getIntent().getData();
            if (uri != null
                    && uri.toString().startsWith(ApplicationData.CALLBACK_URL)) {
                String verifier = uri
                        .getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
                try {
                    // this will populate token and token_secret in consumer
                    provider.retrieveAccessToken(consumer, verifier);

                    // initialize Twitter4J
                    twitter = new TwitterFactory().getInstance();
                    twitter.setOAuthConsumer(ApplicationData.CONSUMER_KEY,
                            ApplicationData.CONSUMER_SECRET);

                    // Get Access Token and persist it
                    twitter4j.auth.AccessToken a = new twitter4j.auth.AccessToken(consumer.getToken(),
                            consumer.getTokenSecret());
                    twitter.setOAuthAccessToken(a);
                    storeAccessToken(a);
                    ((ApplicationData) getApplication()).setTwitter(twitter);

                    startFirstActivity();

                } catch (Exception e) {
                    // Log.e(APP, e.getMessage());
                    e.printStackTrace();
                    // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                    // .show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginManager.getInstance().logOut();
        System.exit(0);
        Log.e("Button", "Back Pressed method");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void finish() {
        super.finish();
        LoginManager.getInstance().logOut();
    }
}
