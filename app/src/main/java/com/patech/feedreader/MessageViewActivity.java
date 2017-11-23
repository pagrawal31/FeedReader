package com.patech.feedreader;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class MessageViewActivity extends AppCompatActivity {

    protected static final String TITLE = "TITLE";
    protected static final String DESCRIPTION = "DESCRIPTION";
    protected static final String LINK = "LINK";
    protected static final String AUTHOR = "AUTHOR";
    protected static final String GUID = "GUID";
    protected static final String DATE = "DATE";
    TextView titleTxtView;
	TextView descriptionTxtView;
	TextView linkTxtView;
	TextView dateView;
	Button openLinkBtn;
	
	public MessageViewActivity() {
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//		getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.feedmessage_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		titleTxtView = (TextView) findViewById(R.id.titleTxtView);
		descriptionTxtView = (TextView) findViewById(R.id.descriptionTxtView);
		linkTxtView = (TextView) findViewById(R.id.linkTxtView);
		dateView = (TextView) findViewById(R.id.dateView);
		
		openLinkBtn = (Button) findViewById(R.id.openLinkBtn);
		openLinkBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickOpenLinkBtn(v);
			}
		});
		
		Intent i = getIntent();
		String title = i.getStringExtra(TITLE);
		title = title.isEmpty() ? " " : title;
		
		String description = i.getStringExtra(DESCRIPTION);
		description = description.isEmpty() ? " " : description;
		
		String link = i.getStringExtra(LINK);
		link = link.isEmpty() ? " " : link;
		
		String author = i.getStringExtra(AUTHOR);
		author = author.isEmpty() ? " " : author;
		String date = i.getStringExtra(DATE);

		Html.ImageGetter imageGetter = new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				LevelListDrawable d = new LevelListDrawable();
				Drawable empty = getResources().getDrawable(R.drawable.abc_btn_check_material);;
				d.addLevel(0, 0, empty);
				d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
				new ImageGetterAsyncTask(MessageViewActivity.this, source, d).execute();

				return d;
			}
		};

		titleTxtView.setText(author + " " + title);
//		descriptionTxtView.setText(Html.fromHtml(description, imageGetter, null));
        descriptionTxtView.setText(Html.fromHtml(description));
		linkTxtView.setText(link);
		dateView.setText(date);
	}
	
	public void onClickOpenLinkBtn(View v) {
		if (linkTxtView != null) {
			String url = linkTxtView.getText().toString();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}

	class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap> {


		private LevelListDrawable levelListDrawable;
		private Context context;
		private String source;
		private TextView t;

		public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable) {
			this.context = context;
			this.source = source;
			this.levelListDrawable = levelListDrawable;
		}

		@Override
		protected Bitmap doInBackground(TextView... params) {
			t = params[0];
			try {
//				Log.d(LOG_CAT, "Downloading the image from: " + source);
				InputStream is = new URL(source).openStream();
				return BitmapFactory.decodeStream(is);
//				return Picasso.with(context).load(source).get();
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			try {
				Drawable d = new BitmapDrawable(context.getResources(), bitmap);
				Point size = new Point();
				((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
				// Lets calculate the ratio according to the screen width in px
				int multiplier = size.x / bitmap.getWidth();
//				Log.d(LOG_CAT, "multiplier: " + multiplier);
				levelListDrawable.addLevel(1, 1, d);
				// Set bounds width  and height according to the bitmap resized size
				levelListDrawable.setBounds(0, 0, bitmap.getWidth() * multiplier, bitmap.getHeight() * multiplier);
				levelListDrawable.setLevel(1);
				t.setText(t.getText()); // invalidate() doesn't work correctly...
			} catch (Exception e) { /* Like a null bitmap, etc. */ }
		}
	}

}
