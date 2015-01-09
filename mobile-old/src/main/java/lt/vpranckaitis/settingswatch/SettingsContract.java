package lt.vpranckaitis.settingswatch;

import android.net.Uri;
import android.provider.Settings;

public class SettingsContract {
    public static final String AUTHORITY = "lt.vpranckaitis.settingswatch.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static class System extends Settings.NameValueTable {
	public static final String TABLE = "system";
	public static final Uri CONTENT_URI = Uri.withAppendedPath(
		AUTHORITY_URI, TABLE);

    }

    public static class Secure extends Settings.NameValueTable {
	public static final String TABLE = "secure";
	public static final Uri CONTENT_URI = Uri.withAppendedPath(
		AUTHORITY_URI, TABLE);
    }
}
