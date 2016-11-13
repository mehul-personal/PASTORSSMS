package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
 * {@link ContactFragment.OnContactFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ArrayList<HashMap<String, String>> CONTACT_DATA;
    ImageButton imbSearchCancle;
    EditText edtSearch;
    ListView ContactList;
    ArrayList<HashMap<String, String>> dataList;
    ArrayList<String> CONTACT_NO, CONTACT_NAME;
    ContactListAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnContactFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        imbSearchCancle = (ImageButton) view.findViewById(R.id.imbSearchCancle);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);
        ContactList = (ListView) view.findViewById(R.id.lvContactList);
        dataList = new ArrayList<HashMap<String, String>>();


        getServerContacts();
        return view;
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
        if (context instanceof OnContactFragmentInteractionListener) {
            mListener = (OnContactFragmentInteractionListener) context;
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

    public void getServerContacts() {
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
                        Log.e("get server contacts", response.toString());
                        CONTACT_NO = new ArrayList<String>();
                        CONTACT_NAME = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONArray dataOb = object.getJSONArray("DATA");
                                if (dataOb.length() > 0) {
                                    JSONArray dataArr = dataOb.getJSONArray(0);

                                    for (int i = 0; i < dataArr.length(); i++) {
                                        JSONObject groupDataOb = dataArr.getJSONObject(i);
                                        CONTACT_NO.add(groupDataOb.getString("cnt_phone"));
                                        CONTACT_NAME.add(groupDataOb.getString("cnt_name"));
                                    }
                                }

                            } else {
//                                Toast.makeText(getActivity(),
//                                        "Oops You have not created any group!",
//                                        Toast.LENGTH_SHORT).show();
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
                        new LoadContacts().execute("");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("get server contacts", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
                new LoadContacts().execute("");
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

    public void AddContact(String name, String phone, final int position) {
        JSONArray jsArr = null;
        try {
            jsArr = new JSONArray();
            JSONObject ob = new JSONObject();
            ob.put("name", name);
            ob.put("phone_no", phone);
            jsArr.put(ob);
            jsArr.put(jsArr.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("response call", "" + jsArr.toString());
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "contact/addtocontact";
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
                        Log.e("addtocontact", response.toString());
                        ArrayList<String> groupId = new ArrayList<String>();
                        ArrayList<String> groupName = new ArrayList<String>();
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {

                                HashMap<String, String> subData = new HashMap<String, String>();
                                subData.put("name", CONTACT_DATA.get(position).get("name"));
                                subData.put("number", CONTACT_DATA.get(position).get("number"));
                                subData.put("status", "UPLOADED");
                                CONTACT_DATA.set(position, subData);
                                /*JSONObject dataOb = object.getJSONObject("DATA");
                                JSONArray dataArr = dataOb.getJSONArray("1");

                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject groupDataOb = dataArr.getJSONObject(i);
                                    groupId.add(groupDataOb.getString("grp_id"));
                                    groupName.add(groupDataOb.getString("grp_name"));
                                }*/
                                adapter.notifyDataSetChanged();
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
                params.put("contact_data", "" + finalJsArr.toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
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
    public interface OnContactFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ContactListAdapter extends BaseAdapter {

        LayoutInflater inflater;


        int m = 0;

        public ContactListAdapter(ArrayList<HashMap<String, String>> contact_data) {
            // TODO Auto-generated constructor stub
            CONTACT_DATA = contact_data;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.contact_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.contactName = (TextView) convertView
                        .findViewById(R.id.txvContactName);
                holder.contactPhone = (TextView) convertView
                        .findViewById(R.id.txvPhoneNumber);
                holder.contactImage = (ImageView) convertView.findViewById(R.id.imvContactImage);
                holder.contactStatusImage = (ImageView) convertView.findViewById(R.id.imvContactStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.contactName.setText(CONTACT_DATA.get(position).get("name"));
            holder.contactPhone.setText(CONTACT_DATA.get(position).get("number"));
            if (CONTACT_DATA.get(position).get("status").equalsIgnoreCase("ADD")) {
                Picasso.with(getActivity())
                        .load(R.drawable.ic_contact_add).transform(new CircleTransform())
                        .into(holder.contactStatusImage);
            } else if (CONTACT_DATA.get(position).get("status").equalsIgnoreCase("UPLOADED")) {
                Picasso.with(getActivity())
                        .load(R.drawable.ic_contact_uploaded).transform(new CircleTransform())
                        .into(holder.contactStatusImage);
            }
            holder.contactStatusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddContact(CONTACT_DATA.get(position).get("name"), CONTACT_DATA.get(position).get("number"), position);
                }
            });

            Picasso.with(getActivity())
                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(holder.contactImage);
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
            TextView contactName, contactPhone;
            CheckBox contactCheckbox;
            ImageView contactImage, contactStatusImage;
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

    private class LoadContacts extends AsyncTask<String, String, String> {
        ProgressDialog mProDialog;
        String phoneNumber, email;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                HashMap<String, String> subData = new HashMap<String, String>();
                subData.put("name", contactName);
                subData.put("number", contactNumber);
                if (CONTACT_NO.toString().contains(contactNumber)) {
                    subData.put("status", "UPLOADED");
                } else {
                    subData.put("status", "ADD");
                }
                dataList.add(subData);


                cursor.moveToNext();
            }
            cursor.close();
            cursor = null;
            Log.d("END", "Got all Contacts");


           /* Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

            Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
            Cursor cursor = contentResolver.query(CONTENT_URI, null, null,
                    null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    // Phone number identify
                    Cursor phoneCursor = contentResolver.query(
                            PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?",
                            new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }
                    phoneCursor.close();
                    Cursor emailCursor = contentResolver
                            .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                    new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        String elist = emailCursor.getString(emailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if (!elist.isEmpty()) {
                            email = elist;
                        }
                    }
                    Log.e("7", "7");
                    emailCursor.close();
                    HashMap<String, String> subData = new HashMap<String, String>();
                    subData.put("name", name);
                    subData.put("number", phoneNumber);
                    subData.put("email", email + "");
                    subData.put("status","ADD");
                    dataList.add(subData);
                    name = "null";
                    phoneNumber = "";
                    email = "";
                    Log.e("8", "8");
                }
            }
            cursor.close();
            Collections.sort(dataList,
                    new Comparator<HashMap<String, String>>() {
                        public int compare(HashMap<String, String> mapping1,
                                           HashMap<String, String> mapping2) {
                            return mapping1.get("name").compareTo(
                                    mapping2.get("name"));

                        }
                    });*/
            mProDialog.dismiss();
            return "";
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mProDialog = new ProgressDialog(getActivity());
            mProDialog.setMessage("Loading Contacts...");
            mProDialog.setCanceledOnTouchOutside(false);
            mProDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            //setData();
            adapter = new ContactListAdapter(dataList);
            ContactList.setAdapter(adapter);
        }

    }
}
