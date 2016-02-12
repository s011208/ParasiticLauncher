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

import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class LoadIconHelper implements Runnable, LoadSingleIconTask.Callback, IconPackHelper.Callback {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_TRACE = true;
    private static final String TAG = "IconLoader";
    private final WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;
    private final WeakReference<IconLoader> mIconLoader;
    private final HashMap<ComponentName, InfoCache> mActivityInfoCache;
    private AtomicInteger mRemainTasks = new AtomicInteger(0);
    private final String mIconPackageName;
    private final IconPackHelper mIconPackHelper;
    private boolean mWaitingForIconPackLoading = false;

    public LoadIconHelper(Context context, String iconPackageName, HashMap<ComponentName, InfoCache> activityInfoCache
            , Callback cb, IconLoader loader) {
        mContext = new WeakReference<>(context);
        mActivityInfoCache = activityInfoCache;
        mCallback = new WeakReference<>(cb);
        mIconLoader = new WeakReference<>(loader);
        mIconPackageName = iconPackageName;
        mIconPackHelper = IconPackHelper.getInstance(context);
        mIconPackHelper.addCallback(this);
    }

    public void destroy() {
        mIconPackHelper.removeCallback(this);
    }

    @Override
    public void onIconPackLoadStart() {

    }

    @Override
    public void onIconPackLoadFinish() {

    }

    @Override
    public void onStartToLoadIconPackContent(String iconPackPkgName) {
        if (iconPackPkgName.equals(mIconPackageName)) {
            mWaitingForIconPackLoading = true;
        }
    }

    @Override
    public void onFinishToLoadIconPackContent(String iconPackPkgName) {
        if (iconPackPkgName.equals(mIconPackageName)) {
            mWaitingForIconPackLoading = false;
        }
    }

    public interface Callback {
        void onIconFinishLoading(String iconPackPkgName);
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
        if (mIconPackHelper.hasIconPackLoaded(mIconPackageName) == false) {
            mIconPackHelper.loadIconPackContent(mIconPackageName);
        }
        while (mWaitingForIconPackLoading) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (ResolveInfo appInfo : apps) {
            new Thread(new LoadSingleIconTask(mContext.get(), pm, appInfo, LoadIconHelper.this, mIconPackageName, mIconLoader.get())).start();
        }
        while (mRemainTasks.get() != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final Callback cb = mCallback.get();
        if (cb != null) {
            cb.onIconFinishLoading(mIconPackageName);
        }
        destroy();
        if (DEBUG_TRACE) {
            Trace.endSection();
        }
        if (DEBUG) {
            Log.d(TAG, "LoadingIconHelper get system app done, takes: " + (System.currentTimeMillis() - time));
        }
    }

    @Override
    public void onIconFinishLoading(ComponentName cn, InfoCache icon) {
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