package lt.vpranckaitis.settingswatch.fragments;

import android.database.Cursor;
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

public class SystemSettings extends SettingsList {
    private static final String TAG = "SystemSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setEditable(true);
	setupDialog(R.layout.dialog_settings);
	setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	String[] projection = { Settings.System._ID, Settings.System.NAME,
		Settings.System.VALUE };
	return new CursorLoader(getActivity(), Settings.System.CONTENT_URI,
		projection, null, null, Settings.System.NAME);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// TODO Auto-generated method stub
	inflater.inflate(R.menu.system_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_backup:
	    backup(SettingsContract.System.CONTENT_URI);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
