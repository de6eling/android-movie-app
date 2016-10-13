package com.de6elinggmail.todo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.de6elinggmail.todo.db.TaskContract;
import com.de6elinggmail.todo.db.TaskDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "ToDo";
    private TaskDbHelper mHelper;
    private ListView mSearchListView;
    private ArrayAdapter<String> mAdapter;
    private static final String API_URL = "http://www.omdbapi.com/?t=";
    public final static String DETAIL_MESSAGE = "com.de6elinggmail.todo.Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchListView = (ListView) findViewById(R.id.search_results);
        mHelper = new TaskDbHelper(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ToDo.EXTRA_MESSAGE);

        try {
            updateSearchUI(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void updateSearchUI(String searchJson) throws JSONException {
        JSONObject jsonRootObj = new JSONObject(searchJson);
        if (jsonRootObj.has("Error")) {
            // If the search result return nothing send an error.
            TextView errorOut = (TextView) findViewById(R.id.search_error);
            errorOut.setText("Nothing to be found under that title.");
        } else {


            JSONArray searchResults = jsonRootObj.optJSONArray("Search");

            ArrayList<String> movieList = new ArrayList<>();

            for (int i = 0; i < searchResults.length(); i++) {
                JSONObject jsonObject = searchResults.getJSONObject(i);

                String movieTitle = jsonObject.optString("Title").toString();
                String year = jsonObject.optString("Year").toString();
                movieList.add(movieTitle + " (" + year + ")");

                //String poserURL = jsonObject.optString("Poster").toString();

            }
            if (mAdapter == null) {
                mAdapter = new ArrayAdapter<String>(this,
                        R.layout.item_movie,
                        R.id.movie_title,
                        movieList);
                mSearchListView.setAdapter(mAdapter);
            } else {
                mAdapter.clear();
                mAdapter.addAll(movieList);
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void addMovie(View view) throws IOException {
        View parent = (View) view.getParent();
        TextView movieTextView = (TextView) parent.findViewById(R.id.movie_title);
        String movieTitle = String.valueOf(movieTextView.getText());

        String yearS = movieTitle.substring(movieTitle.length() - 5);
        int year = parseInt(yearS.substring(0,4));

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, movieTitle);
        values.put(TaskContract.TaskEntry.COL_TASK_ARCHIVE, year);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Context context = getApplicationContext();
        CharSequence text = movieTitle + " added to list!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void detailMovie(View view) {
        View parent = (View) view.getParent();
        TextView movieTextView = (TextView) parent.findViewById(R.id.movie_title);
        String movieTitle = String.valueOf(movieTextView.getText());

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DETAIL_MESSAGE, movieTitle);
        startActivity(intent);


    }




}
