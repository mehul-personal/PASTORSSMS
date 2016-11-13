package com.pastors.pastorssms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenuFragment.OnMainMenuFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment implements
        BuyMoreCreditFragment.OnCardHolderFragmentInteractionListener,
        ContactFragment.OnContactFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView imvLogout, imvGroups, imvBuyMoreCredits,imvScheduleMessage;
    TextView txvLogout, txvGroups, txvBuyMoreCredits,txvScheduleMessage;
    private OnMainMenuFragmentInteractionListener mListener;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuFragment newInstance(String param1, String param2) {
        MainMenuFragment fragment = new MainMenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        imvLogout = (ImageView) view.findViewById(R.id.imvLogout);
        txvLogout = (TextView) view.findViewById(R.id.txvLogout);
        imvGroups = (ImageView) view.findViewById(R.id.imvGroups);
        txvGroups = (TextView) view.findViewById(R.id.txvGroups);
        imvBuyMoreCredits = (ImageView) view.findViewById(R.id.imvBuyMoreCredit);
        txvBuyMoreCredits = (TextView) view.findViewById(R.id.txvBuyMoreCredit);
        imvScheduleMessage=(ImageView) view.findViewById(R.id.imvScheduleMessage);
        txvScheduleMessage=(TextView) view.findViewById(R.id.txvScheduleMessage);
        NavigationDrawerActivity.actionViewItem.setVisible(false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("MAIN MENU");

        imvGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new GroupFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("GROUP");
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("GROUPS");
                // getActivity().getActionBar().setTitle("GROUPS");
                NavigationDrawerActivity.actionViewItem.setVisible(true);
            }
        });
        txvGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new GroupFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("GROUP");
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("GROUPS");
               // getActivity().getActionBar().setTitle("GROUPS");
                NavigationDrawerActivity.actionViewItem.setVisible(true);
            }
        });
        imvBuyMoreCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new BuyMoreCreditFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("BUYCREDIT");
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("BUY MORE CREDITS");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
            }
        });
        txvBuyMoreCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new BuyMoreCreditFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("BUYCREDIT");
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("BUY MORE CREDITS");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
            }
        });
        imvScheduleMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new ScheduleFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("SCHEDULE");
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL", "UNSELECTED");
                    defineDeliveryFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("MESSAGE");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
            }
        });
        txvScheduleMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defineDeliveryFragment = new ScheduleFragment();
                if (defineDeliveryFragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, defineDeliveryFragment);
                    fragmentTransaction.addToBackStack("SCHEDULE");
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL", "UNSELECTED");
                    defineDeliveryFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle("MESSAGE");
                NavigationDrawerActivity.actionViewItem.setVisible(false);
            }
        });
        imvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences5 = getActivity().getSharedPreferences(
                        "LOGIN_DETAIL", 0);
                sharedPreferences5.edit().clear().commit();

                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        txvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences5 = getActivity().getSharedPreferences(
                        "LOGIN_DETAIL", 0);
                sharedPreferences5.edit().clear().commit();

                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
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
        if (context instanceof OnMainMenuFragmentInteractionListener) {
            mListener = (OnMainMenuFragmentInteractionListener) context;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
    public interface OnMainMenuFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
