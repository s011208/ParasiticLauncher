package yhh.bj4.parasitic.launcher.loader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Trace;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class LoadIconHelper implements Runnable, LoadSingleIconTask.Callback {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_TRACE = true;
    private static final String TAG = "IconLoader";
    private final WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;
    private final HashMap<ComponentName, ActivityInfoCache> mActivityInfoCache;
    private AtomicInteger mRemainTasks = new AtomicInteger(0);

    public LoadIconHelper(Context context, HashMap<ComponentName, ActivityInfoCache> activityInfoCache, Callback cb) {
        mContext = new WeakReference<>(context);
        mActivityInfoCache = activityInfoCache;
        mCallback = new WeakReference<>(cb);
    }

    public interface Callback {
        void onFinishLoading();
    }

    @Override
    public void run() {
        final Context context = mContext.get();
        if (context == null) return;
        final Intent queryMainIntent = new Intent(Intent.ACTION_MAIN);
        queryMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> apps = pm.queryIntentActivities(queryMainIntent, 0);
        mRemainTasks.set(apps.size());
        long time = System.currentTimeMillis();
        if (DEBUG) {
            Log.d(TAG, "LoadingIconHelper get system app size: " + mRemainTasks.get());
        }
        if (DEBUG_TRACE) {
            Trace.beginSection("iterate apps");
        }
        for (ResolveInfo appInfo : apps) {
            new Thread(new LoadSingleIconTask(pm, appInfo, LoadIconHelper.this)).start();
        }
        while (mRemainTasks.get() != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (DEBUG_TRACE) {
            Trace.endSection();
        }
        time = System.currentTimeMillis() - time;
        if (DEBUG) {
            Log.d(TAG, "LoadingIconHelper get system app done, takes: " + time);
        }
        final Callback cb = mCallback.get();
        if (cb != null) {
            cb.onFinishLoading();
        }
    }

    @Override
    public void onFinishLoading(ComponentName cn, ActivityInfoCache icon) {
        int current = mRemainTasks.decrementAndGet();
        if (DEBUG) {
            Log.d(TAG, "mRemainTasks: " + current);
        }
        if (mActivityInfoCache == null) return;
        if (cn == null || icon == null) return;
        synchronized (mActivityInfoCache) {
            mActivityInfoCache.put(cn, icon);
        }
    }
}