package lt.vpranckaitis.settingswatch.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import lt.vpranckaitis.settingswatch.R;
import lt.vpranckaitis.settingswatch.SettingsProvider;

import static lt.vpranckaitis.settingswatch.SettingsContract.NameValueTable;

/**
 * Created by vpran_000 on 2015-01-07.
 */
public class SettingsListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

    public static final String EXTRAS_SOURCE_URI = "sourceUri";
    public static final String EXTRAS_TARGET_URI = "targetUri";
    public static final String EXTRAS_WRITABLE = "writable";

    private static final int LOADER_PREPARE = 0;
    private static final int LOADER_PARSE = 1;

    private Uri mSourceUri = null;
    private Uri mTargetUri = null;
    private boolean mOnlyChages = false;
    private ActionMode mActionMode = null;
    private boolean mWritable = false;

    private AlertDialog mAlertDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("Fragment", "aaa");
        setHasOptionsMenu(true);
        mSourceUri = Uri.parse(getArguments().getString(EXTRAS_SOURCE_URI));
        mTargetUri = Uri.parse(getArguments().getString(EXTRAS_TARGET_URI));
        mWritable = getArguments().getBoolean(EXTRAS_WRITABLE);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView list = getListView();
        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new MultiChoiceListener());
        setEmptyText(getActivity().getText(R.string.empty_list));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = null;
        String selection = null;
        switch (id) {
            case 0:
                uri = mSourceUri;
                break;
            case 1:
                Log.d("Loader", "Get data");
                uri = mTargetUri;
                if (mOnlyChages) {
                    selection = NameValueTable.VALUE + " != " + NameValueTable.VALUE_OLD;
                }
                break;
        }
        return new CursorLoader(getActivity(), uri, null, selection, null, Settings.NameValueTable.NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 0) {
            List<ContentValues> cvs = new ArrayList<ContentValues>();
            data.moveToFirst();
            String[] columns = new String[]{
                    Settings.NameValueTable._ID,
                    Settings.NameValueTable.NAME,
                    Settings.NameValueTable.VALUE
            };
            int[] columnIds = new int[columns.length];
            for (int i = 0; i < columns.length; i++) {
                columnIds[i] = data.getColumnIndex(columns[i]);
            }

            while (!data.isAfterLast()) {
                ContentValues cv = new ContentValues();
                for (int i = 0; i < columns.length; i++) {
                    cv.put(columns[i], data.getString(columnIds[i]));
                }
                cvs.add(cv);
                data.moveToNext();
            }
            getActivity().getContentResolver().delete(mTargetUri, null, null);
            getActivity().getContentResolver().bulkInsert(mTargetUri, cvs.toArray(new ContentValues[1]));
            getLoaderManager().initLoader(1, null, this);
        } else {
            String[] from = new String[]{NameValueTable.NAME, NameValueTable.VALUE, NameValueTable.VALUE_OLD};
            int[] to = new int[]{R.id.name_textfield, R.id.value_current_textfield, R.id.value_previous_textfield};

            if (getListAdapter() == null) {
                setListAdapter(new SimpleCursorAdapter(getActivity(), R.layout.list_element, data, from, to, 0));
            } else {
                ((SimpleCursorAdapter) getListAdapter()).changeCursor(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = ((SimpleCursorAdapter) l.getAdapter()).getCursor();
        c.moveToPosition(position);
        if (mAlertDialog == null) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void setDisplayOnlyChanges(boolean onlyChanges) {
        if (onlyChanges != mOnlyChages) {
            Log.d("fragment", "change");
            mOnlyChages = onlyChanges;
            if (isAdded()) {
                Log.d("fragment", Boolean.toString(mOnlyChages));
                getLoaderManager().restartLoader(LOADER_PARSE, null, this);
            }
        }
    }

    public void takeSnapshot() {
        getActivity().getContentResolver().call(
                mTargetUri,
                SettingsProvider.METHOD_TAKE_SNAPSHOT,
                mTargetUri.toString(), null);
    }

    public void closeActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    private class MultiChoiceListener implements AbsListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }
}
