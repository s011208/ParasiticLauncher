package yhh.bj4.parasitic.launcher.widgets.loader;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class LoadingSingleIconTask implements Runnable {
    private final WeakReference<PackageManager> mPm;
    private final WeakReference<ResolveInfo> mAppInfo;
    private final WeakReference<Callback> mCallback;

    public interface Callback {
        void onFinishLoading(ComponentName cn, ActivityInfoCache icon);
    }

    public LoadingSingleIconTask(PackageManager pm, ResolveInfo appInfo, Callback cb) {
        mPm = new WeakReference<PackageManager>(pm);
        mAppInfo = new WeakReference<ResolveInfo>(appInfo);
        mCallback = new WeakReference<Callback>(cb);
    }

    @Override
    public void run() {
        final PackageManager pm = mPm.get();
        final ResolveInfo appInfo = mAppInfo.get();
        final Callback cb = mCallback.get();
        if (pm == null || appInfo == null || cb == null) {
            cb.onFinishLoading(null, null);
            return;
        }
        Drawable activityIcon = appInfo.activityInfo.loadIcon(pm);
        String title = appInfo.loadLabel(pm).toString();

        ActivityInfoCache icon = new ActivityInfoCache();
        icon.setIcon(activityIcon);
        icon.setTitle(title);
        icon.setBitmap(IconLoader.convertDrawableIconToBitmap(activityIcon));
        cb.onFinishLoading(new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name), icon);
    }

}