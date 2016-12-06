package lt.vpranckaitis.settingswatch.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import lt.vpranckaitis.settingswatch.ApplicationConstants;

public class AbstractList extends ListFragment {
    private static final String TAG = DifferenceList.class.getName();
    private static final int AD_ARRAY_SIZE = 10;

    private boolean mNoReturn;
    private boolean mEditable;

    private int mOrient;

    static private AdView mAdView[];
    protected int mDialogLayoutId;
    private Rect mAdRect;

    protected int mAdOffset = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mNoReturn = false;
	mEditable = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onViewCreated(view, savedInstanceState);
	boolean ads = PreferenceManager.getDefaultSharedPreferences(
		getActivity()).getBoolean(ApplicationConstants.PREF_KEY_NO_ADS,
		true);
	if (ads) {
	    mOrient = getResources().getConfiguration().orientation;
	    if (mAdView == null) {
		mAdView = new AdView[AD_ARRAY_SIZE];
	    }
	    if (mAdView[mOrient + mAdOffset] == null) {
		Log.d(TAG, "null");
		mAdView[mOrient + mAdOffset] = new AdView(getActivity());
		mAdView[mOrient + mAdOffset].setAdSize(AdSize.SMART_BANNER);
		mAdView[mOrient + mAdOffset].setAdUnitId("a151aa291e82461");
		AdRequest ar = new AdRequest.Builder()
				.addKeyword("android")
				.addKeyword("settings")
				.build();
		mAdView[mOrient + mAdOffset].loadAd(ar);
	    }

	    if (getListAdapter() != null) {
		ListAdapter adapter = getListAdapter();
		setListAdapter(null);
		try {
		    getListView().addFooterView(mAdView[mOrient + mAdOffset]);
		} catch (Exception e) {
		    Log.d(TAG, e.toString());
		}
		setListAdapter(adapter);
	    } else {
		try {
		    getListView().addFooterView(mAdView[mOrient + mAdOffset]);
		} catch (Exception e) {
		    Log.d(TAG, e.getMessage());
		}
	    }
	}
    }

    protected void setEditable(boolean editable) {
	if (!mNoReturn) {
	    mEditable = editable;
	}
    }

    protected void setNoReturn(boolean noReturn) {
	mNoReturn = noReturn;
    }

    protected boolean isNoReturn() {
	return mNoReturn;
    }

    protected boolean isEditable() {
	return mEditable;
    }

    protected void setupDialog(int resId) {
	if (!mNoReturn) {
	    mDialogLayoutId = resId;
	}
    }
    /*
     * @Override public void onResume() { // TODO Auto-generated method stub
     * super.onResume(); if(mAdRect != null) { mAdView[mOrient +
     * mAdOffset].layout(mAdRect.left, mAdRect.top, mAdRect.right,
     * mAdRect.bottom); } }
     * 
     * @Override public void onPause() { // TODO Auto-generated method stub
     * super.onPause(); if(mAdView != null && mAdView[mOrient] != null) mAdRect
     * = new Rect(mAdView[mOrient].getLeft(), mAdView[mOrient].getTop(),
     * mAdView[mOrient].getRight(), mAdView[mOrient].getBottom()); }
     */
}
