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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPack;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;


/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class IconLoader implements LoadIconHelper.Callback, IconPackHelper.Callback {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_TRACE = true;
    private static final String TAG = "IconLoader";
    public static final int ICON_TYPE_NORMAL = 1 << 1;
    private int mIconDpi;
    private final Bitmap mDefaultIcon;
    private static final HandlerThread sIconLoaderThread = new HandlerThread("Icon loader", android.os.Process.THREAD_PRIORITY_DEFAULT);
    public static final String ICON_PACK_DEFAULT = "icon_pack_default";

    static {
        sIconLoaderThread.start();
    }

    private static Handler sWorker = new Handler(sIconLoaderThread.getLooper());

    private static IconLoader sInstance;

    private final Context mContext;

    private final HashMap<String, HashMap<ComponentName, InfoCache>> mActivityInfoCache = new HashMap<>();

    private static int sDefaultIconSize;
    private final ArrayList<WeakReference<Callback>> mCallbacks = new ArrayList<>();
    private LoadIconHelper mLoadIconHelper;

    private Handler mHandler = new Handler();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
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

    private void onPackageChanged(final String loadedPkg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IconPackHelper.getInstance(mContext).reloadAllIconPackList();
                if (mActivityInfoCache.get(loadedPkg) == null) return;
                requestToLoadIconPack(loadedPkg, true);
                startToLoadIcon(loadedPkg);
            }
        }).start();
    }

    public synchronized static IconLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconLoader(context);
        }
        return sInstance;
    }

    private void runOnMainThread(Runnable task) {
        if (android.os.Process.myTid() == sIconLoaderThread.getThreadId()) {
            mHandler.post(task);
        } else {
            task.run();
        }
    }

    @Override
    public void onIconFinishLoading(final String iconPackPkgName) {
        for (WeakReference<Callback> wr : mCallbacks) {
            final Callback cb = wr.get();
            if (cb != null)
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        cb.onRefresh(iconPackPkgName);
                    }
                });
        }
    }

    @Override
    public void onIconPackListLoadStart() {
        if (DEBUG) {
            Log.d(TAG, "onIconPackListLoadStart");
        }
    }

    @Override
    public void onIconPackListLoadFinish() {
        if (DEBUG) {
            Log.d(TAG, "onIconPackListLoadFinish");
            for (IconPack pack : IconPackHelper.getInstance(mContext).getIconPackList()) {
                Log.d(TAG, "pack: " + pack.getIconPackPackageName() + ", title: " + pack.getIconPackPackageTitle());
            }
        }
    }

    @Override
    public void onStartToLoadIconPackContent(String iconPackPkgName) {
        if (DEBUG) {
            Log.d(TAG, "onStartToLoadIconPackContent, iconPackPkgName: " + iconPackPkgName);
        }
    }

    @Override
    public void onFinishToLoadIconPackContent(String iconPackPkgName) {
        if (DEBUG) {
            Log.d(TAG, "onFinishToLoadIconPackContent, iconPackPkgName: " + iconPackPkgName);
        }
        startToLoadIcon(iconPackPkgName);
    }

    public interface Callback {
        void onRefresh(String iconPackPkgName);
    }

    public void requestToLoadIconPack(final String pkg) {
        requestToLoadIconPack(pkg, false);
    }

    public void requestToLoadIconPack(final String pkg, final boolean force) {
        final IconPackHelper helper = IconPackHelper.getInstance(mContext);
        if (force) {
            helper.loadIconPackContent(pkg);
        } else {
            if (helper.hasIconPackLoaded(pkg)) {
                return;
            }
            helper.loadIconPackContent(pkg);
        }
    }

    private void initParams() {
        sDefaultIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.normal_icon_size);
    }

    private IconLoader(Context context) {
        mContext = context.getApplicationContext();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        mDefaultIcon = makeDefaultIcon();
        registerReceiver();
        initParams();
        startToLoadIcon(ICON_PACK_DEFAULT);
        IconPackHelper.getInstance(mContext).addCallback(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                IconPackHelper.getInstance(mContext).reloadAllIconPackList();
            }
        }).start();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mContext.registerReceiver(mReceiver, filter);
    }

    public void addCallback(Callback cb) {
        mCallbacks.add(new WeakReference(cb));
    }

    private void startToLoadIcon(String iconPackPkg) {
        if (mLoadIconHelper != null) {
            sWorker.removeCallbacks(mLoadIconHelper);
        }
        HashMap<ComponentName, InfoCache> map = new HashMap<>();
        mActivityInfoCache.put(iconPackPkg, map);
        mLoadIconHelper = new LoadIconHelper(mContext, iconPackPkg, map, IconLoader.this, IconLoader.this);
        sWorker.post(mLoadIconHelper);
    }

    public InfoCache getActivityInfo(String iconPackPkg, ComponentName cn, int iconType) {
        return mActivityInfoCache.get(iconPackPkg).get(cn);
    }

    public HashMap<ComponentName, InfoCache> getAllActivitiesInfoCache(String iconPackPkg) {
        if (mActivityInfoCache.get(iconPackPkg) == null) return null;
        return new HashMap<>(mActivityInfoCache.get(iconPackPkg));
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
            return mContext.getPackageManager().getUserBadgedIcon(d, user);
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
}
