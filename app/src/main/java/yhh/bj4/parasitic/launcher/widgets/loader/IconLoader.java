package yhh.bj4.parasitic.launcher.widgets.loader;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.HashMap;

import yhh.bj4.parasitic.launcher.R;


/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class IconLoader implements LoadingIconHelper.Callback {
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

    public synchronized static IconLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconLoader(context);
        }
        return sInstance;
    }

    @Override
    public void onFinishLoading() {
        for (Callback cb : mCallbacks) {
            cb.onRefresh();
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
        initParams();
        startToLoadIcon();
    }

    private final ArrayList<Callback> mCallbacks = new ArrayList<>();

    public void addCallback(Callback cb) {
        while (mCallbacks.contains(cb)) {
            mCallbacks.remove(cb);
        }
        mCallbacks.add(cb);
    }

    public void removeCallback(Callback cb) {
        while (mCallbacks.contains(cb)) {
            mCallbacks.remove(cb);
        }
    }

    private void startToLoadIcon() {
        sWorker.post(new LoadingIconHelper(mContext, mActivityInfoCache, IconLoader.this));
    }

    public ActivityInfoCache getActivityInfo(ComponentName cn, int iconType) {
        return mActivityInfoCache.get(cn);
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
