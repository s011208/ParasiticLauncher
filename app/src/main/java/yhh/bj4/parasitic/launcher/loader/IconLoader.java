package yhh.bj4.parasitic.launcher.loader;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import yhh.bj4.parasitic.launcher.R;


/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class IconLoader implements LoadIconHelper.Callback {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_TRACE = true;
    private static final String TAG = "IconLoader";
    public static final int ICON_TYPE_NORMAL = 1 << 1;
    private static final HandlerThread sIconLoaderThread = new HandlerThread("Icon loader", android.os.Process.THREAD_PRIORITY_DEFAULT);

    static {
        sIconLoaderThread.start();
    }

    private static Handler sWorker = new Handler(sIconLoaderThread.getLooper());

    private static IconLoader sInstance;

    private final Context mContext;

    private final HashMap<ComponentName, ActivityInfoCache> mActivityInfoCache = new HashMap<>();

    private static int sDefaultIconSize;
    private boolean isLoaded = false;
    private final ArrayList<WeakReference<Callback>> mCallbacks = new ArrayList<>();
    private LoadIconHelper mLoadIconHelper;

    private Handler mHandler = new Handler();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {

            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {

            } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {

            }
        }
    };

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
    public void onFinishLoading() {
        for (WeakReference<Callback> wr : mCallbacks) {
            final Callback cb = wr.get();
            if (cb != null)
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        cb.onRefresh();
                    }
                });
        }
    }

    public interface Callback {
        void onRefresh();
    }

    private void initParams() {
        sDefaultIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.default_icon_size);
    }

    private IconLoader(Context context) {
        mContext = context.getApplicationContext();
        registerReceiver();
        initParams();
        startToLoadIcon();
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

    private void startToLoadIcon() {
        if (mLoadIconHelper != null) {
            sWorker.removeCallbacks(mLoadIconHelper);
        }
        mLoadIconHelper = new LoadIconHelper(mContext, mActivityInfoCache, IconLoader.this);
        sWorker.post(mLoadIconHelper);
    }

    public ActivityInfoCache getActivityInfo(ComponentName cn, int iconType) {
        return mActivityInfoCache.get(cn);
    }

    public HashMap<ComponentName, ActivityInfoCache> getAllActivitiesInfoCache() {
        return new HashMap<>(mActivityInfoCache);
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
}
