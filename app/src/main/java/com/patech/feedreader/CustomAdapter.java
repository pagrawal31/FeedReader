package com.patech.feedreader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] titles;
	
	private final Integer[] imageId;
	private final int fixedImageId;
	
	public CustomAdapter(Context context, String[] titles, Integer[] imageId) {
		
		super(context, R.layout.subscriptions_layout, titles);
		this.context = (Activity) context;
		this.titles = titles;
		this.imageId = imageId;
		fixedImageId = R.drawable.abc_ab_share_pack_mtrl_alpha;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.subscriptions_layout, null, true);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.flag);
		TextView timeStampView = (TextView) rowView.findViewById(R.id.cur);
		TextView titleView = (TextView) rowView.findViewById(R.id.txt);
		
		imageView.setImageResource(R.drawable.ic_feed);
		
//		imageView.setImageResource(imageId[position]);
		titleView.setText(titles[position]);
		
//		timeStampView.setText(timeStamps[position]);
		
		return rowView;
		
//		return super.getView(position, convertView, parent);
	}

	
}
