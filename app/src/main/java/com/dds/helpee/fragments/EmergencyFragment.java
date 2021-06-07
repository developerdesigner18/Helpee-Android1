package com.dds.helpee.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.dds.helpee.R;
import com.dds.helpee.RequestPermissionHandler;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.activities.MainActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.interfaces.UpdateableFragment;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Number;
import com.dds.helpee.model.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.TELEPHONY_SERVICE;

public class EmergencyFragment extends Fragment implements UpdateableFragment
{
    TextView tv_police_number , tv_rescue_number, tv_country;
    ImageView img_police_call , img_rescue_call, img_flag;
    public  String countryCode = null;
    String police_no = null, rescue_number = null;
    View view;
    public   String country = null;
            String language = null , token = null;
    ProgressDialog pd;
    LinearLayout layout_rescue, layout_police;
    public final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 909;
    TelephonyManager mTelephonyManager;
    SharedPreferences.Editor et;
    SharedPreferences pref;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_emergency, container, false);
        tv_police_number = (TextView) view.findViewById(R.id.tv_police_number);
        tv_rescue_number = (TextView) view.findViewById(R.id.tv_rescue_number);

        img_police_call = (ImageView) view.findViewById(R.id.img_police_call);
        img_rescue_call = (ImageView) view.findViewById(R.id.img_rescue_call);

        img_flag = (ImageView) view.findViewById(R.id.img_flag);
        tv_country = (TextView) view.findViewById(R.id.tv_country);

        layout_police = (LinearLayout) view.findViewById(R.id.layout_police);
        layout_rescue = (LinearLayout) view.findViewById(R.id.layout_rescue);

        mTelephonyManager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        token = pref.getString(Const.TOKEN, null);

        language = pref.getString(Const.LANGUAGE, null);

//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        country = pref.getString(Const.LOCATION, null);
        Log.e(".....country",""+country);

        if(country != null)
        {
            if(language != null && language.equals("fr"))
            {
                if(MainActivity.listCountries != null && MainActivity.listCountries.size()>0)
                {
                    for(int i=0 ; i < MainActivity.listCountries.size() ; i++)
                    {
                        if(MainActivity.listCountries.get(i).getEnglishName().equals(country))
                        {
                            country = MainActivity.listCountries.get(i).getFrenchName();

                            break;
                        }
                    }
                }
            }

            tv_country.setText(" "+country);

            countryCode = getCountryCode(country);
            Log.e("code",""+countryCode);

            if(countryCode != null )
                {
                    if(countryCode.equalsIgnoreCase("do"))
                    {
                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + "do_f", null, getActivity().getPackageName()));
                    }
                    else
                    {
                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode.toLowerCase(), null, getActivity().getPackageName()));
                    }
                }
            getEmergencyNumber();
        }
        else
        {
            tv_rescue_number.setText("100");
            tv_police_number.setText("108");
        }



