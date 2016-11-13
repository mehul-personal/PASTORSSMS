package com.pastors.pastorssms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuyMoreCreditFragment.OnCardHolderFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuyMoreCreditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyMoreCreditFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // You will need to use your live API key even while testing
    public static final String PUBLISHABLE_KEY = "pk_test_Ey2yUQoLVzLhODC5DkiW8wQT";
    public static final int mEnvironment = WalletConstants.ENVIRONMENT_TEST;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;
    static String strPaymentMethod = "";
    static String strToken = "";
    Button btnCancel, btnMakePayment;
    LinearLayout llCardHolderInfo, llPaymentMethod;
    ImageView imvVisa, imvPaypal;
    TextView txvVisa, txvPaypal;
    EditText edtCardHolderName, edtCardHolderAddress, edtCardHolderPostCode,
            edtCardHolderCity, edtCardHolderCountry, edtCardHolderState, edtCardNumber, edtCardYear, edtCardMonth, edtCardCVV;
    ScrollView svCardHolderList;
    ListView lsvPackageSelection;
    ArrayList<String> package_id;
    String selected_package_id = "";
    String TAG = "BuyMoreCreditFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnCardHolderFragmentInteractionListener mListener;
    private SupportWalletFragment walletFragment;
    private GoogleApiClient googleApiClient;
    public BuyMoreCreditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyMoreCreditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyMoreCreditFragment newInstance(String param1, String param2) {
        BuyMoreCreditFragment fragment = new BuyMoreCreditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_holder, container, false);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnMakePayment = (Button) view.findViewById(R.id.btnMakePayment);
        llCardHolderInfo = (LinearLayout) view.findViewById(R.id.llCardHolderInfo);
        llPaymentMethod = (LinearLayout) view.findViewById(R.id.llPaymentMethod);
        imvVisa = (ImageView) view.findViewById(R.id.imvVisa);
        imvPaypal = (ImageView) view.findViewById(R.id.imvPaypal);
        txvPaypal = (TextView) view.findViewById(R.id.txvPaypal);
        txvVisa = (TextView) view.findViewById(R.id.txvVisa);
        edtCardHolderName = (EditText) view.findViewById(R.id.edtCardHolderName);
        edtCardHolderAddress = (EditText) view.findViewById(R.id.edtCardHolderAddress);
        edtCardHolderPostCode = (EditText) view.findViewById(R.id.edtCardHolderPostCode);
        edtCardHolderCity = (EditText) view.findViewById(R.id.edtCardHolderCity);
        edtCardHolderCountry = (EditText) view.findViewById(R.id.edtCardHolderCountry);
        edtCardHolderState = (EditText) view.findViewById(R.id.edtCardHolderState);
        svCardHolderList = (ScrollView) view.findViewById(R.id.svCardHolderList);
        lsvPackageSelection = (ListView) view.findViewById(R.id.lsvPackageSelection);
        edtCardNumber = (EditText) view.findViewById(R.id.edtCardNumber);
        edtCardYear = (EditText) view.findViewById(R.id.edtCardYear);
        edtCardMonth = (EditText) view.findViewById(R.id.edtCardMonth);
        edtCardCVV = (EditText) view.findViewById(R.id.edtCardCVV);

        walletFragment =
                (SupportWalletFragment) getFragmentManager().findFragmentById(R.id.wallet_fragment);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();

        lsvPackageSelection.setVisibility(View.VISIBLE);
        imvVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPaymentMethod = "VISA";
                //  setCardDetails();
                getStripeToken();
            }
        });
        imvPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPaymentMethod = "PAYPAL";
                //  setCardDetails();
                getStripeToken();
            }
        });
        txvPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPaymentMethod = "PAYPAL";
                //  setCardDetails();
                getStripeToken();
            }
        });
        txvVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPaymentMethod = "VISA";
                //  setCardDetails();
                getStripeToken();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new MainMenuFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.commit();
                }
            }
        });
        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCardHolderName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder Name", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderAddress.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder Address", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderPostCode.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder Postal Code", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderCity.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder City", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderCountry.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder Country", Toast.LENGTH_SHORT).show();
                } else if (edtCardHolderState.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Card Holder State", Toast.LENGTH_SHORT).show();
                } else {
                    svCardHolderList.setVisibility(View.GONE);
                    llPaymentMethod.setVisibility(View.VISIBLE);
                }
            }
        });
        llCardHolderInfo.setVisibility(View.VISIBLE);
        llPaymentMethod.setVisibility(View.GONE);
        lsvPackageSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_LONG).show();
                if (!package_id.get(position).isEmpty()) {
                    selected_package_id = package_id.get(position);
                    lsvPackageSelection.setVisibility(View.GONE);
                    svCardHolderList.setVisibility(View.VISIBLE);
                    Log.e("selected package_id", "" + selected_package_id);
                }
            }
        });
        getPackages();

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

        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_MASKED_WALLET_REQUEST_CODE) { // Unique, identifying constant
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCardHolderFragmentInteractionListener) {
            mListener = (OnCardHolderFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getPackages() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "credit/getpackage";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response.toString());
                        ArrayList<String> id = new ArrayList<String>();
                        ArrayList<String> name = new ArrayList<String>();
                        ArrayList<String> price = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");
                                JSONArray feature_package = dataOb.getJSONArray("featured_package");
                                JSONArray additional_package = dataOb.getJSONArray("additional_package");
                                id.add("");
                                name.add("FEATURED PACKAGE");
                                price.add("");
                                for (int i = 0; i < feature_package.length(); i++) {
                                    JSONObject featureOb = feature_package.getJSONObject(i);
                                    id.add(featureOb.getString("pc_id"));
                                    name.add(featureOb.getString("pc_name"));
                                    price.add(featureOb.getString("pc_price"));
                                }
                                id.add("");
                                name.add("ADDITIONAL PACKAGE");
                                price.add("");
                                for (int i = 0; i < additional_package.length(); i++) {
                                    JSONObject additionalOb = additional_package.getJSONObject(i);
                                    id.add(additionalOb.getString("pc_id"));
                                    name.add(additionalOb.getString("pc_name"));
                                    price.add(additionalOb.getString("pc_price"));
                                }
                                PackageListAdapter adapter = new PackageListAdapter(id, name, price);
                                lsvPackageSelection.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! No Any Package Available!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getStripeToken() {
        if (edtCardHolderName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Name", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderAddress.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Address", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderPostCode.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Postal Code", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCity.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder City", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCountry.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Country", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderState.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder State", Toast.LENGTH_SHORT).show();
        } else if (edtCardNumber.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Number", Toast.LENGTH_SHORT).show();
        } else if (edtCardYear.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Year", Toast.LENGTH_SHORT).show();
        } else if (edtCardMonth.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Month", Toast.LENGTH_SHORT).show();
        } else if (edtCardCVV.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card CVV", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.show();
            try {
                Card card = new Card(edtCardNumber.getText().toString(), Integer.parseInt(edtCardMonth.getText().toString()), Integer.parseInt(edtCardYear.getText().toString()), edtCardCVV.getText().toString());
                if (!card.validateCard()) {
                    Toast.makeText(getActivity(), "Please enter valid card detail", Toast.LENGTH_SHORT).show();
                } else {
                    Stripe stripe = new Stripe(PUBLISHABLE_KEY);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    mProgressDialog.dismiss();
                                    strToken = token.getId();

//                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                    builder.setCancelable(false);
//                                    builder.setMessage("Token:" + strToken + "\n" + "Do you want to continue payment?");
//                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
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
                                    Toast.makeText(getContext(), error.toString(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void setCardDetails(final String token) {

        if (edtCardHolderName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Name", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderAddress.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Address", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderPostCode.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Postal Code", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCity.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder City", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderCountry.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder Country", Toast.LENGTH_SHORT).show();
        } else if (edtCardHolderState.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Card Holder State", Toast.LENGTH_SHORT).show();
        } else {
            String tag_json_obj = "json_obj_req";
            String url = ApplicationData.serviceURL + "user/addcarddetail";
            final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.show();

            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.e("card holder adding", response.toString());
                            try {
                                mProgressDialog.dismiss();
                                JSONObject object = new JSONObject(response.toString());

                                String status = object.getString("STATUS");
                                if (status.equalsIgnoreCase("SUCCESS")) {
                                    JSONObject dataOb = object.getJSONObject("DATA");


                                    setPaymentProcess(dataOb.getString("package_reff_id"), token);
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Sorry! Your Card Detail Adding Failed \n" +
                                                    " Please try again!",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Toast.makeText(getActivity(),
                                        "Sorry! Your Card Detail Adding Failed \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                                if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lgn_id", preferences.getString("ID", ""));
                    params.put("cd_name", edtCardHolderName.getText().toString());
                    params.put("cd_address", edtCardHolderAddress.getText().toString());
                    params.put("cd_zipcode", edtCardHolderPostCode.getText().toString());
                    params.put("cd_country", edtCardHolderCountry.getText().toString());
                    params.put("cd_state", edtCardHolderState.getText().toString());
                    params.put("cd_city", edtCardHolderCity.getText().toString());
                    params.put("package_id", selected_package_id);
                    params.put("prs_type", "0");
                    if (strPaymentMethod.equalsIgnoreCase("PAYPAL"))
                        params.put("cd_payment_type", "0");
                    else
                        params.put("cd_payment_type", "1");
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
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("card holder adding", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");


                                Toast.makeText(getActivity(),
                                        "Your Package Payment Successfully Completed",
                                        Toast.LENGTH_SHORT).show();

                                Fragment defineDeliveryFragment = new MainMenuFragment();
                                if (defineDeliveryFragment != null) {
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                                    fragmentTransaction.commit();
                                }
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! Your Package Payment Failure \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! Your Package Payment Failure \n" +
                                            " Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);

                Map<String, String> params = new HashMap<String, String>();
                params.put("prs_lgn_id", preferences.getString("ID", ""));
                params.put("prs_type", "0");
                params.put("prs_token", token);
                params.put("prs_pr_id", package_reff_id);
                params.put("prs_pc_id", selected_package_id);
                params.put("prs_type", "0");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCardHolderFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PackageListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> package_name, package_amount;

        int m = 0;

        public PackageListAdapter(ArrayList<String> package_id_list, ArrayList<String> package_name_list, ArrayList<String> package_amount_list) {
            // TODO Auto-generated constructor stub
            package_id = package_id_list;
            package_name = package_name_list;
            package_amount = package_amount_list;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.package_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvPackageName = (TextView) convertView.findViewById(R.id.txvPackageName);
                holder.txvPackageAmount = (TextView) convertView.findViewById(R.id.txvPackageAmount);
                holder.imvArrow = (ImageView) convertView.findViewById(R.id.imvArrow);
                //holder.llTitle = (LinearLayout) convertView.findViewById(R.id.llPackageDetail);
                holder.llPackageDetail = (LinearLayout) convertView.findViewById(R.id.llPackageDetail);
                holder.txvPackageTitle = (TextView) convertView.findViewById(R.id.txvPackageTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (package_name.get(position).equalsIgnoreCase("FEATURED PACKAGE")) {
                holder.txvPackageTitle.setVisibility(View.VISIBLE);
                holder.llPackageDetail.setVisibility(View.GONE);
                holder.txvPackageTitle.setText(package_name.get(position));
            } else if (package_name.get(position).equalsIgnoreCase("ADDITIONAL PACKAGE")) {
                holder.txvPackageTitle.setVisibility(View.VISIBLE);
                holder.llPackageDetail.setVisibility(View.GONE);
                holder.txvPackageTitle.setText(package_name.get(position));
            } else {
                holder.txvPackageTitle.setVisibility(View.GONE);
                holder.llPackageDetail.setVisibility(View.VISIBLE);
                holder.txvPackageName.setText(package_name.get(position));
                holder.txvPackageAmount.setText(package_amount.get(position));
            }
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return package_amount.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            TextView txvPackageName, txvPackageAmount, txvPackageTitle;
            LinearLayout llTitle, llPackageDetail;
            ImageView imvArrow;
        }
    }
}
