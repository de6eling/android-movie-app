package com.de6elinggmail.todo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.de6elinggmail.todo.db.TaskDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import static java.lang.Integer.parseInt;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "ToDo";
    private TaskDbHelper mHelper;
    private ListView mSearchListView;
    private ArrayAdapter<String> mAdapter;
    private ImageView mImageView;
    private static final String API_URL = "http://www.omdbapi.com/?t=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String message = intent.getStringExtra(SearchActivity.DETAIL_MESSAGE);

        getDetails(message);

    }

    private void getDetails(String title) {
        try {
            String jsonResponse = new DownloadTask().execute(title).get();
            showDetails(jsonResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(String jsonResponse) throws JSONException {
        JSONObject rootObj = new JSONObject(jsonResponse);

        if (rootObj.has("Error")) {

        } else {
            String details = "Title: " + rootObj.optString("Title").toString() + "\n\n";
            details += "Year: " + rootObj.optString("Year").toString() + "\n\n";
            details += "Plot: " + rootObj.optString("Plot").toString() + "\n\n";

//            Drawable poster = LoadImageFromWebOperations(rootObj.optString("Poster").toString());


//            mImageView = (ImageView) findViewById(R.id.movie_poster);
//            mImageView.setImageDrawable(poster.getCurrent());

            TextView detailView = (TextView) findViewById(R.id.movie_details);
            detailView.setText(details);
        }

    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                return getString(R.string.connection_error);
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
        }
    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        String movieTitle = urlString.substring(0,urlString.length()-7);
        String yearS = urlString.substring(urlString.length() - 5);
        int year = parseInt(yearS.substring(0,4));


        URL url = new URL(API_URL + Uri.encode(movieTitle) + "&y=" + year);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();

        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
