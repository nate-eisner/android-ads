package io.eisner.ads;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * AsyncTask to get a response from a server using a built request
 * <p/>
 * Created by nathan eisner on 10/13/15.
 */
class GetResponse {
    private static OkHttpClient okClient = new OkHttpClient();

    static {
        okClient.setReadTimeout(30, TimeUnit.SECONDS);
        okClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    /**
     * Get a http response for the bytes of the body
     *
     * @param request a built request
     * @return byte array of the contents of the body
     */
    public static byte[] forBytes(Request request) {
        try {
            Response response = okClient.newCall(request).execute();
            if (response != null && response.isSuccessful())
                return response.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Get a http response body
     *
     * @param request a build request
     * @return ResponseBody of the request sent
     */
    public static ResponseBody forBody(Request request) {
        try {
            Response response = okClient.newCall(request).execute();
            if (response != null && response.isSuccessful())
                return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a http response body in byte stream
     *
     * @param request a built request
     * @return the InputStream form of the body
     */
    public static InputStream forStream(Request request) {
        try {
            Response response = okClient.newCall(request).execute();
            if (response != null && response.isSuccessful())
                return response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean forOK(Request request) {
        try {
            okClient.setConnectTimeout(10, TimeUnit.SECONDS);
            Response response = new GetOK().execute(request).get();
            okClient.setConnectTimeout(30, TimeUnit.SECONDS);
            return response != null && response.isSuccessful();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class GetOK extends AsyncTask<Request,Void,Response> {

        @Override
        protected Response doInBackground(Request... params) {
            try {
                return okClient.newCall(params[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
