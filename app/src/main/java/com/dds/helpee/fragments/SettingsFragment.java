package com.dds.helpee.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dds.helpee.LocaleHelper;
import com.dds.helpee.LocaleManager1;
import com.dds.helpee.R;
import com.dds.helpee.activities.BaseActivity;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.activities.MainActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.api.ApiInterface;
import com.dds.helpee.interfaces.BackPressHandler;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Response;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

import static com.dds.helpee.HelpeeApp.setLocale;

public class SettingsFragment extends Fragment implements BackPressHandler
{
    TextView  tv_language;
    ImageView img_notification, img_next;
    View view;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    String token = null, language = "en";
    int userId = 0 ;
    boolean isNotification = false;
    LinearLayout layout_settings;
    ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        tv_language = (TextView) view.findViewById(R.id.tv_language);

        img_notification = (ImageView) view.findViewById(R.id.img_notification);
        img_next = (ImageView) view.findViewById(R.id.img_next);

        layout_settings = (LinearLayout) view.findViewById(R.id.layout_settings);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        userId = pref.getInt(Const.USER_ID, 0);
        token = pref.getString(Const.TOKEN, null);
        language = pref.getString(Const.LANGUAGE, null);

        if(language != null)
        {
            if(language.equals("fr"))
            {
                tv_language.setText(getString(R.string.french));
            }
            else
            {
                tv_language.setText(getString(R.string.english));
            }
        }
        else
        {
            tv_language.setText(getString(R.string.english));
        }

        if(pref.getBoolean(Const.NOTIFICATION, true) == true)
        {
            isNotification = true;
            img_notification.setImageResource(R.drawable.on_switch);
        }
        else
        {
            isNotification = false;
            img_notification.setImageResource(R.drawable.off_switch);
        }

        img_notification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isNotification == true)
                {
                    isNotification = false;
                    img_notification.setImageResource(R.drawable.off_switch);
                }
                else
                {
                    isNotification = true;
                    img_notification.setImageResource(R.drawable.on_switch);
                }
                et.putBoolean(Const.NOTIFICATION,  isNotification);
                et.commit();
                et.apply();
            }
        });
        img_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, new UpdateDetailsFragment()).addToBackStack(null).commit();
            }
        });
        tv_language.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowLanguageDialog();
            }
        });
        return  view;
    }

    public void ShowLanguageDialog()
    {
        Dialog d_language = new Dialog(getActivity());
        d_language.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d_language.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        d_language.setContentView(R.layout.dialog_language);

        ImageView img_en_radio = (ImageView) d_language.findViewById(R.id.img_en_radio);
        LinearLayout layout_en= (LinearLayout) d_language.findViewById(R.id.layout_en);

        ImageView img_fr_radio = (ImageView) d_language.findViewById(R.id.img_fr_radio);
        LinearLayout layout_fr = (LinearLayout) d_language.findViewById(R.id.layout_fr);

        if(language != null && language.equals("en"))
        {
            img_en_radio.setImageResource(R.drawable.radio_on);
            img_fr_radio.setImageResource(R.drawable.radio_off);
        }
        else
        {
            img_fr_radio.setImageResource(R.drawable.radio_on);
            img_en_radio.setImageResource(R.drawable.radio_off);
        }

        layout_en.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                language = "en";
                tv_language.setText(getString(R.string.english));
                et.putString(Const.LANGUAGE , "en");
                et.commit();
                et.apply();



                updateResources(getActivity(), "en");

                LocaleManager1.setNewLocale(getActivity(), "en");

                HomeActivity.changeLanguage((HomeActivity) getActivity(),LocaleManager1.LocaleDef.SUPPORTED_LOCALES[0], userId);
//                setLocale(getActivity(), MainActivity.class, "en");
                img_en_radio.setImageResource(R.drawable.radio_on);
                img_fr_radio.setImageResource(R.drawable.radio_off);
                d_language.dismiss();
            }
        });
        layout_fr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                language = "fr";
                tv_language.setText(getString(R.string.french));
                et.putString(Const.LANGUAGE , "fr");
                et.commit();
                et.apply();



                updateResources(getActivity(), "fr");
                LocaleManager1.setNewLocale(getActivity(), "fr");
                HomeActivity.changeLanguage((HomeActivity) getActivity(),LocaleManager1.LocaleDef.SUPPORTED_LOCALES[1], userId);



//                setLocale(getActivity(), MainActivity.class, "fr");
                img_fr_radio.setImageResource(R.drawable.radio_on);
                img_en_radio.setImageResource(R.drawable.radio_off);
                d_language.dismiss();
            }
        });
        d_language.show();
    }
    private void setNewLocale(AppCompatActivity mContext, @LocaleManager1.LocaleDef String language)
    {
        LocaleManager1.setNewLocale(getActivity(), language);
//        Intent intent = mContext.getIntent();
//        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
    public void RefreshFrag()
    {
        // Reload current fragment
//        Fragment frg = null;
//        frg = getSupportFragmentManager().fin
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }
    @Override
    public void onBack()
    {
    }

//    public class ChangeLanguage extends AsyncTask<Void, Void, Void>
//    {
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//
//            pd = new ProgressDialog(getActivity());
//            pd.setCancelable(false);
//            pd.setMessage(getString(R.string.please_wait));
//            pd.show();
//        }
//        @Override
//        protected Void doInBackground(Void... voids)
//        {
//            UpdateLanguage();
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void aVoid)
//        {
//            super.onPostExecute(aVoid);
//
//            if(language.equals("fr"))
//            {
//                tv_language.setText(getString(R.string.french));
//            }
//            else
//            {
//                tv_language.setText(getString(R.string.english));
//            }
//        }
//    }
//    public void UpdateLanguage()
//    {
//        if(ApiClient.isNetworkAvailable(getActivity()))
//        {
//            Call<Response> call = ApiClient.create_InstanceAuth(token).ChangeLanguage(userId, language);
//            call.enqueue(new Callback<Response>()
//            {
//                @Override
//                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
//                {
//                    if(pd != null && pd.isShowing())
//                    {
//                        pd.dismiss();
//                    }
//                    if(response != null && response.isSuccessful())
//                    {
//                        Log.e("profile_response",""+new Gson().toJson(response.body()));
//
//                        if(response.body() != null && response.body().getSuccess() == 1)
//                        {
//                            String message  = (String) response.body().getMessage();
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//
//                        }
//                        else
//                        {
//                            String message  = (String) response.body().getMessage();
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                @Override
//                public void onFailure(Call<Response> call, Throwable t)
//                {
//                    if(pd != null && pd.isShowing())
//                    {
//                        pd.dismiss();
//                    }
//                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        else
//        {
//            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
//        }
//    }
}
