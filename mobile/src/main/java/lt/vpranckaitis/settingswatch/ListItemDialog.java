package lt.vpranckaitis.settingswatch;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by vpran_000 on 2015-01-09.
 */
public class ListItemDialog extends AlertDialog {

    protected ListItemDialog(Context context) {
        super(context);
    }

    protected ListItemDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ListItemDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
