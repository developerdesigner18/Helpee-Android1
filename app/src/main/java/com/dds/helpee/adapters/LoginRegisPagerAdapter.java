package com.dds.helpee.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dds.helpee.R;
import com.dds.helpee.fragments.LoginFragment;
import com.dds.helpee.fragments.RegistrationFragment;

public class LoginRegisPagerAdapter extends FragmentPagerAdapter
{
    String tabTitle[] ;

    Context context;

    public LoginRegisPagerAdapter(Context con, @NonNull FragmentManager fm)
    {
        super(fm);
        this.context = con;
//        tabTitle = new String[]{context.getString(R.string.login), context.getString(R.string.registration)};
    }
    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:  return context.getString(R.string.login);
            case 1:  return context.getString(R.string.registration);
            default: return context.getString(R.string.login);
        }
//        return tabTitle[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return  new LoginFragment();
            case 1:
                return new RegistrationFragment();

        }
        return null;
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}
