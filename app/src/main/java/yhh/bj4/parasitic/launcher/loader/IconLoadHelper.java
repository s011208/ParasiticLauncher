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

import java.util.HashMap;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;

/**
 * Created by yenhsunhuang on 2016/2/14.
 */
public class IconLoadHelper implements IconPackHelper.Callback {
    private static final String TAG = "IconLoadHelper";
    private static final boolean DEBUG = true;
    public static final String ICON_PACK_DEFAULT = "icon_pack_default";
    private static int sDefaultIconSize;
    private static IconLoadHelper sInstance;

    public synchronized static IconLoadHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconLoadHelper(context);
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
        void onStartLoading();

        void onStartLoadingPackageName(String pkg);

        void onFinishLoading();

        void onFinishLoadingPackageName(String pkg);
    }

    private static class LoadTask implements Runnable {
        private final Context mContext;

        public LoadTask(Context context) {
            mContext = context;
        }

        @Override
        public void run() {

        }
    }

    private final Context mContext;
    private Handler mWorker = new Handler(sIconLoaderThread.getLooper());
    private final HashMap<String, HashMap<ComponentName, InfoCache>> mActivityInfoCache = new HashMap<>();
    private Handler mHandler = new Handler();
    private int mIconDpi;
    private final Bitmap mDefaultIcon;
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

    private IconLoadHelper(Context context) {
        mContext = context.getApplicationContext();
        registerReceiver();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mIconDpi = activityManager.getLauncherLargeIconDensity();
        mDefaultIcon = makeDefaultIcon();
        sDefaultIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.normal_icon_size);
        loadAllIconPackList();
    }

    private void loadAllIconPackList() {
        mWorker.post(new Runnable() {
            @Override
            public void run() {
                IconPackHelper.getInstance(mContext).reloadAllIconPackList();
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

    private void onPackageChanged(String pkgName) {

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


    @Override
    public void onIconPackListLoadStart() {

    }

    @Override
    public void onIconPackListLoadFinish() {

    }

    @Override
    public void onStartToLoadIconPackContent(String iconPackPkgName) {

    }

    @Override
    public void onFinishToLoadIconPackContent(String iconPackPkgName) {

    }
}
