package com.facebook.samples.NativeAdSample;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import base.util.ad.FacebookAdListener;
import base.util.ad.FacebookAds;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;

public class MainActivity extends Activity implements FacebookAdListener {

    private LinearLayout ad_ll;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "FB::onCreate getCountLoaded=" + FacebookAds.get(getContext()).getCountLoaded());
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.native_ad_list_demo);

        ad_ll = (LinearLayout) findViewById(R.id.ad_ll);

        AdSettings.addTestDevice("fe60ee5c6967c9f3c416b11ef48fa682");
        FacebookAds.get(getContext()).setExternalListener(this);
        FacebookAds.get(getContext()).show();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onAdsLoaded(final NativeAd nativeAd) {
        Log.i(getClass().getSimpleName(), "FB::onAdsLoaded " + nativeAd.getAdTitle());

        View adView = FacebookAds.inflateAd(nativeAd, R.layout.ad_unit2, getContext());
        ad_ll.addView(adView);

//        final AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
//        ad.show();
//        ad.getWindow().setContentView(adView);
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public Context getActivity() {
        return this;
    }
}
