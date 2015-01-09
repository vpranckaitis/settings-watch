package lt.vpranckaitis.settingswatch.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.CursorWindow;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import lt.vpranckaitis.database.WindowedCursor;
import lt.vpranckaitis.settingswatch.R;

public class DifferenceList extends AbstractList implements
        LoaderCallbacks<Cursor>, OnClickListener, OnShowListener {
    private static final String TAG = DifferenceList.class.getName();

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VALUE_LEFT = "value_left";
    private static final String COLUMN_VALUE_RIGHT = "value_right";
    private static final int COLUMNS = 4;

    private static final int IDX_COLUMN_ID = 0;
    private static final int IDX_COLUMN_NAME = 1;
    private static final int IDX_COLUMN_VALUE_LEFT = 2;
    private static final int IDX_COLUMN_VALUE_RIGHT = 3;

    public static final int ID_CURSOR_LEFT = 1;
    public static final int ID_CURSOR_RIGHT = 2;

    private static final String STATE_DIALOG = "dialogIsShown";
    private static final String STATE_NAME = "mName";
    private static final String STATE_VALUE_LEFT = "valueLeft";
    private static final String STATE_VALUE_RIGHT = "valueRight";
    private static final String STATE_SCROLL = "scroll";

    private Cursor mCursorLeft;
    private Cursor mCursorRight;

    protected String[] mColumnsLeft; // current
    protected String[] mColumnsRight; // previous

    private AlertDialog mDialog;
    private TextView mTextView1;
    private TextView mTextView2;
    private String mName;

    private InputMethodManager mInputManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdOffset = 4;
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_DIALOG, false)) {
                if (mDialog == null) {
                    mDialog = createDialog();
                    mName = savedInstanceState.getString(STATE_NAME);
                    mDialog.setTitle(mName);
                    String value = savedInstanceState
                            .getString(STATE_VALUE_LEFT);
                    mTextView1.setText(value);
                    value = savedInstanceState.getString(STATE_VALUE_RIGHT);
                    mTextView2.setText(value);
                    mDialog.show();
                }
            }
        }
        setEmptyText(getString(R.string.no_differences));

    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ID_CURSOR_LEFT) {
            mCursorLeft = data;
        } else if (loader.getId() == ID_CURSOR_RIGHT) {
            mCursorRight = data;
        }

        if (mCursorLeft == null) {
            getLoaderManager().initLoader(ID_CURSOR_LEFT, null, this);
        } else if (mCursorRight == null) {
            getLoaderManager().initLoader(ID_CURSOR_RIGHT, null, this);
        } else {
            Cursor c = null;
            if (mCursorRight.getCount() != 0) {
                c = makeCursor(mCursorLeft, mCursorRight);
            } else {
                setEmptyText(getString(R.string.no_stored_values));
            }
            if (getListAdapter() == null) {
                int layout = R.layout.list_content_difference;
                String[] from = {COLUMN_NAME, COLUMN_VALUE_LEFT,
                        COLUMN_VALUE_RIGHT};
                int[] to = {R.id.name, R.id.value_left, R.id.value_right};
                CursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                        layout, c, from, to,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                setListAdapter(adapter);
            } else {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (mDialog == null) {
            mDialog = createDialog();
        }

        Cursor currentSelection = ((Cursor) l.getAdapter().getItem(position));
        String text1 = currentSelection.getString(currentSelection
                .getColumnIndex(COLUMN_VALUE_LEFT));
        String text2 = currentSelection.getString(currentSelection
                .getColumnIndex(COLUMN_VALUE_RIGHT));
        String title = currentSelection.getString(currentSelection
                .getColumnIndex(COLUMN_NAME));
        mName = title;
        mTextView1.setText(text1);
        mTextView2.setText(text2);
        mDialog.setTitle(title);
        mDialog.show();
    }

    protected AlertDialog createDialog() {
        AlertDialog.Builder dialogBuilder = new Builder(getActivity());
        if (isEditable()) {
            dialogBuilder.setPositiveButton(
                    R.string.dialog_edit_value_positive, this);
            dialogBuilder.setNeutralButton(R.string.dialog_edit_value_neutral,
                    this);
        }
        dialogBuilder.setNegativeButton(R.string.dialog_edit_value_negative,
                this);
        View v = LayoutInflater.from(getActivity()).inflate(mDialogLayoutId,
                null);
        mTextView1 = (TextView) v.findViewById(R.id.value_left);
        mTextView2 = (TextView) v.findViewById(R.id.value_right);
        dialogBuilder.setView(v);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(this);
        setNoReturn(true);
        if (isEditable()) {
            if (mInputManager == null)
                mInputManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
            ((AlertDialog) dialog).getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (mDialog != null) {
            outState.putBoolean(STATE_DIALOG, mDialog.isShowing());
            mDialog.dismiss();
            outState.putString(STATE_NAME, mName);
            outState.putString(STATE_VALUE_LEFT, mTextView1.getText()
                    .toString());
            outState.putString(STATE_VALUE_RIGHT, mTextView2.getText()
                    .toString());
            // outState.putInt(STATE_SCROLL,
            // getListView().getFirstVisiblePosition());
        }

    }

    private Cursor makeCursor(Cursor left, Cursor right) {
        CursorWindow cw = new CursorWindow(true);
        cw.setNumColumns(COLUMNS);

        if (mColumnsLeft == null) {
            mColumnsLeft = new String[]{"_id"};
        }
        if (mColumnsRight == null) {
            mColumnsRight = new String[]{"_id"};
        }
        CursorJoiner cj = new CursorJoiner(left, mColumnsLeft, right,
                mColumnsRight);
        int k = 0;
        String na = getString(R.string.no_value);
        for (CursorJoiner.Result r : cj) {
            String name = "";
            String valueLeft = na;
            String valueRight = na;
            switch (r) {
                case LEFT:
                    name = left.getString(1);
                    valueLeft = left.getString(2);
                    break;
                case RIGHT:
                    name = right.getString(1);
                    valueRight = right.getString(2);
                    break;
                case BOTH:
                    name = left.getString(1);
                    valueLeft = left.getString(2);
                    valueRight = right.getString(2);
                    break;
            }
            if (valueLeft.compareTo(valueRight) != 0) {
                cw.allocRow();
                cw.putLong(k, k, IDX_COLUMN_ID);
                cw.putString(name, k, IDX_COLUMN_NAME);
                cw.putString(valueLeft, k, IDX_COLUMN_VALUE_LEFT);
                cw.putString(valueRight, k, IDX_COLUMN_VALUE_RIGHT);
                k++;
            }
        }

        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_VALUE_LEFT,
                COLUMN_VALUE_RIGHT};
        return new WindowedCursor(cw, columns);
    }

    public void onShow(DialogInterface dialog) {
        if (isEditable()) {
            ((EditText) mTextView1).selectAll();
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Settings.System.putString(getActivity().getContentResolver(),
                    mName, mTextView1.getText().toString());
        } else if (which == Dialog.BUTTON_NEUTRAL) {
            Settings.System.putString(getActivity().getContentResolver(),
                    mName, mTextView2.getText().toString());
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    public void onLoaderReset(Loader<Cursor> loader) {

    }

    protected void deleteAll(Uri uri) {
        new AsyncDelete().execute(uri);
    }

    private class AsyncDelete extends AsyncTask<Uri, Void, Void> {

        @Override
        protected Void doInBackground(Uri... params) {
            ContentResolver cr = getActivity().getContentResolver();
            cr.delete(params[0], null, null);
            return null;
        }

    }
}
