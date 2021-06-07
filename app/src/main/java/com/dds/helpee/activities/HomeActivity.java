package com.dds.helpee.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.dds.helpee.LocaleHelper;
import com.dds.helpee.LocaleManager1;
import com.dds.helpee.R;
import com.dds.helpee.RequestPermissionHandler;
import com.dds.helpee.adapters.HomePagerAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.fragments.EmergencyFragment;
import com.dds.helpee.fragments.HomeFragment;
import com.dds.helpee.fragments.NearByFragment;
import com.dds.helpee.fragments.ProfileFragment;
import com.dds.helpee.fragments.SettingsFragment;
import com.dds.helpee.interfaces.UpdateableFragment;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Emergency;
import com.dds.helpee.model.Response;
import com.dds.helpee.model.Users;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener()
    {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener)
    {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener
    {
        void onRefresh();
    }

    ViewPager pager;
//    TabLayout tab_main;
    HomePagerAdapter pagerAdapter;
    ImageView img_home, img_settings,img_profile;
    TextView tv_home, tv_settings, tv_profile;
    LinearLayout layout_home, layout_settings, layout_profile;
    FrameLayout frame;
    GoogleApiClient mGoogleApiClient;
    String currentCountryName = null , currentCode = null;
    Location mLocation;
    LocationManager mLocationManager;
    LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    String address = null , fcmToken = null, token = null  ;
    int userid = 0;
    public static String countryCode = null, countryName = null;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    double current_lat =  0 , current_long = 0, old_lat = 0 , old_long = 0;
    ProgressDialog pd;
    private RequestPermissionHandler mRequestPermissionHandler;

//    MyBroadcastReceiver receiver;

    UpdateableFragment objInter = null;
    String[] permissions =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
//    @Override
//    protected void attachBaseContext(Context base)
//    {
//        super.attachBaseContext(LocaleHelper.onAttach(base));
//    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager1.setLocale(base));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1)
        {
            // update overrideConfiguration with your locale
//            setLocale(overrideConfiguration) ;
            // you will need to implement this
        }
        super.applyOverrideConfiguration(overrideConfiguration);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        frame = (FrameLayout) findViewById(R.id.frame);

        layout_home = (LinearLayout) findViewById(R.id.layout_home);
        layout_settings = (LinearLayout) findViewById(R.id.layout_settings);
        layout_profile = (LinearLayout) findViewById(R.id.layout_profile);

        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_settings = (TextView) findViewById(R.id.tv_settings);
        tv_profile = (TextView) findViewById(R.id.tv_profile);

        img_home = (ImageView) findViewById(R.id.img_home);
        img_settings = (ImageView) findViewById(R.id.img_settings);
        img_profile = (ImageView) findViewById(R.id.img_profile);

        mRequestPermissionHandler = new RequestPermissionHandler();
        checkPermissions();

        pref = getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        userid = pref.getInt(Const.USER_ID, 0);
        token = pref.getString(Const.TOKEN, null);
        fcmToken = pref.getString(Const.FCM_TOKEN, null);

        loadUserInfor();

        Log.e("user_id",""+userid);
        Log.e("fcmToken",""+fcmToken);

        mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkLocation();

        if(token != null)
        {
            saveFCMToken();
        }

        unSelectedColor();
        img_home.setColorFilter(getResources().getColor(R.color.colorPrimary));
        tv_home.setTextColor(getResources().getColor(R.color.colorPrimary));

        if(frame.getChildCount() > 0)
        {
            frame.removeAllViews();
        }

//        if(countryName != null && countryName.equals(currentCountryName))
//        {
//
//        }
//        else
//        {
            getSupportFragmentManager().beginTransaction().add(R.id.frame,new HomeFragment()).addToBackStack(null).commit();
//        }


        layout_home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_home.setColorFilter(getResources().getColor(R.color.colorPrimary));
                tv_home.setTextColor(getResources().getColor(R.color.colorPrimary));

                if(frame.getChildCount() > 0)
                {
                    frame.removeAllViews();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame,new HomeFragment()).commit();
            }
        });
        layout_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_settings.setColorFilter(getResources().getColor(R.color.colorPrimary));
                tv_settings.setTextColor(getResources().getColor(R.color.colorPrimary));

                if(frame.getChildCount() > 0)
                {
                    frame.removeAllViews();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame,new SettingsFragment()).commit();
            }
        });
        layout_profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                unSelectedColor();
                img_profile.setColorFilter(getResources().getColor(R.color.colorPrimary));
                tv_profile.setTextColor(getResources().getColor(R.color.colorPrimary));

                if(frame.getChildCount() > 0)
                {
                    frame.removeAllViews();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.frame,new ProfileFragment()).commit();
            }
        });
    }
    private void checkPermissions()
{
    int PER_CODE = 777;

    mRequestPermissionHandler.requestPermission(HomeActivity.this, permissions,
            PER_CODE, new RequestPermissionHandler.RequestPermissionListener()
            {
                @Override
                public void onSuccess()
                {
                    Log.e("success","location success");
                    if (mGoogleApiClient != null)
                    {
                        startLocationUpdates();
                    }

                }

                @Override
                public void onFailed()
                {
                    Toast.makeText(HomeActivity.this, "Request permission failed", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(HomeActivity.this)
                            .setCancelable(false)
                            .setTitle("Permission necessary")
                            .setMessage("Please grant all permissions to proceed with app.")
                            .setPositiveButton("Re-Try", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    checkPermissions();
                                }
                            })
                            .setNegativeButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);

                                    checkPermissions();
                                }
                            })
                            .create().show();
                }

            });

}
    public void unSelectedColor()
    {
        img_home.setColorFilter(Color.WHITE);
        img_settings.setColorFilter(Color.WHITE);
        img_profile.setColorFilter(Color.WHITE);

        tv_home.setTextColor(Color.WHITE);
        tv_settings.setTextColor(Color.WHITE);
        tv_profile.setTextColor(Color.WHITE);
    }

    @Override
    public void onBackPressed()
    {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null)
        {
            startLocationUpdates();
        }
        if (mLocation != null)
        {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        }
        else
        {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i("TAG", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i("tag", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    public void saveFCMToken()
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
        {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {
                if (!task.isSuccessful())
                {
                    return;
                }
                String fcm_token = task.getResult().getToken();

                et.putString(Const.FCM_TOKEN, fcm_token);
                et.commit();
                et.apply();

                saveFCMToken(fcm_token);

                Log.e("fb", "onComplete: in app ================> " + token);
            }
        });
    }
    public void saveFCMToken(String fcmtoken)
    {
        Call<Response> call = ApiClient.create_InstanceAuth(token).SaveFCMToken(userid, fcmtoken);
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if(response != null)
                {
                    if(response.isSuccessful())
                    {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            Log.e("fcmResponse", ""+new Gson().toJson(response.body()));

                            String message = (String) response.body().getMessage();

//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
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
                                            Toast.makeText(HomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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
                Log.e("token failure", t.toString());
//                String message = (String) response.body().getMessage();
//
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onLocationChanged(Location location)
    {


        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
//        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));

        Log.e("latitude", ""+String.valueOf(location.getLatitude()));
        Log.e("longitude", ""+String.valueOf(location.getLongitude()));

        current_long = location.getLongitude();
        current_lat = location.getLatitude();

        if(old_lat == current_lat && old_long == current_long)
        {

        }
        else
        {
            old_lat = current_lat;
            old_long = current_long;
            changeLatLong();
        }

        address = getCompleteAddressString(location.getLatitude(), location.getLongitude());
        Log.e("address", ""+address);




//        Toast.makeText(this , msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void startLocationUpdates()
    {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }
    @Override
    public void onStart()
    {
        super.onStart();

        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onStop()
    {
        super.onStop();
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }
    private boolean checkLocation()
    {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled()
    {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE)
    {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null)
            {
                Address returnedAddress = addresses.get(0);
                countryName = returnedAddress.getCountryName();

                if(countryName != null)
                {
                    countryCode = getCountryCode(countryName).toLowerCase();
                    HomeFragment.country = countryName;
                    HomeFragment.countryCode = countryCode;

//                    objInter.update();

                    Log.e("homeCode", ""+ HomeFragment.countryCode);
                    Log.e("homeCountry", ""+ HomeFragment.country );

                    Log.e("countryCode",""+countryCode);
                }

                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                strAdd = strReturnedAddress.toString();
                Log.w("addrerss", strReturnedAddress.toString());
            } else {
                Log.w("addrerss", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("addrerss", "Canont get Address!");
        }
        return strAdd;
    }

    public void CreateAlert(Emergency emergency, int position)
    {
        Dialog d_alert = new Dialog(HomeActivity.this);
        d_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d_alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        d_alert.setContentView(R.layout.dialog_create_alert);
        TextView tv_cancel = (TextView) d_alert.findViewById(R.id.tv_cancel);
        TextView tv_alert = (TextView) d_alert.findViewById(R.id.tv_alert);
        TextView tv_location = (TextView) d_alert.findViewById(R.id.tv_location);
        TextView tv_title = (TextView) d_alert.findViewById(R.id.tv_title);

        tv_title.setText(emergency.getTypes());
        tv_location.setText(address);
        tv_alert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAlert(emergency);
                d_alert.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d_alert.dismiss();
            }
        });
        d_alert.show();
    }
    public void createAlert(Emergency emergency)
    {
        if(ApiClient.isNetworkAvailable(HomeActivity.this))
        {
            pd = new ProgressDialog(HomeActivity.this);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
            String datetime = dateformat.format(c.getTime());
            System.out.println(datetime);

            Log.e("datetime",""+datetime);

            Call<Response> call = ApiClient.create_InstanceAuth(token).CreateAlert(userid, emergency.getId(),
                    address.trim(),current_lat, current_long, countryName, datetime );

            call.enqueue(new Callback<Response>()
            {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null )
                    {
                        if(response.isSuccessful())
                        {
                            Log.e("respose_alert_create",""+new Gson().toJson(response.body()));
                            if(response.body()!= null && response.body().getSuccess() == 1)
                            {
                                String msg = (String) response.body().getMessage();
                                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();

//                                if(getFragmentRefreshListener() != null)
//                                {
//                                    getFragmentRefreshListener().onRefresh();
//                                }
                            }
                            else
                            {
                                String msg = (String) response.body().getMessage();
                                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(HomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            if(pd != null && pd.isShowing())
            {
                pd.dismiss();
            }
            Toast.makeText(HomeActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }
    public static void changeLanguage(HomeActivity con , String langu , int userid)
    {
        new ChangeLanguage(con, userid, langu).execute();

        setNewLocale(con ,langu, userid);

//        unSelectedColor();
//        img_settings.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        tv_settings.setTextColor(getResources().getColor(R.color.colorPrimary));
//
//        if(frame.getChildCount() > 0)
//        {
//            frame.removeAllViews();
//        }
        con.  getSupportFragmentManager().beginTransaction().add(R.id.frame,new SettingsFragment()).commit();

        con.tv_profile.setText(con.getString(R.string.profile_cap));
        con.tv_settings.setText(con.getString(R.string.settings_cap));
        con.tv_home.setText(con.getString(R.string.home));

//        con.startActivity(new Intent(con, HomeActivity.class));


    }
    public void loadUserInfor()
    {
        if(ApiClient.isNetworkAvailable(HomeActivity.this))
        {
            pd = new ProgressDialog(HomeActivity.this);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();

            Call<Response> call = ApiClient.create_InstanceAuth(token).UserInfo(userid);
            call.enqueue(new Callback<Response>()
            {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null )
                    {
                        if(response.isSuccessful())
                        {
                            Log.e("profile_response",""+new Gson().toJson(response.body()));

                            if(response.body() != null && response.body().getSuccess() == 1)
                            {
                                Data objdata = response.body().getData();
                                if(objdata != null)
                                {
//                                tv_first_name.setText(objdata.getFirstName());
//                                tv_last_name.setText(objdata.getLastName());
//                                tv_email.setText(objdata.getEmail());
//                                tv_location.setText(objdata.getLocation());

//                                et.putString(Const.TOKEN, token);
                                    et.putString(Const.FIRST_NAME, objdata.getFirstName());
                                    et.putString(Const.LAST_NAME, objdata.getLastName());
                                    et.putInt(Const.USER_ID, objdata.getId());
                                    et.putString(Const.FIRST_NAME, objdata.getFirstName());
                                    et.putString(Const.LAST_NAME, objdata.getLastName());
                                    et.putString(Const.LANGUAGE, objdata.getLanguage());

                                    if(objdata.getMobile() != null)
                                    {
                                        et.putString(Const.PHONE, objdata.getMobile());
                                    }
                                    if(objdata.getEmail() != null)
                                    {
                                        et.putString(Const.EMAIL, objdata.getEmail());
                                    }
                                    if(objdata.getLocation() != null)
                                    {
                                        et.putString(Const.LOCATION, objdata.getLocation());
                                    }
                                    et.commit();
                                    et.apply();

                                    updateResources(HomeActivity.this, objdata.getLanguage().toLowerCase());
                                    LocaleManager1.setNewLocale(HomeActivity.this, objdata.getLanguage().toLowerCase());
                                    getSupportFragmentManager().beginTransaction().add(R.id.frame,new HomeFragment()).addToBackStack(null).commit();

                                    tv_profile.setText(getString(R.string.profile_cap));
                                    tv_settings.setText(getString(R.string.settings_cap));
                                    tv_home.setText(getString(R.string.home));
                                }
                            }
                            else
                            {
                                String message  = (String) response.body().getMessage();
                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(HomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            if(pd != null && pd.isShowing())
            {
                pd.dismiss();
            }
            Toast.makeText(HomeActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }
    private static void updateResources(Context context, String language)
    {
        Locale locale = new Locale(language);

        Configuration config = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= 17)
        {
            config.setLocale(locale);
        }
        else
        {
            config.locale = locale;
        }
        locale.setDefault(locale);
        context.getResources().updateConfiguration(config,  context.getResources().getDisplayMetrics());

    }
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(getPackageName());
//        receiver = new MyBroadcastReceiver();
//        if(receiver != null)
//        {
//            registerReceiver(receiver, intentFilter);
//        }
//    }
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        if(receiver != null)
//        {
//           unregisterReceiver(receiver);
//        }
//    }
    public static void setNewLocale(AppCompatActivity mContext, @LocaleManager1.LocaleDef String language, int user_id)
    {
        LocaleManager1.setNewLocale(mContext, language);
//        Intent intent = mContext.getIntent();
//        mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//        ((HomeActivity)mContext).finish();
    }
//    private class MyBroadcastReceiver extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            Bundle extras = intent.getExtras();
//
//            String state = extras.getString("extra");
//
//            if(state != null)
//            {
//
//            }
////            if (ApiClient.isNetworkAvailable(getActivity()))
////            {
////                AllIncidents();
////            }
////            else
////            {
////                Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
////            }
//        }
//    }
    public void changeLatLong()
    {
        Call<Response> call = ApiClient.create_InstanceAuth(token).CallBack(userid, current_lat, current_long);
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if(response != null )
                {
                    if(response.isSuccessful())
                    {
                        if(response.body() != null && response.body().getSuccess() == 1)
                        {
//                        Toast.makeText(HomeActivity.this, (String) response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
//                                            Toast.makeText(HomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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
                Log.e("callback",""+t.toString());
//                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }




//    @Override
//    public void update()
//    {
//        if(countryCode   != null && countryName != null && countryCode.equals(HomeFragment.countryCode) && countryName.equals(HomeFragment.country))
//        {
//
//        }
//        else
//        {
//            HomeFragment.countryCode = countryCode;
//            HomeFragment.countryCode = countryName;
//            EmergencyFragment.countryCode =HomeFragment.countryCode;
//            EmergencyFragment.country = HomeFragment.country;
//        }
//    }
}
