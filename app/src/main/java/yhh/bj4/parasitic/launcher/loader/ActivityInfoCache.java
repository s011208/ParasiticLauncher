package yhh.bj4.parasitic.launcher.loader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class ActivityInfoCache {
    private String mTitle;
    private Drawable mIconLabel;
    private Bitmap mBitmap;

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
}
