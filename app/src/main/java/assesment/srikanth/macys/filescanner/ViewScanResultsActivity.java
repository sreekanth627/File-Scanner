package assesment.srikanth.macys.filescanner;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("ALL")
public class ViewScanResultsActivity extends AppCompatActivity {

    public static final int TOTAL_TABS_COUNT = 4;
    public static final String TAB_A_TEXT = "Large Files";
    public static final String TAB_B_TEXT = "Frequent Extensions";
    public static final String TAB_C_TEXT = "Recent Files";
    public static final String TAB_D_TEXT = "Average Memory";

    private ShareActionProvider mShareActionProvider;

    private ResultsPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private ArrayList<FileInfo> recentFileList;
    private ArrayList<FileInfo> largeFileList;
    private int totalNoOfFiles;
    private long totalMemory;
    private HashMap<String, Integer> frequentExtensionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        recentFileList = getIntent().getExtras().getParcelableArrayList(Constants.RECENT_FILES_KEY);
        largeFileList = getIntent().getExtras().getParcelableArrayList(Constants.LARGEST_FILES_KEY);
        totalNoOfFiles = getIntent().getExtras().getInt(Constants.TOTAL_FILES_KEY);
        totalMemory = getIntent().getExtras().getLong(Constants.TOTAL_MEMORY_KEY);
        frequentExtensionList = (HashMap<String, Integer>)getIntent().getExtras().getSerializable(Constants.FREQUENT_EXTENSIONS_KEY);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        pagerAdapter = new ResultsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);


        final ActionBar actionBar = getSupportActionBar();
        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < TOTAL_TABS_COUNT; i++) {
            String text = "";
            if( i == 0) {
                text = TAB_A_TEXT;
            } else if( i == 1) {
                text = TAB_B_TEXT;
            } else if( i == 2) {
                text = TAB_C_TEXT;
            } else {
                text = TAB_D_TEXT;
            }
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(text)
                            .setTabListener(tabListener));
        }

    }



    public class ResultsPagerAdapter extends FragmentStatePagerAdapter {
        public ResultsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            Bundle bundle = new Bundle();
            if( i == 0) {
                fragment = new LargeFileListFragment();
                bundle.putParcelableArrayList(Constants.LARGEST_FILES_KEY, largeFileList);
            } else if(i == 1) {
                fragment = new MostFrequentExtensionsFragment();
                bundle.putSerializable(Constants.FREQUENT_EXTENSIONS_KEY, frequentExtensionList);
            } else if(i == 2) {
                fragment = new RecentFileListFragment();
                bundle.putParcelableArrayList(Constants.RECENT_FILES_KEY, recentFileList);
            } else {
                fragment = new AverageMemoryFragment();
                bundle.putInt(Constants.TOTAL_FILES_KEY, totalNoOfFiles);
                bundle.putLong(Constants.TOTAL_MEMORY_KEY, totalMemory);
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return TOTAL_TABS_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                shareData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareData() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getCustomizedString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    private String getCustomizedString() {

        String s = "Scan Completed!";
        if(totalNoOfFiles != 0) {
            s = s + "  Total No oF files scanned : " + totalNoOfFiles;
        }

        if(totalMemory > 0) {
            s = s + "  Total Memory Occupied : " + totalMemory + " KB";
        }

        if(largeFileList.size() > 0) {
            s = s+ "  Largest File Size is : " + largeFileList.get(0).getFileSize() + " KB";
        }

        if(frequentExtensionList.size() > 0) {
            Map.Entry<String,Integer> entry=frequentExtensionList.entrySet().iterator().next();
            String key= entry.getKey();
            Integer value=entry.getValue();
            s = s + " most frequent extension is : " + key + " and count is " + value;
        }
        return s;
    }
}
