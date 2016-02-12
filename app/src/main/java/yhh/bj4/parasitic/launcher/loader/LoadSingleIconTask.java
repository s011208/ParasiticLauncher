package yhh.bj4.parasitic.launcher.loader;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class LoadSingleIconTask implements Runnable {
    private final WeakReference<Context> mContext;
    private final WeakReference<PackageManager> mPm;
    private final WeakReference<ResolveInfo> mAppInfo;
    private final WeakReference<IconLoader> mIconLoader;
    private final Callback mCallback;
    private final String mIconPackPackageName;

    public interface Callback {
        void onIconFinishLoading(ComponentName cn, InfoCache icon);
    }

    public LoadSingleIconTask(Context context, PackageManager pm, ResolveInfo appInfo, Callback cb
            , String iconPackPackageName, IconLoader loader) {
        mContext = new WeakReference<>(context);
        mPm = new WeakReference<>(pm);
        mAppInfo = new WeakReference<>(appInfo);
        mIconLoader = new WeakReference<>(loader);
        mCallback = cb;
        mIconPackPackageName = iconPackPackageName;
    }

    @Override
    public void run() {
        InfoCache icon = null;
        ComponentName cn = null;
        try {
            final PackageManager pm = mPm.get();
            final ResolveInfo appInfo = mAppInfo.get();
            cn = new ComponentName(appInfo.activityInfo.packageName, appInfo.activityInfo.name);
            if (pm == null || appInfo == null || appInfo.activityInfo == null) {
                return;
            }
            Drawable activityIcon = null;
            if (IconLoader.ICON_PACK_DEFAULT.equals(mIconPackPackageName)) {
                activityIcon = appInfo.activityInfo.loadIcon(pm);
            } else {
                final Context context = mContext.get();
                final IconLoader loader = mIconLoader.get();
                if (context == null || loader == null) {
                    activityIcon = appInfo.activityInfo.loadIcon(pm);
                } else {
                    int iconId = IconPackHelper.getInstance(context).getIconResIdViaComponentInfo("ComponentInfo{" + cn.flattenToString() + "}", mIconPackPackageName);
                    if (iconId != -1) {
                        activityIcon = IconPackHelper.getInstance(context).getSpecIconPackIcon(context, mIconPackPackageName, cn, loader);
                    } else {
                        activityIcon = IconPackHelper.getInstance(context).getUnspecIconForDefault(context, appInfo.activityInfo.loadIcon(pm), mIconPackPackageName);
                    }
                }
            }
            String title = appInfo.loadLabel(pm).toString();

            icon = new ActivityInfoCache();
            icon.setIcon(activityIcon);
            icon.setTitle(title);
            icon.setBitmap(IconLoader.convertDrawableIconToBitmap(activityIcon));
            icon.setComponentName(cn);
        } finally {
            mCallback.onIconFinishLoading(cn, icon);
        }
    }

}