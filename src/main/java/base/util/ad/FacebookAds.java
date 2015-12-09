package base.util.ad;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.facebook.ads.*;
import com.facebook.samples.NativeAdSample.R;
import com.squareup.picasso.Picasso;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import base.util.ReleaseUtil;
import base.util.TimeUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FacebookAds implements NativeAdsManager.Listener, AdListener {
    public static final String TAG = FacebookAds.class.getSimpleName();
    public static final String FACEBOOK_AD_CONFIG_URL = "https://s3.amazonaws.com/AllInOneToolbox/ad/facebook/facebook_ad_config.xml";
    public static final int DEFAULT_CONFIG_UPDATE_FREQUENCY = 5;

    public static final String AD_ID = "891068614297165_895249850545708";
    public static final int AD_COUNT = 2;

    //Default facebook update frequency 15
    public static int updateFrequency = 15;

    private Context context;
    private static FacebookAds instance;

    private int adPicker = 0;

    public ArrayList<NativeAd> getNativeAds() {
        return nativeAds;
    }

    private ArrayList<NativeAd> nativeAds;
    private FacebookAdListener externalListener;

    private FacebookAds(Context context) {
        this.context = context;
        this.nativeAds = new ArrayList<NativeAd>();
    }

    public static FacebookAds get(Context context) {
        if (instance == null) {
            instance = new FacebookAds(context);
        }
        return instance;
    }

    private Context getContext() {
        return context;
    }

    public int getCountLoaded() {
        int count = 0;
        for(NativeAd na : nativeAds) {
            if(na.isAdLoaded()) {
                count++;
            }
        }
        return count;
    }

    public void show() {
        Log.i(getClass().getSimpleName(), "FB::show");
        checkShow();
        checkLoad();
    }

    public int hasCache() {
        int hasCache = 0;
        for (int i = 0; i < nativeAds.size(); i++) {
            if (nativeAds.get(i).isAdLoaded()) {
                hasCache++;
            }
        }
        return hasCache;
    }

    public int getPicker() {
        int count = hasCache();
        if (count > 0) {
            return adPicker++ % count;
        } else {
            return -1;
        }
    }

    private void checkShow() {
        Log.i(getClass().getSimpleName(), "FB::checkShow " + hasCache());
        int picker = getPicker();
        if (picker > -1) {
            NativeAd nativeAd = nativeAds.get(picker);
            if(!nativeAd.isAdLoaded()) {
                return;
            }

            if (getExternalListener() != null) {
                getExternalListener().onAdsLoaded(nativeAd);
            }
        }
    }

    private void checkLoad() {
        Log.i(getClass().getSimpleName(), "FB::checkLoad " + nativeAds.size() + "/" + AD_COUNT);
        for (int i = 0; i < AD_COUNT; i++) {
            if (nativeAds.size() < AD_COUNT) {
                load();
            } else {
                break;
            }
        }
    }

    private void replace(Ad ad) {
        for (int i = 0; i < nativeAds.size(); i++) {
            Log.i(TAG, "FB::replace 1 " + nativeAds.get(i).hashCode() + " ?= " + ad.hashCode());
            Log.i(TAG, "FB::replace 2 " + (nativeAds.get(i) == ad));
            NativeAd na = nativeAds.get(i);
            if (na == ad) {
                nativeAds.remove(i);
//                na.unregisterView();
                na = null;
            }
        }
        load();

        Log.i(getClass().getSimpleName(), "FB::replace " + nativeAds.size() + "/" + AD_COUNT);
    }

    public void reload() {
        if (nativeAds.size() >= AD_COUNT) {
            if (nativeAds.get(0).isAdLoaded()) {
                nativeAds.remove(0);
                Log.i(getClass().getSimpleName(), "FB::reload " + nativeAds.size() + "/" + AD_COUNT);
                load();
            }
        } else {
            Log.i(getClass().getSimpleName(), "FB::reload " + nativeAds.size() + "/" + AD_COUNT);
            load();
        }
        Log.i(getClass().getSimpleName(), "FB::reload " + nativeAds.size() + "/" + AD_COUNT);
    }

    private void load() {
        NativeAd na = new NativeAd(getContext(), FacebookAds.AD_ID);
        na.loadAd(NativeAd.MediaCacheFlag.ALL);
        na.setAdListener(this);
        nativeAds.add(na);

        Log.i(getClass().getSimpleName(), "FB::load size=" + nativeAds.size() + ", getAdTitle=" + na.getAdTitle());
    }

//    public void clearAdCache() {
//        for (int i = nativeAds.size() - 1; i >= 0; i--) {
//            NativeAd na = nativeAds.remove(i);
//            na.unregisterView();
//            na = null;
//        }
//    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.i(getClass().getSimpleName(), "FB::onAdClicked");
        checkShow();
        replace(ad);
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.i(getClass().getSimpleName(), "FB::onAdLoaded");
        for (int i = 0; i < nativeAds.size(); i++) {
            NativeAd nativeAd = nativeAds.get(i);
            if (nativeAd != null && nativeAd == ad) {
                // Unregister last ad
                nativeAd.unregisterView();

                if (getExternalListener() != null) {
                    getExternalListener().onAdsLoaded(nativeAd);
                    setExternalListener(null);
                }

                break;
            }
        }
    }

    @Override
    public void onAdsLoaded() {
        Log.i(getClass().getSimpleName(), "FB::onAdsLoaded ");
    }

    @Override
    public void onAdError(AdError error) {
        Log.i(getClass().getSimpleName(), "FB::onAdError " + error.getErrorMessage());
    }

    @Override
    public void onError(Ad ad, AdError error) {
        Log.i(getClass().getSimpleName(), "FB::onError " + error.getErrorMessage());
    }

    public static View inflateAd(NativeAd nativeAd, int layoutId, Context context) {
        Log.i(TAG, "FB::inflateAd ");
        // Create native UI using the ad metadata.

        View adView = LayoutInflater.from(context).inflate(layoutId, null);
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
//        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.nativeAdMedia);
        TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.nativeAdSocialContext);
        RatingBar nativeAdStarRating = (RatingBar) adView.findViewById(R.id.nativeAdStarRating);
        ImageView facebook_ad_iv = (ImageView) adView.findViewById(R.id.facebook_ad_iv);
        TextView toolbar_button_tv = (TextView) adView.findViewById(R.id.toolbar_button_tv);

        // Setting the Text
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        toolbar_button_tv.setText(nativeAd.getAdCallToAction());
        nativeAdTitle.setText(nativeAd.getAdTitle() + "("+nativeAd.hashCode()+")");
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
//        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
        Picasso.with(context).load(adIcon.getUrl()).into(nativeAdIcon);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        Log.i(TAG, "FB::inflateAd " + adCoverImage.getUrl());
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
//        nativeAdMedia.setLayoutParams(new LinearLayout.LayoutParams(
//                screenWidth,
//                Math.min((int) (((double) screenWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3)
//        ));
//        nativeAdMedia.setNativeAd(nativeAd);

        ImageView nativeAdCover = (ImageView) adView.findViewById(R.id.nativeAdCover);
        Picasso.with(context).load(adCoverImage.getUrl()).into(nativeAdCover);
        nativeAdCover.setLayoutParams(new LinearLayout.LayoutParams(
                screenWidth,
                Math.min((int) (((double) screenWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3)
        ));

        NativeAd.Rating rating = nativeAd.getAdStarRating();
        if (rating != null) {
            nativeAdStarRating.setVisibility(View.VISIBLE);
            nativeAdStarRating.setNumStars((int) rating.getScale());
            nativeAdStarRating.setRating((float) rating.getValue());
        } else {
            nativeAdStarRating.setVisibility(View.GONE);
        }

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        nativeAd.registerViewForInteraction(adView);

        // Or you can replace the above call with the following function to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));

        return adView;
    }


    public void setExternalListener(FacebookAdListener listener) {
        this.externalListener = listener;
    }

    public FacebookAdListener getExternalListener() {
        return externalListener;
    }

    public void updateConfig() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                doUpdateConfig();
                return null;
            }
        }.execute();
    }

    private void doUpdateConfig() {
        Log.i(TAG, "FB::updateConfig:: ");
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            ConfigHandler handler = new ConfigHandler();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            conn = (HttpURLConnection) new URL(FACEBOOK_AD_CONFIG_URL).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setReadTimeout(1000);
            is = conn.getInputStream();
            reader.parse(new InputSource(is));

            updateFrequency = handler.getFrequency();
            Log.i(TAG, "FB::updateConfig:: updateFrequency=" + updateFrequency);
        } catch (Exception e) {
        } finally {
            ReleaseUtil.release(is);
            ReleaseUtil.release(conn);
        }
    }

    public class ConfigHandler extends DefaultHandler {
        private static final String TAG_FACEBOOK_AD_CONFIG = "facebook_ad_config";
        private static final String UPDATE_FREQUENCY = "update_frequency";

        private int frequency = updateFrequency;

        public void startDocument() throws SAXException {
            super.startDocument();
        }

        public void endDocument() throws SAXException {
            super.endDocument();
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            try {
                String tagName = localName.length() != 0 ? localName : qName;
                if (tagName.equals(TAG_FACEBOOK_AD_CONFIG)) {
                    setFrequency(Integer.parseInt(attributes.getValue(UPDATE_FREQUENCY)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }

    public void check() {
        Log.i(getClass().getSimpleName(), "FB::check ");
        if(TimeUtil.isCountUp(getContext(), "FacebookAds.get(getContext()).reload()", FacebookAds.updateFrequency)) {
            FacebookAds.get(getContext()).reload();
        }
        if(TimeUtil.isCountUp(getContext(), "FacebookAds.get(getContext()).updateConfig()", FacebookAds.DEFAULT_CONFIG_UPDATE_FREQUENCY)) {
            FacebookAds.get(getContext()).updateConfig();
        }
    }
}
