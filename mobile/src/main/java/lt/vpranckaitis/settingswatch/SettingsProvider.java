package lt.vpranckaitis.settingswatch;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import static lt.vpranckaitis.settingswatch.SettingsContract.NameValueTable;

public class SettingsProvider extends ContentProvider {

    public static final String METHOD_TAKE_SNAPSHOT = "takeSnapshot";

    private static SQLiteOpenHelper sOpenHelper;
    private static UriMatcher sUriMatcher;

    private static final int _ROW_MASK = 1;
    private static final int _SYSTEM = 1 << 1;
    private static final int _SYSTEM_ROW = _SYSTEM | _ROW_MASK;
    private static final int _SECURE = 1 << 2;
    private static final int _SECURE_ROW = _SECURE | _ROW_MASK;
    private static final int _GLOBAL = 1 << 3;
    private static final int _GLOBAL_ROW = _GLOBAL | _ROW_MASK;

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
        sUriMatcher.addURI(SettingsContract.AUTHORITY,
                SettingsContract.Global.TABLE, _GLOBAL);
        sUriMatcher.addURI(SettingsContract.AUTHORITY,
                SettingsContract.Global.TABLE + "/#", _GLOBAL_ROW);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = null;
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode & (~_ROW_MASK)) {
            case _SYSTEM:
                table = SettingsContract.System.TABLE;
                break;
            case _SECURE:
                table = SettingsContract.Secure.TABLE;
                break;
            case _GLOBAL:
                table = SettingsContract.Global.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        if ((uriCode & _ROW_MASK) == 1) {
            selection += " " + SettingsContract.System._ID + " = "
                    + uri.getLastPathSegment();
        }

        if (table != null) {
            int rowsAffected = sOpenHelper.getWritableDatabase().delete(table,
                    selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsAffected;
        } else {
            return 0;
        }
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
            case _GLOBAL:
                table = SettingsContract.Global.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        if (table != null) {
            long id = sOpenHelper.getWritableDatabase().insert(table, null,
                    values);
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.withAppendedPath(uri, Long.toString(id));
        } else {
            return null;
        }
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
        String table;
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode & (~_ROW_MASK)) {
            case _SYSTEM:
                table = SettingsContract.System.TABLE;
                break;
            case _SECURE:
                table = SettingsContract.Secure.TABLE;
                break;
            case _GLOBAL:
                table = SettingsContract.Global.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        if ((uriCode & _ROW_MASK) == 1) {
            selection += " " + SettingsContract.System._ID + " = "
                    + uri.getLastPathSegment();
        }
        if (table != null) {
            String virtualTable = String.format("SELECT %1$s.%3$s, %1$s.%4$s, %1$s.%5$s, IFNULL(%2$s.%5$s, '-') AS %6$s FROM %1$s LEFT JOIN %2$s ON %1$s.%4$s = %2$s.%4$s",
                    table, table + DatabaseHelper.SNAPSHOT_POSTFIX, NameValueTable._ID, NameValueTable.NAME, NameValueTable.VALUE, NameValueTable.VALUE_OLD);
            Cursor c = sOpenHelper.getWritableDatabase()
                    .query("(" + virtualTable + ")", projection, selection, selectionArgs, null,
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
        int uriCode = sUriMatcher.match(uri);
        switch (uriCode & (~_ROW_MASK)) {
            case _SYSTEM:
                table = SettingsContract.System.TABLE;
                break;
            case _SECURE:
                table = SettingsContract.Secure.TABLE;
                break;
            case _GLOBAL:
                table = SettingsContract.Global.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }

        if ((uriCode & _ROW_MASK) == 1) {
            selection += " " + SettingsContract.System._ID + " = "
                    + uri.getLastPathSegment();
        }
        if (table != null) {
            int rowsAffected = sOpenHelper.getWritableDatabase().update(table,
                    values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsAffected;
        } else {
            return 0;
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (method.equals(METHOD_TAKE_SNAPSHOT)) {
            Uri uri = Uri.parse(arg);
            int uriCode = sUriMatcher.match(Uri.parse(arg));
            String table = null;
            switch(uriCode & (~_ROW_MASK)) {
                case _SYSTEM:
                    table = SettingsContract.System.TABLE;
                    break;
                case _SECURE:
                    table = SettingsContract.Secure.TABLE;
                    break;
                case _GLOBAL:
                    table = SettingsContract.Global.TABLE;
                    break;
            }
            String selection = null;
            if ((uriCode & _ROW_MASK) != 0) {
                selection = NameValueTable._ID + " = " + uri.getLastPathSegment();
            }
            if (table != null) {
                String columns = NameValueTable._ID + ", "
                        + NameValueTable.NAME + ", "
                        + NameValueTable.VALUE;
                String innerSelect = " SELECT " + columns + " FROM " + table
                        + (selection != null ? " WHERE " + selection : "");
                String query = "INSERT INTO " + table + DatabaseHelper.SNAPSHOT_POSTFIX + innerSelect;
                Log.d("Provider", "Query: " + query);
                sOpenHelper.getWritableDatabase().execSQL(query);
                getContext().getContentResolver().notifyChange(uri, null);
                return new Bundle();
            }
        }
        return super.call(method, arg, extras);
    }

    @Override
     public int bulkInsert(Uri uri, ContentValues[] values) {
        String table = null;
        Log.w("Provider", "bulk insert");
        switch (sUriMatcher.match(uri)) {
            case _SYSTEM:
                table = SettingsContract.System.TABLE;
                break;
            case _SECURE:
                table = SettingsContract.Secure.TABLE;
                break;
            case _GLOBAL:
                table = SettingsContract.Global.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri.toString());
        }
        Log.w("Provider", table);
        if (table != null) {
            SQLiteDatabase db = sOpenHelper.getWritableDatabase();
            String sql = "INSERT INTO "+ table +" ("
                    + NameValueTable._ID + ", "
                    + NameValueTable.NAME + ", "
                    + NameValueTable.VALUE + ") VALUES (?, ?, ?);";
            SQLiteStatement statement = db.compileStatement(sql);
            db.beginTransaction();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getAsString(NameValueTable.VALUE) != null) {
                    statement.clearBindings();
                    statement.bindString(1, values[i].getAsString(NameValueTable._ID));
                    statement.bindString(2, values[i].getAsString(NameValueTable.NAME));
                    statement.bindString(3, values[i].getAsString(NameValueTable.VALUE));
                    statement.execute();
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            getContext().getContentResolver().notifyChange(uri, null);
            return values.length;
        }
        return 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 7;
        private static final String DATABASE_NAME = "settingswatch.db";
        private static final String SNAPSHOT_POSTFIX = "_snapshot";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(createTableQuery(SettingsContract.System.TABLE));
            db.execSQL(createTableQuery(SettingsContract.Secure.TABLE));
            db.execSQL(createTableQuery(SettingsContract.Global.TABLE));

            db.execSQL(createTableQuery(SettingsContract.System.TABLE + SNAPSHOT_POSTFIX));
            db.execSQL(createTableQuery(SettingsContract.Secure.TABLE + SNAPSHOT_POSTFIX));
            db.execSQL(createTableQuery(SettingsContract.Global.TABLE + SNAPSHOT_POSTFIX));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.System.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.Secure.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.Global.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.System.TABLE + SNAPSHOT_POSTFIX);
            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.Secure.TABLE + SNAPSHOT_POSTFIX);
            db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.Global.TABLE + SNAPSHOT_POSTFIX);

            onCreate(db);
        }

        private static String createTableQuery(String tableName) {
            return "CREATE TABLE IF NOT EXISTS "
                    + tableName
                    + " ("
                    + NameValueTable._ID
                    + " INTEGER, "
                    + NameValueTable.NAME
                    + " TEXT UNIQUE ON CONFLICT REPLACE, "
                    + NameValueTable.VALUE + " TEXT" + ")";
        }
    }
}
