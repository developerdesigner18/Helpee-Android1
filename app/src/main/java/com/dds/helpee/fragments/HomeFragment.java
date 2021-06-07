package com.dds.helpee.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.adapters.AlertsPagerAdapter;
import com.dds.helpee.adapters.HomePagerAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.interfaces.UpdateableFragment;
import com.dds.helpee.model.Emergency;
import com.google.android.material.textfield.TextInputLayout;

public class HomeFragment extends Fragment implements UpdateableFragment
{
    public static String countryCode = null, country = null;
    EditText et_phone, et_user_name, et_password;
    TextView tv_login;
    ViewPager pager_home;
    LinearLayout layout_emergency, layout_report, layout_nearby;
    boolean isphone = false;
    ImageView img_emergency, img_report, img_nearby;

    UpdateableFragment objinter ;
    MyBroadcastReceiver receiver;
    TextInputLayout email_layout, password_layout, phone_layout;
    View view;

    private class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extras = intent.getExtras();

            String state = extras.getString("extra");

            if(state != null)
            {
                if (ApiClient.isNetworkAvailable(getActivity()))
                {
                    final AlertsPagerAdapter pagerAdapter ;
                    pagerAdapter = new AlertsPagerAdapter(getActivity(), getChildFragmentManager());
                    pager_home.setAdapter(pagerAdapter);
                    pager_home.setCurrentItem(0);
                    unSelectedColor();
                }
                else
                {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        layout_emergency = (LinearLayout) view.findViewById(R.id.layout_emergency);
        layout_report = (LinearLayout) view.findViewById(R.id.layout_report);
        layout_nearby = (LinearLayout) view.findViewById(R.id.layout_nearby);
        img_emergency = (ImageView) view.findViewById(R.id.img_emergency);
        img_report = (ImageView) view.findViewById(R.id.img_report_alerts);
        img_nearby = (ImageView) view.findViewById(R.id.img_nearby_alerts);
        pager_home = (ViewPager) view.findViewById(R.id.pager_home);

        final AlertsPagerAdapter pagerAdapter ;
        pagerAdapter = new AlertsPagerAdapter(getActivity(), getChildFragmentManager());
        pager_home.setAdapter(pagerAdapter);
        unSelectedColor();

//        EmergencyFragment.countryCode =HomeFragment.countryCode;
//        EmergencyFragment.country = HomeFragment.country;

        Log.e("EmergencyFragmentCode", ""+ HomeFragment.countryCode);
        Log.e("EmergencyFrtCountry", ""+ HomeFragment.country );

        objinter = this;
        objinter.update();

        img_emergency.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
        layout_emergency.setBackgroundResource(R.drawable.half_rec_yellow);
        pager_home.setCurrentItem(0);

        pager_home.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                switch (position)
                {
                    case 0:
                        unSelectedColor();
                        img_emergency.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                        layout_emergency.setBackgroundResource(R.drawable.half_rec_yellow);
//                        pager_home.setCurrentItem(0);
                        break;
                    case 1:
                        unSelectedColor();
                        img_report.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                        layout_report.setBackgroundResource(R.drawable.half_rec_yellow);
//                        pager_home.setCurrentItem(1);
                        break;
                    case 2:
                        unSelectedColor();
                        img_nearby.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                        layout_nearby.setBackgroundResource(R.drawable.half_rec_yellow);
//                        pager_home.setCurrentItem(2);
                        break;

                    default:
                        unSelectedColor();
                        img_emergency.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                        layout_emergency.setBackgroundResource(R.drawable.half_rec_yellow);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        layout_emergency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_emergency.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                layout_emergency.setBackgroundResource(R.drawable.half_rec_yellow);
                pager_home.setCurrentItem(0);
            }
        });
        layout_report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_report.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                layout_report.setBackgroundResource(R.drawable.half_rec_yellow);
                pager_home.setCurrentItem(1);
            }
        });
        layout_nearby.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_nearby.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                layout_nearby.setBackgroundResource(R.drawable.half_rec_yellow);
                pager_home.setCurrentItem(2);
            }
        });
        return  view;
    }
    public void unSelectedColor()
    {
        img_emergency.setColorFilter(Color.WHITE);
        img_report.setColorFilter(Color.WHITE);
        img_nearby.setColorFilter(Color.WHITE);

        layout_emergency.setBackgroundResource(R.drawable.half_rec_white);
        layout_report.setBackgroundResource(R.drawable.half_rec_white);
        layout_nearby.setBackgroundResource(R.drawable.half_rec_white);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        unSelectedColor();
        img_emergency.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
        layout_emergency.setBackgroundResource(R.drawable.half_rec_yellow);
        pager_home.setCurrentItem(0);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(getActivity().getPackageName());
//        receiver = new MyBroadcastReceiver();
//        if(receiver != null)
//        {
//            getActivity().registerReceiver(receiver, intentFilter);
//        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(receiver != null)
        {
            getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public void update()
    {
//        Log.e("call",""+true);
//        if(country != null && countryCode != null && countryCode.equals(EmergencyFragment.countryCode)&& country.equals(EmergencyFragment.country))
//        {
//
//        }
//        else
//        {
//            EmergencyFragment.country = country;
//            EmergencyFragment.countryCode = countryCode;
//            if(pager_home != null)
//            {
//                pager_home.setCurrentItem(0);
//            }
//        }
    }
}
