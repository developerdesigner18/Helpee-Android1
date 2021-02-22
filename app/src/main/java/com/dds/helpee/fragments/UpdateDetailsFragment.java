package com.dds.helpee.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dds.helpee.R;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.interfaces.BackPressHandler;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Response;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UpdateDetailsFragment extends Fragment implements BackPressHandler
{
    ProgressDialog pd;
    EditText  et_first_name, et_last_name, et_password, et_email, et_location;
    View view;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    String token = null;
    int userid = 0;
    TextView tv_save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_update_details, container, false);
        et_first_name = (EditText) view.findViewById(R.id.et_first_name);
        et_last_name = (EditText) view.findViewById(R.id.et_last_name);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_location = (EditText) view.findViewById(R.id.et_location);

        tv_save = (TextView) view.findViewById(R.id.tv_save);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        userid = pref.getInt(Const.USER_ID, 0);
        token = pref.getString(Const.TOKEN, null);

        loadUserInfor();

        tv_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String firstname = et_first_name.getText().toString().trim();
                String lastname = et_last_name.getText().toString().trim();
                String location = et_location.getText().toString();

                if(TextUtils.isEmpty(firstname))
                {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_first_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(lastname))
                {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_last_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(location))
                {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_location), Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateUserInfo();
            }
        });
        return view;
    }
    public void loadUserInfor()
    {
        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            pd = new ProgressDialog(getActivity());
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
                    if(response != null && response.isSuccessful())
                    {
                        Log.e("profile_response",""+new Gson().toJson(response.body()));

                        if(response.body() != null && response.body().getSuccess() == 1)
                        {
                            Data objdata = response.body().getData();
                            if(objdata != null)
                            {
                                et_first_name.setText(objdata.getFirstName());
                                et_last_name.setText(objdata.getLastName());
                                et_email.setText(objdata.getEmail());
                                et_location.setText(objdata.getLocation());

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
                            }
                        }
                        else
                        {
                            String message  = (String) response.body().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
    public void UpdateUserInfo()
    {
        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();

            Call<Response> call = ApiClient.create_InstanceAuth(token).UpdateUser(userid, et_first_name.getText().toString().trim(),
                    et_last_name.getText().toString().trim(), et_location.getText().toString().trim());

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
                        Log.e("update user response",""+new Gson().toJson(response.body()));

                        if(response.body() != null && response.body().getSuccess() == 1)
                        {

                            Data objdata = response.body().getData();
                            if(objdata != null)
                            {
                                et_first_name.setText(objdata.getFirstName());
                                et_last_name.setText(objdata.getLastName());
                                et_email.setText(objdata.getEmail());
                                et_location.setText(objdata.getLocation());

                                et.putString(Const.FIRST_NAME, objdata.getFirstName());
                                et.putString(Const.LAST_NAME, objdata.getLastName());
                                et.putInt(Const.USER_ID, objdata.getId());
                                if(objdata.getToken() != null)
                                {
                                    et.putString(Const.TOKEN, objdata.getToken());
                                }
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
                            }

                            String message  = (String) response.body().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SettingsFragment()).addToBackStack(null).commit();
                        }
                        else
                        {
                            String message  = (String) response.body().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBack()
    {
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SettingsFragment()).addToBackStack(null).commit();
    }
}
