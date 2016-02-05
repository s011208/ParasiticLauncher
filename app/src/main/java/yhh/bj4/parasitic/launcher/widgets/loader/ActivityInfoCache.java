package yhh.bj4.parasitic.launcher.widgets.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class ActivityInfoCache {
    private String mTitle;
    private Drawable mIconLabel;

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

    public static Bitmap convertToBitmap(Drawable d) {
        Canvas canvas = new Canvas();
        d.setBounds(0, 0, 500, 500);
        Bitmap b = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        d.draw(canvas);
        canvas.setBitmap(null);
        return b;
    }
}
