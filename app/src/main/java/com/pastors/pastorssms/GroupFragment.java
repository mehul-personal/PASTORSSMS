package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
 * {@link GroupFragment.OnGroupFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ArrayList<String> group_id, group_name;
    ListView lsvGroupList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnGroupFragmentInteractionListener mListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        lsvGroupList = (ListView) view.findViewById(R.id.lsvGroupList);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("GROUPS");
        NavigationDrawerActivity.actionViewItem.setVisible(true);
        NavigationDrawerActivity.actionEditItem.setVisible(false);

        getGroupDetails();
        lsvGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //getGroupContactDetails(group_id.get(position), position);
                Fragment defineDeliveryFragment = new EditGroupFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("ADDCONTACT");
                    Bundle bundle = new Bundle();
                    bundle.putString("GROUP_ID", "" + group_id.get(position));
                    bundle.putString("GROUP_NAME", "" + group_name.get(position));
//                    bundle.putStringArrayList("PHONE_LIST", cnt_phone);
//                    bundle.putString("NAME_LIST", "" + cnt_name);
                    defineDeliveryFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle(group_name.get(position));
                // getActivity().getActionBar().setTitle("GROUPS");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
                NavigationDrawerActivity.actionEditItem.setVisible(true);

            }
        });
        //registerForContextMenu(lsvGroupList);
        return view;
    }

    //    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
//    {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Select The Action");
//        menu.add(0, v.getId(), 0, "Delete");//groupId, itemId, order, title
//    }
//    @Override
//    public boolean onContextItemSelected(MenuItem item){
//        if(item.getTitle()=="Delete"){
//
//        }
//        else{
//            return false;
//        }
//        return true;
//    }
    public void deleteGroup(final String groupid) {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "group/deletegroup";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("delete group", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                Toast.makeText(getActivity(),
                                        "Your group deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                                getGroupDetails();
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
                VolleyLog.e("delete group Error", "Error: "
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
                params.put("grp_id", groupid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
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
                                }
                                GroupListAdapter adapter = new GroupListAdapter(groupId, groupName);
                                lsvGroupList.setAdapter(adapter);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupFragmentInteractionListener) {
            mListener = (OnGroupFragmentInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGroupFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class GroupListAdapter extends BaseAdapter {

        LayoutInflater inflater;


        int m = 0;

        public GroupListAdapter(ArrayList<String> group_id_list, ArrayList<String> group_name_list) {
            // TODO Auto-generated constructor stub
            group_id = group_id_list;
            group_name = group_name_list;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.group_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvGroupName = (TextView) convertView.findViewById(R.id.txvGroupName);
                holder.imvGroupArrow = (ImageView) convertView.findViewById(R.id.imvGroupArrow);
                holder.delete = (ImageView) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvGroupName.setText(group_name.get(position));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGroup(group_id.get(position));
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return group_id.size();
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
            TextView txvGroupName;
            ImageView imvGroupArrow, delete;
        }
    }
}