package com.patech.feedreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.feed.Feed;
import com.patech.adapters.FeedMessageDisplayAdapter;
import com.patech.dialog.AddFilterDialog;
import com.patech.utils.AppUtils;
import com.patech.utils.CollectionUtils;
import com.java.rssfeed.feed.FeedMessage;
import com.java.rssfeed.ReadTest;
import com.patech.utils.CommonMsgs;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.support.annotation.Nullable;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class NavigationMenuFragment extends ListFragment implements OnItemClickListener {

	SharedPreferences preferences = null;
	private static final String ARG_POSITION = "position";
    MainActivity mCallback;

	String[] messageArray  = null;
	int listSize = 0;
	Set<FeedMessage> messageList;
	int idx = 0;
	StringBuffer prefVariable = new StringBuffer();
	String title = AppUtils.EMPTY;

    public interface NavigationMenuInterface {
        public boolean onClickManagerFilters(int position);
    }

	public static NavigationMenuFragment newInstance(int position) {
		NavigationMenuFragment fragment = new NavigationMenuFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_POSITION, position);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

		if (getArguments() != null) {
			idx = getArguments().getInt(ARG_POSITION);
			prefVariable.append("pref_");
			prefVariable.append(idx);
		}
        Feed currFeed = FeedInfoStore.getInstance().getFeed(idx);
		title = currFeed.getTitle();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);

		try {
			mCallback = (MainActivity) context;
		} catch (ClassCastException e) {
			
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.addFilter:
            DialogFragment mDialog = AddFilterDialog.newInstance();
            mDialog.show(getFragmentManager(), "Add Filter");
			break;

		case R.id.showAll:
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(prefVariable.toString(), false);
			editor.commit();
			showMessage(false);
			break;

		case R.id.showFiltered:
			SharedPreferences.Editor editor1 = preferences.edit();
			editor1.putBoolean(prefVariable.toString(), true);
			editor1.commit();
			Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
			showMessage(true);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getActivity().setTitle("Feeds");
		super.onViewCreated(view, savedInstanceState);
	}
	
	public void showMessage(boolean filteredView) {
		int counter = 0;
		if (!filteredView)
			messageList = ReadTest.getMessages(idx);
		else
			messageList = ReadTest.getFilteredMessages(idx);

		listSize = messageList.size();

		List<FeedMessage> currMsgs = new ArrayList<>(messageList);
		if (listSize > 0)
		Collections.sort(currMsgs, new Comparator<FeedMessage>() {
            @Override
            public int compare(FeedMessage msg1, FeedMessage msg2) {
                Date date1 = AppUtils.parseDate(msg1.getDate());
                Date date2 = AppUtils.parseDate(msg2.getDate());
                return AppUtils.compareDates(date1, date2) ? -1 : 0;
            }
        });
        
        List<FeedMessage> list = listSize == 0 ? Collections.EMPTY_LIST : currMsgs;
		FeedMessageDisplayAdapter adapter = new FeedMessageDisplayAdapter(getActivity(), list);
		setListAdapter(adapter);
        ListView listView = getListView();
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
		adapter.notifyDataSetChanged();
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Position : " + info.position);
        String[] menuItems = getResources().getStringArray(R.array.menu);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case 0:
                // delete
                FeedMessage msg = getFeedMessage(messageList, info.position);
                messageList.remove(msg);
				FeedMessageDisplayAdapter adapter = (FeedMessageDisplayAdapter) getListAdapter();
				adapter.notifyDataSetChanged();
                break;
            case 1:
                // edit
                break;
            case 2:
                mCallback.onClickManagerFilters(info.position);
                break;
            default:
        }
        return true;
    }

    public <T> T getFeedMessage(Collection<T> collection, int index) {
        T msg = null;

        if (collection != null && !collection.isEmpty() && index < collection.size()) {
            Iterator<T> iterator = collection.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                T element = iterator.next();
                if (count++ == index) {
                    msg = element;
                    break;
                }
            }
        }
        return msg;
    }

    @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		boolean filtered = preferences.getBoolean(prefVariable.toString(), false);
		showMessage(filtered);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (listSize > 0) {
			FeedMessage currMsg = CollectionUtils.findElement(messageList, position);
			Intent showMessageIntent = new Intent(getActivity(), MessageViewActivity.class);
			ActivityUtils.populateIntent(showMessageIntent, currMsg);
			startActivity(showMessageIntent);
		} else {
			Toast.makeText(getActivity(), "Item: " + position,
					Toast.LENGTH_SHORT).show();
		}
	}
}