//        country = HomeFragment.country;
//        countryCode = HomeFragment.countryCode;
//
//        if(countryCode == null && country == null)
//        {
//            Handler h = new Handler();
//            h.postDelayed(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    getCodeName();
//                    h.postDelayed(this, 1000);
//                }
//            }, 2000);
//        }
//        else
//        {
//            if(countryCode != null && country != null)
//            {
//                if(countryCode != null )
//                {
//                    if(countryCode.equals("do"))
//                    {
//                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + "do_f", null, getActivity().getPackageName()));
//                    }
//                    else
//                    {
//                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode, null, getActivity().getPackageName()));
//                    }
//                }
//                tv_country.setVisibility(View.VISIBLE);
//                img_flag.setVisibility(View.VISIBLE);
//                tv_country.setText(country);
//                getEmergencyNumber();
//            }
//            else
//            {
//                tv_country.setVisibility(View.INVISIBLE);
//                img_flag.setVisibility(View.INVISIBLE);
//            }
//        }

        layout_police.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isTelephonyEnabled())
                {
                    checkForPhonePermission(tv_police_number.getText().toString());
                }
                else
                {
                    Toast.makeText(getActivity(), "TELEPHONY NOT ENABLED! ", Toast.LENGTH_LONG).show();
                    Log.d("CALL", "TELEPHONY NOT ENABLED! ");
                }
            }
        });
        layout_rescue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isTelephonyEnabled())
                {
                    checkForPhonePermission(tv_rescue_number.getText().toString());
                }
                else
                {
                    Toast.makeText(getActivity(), "TELEPHONY NOT ENABLED! ", Toast.LENGTH_LONG).show();
                    Log.d("CALL", "TELEPHONY NOT ENABLED! ");
                }
            }
        });
        return  view;
    }
    public String getCountryCode(String countryName)
    {
        // Get all country codes in a string array.
        String[] isoCountryCodes = Locale.getISOCountries();
        Map<String, String> countryMap = new HashMap<>();
        Locale locale;
        String name;

        // Iterate through all country codes:
        for (String code : isoCountryCodes)
        {
            // Create a locale using each country code
            locale = new Locale("", code);
            // Get country name for each code.
            name = locale.getDisplayCountry();
            // Map all country names and codes in key - value pairs.
            countryMap.put(name, code);
        }

        // Return the country code for the given country name using the map.
        // Here you will need some validation or better yet
        // a list of countries to give to user to choose from.
        return countryMap.get(countryName); // "NL" for Netherlands.
    }
    private void checkForPhonePermission(String number)
    {
        if (ActivityCompat.checkSelfPermission(getActivity() , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("Call", "PERMISSION NOT GRANTED!");

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
        else
        {
            // Permission already granted. Enable the call button.
            call(number);
        }
    }
    public void call(String number)
    {
        String phoneNumber = String.format("tel: %s", number);
        Log.e("number",""+phoneNumber);

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));//change the number
        startActivity(callIntent);
    }
    private boolean isTelephonyEnabled()
    {
        if (mTelephonyManager != null)
        {
            if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY)
            {
                return true;
            }
        }
        return false;
    }
    public void getEmergencyNumber()
    {
        try
        {
            if(ApiClient.isNetworkAvailable(getActivity()))
            {
                pd = new ProgressDialog(getActivity());
                pd.setCancelable(false);
                pd.setMessage(getString(R.string.please_wait));
                pd.show();

                Call<Response> call = ApiClient.create_InstanceAuth(token).GetEmergencyNumber(country);
                call.enqueue(new Callback<Response>()
                {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                    {
                        if(pd != null && pd.isShowing())
                        {
                            pd.dismiss();
                        }
                        if(response != null)
                        {
                            if(response.isSuccessful())
                            {
                                if(response.body() != null && response.body().getSuccess() == 1)
                                {
                                    Number objnumber = response.body().getNumber();
                                    if(objnumber != null)
                                    {
                                        police_no = objnumber.getPoliceNo();
                                        rescue_number = objnumber.getRescueNo();
                                        setCountryNameFlag();

                                        Log.e("police_no", ""+police_no);
                                        Log.e("rescue_number", ""+rescue_number);

                                        tv_police_number.setText(police_no);
                                        tv_rescue_number.setText(rescue_number);
                                    }
                                }
                                else
                                {
                                    String message  = (String) response.body().getMessage();
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();
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
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t)
                    {
                        if(pd != null && pd.isShowing())
                        {
                            pd.dismiss();
                        }
                        Log.e("number",""+t.toString());
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                if(pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }
                Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE :
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE) && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)
                {
                    // Permission was granted.
                }
                else
                    {
                    // Permission denied.
                    Log.d("call", "Failure to obtain permission!");
                    Toast.makeText(getActivity(), "Failure to obtain permission!", Toast.LENGTH_LONG).show();
                    // Disable the call button
//                    img_police_call.setVisibility(View.GONE);
//                        img_rescue_call.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void update()
    {
//        Log.e("upate", "update");
//        country = HomeActivity.countryName;
//        countryCode = HomeActivity.countryCode;
//        Log.e("cou",""+country);
//        Log.e("code",""+countryCode);
//        if(countryCode != null && country != null)
//        {
//            if(countryCode != null )
//            {
//                if(countryCode.equals("do"))
//                {
//                    img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + "do_f", null, getActivity().getPackageName()));
//                }
//                else
//                {
//                    img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode, null, getActivity().getPackageName()));
//                }
//            }
//            tv_country.setText(country);
//            getEmergencyNumber();
//        }
//        else
//        {
//            tv_country.setVisibility(View.INVISIBLE);
//            img_flag.setVisibility(View.INVISIBLE);
//        }
    }
    public void getCodeName()
    {
        if(countryCode == null && country == null)
        {
            country = HomeFragment.country;
            countryCode = HomeFragment.countryCode;

            if(countryCode != null && country != null)
            {
                if(countryCode != null )
                {
                    if(countryCode.equals("do"))
                    {
                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + "do_f", null, getActivity().getPackageName()));
                    }
                    else
                    {
                        img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode, null, getActivity().getPackageName()));
                    }
                }
                tv_country.setVisibility(View.VISIBLE);
                img_flag.setVisibility(View.VISIBLE);
                tv_country.setText(country);
                getEmergencyNumber();
            }
            else
            {
                tv_country.setVisibility(View.INVISIBLE);
                img_flag.setVisibility(View.INVISIBLE);
            }
        }
    }
    public void setCountryNameFlag()
    {
        if (country != null)
        {
            if (language != null && language.equals("fr"))
            {
                if (MainActivity.listCountries != null && MainActivity.listCountries.size() > 0)
                {
                    for (int i = 0; i < MainActivity.listCountries.size(); i++)
                    {
                        if (MainActivity.listCountries.get(i).getEnglishName().equals(country))
                        {
                            country = MainActivity.listCountries.get(i).getFrenchName();
                            break;
                        }
                    }
                }
            }

            tv_country.setText(" " + country);

            countryCode = getCountryCode(country);
            Log.e("code", "" + countryCode);

            if (countryCode != null)
            {
                if (countryCode.equalsIgnoreCase("do"))
                {
                    img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + "do_f", null, getActivity().getPackageName()));
                }
                else
                {
                    img_flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode.toLowerCase(), null, getActivity().getPackageName()));
                }
            }
        }
    }
}
