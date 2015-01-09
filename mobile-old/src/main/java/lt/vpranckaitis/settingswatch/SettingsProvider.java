package lt.vpranckaitis.settingswatch;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class SettingsProvider extends ContentProvider {
    private static SQLiteOpenHelper sOpenHelper;
    private static UriMatcher sUriMatcher;

    private static final int _SYSTEM = 0;
    private static final int _SYSTEM_ROW = 1;
    private static final int _SECURE = 2;
    private static final int _SECURE_ROW = 3;

    static {
	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	sUriMatcher.addURI(SettingsContract.AUTHORITY,
		SettingsContract.System.TABLE, _SYSTEM);
	sUriMatcher.addURI(SettingsContract.AUTHORITY,
		SettingsContract.System.TABLE + "/#", _SYSTEM_ROW);
	sUriMatcher.addURI(SettingsContract.AUTHORITY,
		SettingsContract.Secure.TABLE, _SECURE);
	sUriMatcher.addURI(SettingsContract.AUTHORITY,
		SettingsContract.Secure.TABLE + "/#", _SECURE_ROW);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
	String table = null;
	switch (sUriMatcher.match(uri)) {
	case _SYSTEM:
	    table = SettingsContract.System.TABLE;
	    break;

	case _SYSTEM_ROW:
	    table = SettingsContract.System.TABLE;
	    selection += " " + SettingsContract.System._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	case _SECURE:
	    table = SettingsContract.Secure.TABLE;
	    break;

	case _SECURE_ROW:
	    table = SettingsContract.Secure.TABLE;
	    selection += " " + SettingsContract.Secure._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri.toString());
	}
	if (table != null) {
	    int rowsAffected = sOpenHelper.getWritableDatabase().delete(table,
		    selection, selectionArgs);
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsAffected;
	} else
	    return 0;
    }

    @Override
    public String getType(Uri uri) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
	String table = null;
	switch (sUriMatcher.match(uri)) {
	case _SYSTEM:
	    table = SettingsContract.System.TABLE;
	    break;

	case _SECURE:
	    table = SettingsContract.Secure.TABLE;
	    break;

	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri.toString());
	}
	if (table != null) {
	    long id = sOpenHelper.getWritableDatabase().insert(table, null,
		    values);
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.withAppendedPath(uri, Long.toString(id));
	} else
	    return null;
    }

    @Override
    public boolean onCreate() {
	// TODO Auto-generated method stub
	sOpenHelper = new DatabaseHelper(getContext());
	return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
	    String[] selectionArgs, String sortOrder) {
	String table = null;
	switch (sUriMatcher.match(uri)) {
	case _SYSTEM:
	    table = SettingsContract.System.TABLE;
	    break;

	case _SYSTEM_ROW:
	    table = SettingsContract.System.TABLE;
	    selection += " " + SettingsContract.System._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	case _SECURE:
	    table = SettingsContract.Secure.TABLE;
	    break;

	case _SECURE_ROW:
	    table = SettingsContract.Secure.TABLE;
	    selection += " " + SettingsContract.Secure._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri.toString());
	}
	if (table != null) {
	    Cursor c = sOpenHelper.getWritableDatabase()
		    .query(table, projection, selection, selectionArgs, null,
			    null, sortOrder);
	    c.setNotificationUri(getContext().getContentResolver(), uri);
	    // getContext().getContentResolver().notifyChange(uri, null);
	    return c;
	} else
	    return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
	    String[] selectionArgs) {
	String table = null;
	switch (sUriMatcher.match(uri)) {
	case _SYSTEM:
	    table = SettingsContract.System.TABLE;
	    break;

	case _SYSTEM_ROW:
	    table = SettingsContract.System.TABLE;
	    selection += " " + SettingsContract.System._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	case _SECURE:
	    table = SettingsContract.Secure.TABLE;
	    break;

	case _SECURE_ROW:
	    table = SettingsContract.Secure.TABLE;
	    selection += " " + SettingsContract.Secure._ID + " = "
		    + uri.getLastPathSegment();
	    break;

	default:
	    throw new IllegalArgumentException("Unknown URI: " + uri.toString());
	}
	if (table != null) {
	    int rowsAffected = sOpenHelper.getWritableDatabase().update(table,
		    values, selection, selectionArgs);
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsAffected;
	} else
	    return 0;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "settingswatch.db";

	private static final String SYSTEM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
		+ SettingsContract.System.TABLE
		+ " ("
		+ SettingsContract.System._ID
		+ " INTEGER, "
		+ SettingsContract.System.NAME
		+ " TEXT, "
		+ SettingsContract.System.VALUE + " TEXT" + ")";

	private static final String SECURE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
		+ SettingsContract.Secure.TABLE
		+ " ("
		+ SettingsContract.Secure._ID
		+ " INTEGER, "
		+ SettingsContract.Secure.NAME
		+ " TEXT, "
		+ SettingsContract.Secure.VALUE + " TEXT" + ")";

	public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    // TODO Auto-generated method stub
	    db.execSQL(SYSTEM_TABLE_SQL);
	    db.execSQL(SECURE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    // TODO Auto-generated method stub
	    db.execSQL("DROP TABLE " + SettingsContract.System.TABLE);
	    db.execSQL("DROP TABLE " + SettingsContract.Secure.TABLE);
	    onCreate(db);
	}

    }

}
