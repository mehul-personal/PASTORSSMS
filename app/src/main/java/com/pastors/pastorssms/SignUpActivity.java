package com.pastors.pastorssms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MeHuL on 12-04-2016.
 */
public class SignUpActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int mEnvironment = WalletConstants.ENVIRONMENT_TEST;
    public static final String PUBLISHABLE_KEY = "pk_test_Ey2yUQoLVzLhODC5DkiW8wQT";
    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;
    EditText edtUserName, edtUserPhone, edtUserEmail, edtPassword, edtCardHolderName,
            edtCardHolderAddress, edtCardHolderPostCode, edtCardHolderCity, edtCardHolderCountry,
            edtCardHolderState, edtCardNumber, edtCardYear, edtCardMonth, edtCardCVV;
    Button signup, redirectlogin;
    ImageView userimage;
    String selectedImagePath = "";
    RadioButton rbMale, rbFemale;
    TextView txvMale, txvFemale;
    String strCheckValue = "";
    String TAG = "BuyMoreCreditFragment";
    String strToken;
    private SupportWalletFragment walletFragment;
    private GoogleApiClient googleApiClient;
     ProgressDialog mProgressDialog;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (!isKitKat) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtUserPhone = (EditText) findViewById(R.id.edtUserPhone);
        edtUserEmail = (EditText) findViewById(R.id.edtUserEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        signup = (Button) findViewById(R.id.btnSignUp);
        userimage = (ImageView) findViewById(R.id.imvUserImage);
        rbFemale = (RadioButton) findViewById(R.id.rbFeMale);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        txvMale = (TextView) findViewById(R.id.txvMale);
        txvFemale = (TextView) findViewById(R.id.txvFeMale);
        redirectlogin = (Button) findViewById(R.id.btnRedirectLogin);
        edtCardHolderName = (EditText) findViewById(R.id.edtCardHolderName);
        edtCardHolderAddress = (EditText) findViewById(R.id.edtCardHolderAddress);
        edtCardHolderPostCode = (EditText) findViewById(R.id.edtCardHolderPostCode);
        edtCardHolderCity = (EditText) findViewById(R.id.edtCardHolderCity);
        edtCardHolderCountry = (EditText) findViewById(R.id.edtCardHolderCountry);
        edtCardHolderState = (EditText) findViewById(R.id.edtCardHolderState);
        edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
        edtCardYear = (EditText) findViewById(R.id.edtCardYear);
        edtCardMonth = (EditText) findViewById(R.id.edtCardMonth);
        edtCardCVV = (EditText) findViewById(R.id.edtCardCVV);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");

        walletFragment =
                (SupportWalletFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_fragment);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();
        Wallet.Payments.isReadyToPay(googleApiClient).setResultCallback(
                new ResultCallback<BooleanResult>() {
                    @Override
                    public void onResult(@NonNull BooleanResult booleanResult) {
                        if (booleanResult.getStatus().isSuccess()) {
                            if (booleanResult.getValue()) {
                                showAndroidPay();
                            } else {
                                // Hide Android Pay buttons, show a message that Android Pay
                                // cannot be used yet, and display a traditional checkout button
                            }
                        } else {
                            // Error making isReadyToPay call
                            Log.e(TAG, "isReadyToPay:" + booleanResult.getStatus());
                        }
                    }
                });


        Picasso.with(SignUpActivity.this).load(R.drawable.ic_no_image).transform(new CircleTransform())
                .into(userimage);
        redirectlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getStripeToken();
                if (edtUserName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter user name", Toast.LENGTH_LONG).show();
                } else if (edtUserPhone.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Phone No.", Toast.LENGTH_LONG).show();
                } else if (edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                } else if (edtUserEmail.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter email address", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(edtUserEmail.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                } else if (strCheckValue.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please select gender", Toast.LENGTH_LONG).show();
                }  if (edtCardHolderName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder Name", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderAddress.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder Address", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderPostCode.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder Postal Code", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderCity.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder City", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderCountry.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder Country", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderState.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Holder State", Toast.LENGTH_SHORT).show();
                } else if (edtCardNumber.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Number", Toast.LENGTH_SHORT).show();
                } else if (edtCardYear.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Year", Toast.LENGTH_SHORT).show();
                } else if (edtCardMonth.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card Month", Toast.LENGTH_SHORT).show();
                } else if (edtCardCVV.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Card CVV", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.show();
                    setSignUp();
                }
            }
        });
        rbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCheckValue = "1";
                rbFemale.setChecked(true);
                rbMale.setChecked(false);
            }
        });
        txvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCheckValue = "1";
                rbFemale.setChecked(true);
                rbMale.setChecked(false);
            }
        });

        rbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCheckValue = "0";
                rbFemale.setChecked(false);
                rbMale.setChecked(true);
            }
        });
        txvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCheckValue = "0";
                rbFemale.setChecked(false);
                rbMale.setChecked(true);
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    public void showAndroidPay() {


        MaskedWalletRequest maskedWalletRequest = MaskedWalletRequest.newBuilder()

                // Request credit card tokenization with Stripe by specifying tokenization parameters:
                .setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                        .addParameter("gateway", "stripe")
                        .addParameter("stripe:publishableKey", PUBLISHABLE_KEY)
                        .addParameter("stripe:version", com.stripe.Stripe.VERSION)
                        .build())

                // You want the shipping address:
                .setShippingAddressRequired(true)

                // Price set as a decimal:
                .setEstimatedTotalPrice("20.00")
                .setCurrencyCode("USD")
                .build();

        // Set the parameters:
        WalletFragmentInitParams initParams = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(LOAD_MASKED_WALLET_REQUEST_CODE)
                .build();

        // Initialize the fragment:
        walletFragment.initialize(initParams);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            System.out.println("Content Path : " + selectedImage.toString());
            selectedImagePath = getPath(SignUpActivity.this, selectedImage);
            Log.e("selected image", selectedImagePath + "");
            if (selectedImage != null) {
                userimage
                        .setImageBitmap(getCircleBitmap(getScaledBitmap(selectedImage)));
            } else {
                selectedImagePath = "";
                Toast.makeText(SignUpActivity.this, "Error getting Image",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(SignUpActivity.this, "No Photo Selected",
                    Toast.LENGTH_SHORT).show();
            selectedImagePath = "";
        } else if (requestCode == LOAD_MASKED_WALLET_REQUEST_CODE) { // Unique, identifying constant
            if (resultCode == Activity.RESULT_OK) {
                MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                        .setCart(Cart.newBuilder()
                                .setCurrencyCode("USD")
                                .setTotalPrice("20.00")
                                .addLineItem(LineItem.newBuilder() // Identify item being purchased
                                        .setCurrencyCode("USD")
                                        .setQuantity("1")
                                        .setDescription("Premium Llama Food")
                                        .setTotalPrice("20.00")
                                        .setUnitPrice("20.00")
                                        .build())
                                .build())
                        .setGoogleTransactionId(maskedWallet.getGoogleTransactionId())
                        .build();
                Wallet.Payments.loadFullWallet(googleApiClient, fullWalletRequest, LOAD_FULL_WALLET_REQUEST_CODE);
            }
        } else if (requestCode == LOAD_FULL_WALLET_REQUEST_CODE) { // Unique, identifying constant
            if (resultCode == Activity.RESULT_OK) {
                FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                String tokenJSON = fullWallet.getPaymentMethodToken().getToken();

                //A token will only be returned in production mode,
                //i.e. WalletConstants.ENVIRONMENT_PRODUCTION
                if (mEnvironment == WalletConstants.ENVIRONMENT_PRODUCTION) {
                    com.stripe.model.Token token = com.stripe.model.Token.GSON.fromJson(
                            tokenJSON, com.stripe.model.Token.class);

                    // TODO: send token to your server
                }
            }
        }
    }
    public void setSignUp() {
        final String str_user_name = edtUserName.getText().toString();
        final String str_userphone = edtUserPhone.getText().toString();
        final String str_email = edtUserEmail.getText().toString();
        final String str_password = edtPassword.getText().toString();
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "user/signup");

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//                    if (!selectedImagePath.isEmpty())
//                        entity.addPart("usr_image", new FileBody(new File(selectedImagePath)));
                    entity.addPart("usr_image", new StringBody(""));
                    entity.addPart("lgn_email", new StringBody("" + str_email));
                    entity.addPart("lgn_password", new StringBody("" + str_password));
                    entity.addPart("lgn_device_imei_no", new StringBody("11"));
                    entity.addPart("lgn_gcm_no", new StringBody("11"));
                    entity.addPart("lgn_type", new StringBody("0"));
                    entity.addPart("usr_name", new StringBody("" + str_user_name));
                    entity.addPart("usr_phone", new StringBody("" + str_userphone));
                    entity.addPart("usr_gender", new StringBody("" + strCheckValue));

                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();
                    Log.e("signup data", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    Log.e("signup problem", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                try {
                    JSONObject object = new JSONObject(result.toString());
                    Log.e("2", "2");

                    String msg = object.getString("STATUS");

//                    String data=msgArr.getString(0);
//                    if(data.equalsIgnoreCase("Emailid already exists.")){

//                    }
                    if (msg.equalsIgnoreCase("SUCCESS")) {
                        String smsg = object.getString("MESSAGES");

                        JSONObject dataOb = object.getJSONObject("DATA");
                        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("ID", dataOb.getString("lgn_id"));
                        edit.commit();

                        getStripeToken();
//                        Toast.makeText(SignUpActivity.this,
//                                "Your account registered Successfully!",
//                                Toast.LENGTH_LONG).show();

                    } else {
                        JSONArray msgArr = object.getJSONArray("MESSAGES");
//                        Toast.makeText(SignUpActivity.this, msgArr.toString().replace("\"", "").replace("[", "").replace("]", ""),
//                                Toast.LENGTH_LONG).show();
                        Toast.makeText(SignUpActivity.this,
                                "Oopss! You are adding wrong data please check it!",
                                Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    mProgressDialog.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
//                mProgressDialog = new ProgressDialog(SignUpActivity.this);
//                mProgressDialog.setTitle("");
//                mProgressDialog.setCanceledOnTouchOutside(false);
//                mProgressDialog.setMessage("Please Wait...");
//                mProgressDialog.show();

            }
        }.execute();
    }
    public void getStripeToken() {


            try {
                Card card = new Card(edtCardNumber.getText().toString(), Integer.parseInt(edtCardMonth.getText().toString()), Integer.parseInt(edtCardYear.getText().toString()), edtCardCVV.getText().toString());
                if (!card.validateCard()) {
                    Toast.makeText(this, "Please enter valid card detail", Toast.LENGTH_SHORT).show();
                } else {
                    Stripe stripe = new Stripe(PUBLISHABLE_KEY);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    strToken = token.getId();
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
//                                    builder.setCancelable(false);
//                                    builder.setMessage("Token:" + strToken + "\n" + "Do you want to continue payment?");
//                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
                                    Log.e("token","token:"+strToken);
                                            setCardDetails(strToken);
//                                        }
//                                    });
//                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                                    AlertDialog alert = builder.create();
//                                    alert.show();
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    mProgressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, error.toString(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );

                }
            } catch (Exception e) {
            }



    }

    public void setCardDetails(final String token) {

        if (edtCardHolderName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder Name", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder Address", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderPostCode.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder Postal Code", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder City", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCountry.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder Country", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderState.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Card Holder State", Toast.LENGTH_SHORT).show();
        } else {
            String tag_json_obj = "json_obj_req";
            String url = ApplicationData.serviceURL + "user/addcarddetail";


            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.e("card holder adding", response.toString());
                            try {
                              //  mProgressDialog.dismiss();
                                JSONObject object = new JSONObject(response.toString());

                                String status = object.getString("STATUS");
                                if (status.equalsIgnoreCase("SUCCESS")) {
                                    JSONObject dataOb = object.getJSONObject("DATA");


                                    setPaymentProcess(dataOb.getString("package_reff_id"), token);
                                } else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this,
                                            "Sorry! Your Card Detail Adding Failed \n" +
                                                    " Please check card details!",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                mProgressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(SignUpActivity.this,
                                        "Sorry! Your Card Detail Adding Failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                                if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                    Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    VolleyLog.e("card holder adding Error", "Error: "
                            + error.getMessage());
                    // hide the progress dialog
                    error.getCause();
                    error.printStackTrace();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lgn_id", preferences.getString("ID", ""));
                    params.put("cd_name", edtCardHolderName.getText().toString());
                    params.put("cd_address", edtCardHolderAddress.getText().toString());
                    params.put("cd_zipcode", edtCardHolderPostCode.getText().toString());
                    params.put("cd_country", edtCardHolderCountry.getText().toString());
                    params.put("cd_state", edtCardHolderState.getText().toString());
                    params.put("cd_city", edtCardHolderCity.getText().toString());
                    params.put("package_id", "7");
                    params.put("prs_type", "1");

                    params.put("cd_payment_type", "0");
                    return params;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
            // Adding request to request queue
            ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                    tag_json_obj);
        }
    }

    public void setPaymentProcess(final String package_reff_id, final String token) {


        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "payment/paymentprocess";
//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle("");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("payment process", response.toString());
                        mProgressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");

                                Toast.makeText(SignUpActivity.this, "You have registered successfully \n Please login your account",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Your registered failed \n Please check all details",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            mProgressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this,
                                    "Your registered failed \n Please check all details",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("payment process Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);

                Map<String, String> params = new HashMap<String, String>();
                Log.i("id:",""+ preferences.getString("ID", ""));
                params.put("prs_lgn_id", preferences.getString("ID", ""));
                params.put("prs_type", "1");
                params.put("prs_token", token);
                params.put("prs_pr_id", package_reff_id);
                params.put("prs_pc_id", "7");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }



    private Bitmap getScaledBitmap(Uri uri) {
        Bitmap thumb = null, rotatedBitmap = null;
        try {
            ContentResolver cr = getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize=12;
            thumb = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeStream(in, null, options), 100, 100,
                    true);
            // Matrix matrix = new Matrix();
            // matrix.postRotate(90);
            // rotatedBitmap = Bitmap.createBitmap(thumb, 0, 0,
            // thumb.getWidth(), thumb.getHeight(), matrix, true);
        } catch (FileNotFoundException e) {
            // Toast.makeText(sliderContext, "File not found",
            // Toast.LENGTH_SHORT)
            // .show();
        }
        return thumb;
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            // bitmap.recycle();

            return output;
        } catch (Exception e) {
            output = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_no_image);
            return output;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

/*
    public void setForgotPassword() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "forgotpassword";
        final ProgressDialog mProgressDialog = new ProgressDialog(SignUpActivity.this);
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
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Sorry! Your verification code sending failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this,
                                    "Sorry! Your verification code sending failed \n" +
                                            " Please try again!", Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_email", "" + edtUserEmail.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
*/

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
