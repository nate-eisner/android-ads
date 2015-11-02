package io.eisner.ads;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.Request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * The main provider that gets ads and controls the image view contents
 * Builder builds all of the options for the provider
 * <p/>
 * Created by nathan eisner
 */
public class AdProvider {
    protected final String TAG = "AdProvider";
    protected final Builder myBuilder;
    protected boolean initialized = false;
    protected boolean startWhenReady = false;
    //current ad displayed
    protected int adNum = 0;
    protected Ad adView;
    protected AdCache adCache;
    protected String keyValue = "";
    protected boolean networkIsLoading = true;
    protected boolean hasAtLeastOne = false;
    protected Handler handleImageLoad = new Handler();
    protected Runnable imageRunnable = new Runnable() {
        @Override
        public void run() {
            if (!adCache.isEmpty()) {
                //zero indexed so max = size-1 NO OFFBYZERO!
                if (adNum == adCache.getCacheSize() - 1) {
                    adNum = 0;
                } else {
                    adNum++;
                }
                setImage(adNum);
            }
            handleImageLoad.postDelayed(this, myBuilder.delay);
        }
    };

    private AdProvider(Builder builder) {
        this.myBuilder = builder;
        adView = new Ad(myBuilder.context);
        myBuilder.rootView.addView(adView);
        adCache = new AdCache(myBuilder.context, myBuilder.cacheAmt);
        if (myBuilder.drawablePlaceholder != null) {
            adView.setImageDrawable(myBuilder.drawablePlaceholder);
        }
        if (myBuilder.usingKey) {
            getAPIKey(myBuilder.keyServer, myBuilder.keyHeaders);
        } else {
            getAds();
        }
    }

    /**
     * To get the current builder of this provider
     * @return the builder
     */
    public final Builder getBuilder() {
        return myBuilder;
    }

    /**
     * Class to build an AdProvider using a various amount of options
     */
    public static class Builder {
        protected final Context context;
        protected String server;
        protected String xmlTag;
        protected String key;
        protected ViewGroup rootView = null;
        protected boolean useOffline;
        protected long delay;
        protected Drawable drawablePlaceholder = null;
        protected HeaderFields headers = new HeaderFields();
        protected int cacheAmt;
        protected boolean usingKey;
        protected String keyServer;
        protected HeaderFields keyHeaders;
        protected String keyHeaderName;

        public Builder(@NonNull Context context) {
            //default values
            this.context = context;
            this.delay = 10000;
            this.useOffline = true;
            this.xmlTag = "imagefilename";
            this.key = null;
            this.cacheAmt = 6;
            this.usingKey = false;
        }

        public Builder imageServer(@NonNull String path) {
            this.server = path;
            return this;
        }

        public Builder requestHeader(@NonNull HeaderFields addHeaders) {
            this.headers = addHeaders;
            return this;
        }

        public Builder useAPIKey(@NonNull String server, @Nullable HeaderFields headers,
                                 @NonNull String name) {
            this.keyServer = server;
            this.keyHeaders = headers;
            this.keyHeaderName = name;
            this.usingKey = true;
            return this;
        }

        public Builder imageTime(@NonNull long time) {
            this.delay = time;
            return this;
        }

        public Builder attachTo(@NonNull View root) {
            if (root instanceof ViewGroup) {
                this.rootView = (ViewGroup) root;
                return this;
            }
            throw new IllegalArgumentException("A ViewGroup type is needed to attach to");
        }

        public Builder parseTag(@NonNull String tagname) {
            this.xmlTag = tagname;
            return this;
        }

        public Builder offlineUse(boolean option) {
            this.useOffline = option;
            return this;
        }

        public Builder cacheSize(int num) {
            this.cacheAmt = num;
            return this;
        }

        public Builder placeholder(Drawable drawable) {
            this.drawablePlaceholder = drawable;
            return this;
        }

        public AdProvider build() {
            if (this.rootView != null)
                return new AdProvider(this);
            throw new IllegalStateException("Root view cannot be null");
        }

        public AdProvider start() {
            AdProvider ap = build();
            ap.start();
            return ap;
        }

    }

//// TODO: 10/19/15 NEED TO HANDLE NO NETWORK CONNECTIVITY
//      Have a Default Ad image load if no network connectivity?
//        or  Load old ads if no current network connection

    /**
     * Start showing ads into root view
     */
    public void start() {
        if (!networkIsLoading) {
            setImage(0);
            handleImageLoad.postDelayed(imageRunnable, myBuilder.delay);
        } else {
            startWhenReady = true;
        }
    }

    /**
     * If background loading of ads is not needed then stopping the provider can be done
     */
    public void stop() {
        handleImageLoad.removeCallbacks(imageRunnable);
        Log.d(TAG, "Stopped");
    }

    /**
     * If a restart of the provider is needed...ex onResume of Activity
     */
    public void restart() {
        handleImageLoad.removeCallbacks(imageRunnable);
        if (initialized) {
            Log.d(TAG, "Restarted");
            start();
        }
    }

