package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsWidgetService extends RemoteViewsService {
    private static final String TAG = "AllappsWidgetService";
    private static final boolean DEBUG = true;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (DEBUG) {
            Log.d(TAG, "onGetViewFactory with intent: " + intent.toUri(0));
        }
        return (new AllappsListProvider(getApplicationContext(), intent));
    }
}
