package yhh.bj4.parasitic.launcher.loader;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by yenhsunhuang on 2016/2/12.
 */
public abstract class InfoCache {
    private String mTitle;
    private Drawable mIconLabel;
    private Bitmap mBitmap;
    private ComponentName mComponentName;
    private long mClickTime;
    private long mClickFrequency;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setIcon(Drawable iconLabel) {
        mIconLabel = iconLabel;
    }

    public Drawable getIcon() {
        return mIconLabel;
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
        mClickFrequency = clickTime;
    }

    public long getClickTime() {
        return mClickTime;
    }

    public void setClickFrequency(long frequency) {
        mClickFrequency = frequency;
    }

    public long getClickFrequency() {
        return mClickFrequency;
    }

    public static Intent getStartIntent(ComponentName cn) {
        Intent startIntent = new Intent();
        startIntent.setAction(Intent.ACTION_MAIN);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.setComponent(cn);
        return startIntent;
    }

    public ContentValues save() {
        ContentValues cv = new ContentValues();
        onSave(cv);
        return cv;
    }

    public abstract void onSave(ContentValues cv);
}
