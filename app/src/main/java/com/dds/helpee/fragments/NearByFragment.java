package com.dds.helpee.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dds.helpee.BuildConfig;
import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.adapters.NearByAdapters;
import com.dds.helpee.adapters.ReportsAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Alerts;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Emergency;
import com.dds.helpee.model.ErrorPojoClass;
import com.dds.helpee.model.Response;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;

import static com.google.zxing.common.CharacterSetECI.UTF8;

public class NearByFragment extends Fragment
{
    EditText et_phone, et_user_name, et_password;
    TextView tv_login;
    LinearLayout layout_email, layout_number;
    boolean isphone = false;
    ImageView img_email, img_phone;
    RecyclerView rcv_incident;
    TextView tv_empty;

    TextInputLayout email_layout, password_layout, phone_layout;
    View view;
    String token = null;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    ProgressDialog pd;
//    List<Emergency> listEmergency;
    List<Alerts> listIncident;
    int userId = 0;
    MyBroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_near_by, container, false);

        rcv_incident = (RecyclerView) view.findViewById(R.id.rcv_incident);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        userId = pref.getInt(Const.USER_ID, 0);
        token = pref.getString(Const.TOKEN, null);

        if (ApiClient.isNetworkAvailable(getActivity()))
        {
            AllIncidents();
        }
        else
        {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

//        ((HomeActivity)getActivity()).setFragmentRefreshListener(new HomeActivity.FragmentRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                if (ApiClient.isNetworkAvailable(getActivity()))
//                {
//                    AllIncidents();
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });


        return view;
    }
