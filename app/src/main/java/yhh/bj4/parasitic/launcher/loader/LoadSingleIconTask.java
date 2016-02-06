package yhh.bj4.parasitic.launcher.loader;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class LoadSingleIconTask implements Runnable {
    private final WeakReference<PackageManager> mPm;
    private final WeakReference<ResolveInfo> mAppInfo;
    private final Callback mCallback;

    public interface Callback {
        void onFinishLoading(ComponentName cn, ActivityInfoCache icon);
    }

    public LoadSingleIconTask(PackageManager pm, ResolveInfo appInfo, Callback cb) {
        mPm = new WeakReference<PackageManager>(pm);
        mAppInfo = new WeakReference<ResolveInfo>(appInfo);
        mCallback = cb;
    }

    @Override
    public void run() {
        ActivityInfoCache icon = null;
        ComponentName cn = null;
        try {
            final PackageManager pm = mPm.get();
            final ResolveInfo appInfo = mAppInfo.get();
            if (pm == null || appInfo == null || appInfo.activityInfo == null) {
                return;
            }
            Drawable activityIcon = appInfo.activityInfo.loadIcon(pm);
            String title = appInfo.loadLabel(pm).toString();

            icon = new ActivityInfoCache();
            icon.setIcon(activityIcon);
            icon.setTitle(title);
            icon.setBitmap(IconLoader.convertDrawableIconToBitmap(activityIcon));
            cn = new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name);
        } finally {
            mCallback.onFinishLoading(cn, icon);
        }
    }

}