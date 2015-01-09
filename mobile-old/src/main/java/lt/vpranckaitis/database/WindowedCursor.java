package lt.vpranckaitis.database;

import android.database.AbstractWindowedCursor;
import android.database.CursorWindow;

public class WindowedCursor extends AbstractWindowedCursor {
    private String[] mColumnNames;

    public WindowedCursor(CursorWindow window, String[] columns) {
        setWindow(window);
        mColumnNames = columns;
    }

    @Override
    public String[] getColumnNames() {
        return mColumnNames;
    }

    @Override
    public int getCount() {
        return getWindow().getNumRows();
    }
}
