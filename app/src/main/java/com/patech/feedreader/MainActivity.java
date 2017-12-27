package com.patech.feedreader;

import com.patech.adapters.NavigationViewAdapter;
import com.patech.dbhelper.DatabaseUtils;
import com.patech.dbhelper.FeedDatabaseOpenHelper;
import com.patech.dialog.AddFilterDialog;
import com.patech.dialog.FeedDialog;
import com.patech.dialog.UpdateFrequencyDialog;
import com.patech.location.Connectivity;
import com.patech.services.FeedIntentService;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.FeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IPageParser;
import com.patech.utils.AppConstants;
import com.patech.utils.CommonMsgs;
import com.patech.utils.AppUtils;

import android.database.Cursor;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        FeedDialog.FeedDialogInterface, AdapterView.OnItemClickListener,
        AddFilterDialog.AddFilterInterface, NavigationView.OnNavigationItemSelectedListener,
        NavigationMenuFragment.NavigationMenuInterface,
        UpdateFrequencyDialog.UpdateFrequencyInterface {

    private static final String STRING_EXCLUDE = "Exclude";
    private static final String STRING_INCLUDE = "Include";
    private static final String URL_NOT_VALID = "URL Of this Feed is not valid";
    /**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */

	SharedPreferences prefs;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	Intent feedIntentService = null;
	private int currPosition = 0;

	private FeedDatabaseOpenHelper mFeedDbHelper;
    private SQLiteDatabase mWriterFeedDB = null;
    private SQLiteDatabase mReaderFeedDB = null;
    private Map<String, Integer> infoStoreMap;


    private NavigationViewAdapter navViewAdapter;
    private SharedPreferences sharedPreferences;
    private ActionBarDrawerToggle toggle;

    @BindView(R.id.imageView) ImageView headerImageView;
    @BindView(R.id.nav_items_list) ListView mNavListView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        sharedPreferences = ((FeedReaderApplication)getApplication()).getSharedPreferences();
        prefs = getPreferences(MODE_PRIVATE);

        initDB();
        firstTimeInit();
        initUI();
	}

	private void displaySelectedScreen(int position) {
        currPosition = position;

        Fragment fragment = NavigationMenuFragment.newInstance(position);

		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment).commit();

	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        navViewAdapter.notifyDataSetChanged();
		if (!drawer.isDrawerOpen(GravityCompat.START)) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
            Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsActivity);
			break;
        case R.id.action_tutorial:
            Intent tutorialActivity = new Intent(getApplicationContext(), TutorialActivity.class);
            startActivity(tutorialActivity);
            break;
		case R.id.stopService:
			if (feedIntentService != null)
				stopService(feedIntentService);
			break;
        case R.id.deleteAllFilters:
            DatabaseUtils.clearFiltersDb(mWriterFeedDB);
            FeedInfoStore.getInstance().cleanupFilter();
            ReadTest.removeAllFilter();
            break;
        case R.id.setUpdateFrequency:
            UpdateFrequencyDialog mDialog = UpdateFrequencyDialog.newInstance();
            mDialog.setFrequencyInMillis(((FeedReaderApplication)getApplication()).getFrequencyInMillis());
            mDialog.show(getFragmentManager(), "Update Frequency");
            break;
        case R.id.searchFeed:
            Intent feedSearchIntent = new Intent(getApplicationContext(), FeedSearchActivity.class);
            startActivity(feedSearchIntent);
            break;
        case R.id.cleanFeedMsg:
            ReadTest.removeAllFeedMsgs();
            break;
        }
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        displaySelectedScreen(position);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Position : " + info.position);
        String[] menuItems = getResources().getStringArray(R.array.menu_navigation_drawer);
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
                // remove feed
                navViewAdapter.remove(info.position);
                break;
            case 1:
                // edit feed
                Feed feed = FeedInfoStore.getInstance().getFeed(info.position);

                FeedDialog mDialog = FeedDialog.newInstance();
                mDialog.setFeedToDisplay(feed);
                mDialog.show(getFragmentManager(), "Add Feed");

                break;
            case 2:
                // delete feed
                onClickManageFilters(info.position);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Dialog dialogView = dialog.getDialog();
        EditText nameText = (EditText) dialogView.findViewById(R.id.name);
        EditText summaryText = (EditText) dialogView.findViewById(R.id.filterText);
        EditText urlText = (EditText) dialogView.findViewById(R.id.url);
        CheckBox includeGlobalFilter = dialogView.findViewById(R.id.includeGlobalFilterBox);

        String nameVal = "";
        String summaryVal = "";
        String urlVal = "";
        if (nameText != null)
            nameVal = nameText.getText().toString().trim();
        if (summaryText != null)
            summaryVal = summaryText.getText().toString().trim();
        if (urlText != null)
            urlVal = urlText.getText().toString().trim();
        boolean bIncludeGlobalFilter = includeGlobalFilter.isChecked();

        if (validInput(nameVal, summaryVal, urlVal)) {
            // Insert the new row, returning the primary key value of the new row
            Feed newFeed = new Feed(nameVal, urlVal, summaryVal, null, null, null);

            Cursor cursor = DatabaseUtils.fetchFeedFromDatabase(mReaderFeedDB, newFeed);
            cursor.moveToNext();
            if (cursor.getCount() == 0) {
                long newRowId = AppUtils.insertFeedWithGlobalFilters(mWriterFeedDB, newFeed, bIncludeGlobalFilter);
                navViewAdapter.updateList();
                if (newRowId > 0) {
                    Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ADDED, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validInput(String nameVal, String summaryVal, String urlVal) {
        if ((nameVal != null && !nameVal.isEmpty()) && (summaryVal != null && !summaryVal.isEmpty()) && (urlVal != null && !urlVal.isEmpty()))
            return true;
        return false;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogFilterPositiveClick(DialogFragment dialog) {

        Dialog dialogView = dialog.getDialog();
        EditText nameText = (EditText) dialogView.findViewById(R.id.name);
        EditText filterText = (EditText) dialogView.findViewById(R.id.filterText);
        CheckBox includeCheckBox = (CheckBox) dialogView.findViewById(R.id.includeFilterCheckBox);
        CheckBox globalFilterCheckBox = (CheckBox) dialogView.findViewById(R.id.globalFilterCheckBox);

        String nameVal = "";
        String filterValue = "";
        String urlVal = "";
        if (nameText != null)
            nameVal = nameText.getText().toString().trim();
        if (filterText != null)
            filterValue = filterText.getText().toString().trim();
        boolean isInclude = includeCheckBox.isChecked();
        boolean isGlobalFilter = globalFilterCheckBox.isChecked();

        FeedFilter filter = null;
        String filterType = "";
        if (isInclude) {
            filter = new IncludeFeedFilter(filterValue, nameVal, AppUtils.EMPTY, isGlobalFilter);
            filterType = STRING_INCLUDE;
        } else {
            filter = new ExcludeFeedFilter(filterValue, nameVal, AppUtils.EMPTY, isGlobalFilter);
            filterType = STRING_EXCLUDE;
        }

        long filterId = DatabaseUtils.insertFilterIntoDb(mWriterFeedDB, filter);
        IPageParser parser = null;
        try {
            if (isGlobalFilter) {
                for (int idx = 0; idx < FeedInfoStore.getInstance().getFeedSize(); idx++) {
                    Feed feed = FeedInfoStore.getInstance().getFeedInfoList().get(idx);
                    long value = DatabaseUtils.insertFilterFeedIntoDb(mWriterFeedDB, filterId, feed);
                    parser = ReadTest.getFeedParser(idx);
                    parser.addFilter(filter);
                    FeedInfoStore.getInstance().addGlobalFilter(filter);
                }
            } else {
                parser = ReadTest.getFeedParser(currPosition);
                Feed feed = FeedInfoStore.getInstance().getFeedInfoList().get(currPosition);
                long value = DatabaseUtils.insertFilterFeedIntoDb(mWriterFeedDB, filterId, feed);
                parser.addFilter(filter);
            }
        } catch(MalformedURLException e) {
            Toast.makeText(getApplicationContext(), URL_NOT_VALID, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getApplicationContext(), filterType + " filter [" + nameVal + "] containing text [" + filterValue + "] added",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDialogFilterNegativeClick(DialogFragment dialog) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Item clicked" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean onClickManageFilters(int position) {

        Intent showFiltersIntent = new Intent(getApplicationContext(), FilterDisplayActivity.class);
        showFiltersIntent.putExtra(FilterDisplayActivity.POSITION, position);

        startActivity(showFiltersIntent);
        return true;
    }

    @Override
    public void onFrequencyUpdatePositiveClick(DialogFragment dialog) {
        Dialog dialogView = dialog.getDialog();
        EditText nameText = (EditText) dialogView.findViewById(R.id.freqValue);
        String currMilliFreq = nameText.getText().toString().trim();
        long duration;
        try {
            duration = Integer.parseInt(currMilliFreq);
        } catch (Exception e) {
            duration = ((FeedReaderApplication)getApplication()).getFrequencyInMillis();
        }

        if (duration < 1000)
            duration = 1000;

        (( FeedReaderApplication)getApplication()).setFrequencyInMillis(duration);
    }

    @Override
    public void onFrequencyUpdateNegativeClick(DialogFragment dialog) {

    }

    public FeedReaderApplication getApp() {
        return (FeedReaderApplication)getApplication();
    }


    private void initUI() {
        infoStoreMap = new HashMap<>();
        feedIntentService = new Intent(getApplicationContext(), FeedIntentService.class);
        startService(feedIntentService);

        initNavigationView();
    }

    private void initNavigationView() {
        navViewAdapter = new NavigationViewAdapter(this);
        mNavListView.setAdapter(navViewAdapter);
        mNavListView.setOnItemClickListener(this);
        registerForContextMenu(mNavListView);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @OnClick(R.id.fab)
    void showAddNewFeedDialog(View view) {
        DialogFragment mDialog = FeedDialog.newInstance();
        mDialog.show(getFragmentManager(), "Add Feed");
    }

    @OnClick(R.id.imageView)
    void showMainActivity(View view) {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void firstTimeInit() {

        // Check if we need to display our OnboardingFragment
        if (!sharedPreferences.getBoolean(
                AppConstants.FIRST_TIME_LAUNCH, false)) {

            boolean isMobile = Connectivity.isConnectedMobile(getApplicationContext());

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(AppConstants.FIRST_TIME_LAUNCH, true);
            if (!isMobile) {
                sharedPreferencesEditor.putBoolean(AppConstants.WIFI_ONLY, true);
            }
            getApp().setUpdateOnWifiOnly(!isMobile);
            sharedPreferencesEditor.apply();
        } else {
            boolean isWifiOnly = sharedPreferences.getBoolean(AppConstants.WIFI_ONLY, false);
            getApp().setUpdateOnWifiOnly(isWifiOnly);
        }
    }

    private void initDB() {
//        // Get the underlying database for writing
        mReaderFeedDB = ((FeedReaderApplication)getApplication()).getReadableDatabase();
        mWriterFeedDB = ((FeedReaderApplication)getApplication()).getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return mReaderFeedDB;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mWriterFeedDB;
    }

}
