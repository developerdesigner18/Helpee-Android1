package com.dds.helpee.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dds.helpee.LocaleHelper;
import com.dds.helpee.LocaleManager1;
import com.dds.helpee.R;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.pm.PackageManager.GET_META_DATA;

public abstract class BaseActivity extends AppCompatActivity {


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    resetTitles();
  }
//  @Override
//  protected void attachBaseContext(Context base)
//  {
//    super.attachBaseContext(LocaleHelper.onAttach(base));
//  }
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(LocaleManager1.setLocale(base));
  }

  protected void resetTitles() {
    try {
      ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
      if (info.labelRes != 0) {
        setTitle(info.labelRes);
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }
  public static class ChangeLanguage extends AsyncTask<Void, Void, Void>
  {
    int userId;
    String language, token;
    SharedPreferences pref;
    Context context;
    SharedPreferences.Editor et;
    public static ProgressDialog  pd;

    public ChangeLanguage(Context con , int user_id, String language1)
    {
        context = con;
        userId = user_id;
        language = language1;

      pref = context.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
      et = pref.edit();
      token = pref.getString(Const.TOKEN, null);
    }
    @Override
    protected void onPreExecute()
    {
      super.onPreExecute();

      pd = new ProgressDialog(context);
      pd.setCancelable(false);
      pd.setMessage(context.getString(R.string.please_wait));
      pd.show();
    }
    @Override
    protected Void doInBackground(Void... voids)
    {
      UpdateLanguage(pd, context, token, userId, language);
      return null;
    }
    @Override
    protected void onPostExecute(Void aVoid)
    {
      super.onPostExecute(aVoid);
    }
  }
  public static void UpdateLanguage(ProgressDialog pd, Context context , String token, int userId, String language)
  {
    ProgressDialog pdd = pd;
    if(ApiClient.isNetworkAvailable(context))
    {
      Call<Response> call = ApiClient.create_InstanceAuth(token).ChangeLanguage(userId, language);
      call.enqueue(new Callback<Response>()
      {
        @Override
        public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
        {
          if(pdd != null && pdd.isShowing())
          {
            pdd.dismiss();
          }
          if(response != null )
          {
            if(response.isSuccessful())
            {
              Log.e("profile_response",""+new Gson().toJson(response.body()));

              if(response.body() != null && response.body().getSuccess() == 1)
              {
                String message  = (String) response.body().getMessage();
                Toast.makeText(context , message, Toast.LENGTH_SHORT).show();
              }
              else
              {
                String message  = (String) response.body().getMessage();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
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
          if(pdd != null && pdd.isShowing())
          {
            pdd.dismiss();
          }
          Toast.makeText(context , context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }
      });
    }
    else
    {
      Toast.makeText(context, context. getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    }
  }
}
