package com.patech.feedreader;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.model.feed.Feed;
import com.patech.adapters.FeedMessageDisplayAdapter;
import com.patech.dialog.AddFilterDialog;
import com.patech.enums.FilterLevel;
import com.patech.utils.AppUtils;
import com.patech.utils.CollectionUtils;
import com.java.rssfeed.model.feed.FeedMessage;
import com.java.rssfeed.ReadTest;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
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
	List<FeedMessage> messageList = Collections.EMPTY_LIST;
    FeedMessageDisplayAdapter adapter;
    int idx = 0;
	StringBuffer prefVariable = new StringBuffer("int_");
	String title = AppUtils.EMPTY;
	private Context context;

    public interface NavigationMenuInterface {
        public boolean onClickManageFilters(int position);
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
        this.context = context;
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
        SharedPreferences.Editor editor = preferences.edit();
		switch (itemId) {
            case R.id.addFilter:
                DialogFragment mDialog = AddFilterDialog.newInstance();
                mDialog.show(getFragmentManager(), "Add Filter");
                break;

            case R.id.showAll:
                editor.putInt(prefVariable.toString(), FilterLevel.None.getId());
                editor.commit();
                showMessage(FilterLevel.None);
                break;

            case R.id.showFiltered:
                editor.putInt(prefVariable.toString(), FilterLevel.All.getId());
                editor.commit();
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
                showMessage(FilterLevel.All);
                break;

            case R.id.showExcluded:
                editor.putInt(prefVariable.toString(), FilterLevel.EXCLUDED.getId());
                editor.commit();
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
                showMessage(FilterLevel.EXCLUDED);
                break;
            case R.id.cleanFeedMsg:
                ReadTest.removeAllFeedMsgs(idx);
                break;
            case R.id.cleanFeedFilter:

                new AlertDialog.Builder(this.context)
                        .setTitle("Title")
                        .setMessage("Do you really want to delete all Filters")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(context, "Yaay", Toast.LENGTH_SHORT).show();
                                clearAllFilters(idx);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;
        }
		return super.onOptionsItemSelected(item);
	}

    private void clearAllFilters(int idx) {
        ReadTest.removeAllFilter(idx);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getActivity().setTitle("Feeds");

		adapter = new FeedMessageDisplayAdapter(getActivity(), messageList);
        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        adapter.notifyDataSetChanged();

		super.onViewCreated(view, savedInstanceState);
	}
	
	public void showMessage(FilterLevel level) {
		int counter = 0;
		List<FeedMessage> currMsgs = ReadTest.getMessages(idx, level);
		listSize = currMsgs.size();

        if (listSize > 0) {
            messageList = currMsgs;
            Collections.sort(messageList, new AppUtils.Compare());
        } else {
            messageList = Collections.EMPTY_LIST;
        }

        adapter.setData(messageList);
        adapter.notifyDataSetChanged();
	}

//
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        return;
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
//        menu.setHeaderTitle("Position : " + info.position);
//        String[] menuItems = getResources().getStringArray(R.array.menu);
//        for (int i = 0; i<menuItems.length; i++) {
//            menu.add(Menu.NONE, i, i, menuItems[i]);
//        }

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
                mCallback.onClickManageFilters(info.position);
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
		FilterLevel filterLevel = FilterLevel.getFilterLevel(preferences.getInt(prefVariable.toString(), 0));
		showMessage(filterLevel);
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
