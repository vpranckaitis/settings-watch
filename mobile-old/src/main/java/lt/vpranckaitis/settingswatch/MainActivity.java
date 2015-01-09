package lt.vpranckaitis.settingswatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.tabcompat.lib.CompatTab;
import com.example.android.tabcompat.lib.TabCompatActivity;
import com.example.android.tabcompat.lib.TabHelper;

import lt.vpranckaitis.settingswatch.fragments.SecureSettings;
import lt.vpranckaitis.settingswatch.fragments.SystemSettings;

public class MainActivity extends TabCompatActivity implements
	ApplicationConstants {
    private static final int AD_FREE_OPEN = 360;
    private static final long AD_FREE_TIME = 3600000;
    private static final String TAG_SYSTEM = SystemSettings.class.getName();
    private static final String TAG_SECURE = SecureSettings.class.getName();
    private static final String NEWLY_LAUNCHED = "newly launched";

    TabHelper mTabHelper;
    boolean mNoBackup;
    private SharedPreferences mSharedPref;

    private int mOpenCount;
    private long mTimeCount;
    private long mStartTime;
    private boolean mAds = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	if (mSharedPref == null) {
	    mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	}
	mOpenCount = mSharedPref.getInt(PREF_KEY_OPEN_COUNT, 0);
	mTimeCount = mSharedPref.getLong(PREF_KEY_TIME_COUNT, 0);
	mAds = mSharedPref.getBoolean(PREF_KEY_NO_ADS, true);
	if (savedInstanceState == null) {
	    mOpenCount++;
	}

	if (mAds && mOpenCount >= AD_FREE_OPEN && mTimeCount >= AD_FREE_TIME) {
	    mAds = false;
	    mSharedPref.edit()
		    .putBoolean(ApplicationConstants.PREF_KEY_NO_ADS, mAds)
		    .commit();
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.dialog_no_ads_title)
		    .setMessage(R.string.dialog_no_ads_message)
		    .setNeutralButton(R.string.dialog_no_ads_neutral, null)
		    .show();
	}

	if (mSharedPref.getBoolean(PREF_KEY_FIRST_RUN, true)) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.dialog_disclaimer_title)
		    .setMessage(R.string.dialog_disclaimer_message)
		    .setPositiveButton(R.string.dialog_disclaimer_positive,
			    null).setCancelable(false).show();
	    mSharedPref.edit().putBoolean(PREF_KEY_FIRST_RUN, false).commit();
	}

	Log.d("MainActivity",
		Integer.toString(mOpenCount) + " " + Long.toString(mTimeCount)
			+ " " + Boolean.toString(mAds));

	TabHelper mTabHelper = getTabHelper();
	CompatTab tab = mTabHelper.newTab(getString(R.string.tab_system));
	tab.setTabListener(new TabListener<SystemSettings>(this, TAG_SYSTEM,
		SystemSettings.class));
	tab.setText(R.string.tab_system);
	mTabHelper.addTab(tab);

	tab = mTabHelper.newTab(getString(R.string.tab_secure));
	tab.setTabListener(new TabListener<SecureSettings>(this, TAG_SECURE,
		SecureSettings.class));
	tab.setText(R.string.tab_secure);
	mTabHelper.addTab(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	super.onPrepareOptionsMenu(menu);
	menu.findItem(R.id.menu_difference).setEnabled(!mNoBackup);
	return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case R.id.menu_difference:
	    startActivity(new Intent(this, DifferenceActivity.class));
	    break;
	case R.id.menu_timetravel:
	    mOpenCount = AD_FREE_OPEN;
	    mTimeCount = AD_FREE_TIME;
	    break;
	default:
	    return super.onMenuItemSelected(featureId, item);
	}
	return false;
    }

    @Override
    protected void onStart() {
	super.onStart();
	mStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
	super.onStop();
	mTimeCount += System.currentTimeMillis() - mStartTime;
    }

    @Override
    protected void onDestroy() {
	mSharedPref.edit().putInt(PREF_KEY_OPEN_COUNT, mOpenCount)
		.putLong(PREF_KEY_TIME_COUNT, mTimeCount).commit();
	super.onDestroy();
    }
}
