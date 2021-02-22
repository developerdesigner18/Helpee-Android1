package com.dds.helpee.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dds.helpee.R;
import com.dds.helpee.fragments.EmergencyFragment;
import com.dds.helpee.fragments.HomeFragment;
import com.dds.helpee.fragments.LoginFragment;
import com.dds.helpee.fragments.ProfileFragment;
import com.dds.helpee.fragments.RegistrationFragment;
import com.dds.helpee.fragments.SettingsFragment;
import com.dds.helpee.interfaces.UpdateableFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentStatePagerAdapter
{
    String tabTitle[] ;
    int[] tabIcon = new int[]{R.drawable.img_home, R.drawable.img_settings, R.drawable.img_profile};

    Context context;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public HomePagerAdapter(Context con, @NonNull FragmentManager fm)
    {
        super(fm);
        this.context = con;
        tabTitle = new String[]{context.getString(R.string.home), context.getString(R.string.settings), context.getString(R.string.profile_cap)};
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {

        if (object instanceof UpdateableFragment)
        {
            ((UpdateableFragment) object).update();
        }
        notifyDataSetChanged();
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);

    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new SettingsFragment();
            case 2 :
                return new ProfileFragment();
        }
        return null;
    }

    public void addFragment(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public int getCount()
    {
        return tabTitle.length;
    }
    public View getTabView(int position)
    {
        View v = addTabView(tabTitle[position], tabIcon[position]);
        return v;
    }
    public View addTabView(String text, int icon)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_tab);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_tab);
        textView.setText(text.toUpperCase());
        imageView.setImageResource(icon);
        return view;
    }

    public View getTabView1(int position)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null, false);

        TextView textView = (TextView) view.findViewById(R.id.tv_tab);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_tab);

        textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        imageView.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        return view;
    }
    public void SetOnSelectView(TabLayout tabLayout, int position)
    {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View selected = tab.getCustomView();
        TextView iv_text = (TextView) selected.findViewById(R.id.tv_tab);
        ImageView imageView = (ImageView) selected.findViewById(R.id.img_tab);
        iv_text.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        imageView.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
    }

    public void SetUnSelectView(TabLayout tabLayout,int position)
    {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View selected = tab.getCustomView();
        TextView iv_text = (TextView) selected.findViewById(R.id.tv_tab);
        ImageView imageView = (ImageView) selected.findViewById(R.id.img_tab);
        iv_text.setTextColor(Color.WHITE);
        imageView.setColorFilter(Color.WHITE);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
