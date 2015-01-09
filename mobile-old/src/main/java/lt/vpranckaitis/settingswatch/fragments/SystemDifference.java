package lt.vpranckaitis.settingswatch.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import lt.vpranckaitis.settingswatch.R;
import lt.vpranckaitis.settingswatch.SettingsContract;

public class SystemDifference extends DifferenceList {
    private static final String TAG = SystemDifference.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setEditable(true);
	setupDialog(R.layout.dialog_difference);
	mColumnsLeft = new String[] { Settings.System.NAME };
	mColumnsRight = new String[] { SettingsContract.System.NAME };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);

	getLoaderManager().initLoader(ID_CURSOR_LEFT, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	String[] projection;
	Uri uri;
	String sortOrder;
	switch (id) {
	case ID_CURSOR_LEFT:
	    projection = new String[] { Settings.System._ID,
		    Settings.System.NAME, Settings.System.VALUE };
	    uri = Settings.System.CONTENT_URI;
	    sortOrder = Settings.System.NAME;
	    break;
	case ID_CURSOR_RIGHT:
	    projection = new String[] { SettingsContract.System._ID,
		    SettingsContract.System.NAME, SettingsContract.System.VALUE };
	    uri = SettingsContract.System.CONTENT_URI;
	    sortOrder = SettingsContract.System.NAME;
	    break;
	default:
	    return super.onCreateLoader(id, args);
	}
	return new CursorLoader(getActivity(), uri, projection, null, null,
		sortOrder);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// TODO Auto-generated method stub
	inflater.inflate(R.menu.system_difference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_delete_all:
	    deleteAll(SettingsContract.System.CONTENT_URI);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
