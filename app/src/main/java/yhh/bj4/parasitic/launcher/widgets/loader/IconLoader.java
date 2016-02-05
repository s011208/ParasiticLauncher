package yhh.bj4.parasitic.launcher.widgets.loader;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;


/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class IconLoader {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_TRACE = true;
    private static final String TAG = "IconLoader";
    private static final HandlerThread sIconLoaderThread = new HandlerThread("Icon loader", android.os.Process.THREAD_PRIORITY_DEFAULT);

    static {
        sIconLoaderThread.start();
    }

    private static Handler sWorker = new Handler(sIconLoaderThread.getLooper());

    private static IconLoader sInstance;

    private final Context mContext;

    private final HashMap<ComponentName, ActivityInfoCache> mActivityInfoCache = new HashMap<>();

    public synchronized static IconLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconLoader(context);
        }
        return sInstance;
    }

    private IconLoader(Context context) {
        mContext = context.getApplicationContext();
        startToLoadIcon();
    }

    private void startToLoadIcon() {
        sWorker.post(new LoadingIconHelper(mContext, mActivityInfoCache));
    }
}
