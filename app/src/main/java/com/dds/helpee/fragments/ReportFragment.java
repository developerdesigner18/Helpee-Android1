package com.dds.helpee.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dds.helpee.R;
import com.dds.helpee.RequestPermissionHandler;
import com.dds.helpee.adapters.ReportsAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Emergency;
import com.dds.helpee.model.Response;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ReportFragment extends Fragment
{
    RecyclerView rcv_reports;
    String token = null;
    View view;
    public static List<Emergency> listEmergency = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor et;
    ProgressDialog pd;
    ReportFragment obj ;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    LocationManager mLocationManager;
    LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    double currentLat = 0;
    double currentLong = 0;
//    private RequestPermissionHandler mRequestPermissionHandler;
//
//    String[] permissions =
//            {
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_report, container, false);

//        mRequestPermissionHandler = new RequestPermissionHandler();
//        checkPermissions();

        obj = this;

        rcv_reports = (RecyclerView) view.findViewById(R.id.rcv_reports);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        token = pref.getString(Const.TOKEN, null);

        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            new GetAllAlerts().execute();
        }
        else
        {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
        return  view;
    }
//    private void checkPermissions()
//    {
//        int PER_CODE = 777;
//
//        mRequestPermissionHandler.requestPermission(getActivity(), permissions,
//                PER_CODE, new RequestPermissionHandler.RequestPermissionListener()
//                {
//                    @Override
//                    public void onSuccess()
//                    {
//                        Log.e("success","location success");
//                    }
//
//                    @Override
//                    public void onFailed()
//                    {
//                        Toast.makeText(getActivity(), "Request permission failed", Toast.LENGTH_SHORT).show();
//
//                        new AlertDialog.Builder(getActivity())
//                                .setCancelable(false)
//                                .setTitle("Permission necessary")
//                                .setMessage("Please grant all permissions to proceed with app.")
//                                .setPositiveButton("Re-Try", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        checkPermissions();
//                                    }
//                                })
//                                .setNegativeButton("Settings", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        Intent intent = new Intent();
//                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                                        intent.setData(uri);
//                                        startActivity(intent);
//
//                                        checkPermissions();
//                                    }
//                                })
//                                .create().show();
//                    }
//
//                });
//
//    }
    private boolean checkLocation()
    {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public void AlertsType()
    {
        Call<Response> call = ApiClient.create_InstanceAuth(token).GetAlertsTypes();
        Log.e("call_reoport",""+call.request());
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if(pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }
                if(response != null && response.isSuccessful())
                {
                    if(response.isSuccessful())
                    {
                        Log.e("alert_response",""+new Gson().toJson(response.body()));

                        if(response.body() != null && response.body().getSuccess() == 1)
                        {
                            if(response.body().getEmergency() != null)
                            {
                                listEmergency = response.body().getEmergency();
                                Log.e("listEmergency", ""+listEmergency.size());
                                if(listEmergency != null && listEmergency.size() > 0)
                                {
                                    GridLayoutManager manager = new GridLayoutManager(getActivity() , 2);
                                    rcv_reports.setLayoutManager(manager);
                                    ReportsAdapter adapter;
                                    adapter = new ReportsAdapter(getActivity(), listEmergency, obj);
                                    rcv_reports.setAdapter(adapter);
                                }
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
                Log.e("exception",""+t.toString());
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CreateAlert(int position)
    {
            Toast.makeText(getActivity(), ""+listEmergency.get(position), Toast.LENGTH_SHORT).show();
    }

//@Override
//public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//{
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    mRequestPermissionHandler.onRequestPermissionsResult(requestCode, grantResults);
//}
    @Override
    public void onStart()
    {
        super.onStart();
    }
    @Override
    public void onStop()
    {
        super.onStop();
    }
    public class  GetAllAlerts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            AlertsType();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Log.e("listEmergency1", ""+listEmergency.size());
            if(pd != null && pd.isShowing())
            {
                pd.dismiss();
            }
            if(listEmergency != null && listEmergency.size() > 0)
            {
                GridLayoutManager manager = new GridLayoutManager(getActivity() , 2);
                rcv_reports.setLayoutManager(manager);
                ReportsAdapter adapter = new ReportsAdapter(getActivity(), listEmergency, obj);
                rcv_reports.setAdapter(adapter);
            }
        }
    }
}
