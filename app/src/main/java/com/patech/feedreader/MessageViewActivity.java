package com.patech.feedreader;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

import com.patech.utils.AppConstants;
import com.patech.utils.AppUtils;
import com.patech.utils.CommonMsgs;

import org.junit.validator.TestClassValidator;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MessageViewActivity extends AppCompatActivity {

    protected static final String TITLE = "TITLE";
    protected static final String DESCRIPTION = "DESCRIPTION";
    protected static final String LINK = "LINK";
    protected static final String AUTHOR = "AUTHOR";
    protected static final String GUID = "GUID";
    protected static final String DATE = "DATE";

    @BindView(R.id.titleTxtView) TextView titleTxtView;
	@BindView(R.id.descriptionTxtView) TextView descriptionTxtView;
	@BindView(R.id.linkTxtView) TextView linkTxtView;
	@BindView(R.id.dateView) TextView dateView;
	@BindView(R.id.openLinkBtn) Button openLinkBtn;

    public MessageViewActivity() {
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.feedmessage_view);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

		if (author == null || author.isEmpty()) {
        } else {
            title = Html.fromHtml(author) + " : " + title;
        }
		titleTxtView.setText(title);
        descriptionTxtView.setText(Html.fromHtml(description));
		linkTxtView.setText(link);
		dateView.setText(date);
	}


	@OnClick(R.id.openLinkBtn)
	public void onClickOpenLinkBtn(View v) {
		if (linkTxtView != null) {
			String url = linkTxtView.getText().toString();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}

    @OnLongClick(R.id.descriptionTxtView)
    public boolean createFilterForDesc(View v) {
        createFilter(v);
        return true;
    }

	@OnLongClick(R.id.titleTxtView)
    public boolean createFilterForTitle(View v) {
        createFilter(v);
        return true;
    }

    public void createFilter(View v) {
        TextView view = (TextView)v;
        ((FeedReaderApplication) getApplication()).copyToClipboard(view, AppConstants.FILTER_DATA);
//        AppUtils.copyToClipboard(view, AppConstants.FILTER_DATA);

//        String txt = view.getText().toString();
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText(AppConstants.FILTER_DATA, txt);
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(getApplicationContext(), CommonMsgs.getTextCopied(txt), Toast.LENGTH_SHORT).show();
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
