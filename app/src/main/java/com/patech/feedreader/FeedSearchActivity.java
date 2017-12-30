package com.patech.feedreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.java.rssfeed.model.feed.Feed;
import com.patech.adapters.FeedSearchDisplayAdapter;
import com.patech.dbhelper.DatabaseUtils;
import com.patech.utils.AppConstants;
import com.patech.utils.CommonMsgs;
import com.patech.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static List<String> tags = Arrays.asList("website", "description", "language", "title",
            "lastUpdated", "feedId", "iconUrl", "contentType");

    private List<Feed> feeds = Collections.EMPTY_LIST;
    private ListView listView;
    private Button searchBtn;
    private EditText queryText;
    private FeedSearchDisplayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queryText = (EditText)findViewById(R.id.query);
        searchBtn = (Button)findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButtonClick(view);
            }
        });

        listView = (ListView)findViewById(R.id.feed_list);
        adapter = new FeedSearchDisplayAdapter(getApplicationContext(), feeds);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
//        registerForContextMenu(listView);
    }

    private void searchButtonClick(View view) {

        String urlText = queryText.getText().toString();
        if (urlText != null && urlText.length() > 2 ) {
            OkHttpHandler executor = new OkHttpHandler(FeedSearchActivity.this);
            executor.execute(urlText);
        } else {
            Toast.makeText(getApplicationContext(), CommonMsgs.ENTER_VALID_TEXT_TO_SEARCH, Toast.LENGTH_SHORT).show();;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Feed selectedFeed = feeds.get(position);
        SQLiteDatabase readerDB = ((FeedReaderApplication)getApplication()).getReadableDatabase();
        SQLiteDatabase writeDB  = ((FeedReaderApplication)getApplication()).getWritableDatabase();
        Cursor feedCursor = DatabaseUtils.fetchFeedFromDatabase(readerDB, selectedFeed);
        feedCursor.moveToNext();
        if (feedCursor.getCount() > 0) {
            Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
        } else {
            long newRowId = AppUtils.insertFeedWithGlobalFilters(writeDB, selectedFeed, true);
            Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ADDED, Toast.LENGTH_SHORT).show();
        }
    }

    private class OkHttpHandler extends AsyncTask<String, Void, String> {

        private static final String TAG = "HttpGet";
        OkHttpClient client = new OkHttpClient();
        private ProgressDialog dialog;
        private Context context;

        public OkHttpHandler(Activity activity) {
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage(AppConstants.SEARCHING_FEED);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            String keyWord = url[0];
            String prefix = AppUtils.FEEDLY_URL_PREFIX;
            System.setProperty("http.agent", "");
            String feedUrl = prefix.concat(keyWord);
            feeds = new ArrayList<>();
            String errorCode = AppUtils.EMPTY;

            try {
                Request.Builder builder = new Request.Builder();
                builder.url(feedUrl);
                Request request = builder.build();

                Response response = client.newCall(request).execute();
                String webpage = response.body().string();
                JSONObject jsonObject = new JSONObject(webpage);

                JSONArray array = jsonObject.getJSONArray("results");
                int size = array.length();
                feeds = new ArrayList<>();
                for (int i =0; i < size; i++) {
                    JSONObject resultObject = (JSONObject) array.get(i);
                    Feed newFeed = getFeedObject(resultObject);
                    if (newFeed != null)
                        feeds.add(newFeed);
                }
                return feedUrl;
            } catch (MalformedURLException e) {
                errorCode = CommonMsgs.URL_NOT_VALID;
            } catch (IOException e) {
                errorCode = CommonMsgs.IO_ERROR;
            } catch (JSONException e) {
                errorCode = CommonMsgs.PARSING_ERROR;
            }
            if (errorCode != AppUtils.EMPTY) {
                Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_LONG).show();
            }
            return feedUrl;
        }

        @Override
        protected void onPostExecute(String url) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                adapter.updateList(feeds);
            }
        }
    }

    private Feed getFeedObject(JSONObject resultObject) throws JSONException {
        String currUrl = AppUtils.EMPTY;

        Feed.FeedBuilder feedBuilder = new Feed.FeedBuilder();
        for (String tag : tags) {
            if (resultObject.has(tag)) {
                Object tagValueObj = resultObject.get(tag);
                System.out.println(tagValueObj);
                if (tagValueObj instanceof String) {
                    String tagValue = (String) tagValueObj;
                    switch(tag) {
                        case "website":
                        case "language":
                        case "contentType":
                            break;
                        case "description":
                            feedBuilder.setDescription(tagValue);
                            break;
                        case "title":
                            feedBuilder.setName(tagValue);
                            break;
                        case "feedId":
                            currUrl = tagValue;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (currUrl != AppUtils.EMPTY) {
            if (currUrl.startsWith(AppUtils.FEED_PREFIX)) {
                currUrl = currUrl.replaceFirst(AppUtils.FEED_PREFIX, AppUtils.EMPTY);
            }
            return feedBuilder.build(currUrl);
        }
        return null;
    }
}
