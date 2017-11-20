package com.patech.feedreader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
		
		titleTxtView.setText(author + " " + title);
		descriptionTxtView.setText(description);
		linkTxtView.setText(link);
		dateView .setText(date);
	}
	
	public void onClickOpenLinkBtn(View v) {
		if (linkTxtView != null) {
			String url = linkTxtView.getText().toString();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}
}
