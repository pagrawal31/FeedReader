package com.patech.feedreader;

import com.google.android.gms.ads.AdView;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.java.rssfeed.model.feed.Outline;
import com.patech.adapters.NavigationViewAdapter;
import com.patech.dbhelper.DatabaseUtils;
import com.patech.dbhelper.FeedContract.FilterEntry;
import com.patech.dbhelper.FeedDatabaseOpenHelper;
import com.patech.dialog.AddFilterDialog;
import com.patech.dialog.FeedDialog;
import com.patech.dialog.UpdateFrequencyDialog;
import com.patech.imexport.opml.OpmlParser;
import com.patech.location.Connectivity;
import com.patech.services.FeedIntentService;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.model.feed.Feed;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IPageParser;
import com.patech.utils.AppConstants;
import com.patech.utils.CommonMsgs;
import com.patech.utils.AppUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        FeedDialog.FeedDialogInterface, AdapterView.OnItemClickListener,
        AddFilterDialog.AddFilterInterface, NavigationView.OnNavigationItemSelectedListener,
        NavigationMenuFragment.NavigationMenuInterface,
        UpdateFrequencyDialog.UpdateFrequencyInterface {

    private static final String STRING_EXCLUDE = "Exclude";
    private static final String STRING_INCLUDE = "Include";
    private static final String URL_NOT_VALID = "URL Of this Feed is not valid";
    private static final String FILENAME = "feedExport";
    private static final String EXTENSION = "opml";
    private static final String dirPath = "feedreader";
    /**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */

//	SharedPreferences prefs;

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

    // flag for doing checks like counting days etc
    private boolean redoChecks = true;

    @BindView(R.id.imageView) ImageView headerImageView;
    @BindView(R.id.nav_items_list) ListView mNavListView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.adView) AdView mAdView;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        mTitle = getResources().getString(R.string.app_name);
        getSupportActionBar().setTitle(mTitle);
        sharedPreferences = ((FeedReaderApplication)getApplication()).getSharedPreferences();

        initDB();
        requestPermission(android.Manifest.permission.READ_PHONE_STATE, getString(R.string.permission_rationale_read_phone_state),
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE);

        int flags = getIntent().getFlags();
