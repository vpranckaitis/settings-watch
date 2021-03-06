package lt.vpranckaitis.settingswatch;

import android.net.Uri;
import android.provider.Settings;

public class SettingsContract {
    public static final String AUTHORITY = "lt.vpranckaitis.settingswatch.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static class NameValueTable extends Settings.NameValueTable {
        public static final String VALUE_OLD = "valueOld";
    }

    public static class System extends NameValueTable {
        public static final String TABLE = "system";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                AUTHORITY_URI, TABLE);

    }

    public static class Secure extends NameValueTable {
        public static final String TABLE = "secure";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                AUTHORITY_URI, TABLE);
    }

    public static class Global extends NameValueTable {
        public static final String TABLE = "global";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                AUTHORITY_URI, TABLE);
    }
}
