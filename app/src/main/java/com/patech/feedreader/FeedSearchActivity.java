package com.patech.feedreader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.feed.Feed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedSearchActivity extends AppCompatActivity {

    static List<String> tags = Arrays.asList("website", "description", "language", "title",
            "lastUpdated", "feedId", "iconUrl", "contentType");

    private List<Feed> feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class HttpGetTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "HttpGet";

        @Override
        protected String doInBackground(String... url) {
            String keyWord = url[0];
            String prefix = "http://cloud.feedly.com/v3/search/feeds?n=20&q=";
            System.setProperty("http.agent", "");
            String feedUrl = prefix.concat(keyWord);
            feeds = new ArrayList<>();

            try {

                URL feedurl = new URL(feedUrl);
                InputStream inputStream = feedurl.openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer webpageBuffer = new StringBuffer();

                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    webpageBuffer.append(inputLine);
                }
                br.close();
                String webpage = webpageBuffer.toString();
                JSONObject jsonObject = new JSONObject(webpage);

                JSONArray array = jsonObject.getJSONArray("results");
                int size = array.length();

                for (int i =0; i < size; i++) {
                    JSONObject resultObject = (JSONObject) array.get(i);

                    //Feed.FeedBuilder builder = new Feed.FeedBuilder();

                    for (String tag : tags) {
                        if (resultObject.has(tag)) {
                            Object tagValue = resultObject.get(tag);
                        }
                    }
                }
                return feedUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
            }
            return feedUrl;
        }

        @Override
        protected void onPostExecute(String url) {

        }
    }



}
