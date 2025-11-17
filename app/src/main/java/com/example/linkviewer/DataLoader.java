package com.example.linkviewer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class DataLoader extends AsyncTask<String, Void, String> {

    private static final String TAG = "DataLoader";
    private final ResultCallback callback;
    private Exception error = null;

    public interface ResultCallback {
        void onSuccess(String rawData);
        void onError(Exception e);
    }

    public DataLoader(ResultCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "doInBackground() called");

        try {
            String urlString = params[0];
            Log.i(TAG, "Parsiustas: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            InputStream is = conn.getInputStream();
            return convertStreamToString(is);

        } catch (Exception e) {
            Log.e(TAG, "Nepavyko atsisiusti", e);
            error = e;
            return null;
        }
    }

    private String convertStreamToString(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len));
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (error != null) {
            callback.onError(error);
        } else {
            callback.onSuccess(result);
        }
    }
}

