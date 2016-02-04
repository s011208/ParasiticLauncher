package yhh.bj4.parasitic.launcher.widgets.utils;

import android.content.Context;

/**
 * Created by yenhsunhuang on 2016/2/4.
 */
public class StoreWidgetHelper {
    private static StoreWidgetHelper sInstance = null;

    private final Context mContext;

    public synchronized static final StoreWidgetHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StoreWidgetHelper(context);
        }
        return sInstance;
    }

    private StoreWidgetHelper(Context context) {
        mContext = context;
    }
}
