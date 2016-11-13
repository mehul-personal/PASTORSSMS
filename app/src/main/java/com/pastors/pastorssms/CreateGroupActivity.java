package com.pastors.pastorssms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    static MenuItem actionNextItem;
    static String CALL = "", GROUP_ID = "";
    EditText edtGroupName;
    TextView txvReaminNameCount;
    ImageView imvGroupImage;
    Button addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        edtGroupName = (EditText) findViewById(R.id.edtGroupName);
        txvReaminNameCount = (TextView) findViewById(R.id.txvReaminNameCount);
        imvGroupImage = (ImageView) findViewById(R.id.imvGroupImage);
        addContact = (Button) findViewById(R.id.addContact);

        Intent i = getIntent();
        CALL = i.getStringExtra("CALL");
        if (CALL.equalsIgnoreCase("NEW")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("NEW GROUP");
            addContact.setText("Add Contacts");
        } else if (CALL.equalsIgnoreCase("EDIT")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("UPDATE GROUP");
            GROUP_ID = i.getStringExtra("GROUP_ID");
            edtGroupName.setText(i.getStringExtra("GROUP_NAME"));
            addContact.setText("Update Contacts");

            txvReaminNameCount.setText("" + (25 - edtGroupName.getText().toString().length()));

        }
        Picasso.with(CreateGroupActivity.this)
                .load(R.drawable.ic_no_image).transform(new CircleTransform())
                .into(imvGroupImage);
        edtGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txvReaminNameCount.setText("" + (25 - edtGroupName.getText().toString().length()));
            }
        });
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CALL.equalsIgnoreCase("NEW")) {
                    creatGroup("contact");

                } else if (CALL.equalsIgnoreCase("EDIT")) {
                    creatGroup("contact");

                }
            }
        });
    }

    public void creatGroup(final String type) {

        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "group/creategroup";
        final ProgressDialog mProgressDialog = new ProgressDialog(CreateGroupActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("creategroup", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("STATUS");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                JSONObject dataob = object.getJSONObject("DATA");
                                if (CALL.equalsIgnoreCase("EDIT")) {
                                    Toast.makeText(CreateGroupActivity.this,
                                            "Your group name updated successfully!",
                                            Toast.LENGTH_SHORT).show();


                                    Intent i = new Intent();
                                    if (type.equalsIgnoreCase("contact"))
                                        i.putExtra("msg", "contact");
                                    else
                                        i.putExtra("msg", "success");

                                    i.putExtra("GROUP_ID", "" + dataob.getString("grp_id"));
                                    i.putExtra("GROUP_NAME", "" + edtGroupName.getText().toString());
                                    setResult(15, i);
                                    finish();
                                } else {
                                    Toast.makeText(CreateGroupActivity.this,
                                            "Your group created successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent();
                                    i.putExtra("GROUP_ID", "" + dataob.getString("grp_id"));
                                    if (type.equalsIgnoreCase("contact"))
                                        i.putExtra("msg", "contact");
                                    else
                                        i.putExtra("msg", "success");
                                    setResult(10, i);
                                    finish();
                                }
                            } else {
                                Toast.makeText(CreateGroupActivity.this,
                                        "Oops Your group creating failed please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(CreateGroupActivity.this,
                                    "Sorry! we can't creating group. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(CreateGroupActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreateGroupActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("creategroup Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(CreateGroupActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grp_lgn_id", getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("grp_name", "" + edtGroupName.getText().toString());
                if (CALL.equalsIgnoreCase("EDIT")) {
                    params.put("grp_id", "" + GROUP_ID);
                }
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.txvCustomAction:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_create_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        actionNextItem = menu.findItem(R.id.action_next_group);
        View v = MenuItemCompat.getActionView(actionNextItem);
        TextView add = (TextView) v.findViewById(R.id.txvCustomAction);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("next call", "next click call");
                creatGroup("create");
            }
        });
        return super.onPrepareOptionsMenu(menu);
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
