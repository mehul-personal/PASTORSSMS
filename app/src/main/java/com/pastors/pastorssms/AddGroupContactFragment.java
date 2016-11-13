package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddGroupContactFragment.OnAddGroupContactFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddGroupContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGroupContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "GROUP_ID";
    private static final String ARG_PARAM2 = "PHONE_LIST";
    private static final String ARG_PARAM3 = "NAME_LIST";
    private static final String ARG_PARAM4 = "CALL";
    static ArrayList<HashMap<String, String>> CONTACT_DATA;
    static ArrayList<String> cnt_phone;
    static ArrayList<String> cnt_name, SELECTED_NUMBER_LIST;
    Button btnAddContactOnGroup;
    ListView lvContactList;
    ArrayList<HashMap<String, String>> dataList;
    ArrayList<String> checkbox_val;
    ContactListAdapter adapter;
    EditText edtSearch;
    Context mContext;
    ImageButton imbSearchCancle;
    // TODO: Rename and change types of parameters
    private String mParam1, mParam4;
    private ArrayList<String> mParam2, mParam3;
    private OnAddGroupContactFragmentInteractionListener mListener;

    public AddGroupContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddGroupContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGroupContactFragment newInstance(String param1, ArrayList<String> param2, ArrayList<String> param3) {
        AddGroupContactFragment fragment = new AddGroupContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2, param2);
        args.putStringArrayList(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cnt_phone = new ArrayList<String>();
            cnt_name = new ArrayList<String>();

            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam4 = getArguments().getString(ARG_PARAM4);
            if (mParam4.equalsIgnoreCase("UPDATE")) {
                mParam2 = getArguments().getStringArrayList(ARG_PARAM2);
                mParam3 = getArguments().getStringArrayList(ARG_PARAM3);

                cnt_phone = mParam2;
                cnt_name = mParam3;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_group_contact, container, false);
        mContext = getActivity();
        lvContactList = (ListView) view.findViewById(R.id.lvContactList);
        btnAddContactOnGroup = (Button) view.findViewById(R.id.btnAddContactOnGroup);
        imbSearchCancle = (ImageButton) view.findViewById(R.id.imbSearchCancle);
        Log.e("group id", mParam1 + "");
        SELECTED_NUMBER_LIST = new ArrayList<String>();
        dataList = new ArrayList<HashMap<String, String>>();
        checkbox_val = new ArrayList<String>();
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        btnAddContactOnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactOnGroup();
            }
        });
        // getGroupDetails(mParam1);
        imbSearchCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (edtSearch.getText().toString().isEmpty()) {
//                    adapter = new ContactListAdapter(dataList);
//                    lvContactList.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                } else {
                    searchContact(edtSearch.getText().toString());
//                }// new LoadContacts().execute(edtSearch.getText().toString());
            }
        });

        new LoadContacts().execute("");
        return view;
    }

    public void searchContact(String search) {

        ArrayList<HashMap<String, String>> searchContact = new ArrayList<HashMap<String, String>>();
        // checkbox_val = new ArrayList<String>();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).get("name").toLowerCase().contains(search) || dataList.get(i).get("number").toLowerCase().contains(search)) {
                HashMap<String, String> subData = new HashMap<String, String>();
                subData.put("name", dataList.get(i).get("name"));
                subData.put("number", dataList.get(i).get("number"));
                int flag = 0;
                Log.e("search number list",""+SELECTED_NUMBER_LIST.toString());
                for (int j = 0; j < SELECTED_NUMBER_LIST.size(); j++) {
                    if (dataList.get(i).get("number").equalsIgnoreCase(SELECTED_NUMBER_LIST.get(j))) {
                        flag++;
                    }
                }
                if (flag > 0) {
                    subData.put("check", "true");
                } else {
                    if (cnt_phone.toString().contains(dataList.get(i).get("number"))){
                        subData.put("check", "true");
                    }else{
                        subData.put("check", "false");
                    }

                }
                searchContact.add(subData);

//               if (cnt_phone.toString().contains(dataList.get(i).get("number")))
//                   checkbox_val.add("true");
//               else
//                   checkbox_val.add("false");
            }
        }
        adapter = new ContactListAdapter(searchContact);
        lvContactList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        if (context instanceof OnAddGroupContactFragmentInteractionListener) {
            mListener = (OnAddGroupContactFragmentInteractionListener) context;
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

    public void AddContactOnGroup() {
        JSONArray jsArr = null;
        try {
            jsArr = new JSONArray();
            for (int i = 0; i < CONTACT_DATA.size(); i++) {
                if (CONTACT_DATA.get(i).get("check").equalsIgnoreCase("true")) {
                    JSONObject ob = new JSONObject();
                    ob.put("name", CONTACT_DATA.get(i).get("name"));
                    ob.put("phone_no", CONTACT_DATA.get(i).get("number"));
                    jsArr.put(ob);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("response call", "" + jsArr.toString());
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "group/addtogroup";
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
                        Log.e("addtocontact", "data:" + response.toString());
                        ArrayList<String> groupId = new ArrayList<String>();
                        ArrayList<String> groupName = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                Toast.makeText(getActivity(),
                                        "Your group contacts added successfully!",
                                        Toast.LENGTH_SHORT).show();

                                Fragment defineDeliveryFragment = new GroupFragment();
                                if (defineDeliveryFragment != null) {
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                                    fragmentTransaction.commit();
                                }
                                AppCompatActivity activity = (AppCompatActivity) getActivity();
                                ActionBar actionBar = activity.getSupportActionBar();
                                actionBar.setTitle("GROUPS");
                                // getActivity().getActionBar().setTitle("GROUPS");
                                NavigationDrawerActivity.actionEditItem.setVisible(false);
                                NavigationDrawerActivity.actionViewItem.setVisible(true);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oops Your group contacts added failed!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we can't added your contacts on group \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("addtocontact Error", "Error: "
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
                params.put("lgn_id", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("group_contact_data", "" + finalJsArr.toString());
                params.put("grp_id", "" + mParam1);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }


    public boolean checkNumber(String string) {
        if (string.matches("\\d+(?:\\.\\d+)?")) {
            return true;
        } else {
            return false;
        }
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
    public interface OnAddGroupContactFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class LoadContacts extends AsyncTask<String, String, String> {
        ProgressDialog mProDialog;
        String phoneNumber, email;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String filter = "" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
            Cursor cursor = null;
            if (params[0].isEmpty())
                cursor = getActivity().getApplicationContext().getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            else if (!checkNumber(params[0]))
                cursor = getActivity().getApplicationContext().getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID}, ContactsContract.CommonDataKinds.Phone.NUMBER + "=" + params[0], null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            else if (!checkNumber(params[0]))
                cursor = getActivity().getApplicationContext().getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=" + params[0], null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            else
                cursor = getActivity().getApplicationContext().getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                String PhoneNo = contactNumber.replace(" ", "");
                //Log.e("contact data","data:"+dataList.toString());
                if (!dataList.toString().contains(PhoneNo)) {
                    HashMap<String, String> subData = new HashMap<String, String>();
                    subData.put("name", contactName);
                    subData.put("number", PhoneNo);

                    dataList.add(subData);
                    if (cnt_phone.toString().contains(PhoneNo)) {
                        // checkbox_val.add("true");
                        subData.put("check", "true");
                    } else {
                        subData.put("check", "false");
                    }
                }

                cursor.moveToNext();
            }
            cursor.close();
            cursor = null;
            Log.d("END", "Got all Contacts");


            mProDialog.dismiss();
            return "";
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mProDialog = new ProgressDialog(mContext);
            mProDialog.setMessage("Loading Contacts...");
            mProDialog.setCanceledOnTouchOutside(false);
            mProDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            //setData();
            adapter = new ContactListAdapter(dataList);
            lvContactList.setAdapter(adapter);
        }

    }

    public class ContactListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        int m = 0;

        public ContactListAdapter(ArrayList<HashMap<String, String>> contact_data) {
            // TODO Auto-generated constructor stub
            CONTACT_DATA = contact_data;

            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void refreshList() {
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            // View view = null;

            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(
                        R.layout.row_select_contact, parent, false);
                holder = new ViewHolder();
                holder.txvContactName = (TextView) convertView
                        .findViewById(R.id.txvContactName);
                holder.chbContact = (CheckBox) convertView
                        .findViewById(R.id.chbContact);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvContactName.setText(CONTACT_DATA.get(position).get("name"));
            if (CONTACT_DATA.get(position).get("check").equalsIgnoreCase("false")) {
                holder.chbContact.setChecked(false);
            } else {
                holder.chbContact.setChecked(true);
            }
            holder.chbContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("adapter add list",""+SELECTED_NUMBER_LIST.toString());
                    if (CONTACT_DATA.get(position).get("check").equalsIgnoreCase("false")) {
                        HashMap<String, String> subData = new HashMap<String, String>();
                        subData.put("name", CONTACT_DATA.get(position).get("name"));
                        subData.put("number", CONTACT_DATA.get(position).get("number"));
                        subData.put("check", "true");
                        CONTACT_DATA.set(position, subData);
                        //checkbox_val.set(position, "true");
                        SELECTED_NUMBER_LIST.add(CONTACT_DATA.get(position).get("number"));
                    } else if (CONTACT_DATA.get(position).get("check").equalsIgnoreCase("true")) {
                        HashMap<String, String> subData = new HashMap<String, String>();
                        subData.put("name", CONTACT_DATA.get(position).get("name"));
                        subData.put("number", CONTACT_DATA.get(position).get("number"));
                        subData.put("check", "false");
                        CONTACT_DATA.set(position, subData);

                        for (int i = 0; i < SELECTED_NUMBER_LIST.size(); i++)
                            if (SELECTED_NUMBER_LIST.get(i).equalsIgnoreCase(CONTACT_DATA.get(position).get("number"))) {
                                SELECTED_NUMBER_LIST.remove(i);
                            }
                    }
                }
            });


            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return CONTACT_DATA.size();
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
            TextView txvContactName;
            CheckBox chbContact;

        }
    }
}
