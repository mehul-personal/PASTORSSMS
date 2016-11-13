package com.pastors.pastorssms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EditProfileFragment.OnEditProfileFragmentInteractionListener,
        MainMenuFragment.OnMainMenuFragmentInteractionListener,
        ContactFragment.OnContactFragmentInteractionListener,
        BuyMoreCreditFragment.OnCardHolderFragmentInteractionListener,
        GroupFragment.OnGroupFragmentInteractionListener,
        AddGroupContactFragment.OnAddGroupContactFragmentInteractionListener,
        ScheduleFragment.OnScheduleFragmentInteractionListener,
        MessageListFragment.OnFragmentInteractionListener,
        EditGroupFragment.OnFragmentInteractionListener,
        MessageDetailFragment.OnFragmentInteractionListener {
    static MenuItem actionViewItem, actionEditItem;
    ImageView userPhoto;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
                userName.setText(preferences.getString("NAME", ""));
                Log.e("Image", "" + preferences.getString("IMAGE", ""));
                if (preferences.getString("IMAGE", "").isEmpty()) {
                    Picasso.with(NavigationDrawerActivity.this)
                            .load(R.drawable.ic_no_image).transform(new CircleTransform())
                            .into(userPhoto);
                } else {
                    Picasso.with(NavigationDrawerActivity.this)
                            .load(preferences.getString("IMAGE", "")).transform(new CircleTransform())
                            .into(userPhoto);
                }
            }
        };
        drawer.setDrawerListener(toggle);

        toggle.syncState();