    /**
     * Check for a 204 response from Google server to verify network connectivity
     *
     * @return true if the network is connected
     */
    public static boolean isConnected() {
        Request request = new Request.Builder()
                .get()
                .url("http://clients3.google.com/generate_204")
                .build();
        return GetResponse.forOK(request);
    }

    /**
     * Handles building a OkHttp Request with a dynamic collection of header information
     *
     * @param url              the server url where the request is being made to
     * @param headerFields a collection of names and values needed to make request
     * @return the built Request
     */
    public static Request buildARequest(@NonNull String url,
                                        @Nullable HeaderFields headerFields) {
        Request.Builder builder = new Request.Builder();
        builder.get()
                .url(url);

        if (headerFields != null) {
            Set<String> headers = headerFields.getNameSet();
            if (headers != null && !headers.isEmpty()) {
                for (String header : headers) {
                    builder.addHeader(header, headerFields.getValue(header));
                }
            }
        }

        return builder.build();
    }

    /**
     * This is used to get an API Key from a server if one is needed to insert in the GET header
     * Saves key into a stored variable and is used to GET xml of all image urls
     *
     * @param url     used in GET that returns the API key
     * @param headers Headers to add to the request for the API key
     */
    private String getAPIKey(String url, @Nullable HeaderFields headers) {
        Request request = buildARequest(url, headers);
        networkIsLoading = true;
        if (isConnected()) {
            try {
                return new GetAPIKey().execute(request).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Task to get the api key from a server. Starts to get ads when key is received
     */
    private class GetAPIKey extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... params) {
            InputStream is = GetResponse.forStream(params[0]);
            if (is != null) {
                return AdParser.parseForKey(is);
            }
            Log.e("API Key", "key response null");
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            keyValue = s;
            getAds();
        }
    }

    /**
     * Starts the get ads task based off of some certain parameters
     */
    public void getAds() {
        if (isConnected() && (myBuilder.usingKey && !keyValue.equals(""))) {
            new GetAdsTask().execute();
        } else {
            //// TODO: 10/30/15 try again sometime?? use cached ads??
            //if offline mode enabled use cached local ads
            //else nothing is shown or placeholder is shown if added
        }
    }

    /**
     * Task to get ads from server and to load each ad in the cache
     */
    protected class GetAdsTask extends AsyncTask<Void, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            networkIsLoading = true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            networkIsLoading = aBoolean;
            if (startWhenReady) {
                start();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Starting to get ads");
            if (myBuilder.usingKey) {
                myBuilder.headers.add(myBuilder.keyHeaderName, keyValue);
            }
            Request request = buildARequest(myBuilder.server, myBuilder.headers);
            Log.d("Request", request.toString());
            ArrayList<String> ads = null;
            long start = System.currentTimeMillis();
            InputStream is = GetResponse.forStream(request);
            Log.d("time for response", "" + (start - System.currentTimeMillis()));
            if (is != null) {
//                start = System.currentTimeMillis();
                ads = AdParser.parseForImages(is, myBuilder.xmlTag);
//                Log.d("time for parse", "" + (start - System.currentTimeMillis()));
            } else {
                Log.e(TAG, "Response was not good...");
            }
            //cache ads
            if (ads != null && !ads.isEmpty()) {
                for (String uri : ads) {
                    adCache.cache(uri);
                    hasAtLeastOne = true;
                }
            }
            Log.d(TAG, "Done with ads");
            return false;
        }
    }

    /**
     * Starts the task to load image into the ad view depending on some logic
     *
     * @param imageId the position of the ad that was cached
     */
    private void setImage(final int imageId) {
        if (adView != null) {
            boolean hasNetwork = isConnected();
            if (!networkIsLoading && hasNetwork) {
                new SetImageTask().execute(imageId);
                //// TODO: 11/1/15 VVV Handle different loading situations VVV
//            } else if (hasAtLeastOne) {
//                //load first image
//                Log.d("SetImage", "has at least one image");
//            } else if (!hasNetwork) {
//                Log.d("SetImage", "no network");
//                adView.setAlpha(0f);
//                adView.setVisibility(View.VISIBLE);
//                adView.animate()
//                        .alpha(1f)
//                        .setDuration(1500)
//                        .setListener(null);
//            } else {
//                Log.d("SetImage", "nothing...");
////                progressBar.setVisibility(View.VISIBLE);

            }
        }
    }

    /**
     * Task to get the Bitmap from the cache and animate the bitmap in the ad view
     */
    protected class SetImageTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... params) {
            return BitmapFactory.decodeFile(adCache.retrieve(params[0]));
        }

        @Override
        protected void onPostExecute(final Bitmap bm) {
            adView.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            adView.setImageBitmap(bm);
                            adView.setAlpha(0f);
                            adView.setVisibility(View.VISIBLE);
                            adView.animate()
                                    .alpha(1f)
                                    .setDuration(1500)
                                    .setListener(null)
                                    .start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();

        }
    }
}
