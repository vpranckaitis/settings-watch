package lt.vpranckaitis.settingswatch.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import lt.vpranckaitis.settingswatch.R;

public class SettingsList extends AbstractList implements
	LoaderCallbacks<Cursor>, OnClickListener, OnShowListener {
    private static final String STATE_NAME = "mName";
    private static final String STATE_VALUE = "value";
    private static final String STATE_DIALOG = "dialogIsShowing";
    private static final String STATE_SCROLL = "mScroll";

    private static final String TAG = "SettingsList";
    private AlertDialog mDialog;
    private String mName;
    private InputMethodManager mInputManager;

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	mAdOffset = 0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onViewCreated(view, savedInstanceState);
	setEmptyText(getString(R.string.list_empty_settings));
	if (savedInstanceState != null) {
	    Log.d(TAG, "savedInstanceState");
	    if (savedInstanceState.getBoolean(STATE_DIALOG)) {
		Log.d("TAG", "dialog");
		if (mDialog == null) {
		    mDialog = createDialog();
		}

		mName = savedInstanceState.getString(STATE_NAME);
		mDialog.setTitle(mName);
		String value = savedInstanceState.getString(STATE_VALUE);
		mTextView.setText(value);
		mDialog.show();
	    }
	}
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	// TODO Auto-generated method stub
	if (getListAdapter() == null) {
	    String[] from = { Settings.System.NAME, Settings.System.VALUE };
	    int[] to = { R.id.name, R.id.value };
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    getActivity(), R.layout.list_content, data, from, to,
		    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    setListAdapter(adapter);
	} else {
	    ((SimpleCursorAdapter) getListAdapter()).swapCursor(data);
	}
	// getListView().setSelection(mScroll);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
	// TODO Auto-generated method stub
	if (getListAdapter() != null) {
	    ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	if (mDialog != null) {
	    outState.putBoolean(STATE_DIALOG, mDialog.isShowing());
	    outState.putString(STATE_NAME, mName);
	    outState.putString(STATE_VALUE, mTextView.getText().toString());
	    mDialog.dismiss();
	}
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	// TODO Auto-generated method stub
	if (mDialog == null) {
	    mDialog = createDialog();
	}

	Cursor currentSelection = ((Cursor) l.getAdapter().getItem(position));
	String text = currentSelection.getString(currentSelection
		.getColumnIndex(Settings.System.VALUE));
	String title = currentSelection.getString(currentSelection
		.getColumnIndex(Settings.System.NAME));
	mName = title;
	mTextView.setText(text);
	mDialog.setTitle(title);
	mDialog.show();
    }

    protected AlertDialog createDialog() {
	AlertDialog.Builder dialogBuilder = new Builder(getActivity());
	if (isEditable()) {
	    dialogBuilder.setPositiveButton(
		    R.string.dialog_edit_value_positive, this);
	}
	// dialogBuilder.setNeutralButton(R.string.dialog_edit_value_neutral2,
	// this);
	dialogBuilder.setNegativeButton(R.string.dialog_edit_value_negative,
		this);
	View v = LayoutInflater.from(getActivity()).inflate(mDialogLayoutId,
		null);
	mTextView = (TextView) v.findViewById(R.id.value);
	dialogBuilder.setView(v);

	AlertDialog dialog = dialogBuilder.create();
	dialog.setOnShowListener(this);
	if (isEditable()) {
	    if (mInputManager == null)
		mInputManager = (InputMethodManager) getActivity()
			.getSystemService(Context.INPUT_METHOD_SERVICE);
	    ((AlertDialog) dialog).getWindow().setSoftInputMode(
		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	setNoReturn(true);
	return dialog;
    }

    public void onClick(DialogInterface dialog, int which) {
	// TODO Auto-generated method stub
	Log.d(TAG, "onClick");
	if (which == Dialog.BUTTON_POSITIVE) {
	    Settings.System.putString(getActivity().getContentResolver(),
		    mName, mTextView.getText().toString());
	} else if (which == Dialog.BUTTON_NEUTRAL) {
	    if (this.getClass().equals(SystemSettings.class)) {

	    }

	}
    }

    public void onShow(DialogInterface dialog) {
	// TODO Auto-generated method stub
	if (isEditable()) {
	    ((EditText) mTextView).selectAll();
	}
    }

    protected void backup(Uri uri) {
	new AsyncCursorStore().execute(uri);
    }

    private class AsyncCursorStore extends AsyncTask<Uri, Void, Void> {

	@Override
	protected Void doInBackground(Uri... params) {
	    Cursor c = ((SimpleCursorAdapter) getListAdapter()).getCursor();
	    int n = c.getCount();
	    int columns = c.getColumnCount();
	    ContentValues[] values = new ContentValues[n];
	    c.moveToFirst();
	    for (int i = 0; i < n; i++) {
		values[i] = new ContentValues();
		for (int j = 0; j < columns; j++)
		    values[i].put(c.getColumnName(j), c.getString(j));
		c.moveToNext();
	    }
	    ContentResolver cr = getActivity().getContentResolver();
	    cr.delete(params[0], null, null);
	    cr.bulkInsert(params[0], values);
	    return null;
	}

    }
}
