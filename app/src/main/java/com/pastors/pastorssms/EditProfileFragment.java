package com.pastors.pastorssms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnEditProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText edtUserName, edtUserPhone, edtUserEmail, edtPassword;
    Button signup;
    ImageView userimage;
    String selectedImagePath = "";
    RadioButton rbMale, rbFemale;
    TextView txvMale, txvFemale;
    String strCheckValue = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnEditProfileFragmentInteractionListener mListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        edtUserName = (EditText) view.findViewById(R.id.edtUserName);
        edtUserPhone = (EditText) view.findViewById(R.id.edtUserPhone);
        edtUserEmail = (EditText) view.findViewById(R.id.edtUserEmail);
        edtPassword = (EditText) view.findViewById(R.id.edtPassword);
        signup = (Button) view.findViewById(R.id.btnSignUp);
        userimage = (ImageView) view.findViewById(R.id.imvUserImage);
        rbFemale = (RadioButton) view.findViewById(R.id.rbFeMale);
        rbMale = (RadioButton) view.findViewById(R.id.rbMale);
        txvMale = (TextView) view.findViewById(R.id.txvMale);
        txvFemale = (TextView) view.findViewById(R.id.txvFeMale);
        edtUserEmail.setClickable(false);
        edtUserEmail.setFocusable(false);
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
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
        getUserProfile();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUpdateProfile();

            }
        });
        return view;
    }

    public void setUpdateProfile() {
        final String str_user_name = edtUserName.getText().toString();
        final String str_userphone = edtUserPhone.getText().toString();
        final String str_email = edtUserEmail.getText().toString();
        final String str_password = edtPassword.getText().toString();
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "user/profile");

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    if (!selectedImagePath.isEmpty()) {
                        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        entity.addPart("usr_image", new StringBody(encodedImage));
                        //entity.addPart("usr_image", new FileBody(new File(selectedImagePath)));
                    }

                    entity.addPart("usr_lgn_id", new StringBody(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "")));
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
                    mProgressDialog.dismiss();
                    JSONObject object = new JSONObject(result.toString());
                    Log.e("2", "2");

                    String msg = object.getString("STATUS");
//                    JSONArray msgArr=object.getJSONArray("MESSAGES");
//                    String data=msgArr.getString(0);
//                    if(data.equalsIgnoreCase("Emailid already exists.")){
//                        Toast.makeText(SignUpActivity.this,
//                                "Email address already registered",
//                                Toast.LENGTH_LONG).show();
//                    }
                    if (msg.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(getActivity(),
                                "Your Profile Updated Successfully!",
                                Toast.LENGTH_LONG).show();
                        JSONArray dataArr = object.getJSONArray("DATA");
                        JSONObject subOb = dataArr.getJSONObject(0);
                        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("NAME", subOb.getString("usr_name"));
                        edit.putString("PHONE", subOb.getString("usr_phone"));
                        edit.putString("IMAGE", subOb.getString("usr_image"));
                        edit.putString("GENDER", subOb.getString("usr_gender"));
                        edit.commit();

                    } else {
                        Toast.makeText(getActivity(),
                                "Oopss! We are troubling to send data",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    mProgressDialog.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            System.out.println("Content Path : " + selectedImage.toString());
            selectedImagePath = getPath(getActivity(), selectedImage);
            Log.e("selected image", selectedImagePath + "");
            if (selectedImage != null) {
                userimage
                        .setImageBitmap(getCircleBitmap(getScaledBitmap(selectedImage)));
            } else {
                selectedImagePath = "";
                Toast.makeText(getActivity(), "Error getting Image",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "No Photo Selected",
                    Toast.LENGTH_SHORT).show();
            selectedImagePath = "";
        }
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
        if (context instanceof OnEditProfileFragmentInteractionListener) {
            mListener = (OnEditProfileFragmentInteractionListener) context;
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

    public void getUserProfile() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "user/getuserbyid";
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
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONArray dataArr = object.getJSONArray("DATA");
                                JSONObject dataOb = dataArr.getJSONObject(0);
                                String name = dataOb.getString("usr_name");
                                String image = dataOb.getString("usr_image");
                                String gender = dataOb.getString("usr_gender");

                                edtUserName.setText(name);
                                edtUserPhone.setText(dataOb.getString("usr_phone"));
                                SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                                edtUserEmail.setText(preferences.getString("EMAIL", ""));

                                if (gender.equalsIgnoreCase("Female")) {
                                    strCheckValue = "1";
                                    rbFemale.setChecked(true);
                                    rbMale.setChecked(false);
                                } else {
                                    strCheckValue = "0";

                                    rbFemale.setChecked(false);
                                    rbMale.setChecked(true);
                                }
                                if (image.isEmpty()) {
                                    Picasso.with(getActivity())
                                            .load(R.drawable.ic_no_image).transform(new CircleTransform())
                                            .into(userimage);
                                } else {
                                    Picasso.with(getActivity())
                                            .load(image).transform(new CircleTransform())
                                            .into(userimage);
                                }
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! We are troubling to get your data \n" +
                                                " Please try again!",
                                        Toast.LENGTH_SHORT).show();
                                Picasso.with(getActivity())
                                        .load(R.drawable.ic_no_image).transform(new CircleTransform())
                                        .into(userimage);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Picasso.with(getActivity())
                                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                                    .into(userimage);
                            Toast.makeText(getActivity(),
                                    "Sorry! We are troubling to get your data \n" +
                                            " Please try again!", Toast.LENGTH_LONG).show();
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
                SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lgn_id", "" + preferences.getString("ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    private Bitmap getScaledBitmap(Uri uri) {
        Bitmap thumb = null, rotatedBitmap = null;
        try {
            ContentResolver cr = getActivity().getContentResolver();
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
    public interface OnEditProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
