package com.dds.helpee.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dds.helpee.LocaleHelper;
import com.dds.helpee.LocaleManager1;
import com.dds.helpee.R;
import com.dds.helpee.adapters.LoginRegisPagerAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.fragments.HomeFragment;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.CountryListResponse;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Number;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
{
    public static  ViewPager pager;
    TabLayout tab_login_regis;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    public static  List<Number> listCountries = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager1.setLocale(base));
    }
//    @Override
//    protected void attachBaseContext(Context base)
//    {
//        super.attachBaseContext(LocaleHelper.onAttach(base));
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        et = pref.edit();

//        printHashKey(MainActivity.this);

        if(ApiClient.isNetworkAvailable(MainActivity.this))
        {
            getAllCountries();
        }
        else
        {
            Toast.makeText(MainActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }



        if(pref.getBoolean(Const.LOGIN, false)== true)
        {
            Intent i_go = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i_go);
            finish();
        }

        pager = (ViewPager) findViewById(R.id.pager);
        tab_login_regis = (TabLayout) findViewById(R.id.tab_login_regis);

        pager.setAdapter(new LoginRegisPagerAdapter(MainActivity.this, getSupportFragmentManager()));
        tab_login_regis.setupWithViewPager(pager);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            for (Fragment fragment : getSupportFragmentManager().getFragments())
            {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Activity", "ON RESULT CALLED");
            }
        }
        catch (Exception e)
        {
            Log.d("ERROR", e.toString());
        }
    }
    public void getAllCountries()
    {
        Call<CountryListResponse> call = ApiClient.create_Istance().GetAllCountries();
        call.enqueue(new Callback<CountryListResponse>()
        {
            @Override
            public void onResponse(Call<CountryListResponse> call, retrofit2.Response<CountryListResponse> response)
            {
                if(response != null )
                {
                    if(response.isSuccessful())
                    {
                        if(response.body() != null)
                        {
                            listCountries = response.body().getNumberList();

                            if(listCountries != null && listCountries.size() > 0 )
                            {
//                            Toast.makeText(MainActivity.this, "countrylist"+listCountries.size() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        if(response.errorBody() != null)
                        {
                            String msg = response.errorBody().source().toString();
                            Log.e("msg",""+msg);
                            String[] arr = msg.split("=");
                            if(arr.length == 2)
                            {
                                msg = arr[1].replace("]"," ").trim();
                                if(msg != null)
                                {
                                    try
                                    {
                                        JSONObject obh = new JSONObject(msg);
                                        if(obh.getString("message") != null)
                                        {
                                            String message =  obh.getString("message").toString();
                                            Log.e("message",""+message);
                                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                Log.e("msg",""+msg);
                            }
                        }
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryListResponse> call, Throwable t)
            {
                Log.e("failure", ""+t.toString());
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}