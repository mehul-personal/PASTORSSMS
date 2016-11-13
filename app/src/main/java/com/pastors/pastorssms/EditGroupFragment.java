package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
 * {@link EditGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "GROUP_ID";
    private static final String ARG_PARAM2 = "GROUP_NAME";
    ListView lsvGroupList;
    ArrayList<String> cnt_phone, cnt_name;
    // TODO: Rename and change types of parameters
    private String GROUP_ID;
    private String GROUP_NAME;

    private OnFragmentInteractionListener mListener;

    public EditGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditGroupFragment newInstance(String param1, String param2) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setHasOptionsMenu(true);
        if (getArguments() != null) {
            GROUP_ID = getArguments().getString(ARG_PARAM1);
            GROUP_NAME = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 15) {
                if (data.getStringExtra("msg").equalsIgnoreCase("contact")) {
                    Fragment defineDeliveryFragment = new AddGroupContactFragment();
                    if (defineDeliveryFragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                        fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                        fragmentTransaction.addToBackStack("ADDCONTACT");
                        Bundle bundle = new Bundle();
                        bundle.putString("CALL","UPDATE");
                        bundle.putString("GROUP_ID", "" + data.getStringExtra("GROUP_ID"));
                        bundle.putStringArrayList("PHONE_LIST", cnt_phone);
                        bundle.putStringArrayList("NAME_LIST", cnt_name);
                        defineDeliveryFragment.setArguments(bundle);
                        fragmentTransaction.commit();
                    }
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    ActionBar actionBar = activity.getSupportActionBar();
                    actionBar.setTitle(data.getStringExtra("GROUP_NAME"));
                    GROUP_NAME = data.getStringExtra("GROUP_NAME");
                    // getActivity().getActionBar().setTitle("GROUPS");
                    NavigationDrawerActivity.actionViewItem.setVisible(false);
                    NavigationDrawerActivity.actionEditItem.setVisible(false);
                }else{
                    Fragment defineDeliveryFragment = new ScheduleFragment();
                    if (defineDeliveryFragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                        fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                        fragmentTransaction.addToBackStack("MESSAGE");
                        Bundle bundle = new Bundle();
                        bundle.putString("CALL", "SELECTED");
                        bundle.putString("GROUP_ID", "" + data.getStringExtra("GROUP_ID"));
                        defineDeliveryFragment.setArguments(bundle);
                        fragmentTransaction.commit();
                    }
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    ActionBar actionBar = activity.getSupportActionBar();
                    actionBar.setTitle("MESSAGES");
                    NavigationDrawerActivity.actionViewItem.setVisible(false);
                    NavigationDrawerActivity.actionEditItem.setVisible(false);
                }

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        lsvGroupList = (ListView) view.findViewById(R.id.lsvGroupList);
        //
        getGroupContactDetails(GROUP_ID);
        lsvGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });
        NavigationDrawerActivity.actionViewItem.setVisible(false);
        NavigationDrawerActivity.actionEditItem.setVisible(true);

        View v = MenuItemCompat.getActionView(NavigationDrawerActivity.actionEditItem);
        ImageView edit = (ImageView) v.findViewById(R.id.txvCustomEditAction);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), CreateGroupActivity.class);
                i.putExtra("CALL", "EDIT");
                i.putExtra("GROUP_ID", "" + GROUP_ID);
                i.putExtra("GROUP_NAME", "" + GROUP_NAME);
                startActivityForResult(i, 15);


            }
        });
        return view;
    }

    public void getGroupContactDetails(final String groupid) {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "contact/fetchallcontactbyloginid";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("fetchgroupbyloginid", "response:" + response.toString());
                        ArrayList<String> groupId = new ArrayList<String>();
                        ArrayList<String> groupName = new ArrayList<String>();
                        HashMap<String, ArrayList<String>> groupContact = new HashMap<String, ArrayList<String>>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONArray dataArr = object.getJSONArray("DATA");

                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject groupDataOb = dataArr.getJSONObject(i);
                                    groupId.add(groupDataOb.getString("grp_id"));
                                    groupName.add(groupDataOb.getString("grp_name"));

                                    cnt_phone = new ArrayList<String>();
                                    cnt_name = new ArrayList<String>();
                                    try {
                                        JSONArray contact_data = groupDataOb.getJSONArray("grp_contact");
                                        for (int j = 0; j < contact_data.length(); j++) {
                                            JSONObject contact_ob = contact_data.getJSONObject(j);
                                            cnt_phone.add(contact_ob.getString("cnt_phone"));
                                            cnt_name.add(contact_ob.getString("cnt_name"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    groupContact.put("PHONE" + groupDataOb.getString("grp_id"), cnt_phone);
                                    groupContact.put("NAME" + groupDataOb.getString("grp_id"), cnt_name);
                                }
                                ContactListAdapter cAdapter = new ContactListAdapter(cnt_name, cnt_phone);
                                lsvGroupList.setAdapter(cAdapter);

                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we are stuff to fetching group data. \n" +
                                                " Please try again!",
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
                            // new LoadContacts().execute("");
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
                params.put("lgn_id", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("grp_id", groupid);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    //    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.navigation_create_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {


            return true;
        }

        return false;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ContactListAdapter extends BaseAdapter {

        LayoutInflater inflater;

        ArrayList<String> NAME_LIST, NUMBER_LIST;
        int m = 0;

        public ContactListAdapter(ArrayList<String> name_list, ArrayList<String> number_list) {
            // TODO Auto-generated constructor stub
            NAME_LIST = name_list;
            NUMBER_LIST = number_list;
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
                        R.layout.row_groupdetail_listitem, parent, false);
                holder = new ViewHolder();
                holder.txvContactName = (TextView) convertView
                        .findViewById(R.id.txvContactName);
                holder.txvPhoneNumber = (TextView) convertView
                        .findViewById(R.id.txvPhoneNumber);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvContactName.setText(NAME_LIST.get(position));
            holder.txvPhoneNumber.setText(NUMBER_LIST.get(position));
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NAME_LIST.size();
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
            TextView txvContactName, txvPhoneNumber;
        }
    }
}
