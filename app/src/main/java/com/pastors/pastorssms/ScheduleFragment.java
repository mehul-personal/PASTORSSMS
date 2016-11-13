package com.pastors.pastorssms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnScheduleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "GROUP_ID";
    private static final String ARG_PARAM2 = "CALL";
    public static String Day = "", Month = "", Year = "", Minit = "", Hours = "", getDtae = "", getTime = "";
    static ArrayList<String> GROUPIDADAPTER, CHECKLISTADAPTER;
    Dialog DateTimeDialog;
    TextView txvSetDate, txvmsgchar;
    Button btnDialogCancle, btnDialogOk;
    DatePicker dpDatePicker;
    TimePicker tpTimePicker;
    ExpandableHeightGridView ehgGroupList;
    EditText edtPhone, edtSubject, edtMessages, edtDeliveryTime;
    Button btnSave;
    ImageView ivCalender;
    String selecteddate = "", selectedtime = "";
    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";
    private OnScheduleFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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
            mParam2 = getArguments().getString(ARG_PARAM2);
            if (mParam2.equalsIgnoreCase("SELECTED"))
                mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ehgGroupList = (ExpandableHeightGridView) view.findViewById(R.id.ehgGroupList);
        ehgGroupList.setExpanded(true);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);
        edtSubject = (EditText) view.findViewById(R.id.edtSubject);
        edtMessages = (EditText) view.findViewById(R.id.edtMessages);
        edtDeliveryTime = (EditText) view.findViewById(R.id.edtDeliveryTime);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        ivCalender = (ImageView) view.findViewById(R.id.ivCalender);
        txvmsgchar = (TextView) view.findViewById(R.id.txvmsgchar);

        DateTimeDialog = new Dialog(getActivity());
        DateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DateTimeDialog.setContentView(R.layout.dialog_date_details);
        DateTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txvSetDate = (TextView) DateTimeDialog.findViewById(R.id.txvSetDate);
        dpDatePicker = (DatePicker) DateTimeDialog.findViewById(R.id.dpDatePicker);
        tpTimePicker = (TimePicker) DateTimeDialog.findViewById(R.id.tpTimePicker);
        btnDialogCancle = (Button) DateTimeDialog.findViewById(R.id.btnDialogCancle);
        btnDialogOk = (Button) DateTimeDialog.findViewById(R.id.btnDialogOk);
        getGroupDetails();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        edtMessages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txvmsgchar.setText((160 - edtMessages.getText().toString().length()) + " Remaining Characters");
            }
        });
        edtDeliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        ivCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        return view;
    }

    public void setTime() {
        DateTimeDialog.show();
        btnDialogCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeDialog.dismiss();
            }
        });

        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Year = String.valueOf(dpDatePicker.getYear());
                Month = String.format("%02d", (dpDatePicker.getMonth() + 1));
                Day = String.format("%02d", dpDatePicker.getDayOfMonth());

                Hours = String.format("%02d", tpTimePicker.getCurrentHour());
                Minit = String.format("%02d", tpTimePicker.getCurrentMinute());

                getDtae = String.valueOf(Month + "/" + Day + "/" + Year);

                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                Log.e("currentDateTimeString", currentDateTimeString);
                SimpleDateFormat df;
                Date CurrentDD, EndDD;

                Calendar c = Calendar.getInstance();
                df = new SimpleDateFormat("MM/dd/yyyy");
                String CurrentDate = df.format(c.getTime());

                Date dt = new Date();
                int hours = dt.getHours();
                int minutes = dt.getMinutes();
                int seconds = dt.getSeconds();

                String curTime = hours + ":" + minutes;
                getTime = Hours + ":" + Minit;//showTime(Integer.parseInt(Hours), Integer.parseInt(Minit));

                try {
                    CurrentDD = df.parse(CurrentDate);
                    EndDD = df.parse(getDtae);

                    if (CurrentDD.compareTo(EndDD) < 0) {

                        Log.e("CurrentDD......", CurrentDD.toString());
                        Log.e("EndDD......", EndDD.toString());

                        edtDeliveryTime.setText(getDtae + " " + getTime);
                        DateTimeDialog.dismiss();

                    } else if (CurrentDD.equals(EndDD)) {

                        Log.e("CurrentDD......else if ", CurrentDD.toString());
                        Log.e("EndDD......else if ", EndDD.toString());

                        if (curTime.compareTo(Hours + ":" + Minit) >= 0) {

                            Log.e("curTime......", curTime.toString());
                            Log.e("getTime......", Hours + ":" + Minit);

                            edtDeliveryTime.setText(getDtae + " " + getTime);
                            DateTimeDialog.dismiss();

                        } else {

                            Log.e("curTime......else : ", curTime.toString());
                            Log.e("getTime......else : ", Hours + ":" + Minit);
                            getTime = "";
                            Toast.makeText(getActivity(), "Selected time should be greater than current time", Toast.LENGTH_SHORT).show();

                        }
                    } else {

                        Log.e("CurrentDD else", CurrentDD.toString());
                        Log.e("EndDD......else", EndDD.toString());

                        getDtae = "";

                        Toast.makeText(getActivity(), "Selected date should be greater than current date", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                selecteddate = getDtae;
                selectedtime = getTime;
                Log.e("Get Date ", " : " + Year + Month + Day);
                Log.e("Get Time ", " : " + Minit + Hours);

            }
        });

    }

    public String showTime(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return (new StringBuilder().append(hour).append(":").append(min)
                .append(" ").append(format)).toString();
    }

    public void getGroupDetails() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "group/fetchgroupbyloginid";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("fetchgroupbyloginid", response.toString());
                        ArrayList<String> groupId = new ArrayList<String>();
                        ArrayList<String> groupName = new ArrayList<String>();
                        CHECKLISTADAPTER = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataOb = object.getJSONObject("DATA");
                                JSONArray dataArr = dataOb.getJSONArray(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));

                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject groupDataOb = dataArr.getJSONObject(i);
                                    groupId.add(groupDataOb.getString("grp_id"));
                                    groupName.add(groupDataOb.getString("grp_name"));
                                    if (mParam1.equalsIgnoreCase(groupDataOb.getString("grp_id")))
                                        CHECKLISTADAPTER.add("true");
                                    else
                                        CHECKLISTADAPTER.add("false");
                                }
                                GroupListAdapter adapter = new GroupListAdapter(groupId, groupName);
                                ehgGroupList.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oops You have not created any group!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
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
                VolleyLog.e("fetchgroupbyloginid Error", "Error: "
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
                params.put("grp_lgn_id", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void sendSMS() {
        JSONArray jsArr = null;
        try {
            jsArr = new JSONArray();
            for (int i = 0; i < CHECKLISTADAPTER.size(); i++) {
                if (CHECKLISTADAPTER.get(i).equalsIgnoreCase("true")) {
                    JSONObject ob = new JSONObject();
                    ob.put("mg_grp_id", GROUPIDADAPTER.get(i));
                    jsArr.put(ob);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("array", "" + jsArr.toString());
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/sendmessage";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();


        final JSONArray finalJsArr = jsArr;
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("send message", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                Toast.makeText(getActivity(),
                                        "Your schedule message sent successfully",
                                        Toast.LENGTH_SHORT).show();

                                Fragment defineDeliveryFragment = new MainMenuFragment();
                                if (defineDeliveryFragment != null) {
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                                    fragmentTransaction.commit();
                                }
                                AppCompatActivity activity = (AppCompatActivity) getActivity();
                                ActionBar actionBar = activity.getSupportActionBar();
                                actionBar.setTitle("MAIN MENU");
                                // getActivity().getActionBar().setTitle("GROUPS");
                                NavigationDrawerActivity.actionViewItem.setVisible(true);
                            } else {
                                if (status.equalsIgnoreCase("FAIL")) {
                                    JSONArray msgArray = object.getJSONArray("MESSAGES");

                                    Toast.makeText(getActivity(), msgArray.getString(0),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Oops We can't schedule your message !",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to send data. \n Please try again!",
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
                VolleyLog.e("send message Error", "Error: "
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

                Log.e("message param", "loginid" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "") + "" + edtPhone.getText().toString() + "," + edtMessages.getText().toString() + "," + finalJsArr.toString() + "," + selecteddate + "," + selectedtime);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_id", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("msg_phone_no", "" + edtPhone.getText().toString());
                params.put("msg_text", "" + edtMessages.getText().toString());
                if (finalJsArr.length() > 0) {
                    params.put("mg_grp_id", "" + finalJsArr.toString());
                }
                params.put("msg_delivery_date", "" + selecteddate);
                params.put("msg_delivery_time", "" + selectedtime);


                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScheduleFragmentInteractionListener) {
            mListener = (OnScheduleFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnScheduleFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class GroupListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> TitleList;

        public GroupListAdapter(ArrayList<String> categoryId,
                                ArrayList<String> title) {
            GROUPIDADAPTER = categoryId;
            TitleList = title;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return TitleList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_merchant_category, null);
                holder = new ViewHolder();
                holder.categoryName = (TextView) convertView.findViewById(R.id.txvMerchantCategory);
                holder.categoryCheckbox = (CheckBox) convertView.findViewById(R.id.chbMerchantCategory);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.categoryName.setText(TitleList.get(position));
            holder.categoryCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CHECKLISTADAPTER.get(position).equalsIgnoreCase("false")) {
                        CHECKLISTADAPTER.set(position, "true");
                    } else {
                        CHECKLISTADAPTER.set(position, "false");
                    }
                }
            });
            if (CHECKLISTADAPTER.get(position).equalsIgnoreCase("false")) {
                holder.categoryCheckbox.setChecked(false);
            } else {
                holder.categoryCheckbox.setChecked(true);
            }
            return convertView;
        }

        class ViewHolder {
            TextView categoryName;
            CheckBox categoryCheckbox;
        }

    }
}
