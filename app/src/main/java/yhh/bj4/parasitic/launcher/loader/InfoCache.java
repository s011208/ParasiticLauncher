package yhh.bj4.parasitic.launcher.loader;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

import yhh.bj4.parasitic.launcher.LauncherProvider;
import yhh.bj4.parasitic.launcher.Utilities;

/**
 * Created by yenhsunhuang on 2016/2/12.
 */
public abstract class InfoCache {

    private String mTitle;
    private Drawable mIconDrawable;
    private Bitmap mBitmap;
    private ComponentName mComponentName;
    private long mClickTime;
    private int mClickFrequency;
    private int mPackageInfoFlag;
    private String mIconPackPackage = IconLoader.ICON_PACK_DEFAULT;

    public InfoCache() {

    }

    public InfoCache(Cursor c) {
        mIconPackPackage = c.getString(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_ICON_PACK));
        mPackageInfoFlag = c.getInt(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_PACKAGE_INFO_FLAG));
        mTitle = c.getString(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_ICON_LABEL));
        mBitmap = Utilities.getBlobBitmap(c.getBlob(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_ICON_BITMAP)));
        mComponentName = new ComponentName(c.getString(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_PACKAGE_NAME)),
                c.getString(c.getColumnIndex(LauncherProvider.COLUMN_INFO_CACHE_CLASSNAME)));
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setIcon(Drawable iconLabel) {
        mIconDrawable = iconLabel;
    }

    public Drawable getIcon() {
        return mIconDrawable;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap b) {
        mBitmap = b;
    }

    public void setComponentName(ComponentName cn) {
        mComponentName = cn;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setClickTime(long clickTime) {
        mClickTime = clickTime;
    }

    public long getClickTime() {
        return mClickTime;
    }

    public void setClickFrequency(int frequency) {
        mClickFrequency = frequency;
    }

    public int getClickFrequency() {
        return mClickFrequency;
    }

    public void setPackageInfoFlag(int flag) {
        mPackageInfoFlag = flag;
    }

    public int getPackageInfoFlag() {
        return mPackageInfoFlag;
    }

    public void setIconPackPackage(String pack) {
        mIconPackPackage = pack;
    }

    public ContentValues save() {
        ContentValues cv = new ContentValues();
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_ICON_PACK, mIconPackPackage);
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_PACKAGE_INFO_FLAG, mPackageInfoFlag);
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_ICON_LABEL, mTitle);
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_PACKAGE_NAME, mComponentName.getPackageName());
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_CLASSNAME, mComponentName.getClassName());
        cv.put(LauncherProvider.COLUMN_INFO_CACHE_ICON_BITMAP, Utilities.getBitmapBlob(mBitmap));
        onSave(cv);
        return cv;
    }

    public abstract void onSave(ContentValues cv);


}