//toggle.onDrawerOpened(new );
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        userPhoto = (ImageView) headerLayout.findViewById(R.id.imvUserPhoto);
        userName = (TextView) headerLayout.findViewById(R.id.txvUserName);

        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
        userName.setText(preferences.getString("NAME", ""));
        Log.e("Image", "" + preferences.getString("IMAGE", ""));
        if (preferences.getString("IMAGE", "").isEmpty()) {
//            Glide.with(NavigationDrawerActivity.this)
//                    .load(R.drawable.ic_no_image).transform(new CircleTransform(NavigationDrawerActivity.this))
//                    .into(userPhoto);
            Picasso.with(NavigationDrawerActivity.this)
                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(userPhoto);
        } else {
            Picasso.with(NavigationDrawerActivity.this)
                    .load(preferences.getString("IMAGE", "")).transform(new CircleTransform())
                    .into(userPhoto);

//            Glide.with(NavigationDrawerActivity.this)
//                    .load(preferences.getString("IMAGE", "")).transform(new CircleTransform(NavigationDrawerActivity.this))
//                    .into(userPhoto);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
        userName.setText(preferences.getString("NAME", ""));
        Log.e("Image", "" + preferences.getString("IMAGE", ""));
        if (preferences.getString("IMAGE", "").isEmpty()) {
            Picasso.with(NavigationDrawerActivity.this)
                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(userPhoto);
        } else {
            Picasso.with(NavigationDrawerActivity.this)
                    .load(preferences.getString("IMAGE", "")).transform(new CircleTransform())
                    .into(userPhoto);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 10) {
                if (data.getStringExtra("msg").equalsIgnoreCase("contact")) {
                    Fragment defineDeliveryFragment = new AddGroupContactFragment();
                    if (defineDeliveryFragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                        fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                        fragmentTransaction.addToBackStack("ADDCONTACT");
                        Bundle bundle = new Bundle();
                        bundle.putString("CALL", "ADD");
                        bundle.putString("GROUP_ID", "" + data.getStringExtra("GROUP_ID"));
                        defineDeliveryFragment.setArguments(bundle);
                        fragmentTransaction.commit();
                    }
                    getSupportActionBar().setTitle("GROUPS");
                    actionViewItem.setVisible(false);
                    actionEditItem.setVisible(false);
                } else {
                    Fragment defineDeliveryFragment = new ScheduleFragment();
                    if (defineDeliveryFragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                        fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                        fragmentTransaction.addToBackStack("SCHEDULE");
                        Bundle bundle = new Bundle();
                        bundle.putString("CALL", "SELECTED");
                        bundle.putString("GROUP_ID", "" + data.getStringExtra("GROUP_ID"));
                        defineDeliveryFragment.setArguments(bundle);
                        fragmentTransaction.commit();
                    }
                    getSupportActionBar().setTitle("GROUPS");
                    actionViewItem.setVisible(false);
                    actionEditItem.setVisible(false);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            for(int i=0;i<getFragmentManager().getBackStackEntryCount();i++)
            Log.e("back content", "content:" + getFragmentManager().getBackStackEntryCount());
//            if(getFragmentManager().getBackStackEntryCount()==0)
            super.onBackPressed();
//            else if(getFragmentManager().getBackStackEntryCount()==1)
//                getSupportActionBar().setTitle("GROUPS");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        selectedItem(id);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        actionViewItem = menu.findItem(R.id.action_create);
        actionEditItem = menu.findItem(R.id.action_edit);
        View v = MenuItemCompat.getActionView(actionViewItem);
        ImageView add = (ImageView) v.findViewById(R.id.txvCustomAction);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("next call", "next click call");
//                picker = new Dialog(NavigationDrawerActivity.this);
//                picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                picker.setContentView(R.layout.dialog_create_group);
//                groupname = (EditText) picker.findViewById(R.id.edtGroupName);
//                close = (ImageView) picker.findViewById(R.id.imvClose);
//                submit = (TextView) picker.findViewById(R.id.txvSubmit);
//                close.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        picker.dismiss();
//
//                    }
//                });
//                submit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        creatGroup();
//                    }
//                });
//                picker.show();

                Intent i = new Intent(NavigationDrawerActivity.this, CreateGroupActivity.class);
                i.putExtra("CALL", "NEW");
                startActivityForResult(i, 10);
            }
        });
        selectedItem(R.id.nav_home);
        return super.onPrepareOptionsMenu(menu);
    }


    public void selectedItem(int id) {
        if (id == R.id.nav_home) {
            Fragment defineDeliveryFragment = new MainMenuFragment();
            if (defineDeliveryFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("MAIN MENU");
            actionViewItem.setVisible(false);
            actionEditItem.setVisible(false);
        } else if (id == R.id.nav_editprofile) {
            Fragment defineDeliveryFragment = new EditProfileFragment();
            if (defineDeliveryFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("EDIT PROFILE");
            actionViewItem.setVisible(false);
            actionEditItem.setVisible(false);
        } /*else if (id == R.id.nav_contact) {
            Fragment defineDeliveryFragment = new ContactFragment();
            if (defineDeliveryFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("CONTACTS");
            actionViewItem.setVisible(false);
        }*/ else if (id == R.id.nav_calendar) {
            actionViewItem.setVisible(false);
            actionEditItem.setVisible(false);
        } else if (id == R.id.nav_messages) {
            Fragment defineDeliveryFragment = new MessageListFragment();
            if (defineDeliveryFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("MESSAGES");
            actionViewItem.setVisible(false);
            actionEditItem.setVisible(false);
        } else if (id == R.id.nav_mygroup) {
            Fragment defineDeliveryFragment = new GroupFragment();
            if (defineDeliveryFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("GROUPS");
            actionViewItem.setVisible(false);
            actionEditItem.setVisible(false);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences5 = getSharedPreferences(
                    "LOGIN_DETAIL", 0);
            sharedPreferences5.edit().clear().commit();

            Intent intent = new Intent(NavigationDrawerActivity.this,
                    LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
  /* public class CircleTransform extends BitmapTransformation {
       private Context context;

       public CircleTransform(Context context) {
           super(context);
           this.context = context;
       }

       @Override
       protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
           return getCircularBitmapImage(source);
       }

       @Override
       public String getId() {
           return "Glide_Circle_Transformation";
       }
   }
    public  Bitmap getCircularBitmapImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }*/
}
