package io.eisner.ads;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class that caches images to disk
 * The limit of images stored on disk can be changed
 * Created by nathan eisner
 */
public class AdCache {
    private static String dir;
    private static final String TAG = "AdCache";
    private static ArrayList<String> urls = new ArrayList<>();
    private int limit;
    private int cacheCount;

    /**
     * Constructor with setting cache limit
     *
     * @param context the context to which the AdProvider is running on
     */
    public AdCache(Context context, int limit) {
        this.limit = limit;
        initialize(context);
    }

    /**
     * Initialize the cache directory and clear old cache is exists
     *
     * @param context the context to which the AdProvider is running on
     */
    private void initialize(Context context) {
        dir = context.getFilesDir().getAbsolutePath() + "/ads";
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdir();
        } else {
            //clear cache
            File[] files = folder.listFiles();
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * Amount of ads in cache
     *
     * @return int number of ads
     */
    public int getCacheSize() {
        return urls.size();
    }

    /**
     * Cache the response from the server until limit is hit then just save the url
     *
     * @param url the location of the response to cache
     */
    public void cache(String url) {
        if (cacheCount < limit) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                byte[] bytes = GetResponse.forBytes(request);
                if (bytes.length != 0) {
                    FileOutputStream fo = new FileOutputStream(dir + "/" + urls.size());
                    urls.add(url);
                    fo.write(bytes);
                    fo.close();
                    cacheCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            urls.add(url);
        }
    }

    /**
     * Internal cache for a saved url
     *
     * @param position position in the url list to cache
     */
    private boolean cache(int position) {
        if (urls != null && !urls.isEmpty()) {
            String url = urls.get(position);
            if (url != null && !url.isEmpty()) {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    byte[] bytes = GetResponse.forBytes(request);
                    if (bytes.length != 0) {
                        FileOutputStream fo = new FileOutputStream(dir + "/" + position);
                        fo.write(bytes);
                        fo.close();
                        cacheCount++;
                    } else {
                        Log.i(TAG, "Image is empty");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {

            Log.i(TAG, "no urls");

        }
        return false;
    }

    /**
     * To retrieve an ad from the cache. If not cached, cache and try to retrieve
     * If that fails then
     * Also, caches next file for quicker loading.
     *
     * @param position the position of the ad in the cache
     * @return Path of the file
     */
    public String retrieve(int position) {
        File image = new File(dir + "/" + position);
        if (!image.exists()) {
            if (cache(position)) {
                removeLeastUsed(position);
                retrieve(position);
            } else {
                return retrieve(position ++);
            }
        }
//// TODO: 11/1/15 fix looping issue
//        //cache next one
//        int next = position + 1;
//        //are we at the last image?
//        if (next >= urls.size()) {
//            //loop to first again
//            next = 0;
//        }
//        File nextImage = new File(dir + "/" + next);
//        if (!nextImage.exists()) {
//            final int finalNext = next;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if ;
//                }
//            }).start();
//        }
        return image.getAbsolutePath();
    }

    /**
     * Deleting of the furthest possible cached image away from current position if we are over
     * the limit
     *
     * @param currentPos position of the current image
     */
    public void removeLeastUsed(int currentPos) {
        int leastPos = currentPos - limit;
        if (leastPos < 0) {
            leastPos = urls.size() + leastPos;
        }
        File file = new File(dir + "/" + leastPos);
        if (file.exists()) {
            if (file.delete())
                cacheCount--;
            else
                Log.e(TAG, "File did not delete from cache");
        }
    }

    public boolean isEmpty() {
        return getCacheSize() == 0;
    }
}
