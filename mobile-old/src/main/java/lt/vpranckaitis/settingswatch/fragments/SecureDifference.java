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

public class SecureDifference extends DifferenceList {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setEditable(false);
	setupDialog(R.layout.dialog_difference_no_edit);
	mColumnsLeft = new String[] { Settings.Secure.NAME };
	mColumnsRight = new String[] { SettingsContract.Secure.NAME };
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
	    projection = new String[] { Settings.Secure._ID,
		    Settings.Secure.NAME, Settings.Secure.VALUE };
	    uri = Settings.Secure.CONTENT_URI;
	    sortOrder = Settings.Secure.NAME;
	    break;
	case ID_CURSOR_RIGHT:
	    projection = new String[] { SettingsContract.Secure._ID,
		    SettingsContract.Secure.NAME, SettingsContract.Secure.VALUE };
	    uri = SettingsContract.Secure.CONTENT_URI;
	    sortOrder = SettingsContract.Secure.NAME;
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
	inflater.inflate(R.menu.secure_difference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_delete_all:
	    deleteAll(SettingsContract.Secure.CONTENT_URI);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
