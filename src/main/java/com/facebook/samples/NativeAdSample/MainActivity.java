package com.facebook.samples.NativeAdSample;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import base.util.ad.FacebookAdListener;
import base.util.ad.FacebookAds;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;

public class MainActivity extends Activity implements FacebookAdListener {

    public void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "FB::onCreate =========================================================");
        Log.i(getClass().getSimpleName(), "FB::onCreate getCountLoaded=" + FacebookAds.get(getContext()).getCountLoaded());
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.native_ad_list_demo);

        AdSettings.addTestDevice("fe60ee5c6967c9f3c416b11ef48fa683");
        FacebookAds.get(getContext()).setExternalListener(this);
        FacebookAds.get(getContext()).show();

        TextView ad_tv = (TextView) findViewById(R.id.ad_tv);
        ad_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NativeAdSampleActivity.class));
            }
        });
    }


    @Override
    public void onAdsLoaded(final NativeAd nativeAd) {
        Log.i(getClass().getSimpleName(), "FB::onAdsLoaded " + nativeAd.getAdTitle());

        View adView = FacebookAds.inflateAd(nativeAd, R.layout.ad_unit2, getContext());
        LinearLayout ad_ll = (LinearLayout) findViewById(R.id.ad_ll);
        if(ad_ll != null) {
            if(ad_ll.getChildCount() > 1) {
                ad_ll.removeViewAt(1);
            }
            ad_ll.addView(adView);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(getClass().getSimpleName(), "FB::onDestroy getCountLoaded=" + FacebookAds.get(getContext()).getCountLoaded());
        super.onDestroy();
        FacebookAds.get(getContext()).check();
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public Context getActivity() {
        return this;
    }
}
