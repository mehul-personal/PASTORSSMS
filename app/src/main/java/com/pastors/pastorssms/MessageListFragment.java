package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView messagelist;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
ArrayList<String> msg_text,msg_creadted_datetime, msgId ;
    private OnFragmentInteractionListener mListener;

    public MessageListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
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
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        messagelist = (ListView) view.findViewById(R.id.lsvMessageList);
        getMessageDetails();
        messagelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment defineDeliveryFragment = new MessageDetailFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("Message");
                    Bundle bundle = new Bundle();
                    bundle.putString("MSGID",msgId.get(position));
                    bundle.putString("MSG", "" + msg_text.get(position));
                    bundle.putString("MSGTIME", "" + msg_creadted_datetime .get(position).toUpperCase());
                    defineDeliveryFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("MESSAGES");
                // getActivity().getActionBar().setTitle("GROUPS");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
                NavigationDrawerActivity.actionEditItem.setVisible(false);
            }
        });
        return view;
    }

    public void getMessageDetails() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "message/getmessage";
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("getmessage", response.toString());
                        msgId = new ArrayList<String>();
                        ArrayList<String> groupName = new ArrayList<String>();
                         msg_text = new ArrayList<String>();
                        msg_creadted_datetime = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONArray dataArr = object.getJSONArray("DATA");

                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject groupDataOb = dataArr.getJSONObject(i);
                                    msgId.add(groupDataOb.getString("msg_id"));
                                    groupName.add(groupDataOb.getString("grp_name"));
                                    msg_text.add(groupDataOb.getString("msg_text"));
                                    msg_creadted_datetime.add(groupDataOb.getString("msg_createddate"));
                                }
                                GroupListAdapter adapter = new GroupListAdapter(msgId, groupName, msg_text, msg_creadted_datetime);
                                messagelist.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oops we are stuff to fetching data!",
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
                params.put("lgn_id", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
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

    public class GroupListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> msg_id_list, group_name_list, msg_text_list, create_date_list;

        int m = 0;

        public GroupListAdapter(ArrayList<String> msgId, ArrayList<String> groupName, ArrayList<String> msg_text,
                                ArrayList<String> msg_creadted_datetime) {
            // TODO Auto-generated constructor stub
            msg_id_list = msgId;
            group_name_list = groupName;
            msg_text_list = msg_text;
            create_date_list = msg_creadted_datetime;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.message_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvGroupName = (TextView) convertView.findViewById(R.id.txvGroupName);
                holder.txvMessageContent = (TextView) convertView.findViewById(R.id.txvMessageContent);
                holder.imvMessageImage = (ImageView) convertView.findViewById(R.id.imvMessageImage);
                holder.txvMessageDate = (TextView) convertView.findViewById(R.id.txvMessageDate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvGroupName.setText(group_name_list.get(position));
            holder.txvMessageContent.setText(msg_text_list.get(position));
            holder.txvMessageDate.setText(create_date_list.get(position));
            Picasso.with(getActivity())
                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(holder.imvMessageImage);
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return msg_id_list.size();
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
            TextView txvMessageContent, txvGroupName, txvMessageDate;
            ImageView imvMessageImage;
        }
    }

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
