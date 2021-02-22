package com.dds.helpee.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.dds.helpee.R;
import com.dds.helpee.activities.MainActivity;
import com.dds.helpee.activities.TermsConditionActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.ErrorPojoClass;
import com.dds.helpee.model.Response;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileFragment extends Fragment
{
    EditText et_phone, et_user_name, et_password;
    TextView tv_first_name, tv_last_name, tv_password, tv_email, tv_location, tv_terms,tv_logout, tv_delete_account;
    LinearLayout layout_terms, layout_email, layout_number;
    boolean isphone = false;
    ImageView img_email, img_phone;

    TextInputLayout email_layout, password_layout, phone_layout;
    View view;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    ProgressDialog pd;
    String type=null, token = null;
    int userid = 0 ;
    GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        tv_first_name = (TextView) view.findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) view.findViewById(R.id.tv_last_name);
        tv_password = (TextView) view.findViewById(R.id.tv_password);
        tv_email = (TextView) view.findViewById(R.id.tv_email);
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        tv_terms = (TextView) view.findViewById(R.id.tv_terms);
        tv_logout = (TextView) view.findViewById(R.id.tv_logout);
        tv_delete_account = (TextView) view.findViewById(R.id.tv_delete_account);

        layout_terms = (LinearLayout) view.findViewById(R.id.layout_terms);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        userid = pref.getInt(Const.USER_ID, 0);
        token = pref.getString(Const.TOKEN, null);
        type = pref.getString(Const.TYPE, null);

        loadUserInfor();

        tv_terms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i_go = new Intent(getActivity(), TermsConditionActivity.class);
                startActivity(i_go);
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(type != null && type.equals("4"))
                {
                    signOut();
                }
                else if(type != null && type.equals("3"))
                {
                    graphRevokeUserStatusPermission();
                    LoginManager.getInstance().logOut();
                    Logout();
                }
                else
                {
                    Logout();
                }

            }
        });
        tv_delete_account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Delete_Account();
            }
        });
        return  view;
    }
    public void graphRevokeUserStatusPermission()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null)
            return;

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/permissions/",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {

                        LoginManager.getInstance().logOut();
                        // response
                    }
                }
        );
        request.setHttpMethod(HttpMethod.DELETE);
        request.executeAsync();
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
                                tv_first_name.setText(objdata.getFirstName());
                                tv_last_name.setText(objdata.getLastName());
                                tv_email.setText(objdata.getEmail());
                                tv_location.setText(objdata.getLocation());

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
    public  void signOut()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        revokeAccess();
                        Logout();
                    }
                });
    }

    public  void revokeAccess()
    {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
    public void Delete_Account()
    {
        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();

            Call<Response> call = ApiClient.create_InstanceAuth(token).DeleteAccount(userid);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null && response.isSuccessful())
                    {
                        if (response.body() != null && response.body().getSuccess() == 1)
                        {
                            et.clear();
                            et.commit();
                            et.apply();

                            String message  = (String) response.body().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                            Intent i_go = new Intent(getActivity(), MainActivity.class);
                            startActivity(i_go);
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
    public void Logout()
    {
        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();

            Call<Response> call = ApiClient.create_InstanceAuth(token).Logout();
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null && response.isSuccessful())
                    {
                        if(response.errorBody()!= null)
                        {
                            Log.e("error",""+new Gson().toJson(response.errorBody()));
//                            ErrorPojoClass error = response.errorBody();

                        }
                        else if (response.body() != null && response.body().getSuccess() == 1)
                        {
                            et.putBoolean(Const.LOGIN , false);
                            et.commit();
                            et.apply();

                            String message  = (String) response.body().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                            Intent i_go = new Intent(getActivity(), MainActivity.class);
                            startActivity(i_go);
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
}
