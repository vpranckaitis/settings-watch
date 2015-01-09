package lt.vpranckaitis.settingswatch.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lt.vpranckaitis.settingswatch.R;
import lt.vpranckaitis.settingswatch.SettingsContract;
import lt.vpranckaitis.settingswatch.fragments.SettingsListFragment;


public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean mShowOnlyChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("Fragment", "aaa");
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (SettingsListFragment f : mSectionsPagerAdapter.getFragments()) {
                    f.closeActionMode();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_only_changes).setChecked(mShowOnlyChanged);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                for (SettingsListFragment f : mSectionsPagerAdapter.getFragments()) {
                    f.takeSnapshot();
                }
                return true;
            case R.id.action_only_changes:
                mShowOnlyChanged = !item.isChecked();
                item.setChecked(mShowOnlyChanged);
                for (SettingsListFragment f : mSectionsPagerAdapter.getFragments()) {
                    f.setDisplayOnlyChanges(mShowOnlyChanged);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private List<SettingsListFragment> mFragments = new ArrayList<>();

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            SettingsListFragment fragment = new SettingsListFragment();
            mFragments.add(fragment);
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    args.putString(SettingsListFragment.EXTRAS_SOURCE_URI, Settings.System.CONTENT_URI.toString());
                    args.putString(SettingsListFragment.EXTRAS_TARGET_URI, SettingsContract.System.CONTENT_URI.toString());
                    args.putBoolean(SettingsListFragment.EXTRAS_WRITABLE, true);
                    break;
                case 1:
                    args.putString(SettingsListFragment.EXTRAS_SOURCE_URI, Settings.Secure.CONTENT_URI.toString());
                    args.putString(SettingsListFragment.EXTRAS_TARGET_URI, SettingsContract.Secure.CONTENT_URI.toString());
                    args.putBoolean(SettingsListFragment.EXTRAS_WRITABLE, false);
                    break;
                case 2:
                    args.putString(SettingsListFragment.EXTRAS_SOURCE_URI, Settings.Global.CONTENT_URI.toString());
                    args.putString(SettingsListFragment.EXTRAS_TARGET_URI, SettingsContract.Global.CONTENT_URI.toString());
                    args.putBoolean(SettingsListFragment.EXTRAS_WRITABLE, false);
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return 3;
            }
            return 2;
        }

        public List<SettingsListFragment> getFragments() {
            return mFragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}
