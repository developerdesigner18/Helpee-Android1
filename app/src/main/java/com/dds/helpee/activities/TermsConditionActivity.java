package com.dds.helpee.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dds.helpee.R;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.api.ApiInterface;
import com.dds.helpee.model.Const;

import org.intellij.lang.annotations.Language;

public class TermsConditionActivity extends AppCompatActivity
{
    WebView terms_cond_mWebView;
    ProgressBar termsCondition_mProgressBar;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    String language = null, url = null;

    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);

        pref = getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        language = pref.getString(Const.LANGUAGE, null);

        if(language.equalsIgnoreCase("FR"))
        {
            url = ApiClient.TC_FR_BASE_URL;
        }
        else
        {
            url = ApiClient.TC_IN_BASE_URL;
        }
        termsCondition_mProgressBar= findViewById(R.id.termsCondition_mProgressBar);
        terms_cond_mWebView = findViewById(R.id.web_terms);
        terms_cond_mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = terms_cond_mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        terms_cond_mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        terms_cond_mWebView.getSettings().setBuiltInZoomControls(true);
        terms_cond_mWebView.getSettings().setLoadWithOverviewMode(true);
//        privacypolicy_mWebView.setWebViewClient(new GeoWebViewClient());
        terms_cond_mWebView.getSettings().setAllowFileAccess(true);
        terms_cond_mWebView.getSettings().setJavaScriptEnabled(true);
        terms_cond_mWebView.getSettings().setGeolocationEnabled(true);
        terms_cond_mWebView.getSettings().setDomStorageEnabled(true);
//        privacypolicy_mWebView.setWebChromeClient(new GeoWebChromeClient());
        terms_cond_mWebView.clearCache(true);


        terms_cond_mWebView.loadUrl(url);

        if (Build.VERSION.SDK_INT >= 19) {
            terms_cond_mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            terms_cond_mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        termsCondition_mProgressBar.setVisibility(View.GONE);
    }
}
