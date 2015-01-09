package lt.vpranckaitis.settingswatch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.android.tabcompat.lib.CompatTab;
import com.example.android.tabcompat.lib.CompatTabListener;

public class TabListener<T extends Fragment> implements CompatTabListener {

    private final String mTag;
    private final FragmentActivity mActivity;
    private final Class<T> mClass;

    public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
	mActivity = activity;
	mTag = tag;
	mClass = clz;

    }

    public void onTabSelected(CompatTab tab, FragmentTransaction ft) {
	Fragment fragment = tab.getFragment();
	if (fragment == null) {
	    fragment = mActivity.getSupportFragmentManager().findFragmentByTag(
		    mTag);
	    tab.setFragment(fragment);
	}
	if (fragment == null) {
	    fragment = Fragment.instantiate(mActivity, mClass.getName());
	    tab.setFragment(fragment);
	    ft.add(android.R.id.tabcontent, fragment, mTag);
	} else {
	    ft.attach(fragment);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	}
    }

    public void onTabUnselected(CompatTab tab, FragmentTransaction ft) {
	Fragment fragment = tab.getFragment();
	if (fragment != null) {
	    ft.detach(fragment);
	    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	}
    }

    public void onTabReselected(CompatTab tab, FragmentTransaction ft) {
	// TODO Auto-generated method stub

    }

}
