package com.dds.helpee;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class LocaleManager1
{
  @Retention(RetentionPolicy.SOURCE)
  @StringDef({ ENGLISH, FRENCH })

  public @interface LocaleDef
  {
    String[] SUPPORTED_LOCALES = { ENGLISH, FRENCH };
  }
  static final String ENGLISH = "en";
  static final String FRENCH = "fr";
  /**
   * SharedPreferences Key
   */
  private static final String LANGUAGE_KEY = "language_key";
//  private static final String LANGUAGE_KEY = "Locale.Helper.Selected.Language";
  /**
   * set current pref locale
   */
  public static Context setLocale(Context mContext) {
    return updateResources(mContext, getLanguagePref(mContext));
  }

  /**
   * Set new Locale with context
   */
  public static Context setNewLocale(Context mContext, @LocaleDef String language) {
    setLanguagePref(mContext, language);
    return updateResources(mContext, language);
  }

  /**
   * Get saved Locale from SharedPreferences
   *
   * @param mContext current context
   * @return current locale key by default return english locale
   */
  public static String getLanguagePref(Context mContext) {
    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    Log.e("pref",""+mPreferences.getString(LANGUAGE_KEY, ENGLISH));
    return mPreferences.getString(LANGUAGE_KEY, ENGLISH);
  }

  /**
   * set pref key
   */
  private static void setLanguagePref(Context mContext, String localeKey) {
    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();
  }

  /**
   * update resource
   */
  private static Context updateResourcesLegacy(Context context, String language)
  {
    Locale locale = new Locale(language);
    Locale.setDefault(locale);

    Resources resources = context.getResources();

    Configuration configuration = resources.getConfiguration();
    configuration.locale = locale;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
    {
      configuration.setLayoutDirection(locale);
    }

    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    return context;
  }
  @TargetApi(Build.VERSION_CODES.N)
  private static Context updateResources1(Context context, String language)
  {
    Locale locale = new Locale(language);
    Locale.setDefault(locale);

    Configuration configuration = context.getResources().getConfiguration();
    configuration.setLocale(locale);
    configuration.setLayoutDirection(locale);

    return context.createConfigurationContext(configuration);
  }
  private static Context updateResources(Context context, String language)
  {
    Context newcon;

    Locale locale = new Locale(language);
    Locale.setDefault(locale);
//    Resources res = context.getResources();
    Configuration config = new Configuration(context.getResources().getConfiguration());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    {
      config.setLocale(locale);
      config.setLayoutDirection(locale);
      newcon = context.createConfigurationContext(config);
    }
    else
      {
        Resources res = context.getResources();
      config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        newcon = context;
    }
    return newcon;
  }

  /**
   * get current locale
   */
  public static Locale getLocale(Resources res) {
    Configuration config = res.getConfiguration();
    return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
  }
}