//        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY )!=0) {
//            checkForCleanupMsgs();
//        }
        if (flags != 0) {
            checkForCleanupMsgs();
        }
        super.initAds(mAdView);
        firstTimeInit();
        initUI();
	}

    private void checkForCleanupMsgs() {

        String storedVal = sharedPreferences.getString(AppConstants.CLEANUP_DAYS, "0");
        int daysVal = 0;
        try {
            daysVal = Integer.parseInt(storedVal);
        } catch (NumberFormatException nfe) {
            daysVal = 0;
        }

        if (daysVal <= 0 || daysVal > 31)
            return;

        String lastSavedDate = sharedPreferences.getString(AppConstants.CLEANUP_DATE, AppConstants.EMPTY);

        if (lastSavedDate.isEmpty()) {
            lastSavedDate = AppUtils.formatDate(new Date());
            updateLastSavedDate(lastSavedDate);
            return;
        }
        Date oldDate = AppUtils.parseDate(lastSavedDate);
        if (AppUtils.dateDiff(oldDate, new Date(), daysVal)) {
            cleanupAllMsgs();
            lastSavedDate = AppUtils.formatDate(new Date());
            updateLastSavedDate(lastSavedDate);
        }
    }

    private void updateLastSavedDate(String lastSavedDate) {
        sharedPreferences.edit().putString(AppConstants.CLEANUP_DATE, lastSavedDate).commit();
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
        case R.id.import_file:
//            Intent tutorialActivity = new Intent(getApplicationContext(), TutorialActivity.class);
//            startActivity(tutorialActivity);
            fileImport();
            break;
        case R.id.export_file:
            fileExport();
            break;
		case R.id.stopService:
			if (feedIntentService != null)
				stopService(feedIntentService);
			break;
        case R.id.deleteAllFilters:

            new AlertDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Do you really want to delete all Filters")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                            clearAllFilters();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

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
        case R.id.cleanAllFeedMsg:
            cleanupAllMsgs();
            break;
        }
		return super.onOptionsItemSelected(item);
	}

    private void clearAllFilters() {
        DatabaseUtils.clearFiltersDb(mWriterFeedDB);
        FeedInfoStore.getInstance().cleanupFilter();
        ReadTest.removeAllFilter();
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
        Feed feed = FeedInfoStore.getInstance().getFeed(info.position);
        switch (menuItemIndex) {
            case 0:
                // remove feed
                navViewAdapter.remove(info.position);
                DatabaseUtils.deleteFeed(getWritableDatabase(), feed);

                break;
            case 1:
                // edit feed
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
            Feed.FeedBuilder builder = new Feed.FeedBuilder();
            Feed newFeed = builder.setTitle(nameVal).setDescription(summaryVal).build(urlVal);
            if (tryToAddFeed(newFeed, bIncludeGlobalFilter)) {
                Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ADDED, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), CommonMsgs.FEED_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean tryToAddFeed(Feed newFeed, boolean bIncludeGlobalFilter) {
        Cursor cursor = DatabaseUtils.fetchFeedFromDatabase(mReaderFeedDB, newFeed);
        cursor.moveToNext();
        if (cursor.getCount() == 0) {
            long newRowId = AppUtils.insertFeedWithGlobalFilters(mWriterFeedDB, newFeed, bIncludeGlobalFilter);
            navViewAdapter.updateList();
            if (newRowId > 0 )
                return true;
        }
        return false;
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
        if (nameText != null)
            nameVal = nameText.getText().toString().trim();
        if (filterText != null)
            filterValue = filterText.getText().toString().trim();

        if (filterValue.length() <= 1) {
            Toast.makeText(getApplicationContext(), AppConstants.INVALID_FILTER, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInclude = includeCheckBox.isChecked();
        boolean isGlobalFilter = globalFilterCheckBox.isChecked();

        IFeedFilter filter = null;
        String filterType = "";
        if (isInclude) {
            filter = new IncludeFeedFilter(filterValue, nameVal, AppUtils.EMPTY, isGlobalFilter);
            filterType = STRING_INCLUDE;
        } else {
            filter = new ExcludeFeedFilter(filterValue, nameVal, AppUtils.EMPTY, isGlobalFilter);
            filterType = STRING_EXCLUDE;
        }


        Cursor filterCursor = DatabaseUtils.fetchFiltersFromFilterDb(getReadableDatabase(), filter);
        int currFilterId = -1;

        IFeedFilter currFilter = null;
        while(filterCursor.moveToNext()) {
            currFilterId = Integer.parseInt(filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry._ID)));
            String currFilterType = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TYPE));
            if (currFilterType != filterType)
                continue;

            String filterDesc = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_DESC));
            String filterName = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_NAME));
            String currFilterText = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TEXT));

            boolean isGlobal = AppUtils.getBooleanFromInt(filterCursor.getInt((filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_GLOBAL))));

            if (filterType.equals(IncludeFeedFilter.FILTERTYPE)) {
                currFilter = new IncludeFeedFilter(currFilterText, filterName, filterDesc, isGlobal);
            } else {
                currFilter = new ExcludeFeedFilter(currFilterText, filterName, filterDesc, isGlobal);
            }
        }
        filterCursor.close();
        long filterId = currFilterId;

        if (currFilterId != -1 && !isGlobalFilter) {
            filter = currFilter;
        } else {
            filterId = DatabaseUtils.insertFilterIntoDb(mWriterFeedDB, filter);
        }

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

    /*
    method to remove all msgs
     */
    private void cleanupAllMsgs() {
        ReadTest.removeAllFeedMsgs();
    }

    private void firstTimeInit() {

        // Check if we need to display our OnboardingFragment
        if (!sharedPreferences.getBoolean(
                AppConstants.FIRST_TIME_LAUNCH, false)) {

            boolean isMobile = Connectivity.isConnectedMobile(getApplicationContext());

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(AppConstants.FIRST_TIME_LAUNCH, true);
            if (!isMobile) {
                sharedPreferencesEditor.putBoolean(getResources().getString(R.string.download_over_wifi), true);
//                sharedPreferencesEditor.putBoolean(AppConstants.WIFI_ONLY, true);
            }
            getApp().setUpdateOnWifiOnly(!isMobile);
            sharedPreferencesEditor.apply();
        } else {
            boolean isWifiOnly = sharedPreferences.getBoolean(getResources().getString(R.string.download_over_wifi), false);
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

    public void fileImport() {
        List<Outline> outlines = new ArrayList<>();

        File f = new File(getAbsoluteFilePath(FILENAME));
        try {
            outlines = OpmlParser.read(f);
            Toast.makeText(getApplicationContext(), "Successfully Imported", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error in Importing", Toast.LENGTH_SHORT).show();
        }

        int addedCount = 0;
        for (Outline outline : outlines) {
            Feed feed = outline.getSubscriptions().get(0);
            if (tryToAddFeed(feed, true)) {
                addedCount++;
            }
        }
        Toast.makeText(getApplicationContext(), "[" + addedCount + "]" + CommonMsgs.FEED_ADDED + " out of [" + outlines.size() +"].", Toast.LENGTH_SHORT).show();

        navViewAdapter.notifyDataSetChanged();
    }

    public void fileExport() {
        List<Outline> outlines = new ArrayList<>();
        for (Feed feed : FeedInfoStore.getInstance().getFeedInfoList()) {
            List<Feed> subscriptionList = Collections.singletonList(feed);
            Outline outline = new Outline();
            outline.setSubscriptions(subscriptionList);
            outlines.add(outline);
        }

        File f = new File(getAbsoluteFilePath(FILENAME));
        try {
            OpmlParser.write(outlines, f);
            Toast.makeText(getApplicationContext(), "Successfully Exported", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error in Exporting", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAbsoluteFilePath(String fileName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(dirPath, Context.MODE_PRIVATE);
        String imageFileName = fileName + EXTENSION;
        return String.format("%s/%s", directory, imageFileName);
    }



}
