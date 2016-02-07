package yhh.bj4.parasitic.launcher.utils.iconpack;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AppFilterInfo {
    private static final float DEFAULT_ICON_SCALE = 1f;
    HashMap<String, Integer> mSpecIconComponentInfoToResId = new HashMap<String, Integer>();
    HashMap<String, String> mSpecIconComponentInfoToDrawableName = new HashMap<String, String>();//++Steven, for CDN icon pack
    ArrayList<Bitmap> mIconBackgroundBitmaps = new ArrayList<Bitmap>();
    ArrayList<Drawable> mIconOverlayDrawables = new ArrayList<Drawable>();
    ArrayList<Bitmap> mIconMaskBitmaps = new ArrayList<Bitmap>();
    float mIconScale = DEFAULT_ICON_SCALE;

    void clear() {
        synchronized (this) {
            this.mSpecIconComponentInfoToResId.clear();
            this.mSpecIconComponentInfoToDrawableName.clear();
            this.mIconBackgroundBitmaps.clear();
            this.mIconOverlayDrawables.clear();
            this.mIconMaskBitmaps.clear();
            this.mIconScale = DEFAULT_ICON_SCALE;
        }
    }

    AppFilterInfo copy() {
        final AppFilterInfo appfilterInfo = new AppFilterInfo();
        synchronized (this) {
            appfilterInfo.mSpecIconComponentInfoToResId.putAll(this.mSpecIconComponentInfoToResId);
            appfilterInfo.mSpecIconComponentInfoToDrawableName.putAll(this.mSpecIconComponentInfoToDrawableName);
            appfilterInfo.mIconBackgroundBitmaps.addAll(this.mIconBackgroundBitmaps);
            appfilterInfo.mIconOverlayDrawables.addAll(this.mIconOverlayDrawables);
            appfilterInfo.mIconMaskBitmaps.addAll(this.mIconMaskBitmaps);
            appfilterInfo.mIconScale = this.mIconScale;
        }
        return appfilterInfo;
    }

    boolean isEmpty() {
        return isSpecifiedPartEmpty() && isUnspecifiedPartEmpty();
    }

    boolean isSpecifiedPartEmpty() {
        synchronized (this) {
            return this.mSpecIconComponentInfoToResId.isEmpty() && mSpecIconComponentInfoToDrawableName.isEmpty();
        }
    }

    boolean isUnspecifiedPartEmpty() {
        synchronized (this) {
            return this.mIconBackgroundBitmaps.isEmpty()
                    && this.mIconOverlayDrawables.isEmpty()
                    && this.mIconMaskBitmaps.isEmpty()
                    && this.mIconScale == DEFAULT_ICON_SCALE;
        }
    }

    @Override
    public String toString() {
        synchronized (this) {
            return "IconPack specIconComponentInfoAndResId.size()= " + this.mSpecIconComponentInfoToResId.size()
                    + "IconPack specIconComponentInfoToDrawableName.size()= " + this.mSpecIconComponentInfoToDrawableName.size()
                    + ", IconPack background.size()= " + this.mIconBackgroundBitmaps.size()
                    + ", IconPack overlay.size()= " + this.mIconOverlayDrawables.size()
                    + ", IconPack mask.size()= " + this.mIconMaskBitmaps.size()
                    + ", IconPack scale= " + this.mIconScale;
        }
    }
}
