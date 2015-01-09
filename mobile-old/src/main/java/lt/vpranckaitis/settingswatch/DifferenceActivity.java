package lt.vpranckaitis.settingswatch;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.tabcompat.lib.CompatTab;
import com.example.android.tabcompat.lib.TabCompatActivity;
import com.example.android.tabcompat.lib.TabHelper;

import lt.vpranckaitis.settingswatch.fragments.SecureDifference;
import lt.vpranckaitis.settingswatch.fragments.SystemDifference;

public class DifferenceActivity extends TabCompatActivity {
    private static final String TAG_SYSTEM = SystemDifference.class.getName();
    private static final String TAG_SECURE = SecureDifference.class.getName();

    @TargetApi(14)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	TabHelper mTabHelper = getTabHelper();
	CompatTab tab = mTabHelper.newTab(getString(R.string.tab_system));
	tab.setTabListener(new TabListener<SystemDifference>(this, TAG_SYSTEM,
		SystemDifference.class));
	tab.setText(R.string.tab_system);
	mTabHelper.addTab(tab);

	tab = mTabHelper.newTab(getString(R.string.tab_secure));
	tab.setTabListener(new TabListener<SecureDifference>(this, TAG_SECURE,
		SecureDifference.class));
	tab.setText(R.string.tab_secure);
	mTabHelper.addTab(tab);

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	    ActionBar ab = getActionBar();
	    ab.setHomeButtonEnabled(true);
	    ab.setDisplayHomeAsUpEnabled(true);
	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    this.finish();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
    }
}
