package com.dds.helpee;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.model.Const;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Locale;
@ReportsCrashes(
        mailTo = "",
        customReportContent = {ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.SILENT
)
public class HelpeeApp extends Application
{
    SharedPreferences pref;
    SharedPreferences.Editor et;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(LocaleManager1.setLocale(base));
        ACRA.init(this);
    }
//    @Override
//    protected void attachBaseContext(Context base)
//    {
//        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
////        ACRA.init(this);
//    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        LocaleManager1.setLocale(this);
    }
    @Override
    public void onCreate()
    {


        pref = getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        String lang = pref.getString(Const.LANGUAGE, null);
        if(lang != null)
        {
            if(lang.contains("fr"))
            {
                setLocale(this, null, lang);
            }
            else
            {
                setLocale(this, null, lang);
            }
        }
        else
        {
            setLocale(this, null, "en");
        }
        super.onCreate();
    }
    public static void setLocale(Context context, Class classToStart, String lang)
    {
        SharedPreferences pref = context.getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor et = pref.edit();
        et.putString(Const.LANGUAGE, lang);
        et.commit();
        et.apply();

        Log.e("lang_app",""+lang);
        Locale locale1 = new Locale(lang);
        Locale.setDefault(locale1);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = locale1;
        res.updateConfiguration(conf, dm);

        if(classToStart != null)
        {
            Intent refresh = new Intent(context, classToStart);
            refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(refresh);

            ((HomeActivity) context).finish();
        }
    }
}
