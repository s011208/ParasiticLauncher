package yhh.bj4.parasitic.launcher.loader;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPack;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;

/**
 * Created by yenhsunhuang on 2016/2/14.
 */
public class IconLoader implements IconPackHelper.Callback {
    private static final String TAG = "IconLoader";
    private static final boolean DEBUG = true;
    public static final String ICON_PACK_DEFAULT = "icon_pack_default";
    private static int sDefaultIconSize;
    private static IconLoader sInstance;

    public synchronized static IconLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconLoader(context);
        }
        return sInstance;
    }

    public static Bitmap convertDrawableIconToBitmap(Drawable d) {
        Canvas canvas = new Canvas();
        d.setBounds(0, 0, sDefaultIconSize, sDefaultIconSize);
        Bitmap b = Bitmap.createBitmap(sDefaultIconSize, sDefaultIconSize, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        d.draw(canvas);
        canvas.setBitmap(null);
        return b;
    }

    private static final HandlerThread sIconLoaderThread = new HandlerThread("Icon loader", android.os.Process.THREAD_PRIORITY_DEFAULT);

    static {
        sIconLoaderThread.start();
    }

    public interface Callback {
        void onStartLoadingPackageName(String pkg);

        void onFinishLoadingPackageName(String pkg);
    }

    private static class LoadTask implements Runnable {
        private final Context mContext;
        private final IconLoader mIconLoader;
        private final String mLoadPkgName;

        public LoadTask(Context context, IconLoader lh, String lp) {
            mContext = context;
            mIconLoader = lh;
            mLoadPkgName = lp;
        }

        @Override
        public void run() {
            if (DEBUG) {
                Log.d(TAG, "start LoadTask with icon pack: " + mLoadPkgName);
            }
            final ArrayList<Callback> callbacks = new ArrayList<>();
            synchronized (mIconLoader.mCallbacks) {
                callbacks.addAll(mIconLoader.mCallbacks);
            }
            mIconLoader.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    for (Callback cb : callbacks) {
                        cb.onStartLoadingPackageName(mLoadPkgName);
                    }
                }
            });
            final Intent queryMainIntent = new Intent(Intent.ACTION_MAIN);
            queryMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = mContext.getPackageManager();
            final HashMap<ComponentName, InfoCache> iconMap = new HashMap<>();
            final List<ResolveInfo> apps = pm.queryIntentActivities(queryMainIntent, 0);
            for (ResolveInfo appInfo : apps) {
                InfoCache icon;
                ComponentName cn;
                cn = new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name);
                if (pm == null || appInfo == null || appInfo.activityInfo == null) {
                    return;
                }
                Drawable activityIcon;
                if (IconLoader.ICON_PACK_DEFAULT.equals(mLoadPkgName)) {
                    activityIcon = appInfo.activityInfo.loadIcon(pm);
                } else {
                    try {
                        if (mContext == null || mIconLoader == null) {
                            activityIcon = appInfo.activityInfo.loadIcon(pm);
                        } else {
                            int iconId = IconPackHelper.getInstance(mContext).getIconResIdViaComponentInfo("ComponentInfo{" + cn.flattenToString() + "}", mLoadPkgName);
                            if (iconId != -1) {
                                activityIcon = IconPackHelper.getInstance(mContext).getSpecIconPackIcon(mContext, mLoadPkgName, cn, mIconLoader);
                            } else {
                                activityIcon = IconPackHelper.getInstance(mContext).getUnspecIconForDefault(mContext, appInfo.activityInfo.loadIcon(pm), mLoadPkgName);
                            }
                        }
                    } catch (Exception e) {
                        activityIcon = appInfo.activityInfo.loadIcon(pm);
                    }
                }
                String title = appInfo.loadLabel(pm).toString();

                icon = new ActivityInfoCache();
                icon.setIcon(activityIcon);
                icon.setTitle(title);
                icon.setBitmap(IconLoader.convertDrawableIconToBitmap(activityIcon));
                icon.setComponentName(cn);
                iconMap.put(cn, icon);
            }
            mIconLoader.mActivityInfoCache.put(mLoadPkgName, iconMap);
            mIconLoader.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    for (Callback cb : callbacks) {
                        cb.onFinishLoadingPackageName(mLoadPkgName);
                    }
                }
            });
        }
    }

    private final Context mContext;
    private Handler mWorker = new Handler(sIconLoaderThread.getLooper());
    private final HashMap<String, HashMap<ComponentName, InfoCache>> mActivityInfoCache = new HashMap<>();
    private Handler mHandler = new Handler();
    private int mIconDpi;
    private final Bitmap mDefaultIcon;
    private final IconPackHelper mIconPackHelper;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private final ArrayList<String> mReloadIconPackList = new ArrayList<>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                onPackageChanged(intent.getData().toString());
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                onPackageChanged(intent.getData().toString());
            } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                onPackageChanged(intent.getData().toString());
            }
        }
    };

    private IconLoader(Context context) {
        mContext = context.getApplicationContext();
        registerReceiver();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        mDefaultIcon = makeDefaultIcon();
        sDefaultIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.normal_icon_size);
        mIconPackHelper = IconPackHelper.getInstance(mContext);
        mIconPackHelper.addCallback(this);
        loadAllIconPackList();
        loadDefaultIcons();
    }

    private void loadDefaultIcons() {
        mWorker.post(new LoadTask(mContext, IconLoader.this, ICON_PACK_DEFAULT));
    }

    private void loadAllIconPackList() {
        mWorker.post(new Runnable() {
            @Override
            public void run() {
                mIconPackHelper.reloadAllIconPackList();
            }
        });
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mReceiver, filter);
    }

    private void runOnMainThread(Runnable task) {
        if (android.os.Process.myTid() == sIconLoaderThread.getThreadId()) {
            mHandler.post(task);
        } else {
            task.run();
        }
    }

    public void addCallback(Callback cb) {
        synchronized (mCallbacks) {
            while (mCallbacks.contains(cb))
                mCallbacks.remove(cb);
            mCallbacks.add(cb);
        }
    }

    public void removeCallback(Callback cb) {
        synchronized (mCallbacks) {
            while (mCallbacks.contains(cb))
                mCallbacks.remove(cb);
        }
    }

    public HashMap<ComponentName, InfoCache> getAllActivitiesInfoCache(String iconPackPkg) {
        if (mActivityInfoCache.get(iconPackPkg) == null)
            return new HashMap<>();
        return new HashMap<>(mActivityInfoCache.get(iconPackPkg));
    }

    public HashMap<ComponentName, InfoCache> requestToLoadIconPack(String pkgName, boolean forceReload) {
        if (!forceReload) {
            synchronized (mActivityInfoCache) {
                if (mActivityInfoCache.containsKey(pkgName)) {
                    if (DEBUG) Log.d(TAG, "return loaded data");
                    return new HashMap<>(mActivityInfoCache.get(pkgName));
                }
            }
        }
        if (DEBUG) Log.d(TAG, "return unloaded data and start reload");
        reloadIconPack(pkgName);
        return new HashMap<>();
    }

    private void reloadIconPack(final String pkgName) {
        if (ICON_PACK_DEFAULT.equals(pkgName)) {
            loadDefaultIcons();
        } else {
            mWorker.post(new Runnable() {
                @Override
                public void run() {
                    mIconPackHelper.loadIconPackContent(pkgName);
                }
            });
        }
    }

    private void onPackageChanged(String pkgName) {
        synchronized (mReloadIconPackList) {
            while (mReloadIconPackList.contains(pkgName)) {
                mReloadIconPackList.remove(pkgName);
            }
            mReloadIconPackList.add(pkgName);
        }
        loadAllIconPackList();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon, android.os.Process.myUserHandle());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable getFullResIcon(Resources resources, int iconId, UserHandle user) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }
        if (d == null) {
            d = getFullResDefaultActivityIcon();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (user == null) {
                return d;
            } else {
                return mContext.getPackageManager().getUserBadgedIcon(d, user);
            }
        } else {
            return d;
        }
    }

    public Drawable getFullResIcon(String packageName, int iconId, UserHandle user) {
        Resources resources;
        try {
            resources = mContext.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId, user);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info, UserHandle user) {
        return getFullResIcon(info.activityInfo, user);
    }

    public Drawable getFullResIcon(ActivityInfo info, UserHandle user) {
        Resources resources;
        try {
            resources = mContext.getPackageManager().getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId, user);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }


    @Override
    public void onIconPackListLoadStart() {

    }

    @Override
    public void onIconPackListLoadFinish() {
        synchronized (mReloadIconPackList) {
            for (String pkg : mReloadIconPackList) {
                for (IconPack iconPack : mIconPackHelper.getIconPackList()) {
                    if (pkg.equals(iconPack.getIconPackPackageName())) {
                        reloadIconPack(pkg);
                        mReloadIconPackList.remove(pkg);
                    }
                }
            }
        }
    }

    @Override
    public void onStartToLoadIconPackContent(String iconPackPkgName) {

    }

    @Override
    public void onFinishToLoadIconPackContent(String iconPackPkgName) {
        if (DEBUG) {
            Log.d(TAG, "onFinishToLoadIconPackContent");
        }
        mWorker.post(new LoadTask(mContext, IconLoader.this, iconPackPkgName));
    }
}