//    public class GetAllIncident extends AsyncTask<Void, Void, Void>
//    {
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//            pd = new ProgressDialog(getActivity());
//            pd.setCancelable(false);
//            pd.setMessage(getString(R.string.please_wait));
//            pd.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids)
//        {
//            AllIncidents();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            super.onPostExecute(aVoid);
//            if (pd != null && pd.isShowing())
//            {
//                pd.dismiss();
//            }
//            Log.e("listEmergency_data1111", "" + listIncident.size());
//            if (listIncident != null && listIncident.size() > 0)
//            {
//                tv_empty.setVisibility(View.GONE);
//                rcv_incident.setVisibility(View.VISIBLE);
//
//                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
//                rcv_incident.setLayoutManager(manager);
//                NearByAdapters adapter = new NearByAdapters(getActivity(), listIncident);
//                rcv_incident.setAdapter(adapter);
//            }
//            else
//            {
//                tv_empty.setVisibility(View.VISIBLE);
//                rcv_incident.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "No data found for this user id.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
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
                    AllIncidents_Auto();
                }
                else
                {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getActivity().getPackageName());
        receiver = new MyBroadcastReceiver();
        if(receiver != null)
        {
            getActivity().registerReceiver(receiver, intentFilter);
        }
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
    public void AllIncidents_Auto()
    {
        Call<Response> call = ApiClient.create_InstanceAuth(token).GetAllIncidents(userId);
        Log.e("call_reoport", "" + call.request());
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                Log.e("response success","?"+response.isSuccessful());
                if (response != null )
                {
                    if(response.isSuccessful())
                    {
                        Log.e("incident_response", "" + new Gson().toJson(response.body()));

                        if (response.body() != null && response.body().getSuccess() == 1)
                        {
                            if (response.body().getAlerts() != null)
                            {
                                listIncident = response.body().getAlerts();
                                Log.e("listEmergency_data", "" + listIncident.size());
                                if (listIncident != null && listIncident.size() > 0)
                                {
                                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                                    rcv_incident.setLayoutManager(manager);
                                    NearByAdapters adapter = new NearByAdapters(getActivity(), listIncident);
                                    rcv_incident.setAdapter(adapter);
                                }
                            }
                        }
                        else if (response.errorBody() != null && response.code() == 400)
                        {
                            Log.e("mError.getMessage()111",""+response.getClass());
                            Gson gson = new GsonBuilder().create();
                            ErrorPojoClass mError = new ErrorPojoClass();
                            try
                            {
                                mError = gson.fromJson(response.errorBody().string(), ErrorPojoClass.class);
                                Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("mError.getMessage()",""+mError.getMessage());
                            }
                            catch (IOException e)
                            {
                                // handle failure to read error
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
                                            Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();

                                            tv_empty.setVisibility(View.VISIBLE);
                                            rcv_incident.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "No data found for this user id.", Toast.LENGTH_SHORT).show();
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
                Log.e("exception", "" + t.toString());
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void AllIncidents()
    {
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.please_wait));
        pd.show();

        Call<Response> call = ApiClient.create_InstanceAuth(token).GetAllIncidents(userId);
        Log.e("call_reoport", "" + call.request());
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if (pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }
                Log.e("response success","?"+response.isSuccessful());
                if (response != null )
                {
                    if(response.isSuccessful())
                    {
                        Log.e("incident_response", "" + new Gson().toJson(response.body()));

                        if (response.body() != null && response.body().getSuccess() == 1)
                        {
                            if (response.body().getAlerts() != null)
                            {
                                listIncident = response.body().getAlerts();
                                Log.e("listEmergency_data", "" + listIncident.size());
                                if (listIncident != null && listIncident.size() > 0)
                                {
                                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                                    rcv_incident.setLayoutManager(manager);
                                    NearByAdapters adapter = new NearByAdapters(getActivity(), listIncident);
                                    rcv_incident.setAdapter(adapter);
                                }
                            }
                        }
                        else if (response.errorBody() != null && response.code() == 400)
                        {
                            Log.e("mError.getMessage()111",""+response.getClass());
                            Gson gson = new GsonBuilder().create();
                            ErrorPojoClass mError = new ErrorPojoClass();
                            try
                            {
                                mError = gson.fromJson(response.errorBody().string(), ErrorPojoClass.class);
                                Toast.makeText(getActivity(), mError.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("mError.getMessage()",""+mError.getMessage());
                            }
                            catch (IOException e)
                            {
                                // handle failure to read error
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
                                           Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();

                                            tv_empty.setVisibility(View.VISIBLE);
                                            rcv_incident.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "No data found for this user id.", Toast.LENGTH_SHORT).show();
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
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                Log.e("exception", "" + t.toString());
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


//    public void AlertsType() {
//        Call<Response> call = ApiClient.create_InstanceAuth(token).GetAlertsTypes();
//        Log.e("call_reoport", "" + call.request());
//        call.enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                if (pd != null && pd.isShowing()) {
//                    pd.dismiss();
//                }
//                if (response != null && response.isSuccessful()) {
//                    Log.e("alert_response", "" + new Gson().toJson(response.body()));
//
//                    if (response.body() != null && response.body().getSuccess() == 1) {
//                        if (response.body().getEmergency() != null) {
//                            listEmergency = response.body().getEmergency();
//                            Log.e("listEmergency", "" + listEmergency.size());
//                            if (listEmergency != null && listEmergency.size() > 0) {
////                                GridLayoutManager manager = new GridLayoutManager(getActivity() , 2);
////                                rcv_reports.setLayoutManager(manager);
////                                ReportsAdapter adapter = new ReportsAdapter(getActivity(), listEmergency);
////                                rcv_reports.setAdapter(adapter);
//                            }
//                        }
//                    } else {
//                        String message = (String) response.body().getMessage();
//                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//                if (pd != null && pd.isShowing()) {
//                    pd.dismiss();
//                }
//                Log.e("exception", "" + t.toString());
//                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    public class GetAllAlerts extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(getActivity());
//            pd.setCancelable(false);
//            pd.setMessage(getString(R.string.please_wait));
//            pd.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            AlertsType();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
////            Log.e("listEmergency1", ""+listEmergency.size());
//            if (pd != null && pd.isShowing()) {
//                pd.dismiss();
//            }
//            if (listEmergency != null && listEmergency.size() > 0) {
////                GridLayoutManager manager = new GridLayoutManager(getActivity() , 2);
////                rcv_reports.setLayoutManager(manager);
////                ReportsAdapter adapter = new ReportsAdapter(getActivity(), listEmergency);
////                rcv_reports.setAdapter(adapter);
//            }
//        }
//    }
}
