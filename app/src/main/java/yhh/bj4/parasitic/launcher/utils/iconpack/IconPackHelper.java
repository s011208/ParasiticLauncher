package yhh.bj4.parasitic.launcher.utils.iconpack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.IconLoader;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class IconPackHelper {
    private static final String TAG = "IconPackHelper";
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_APPFILTER = false;
    private static final int NUM_INFINITE = -1;
    private static final int ICON_ARRAYLIST_UPPER_BOUND = 10;

    private static IconPackHelper sInstance;

    private final ArrayList<IconPack> mIconPackList = new ArrayList<>();

    private final ArrayList<Callback> mCallbacks = new ArrayList<>();

    private final HashMap<String, AppFilterInfo> mAppFilterInfos = new HashMap<>();

    private final AtomicBoolean mIsLoadingIconPackList = new AtomicBoolean(false);

    private final ArrayList<String> mLoadingIconPackList = new ArrayList<>();

    public interface Callback {
        void onIconPackListLoadStart();

        void onIconPackListLoadFinish();

        void onStartToLoadIconPackContent(String iconPackPkgName);

        void onFinishToLoadIconPackContent(String iconPackPkgName);
    }

    private IconPackHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public ArrayList<IconPack> getIconPackList() {
        return mIconPackList;
    }

    public void addCallback(Callback cb) {
        if (cb == null) return;
        synchronized (mCallbacks) {
            removeCallback(cb);
            mCallbacks.add(cb);
        }
    }

    public void removeCallback(Callback cb) {
        while (mCallbacks.contains(cb)) mCallbacks.remove(cb);
    }

    public synchronized static IconPackHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconPackHelper(context);
        }
        return sInstance;
    }

    private final Context mContext;

    public void loadIconPackContent(final String iconPackPkgName) {
        if (DEBUG) {
            Log.d(TAG, "loadIconPackContent start");
        }
        synchronized (mLoadingIconPackList) {
            if (mLoadingIconPackList.contains(mLoadingIconPackList))
                return;
            mLoadingIconPackList.add(iconPackPkgName);
        }
        synchronized (mCallbacks) {
            for (Callback cb : mCallbacks) {
                cb.onStartToLoadIconPackContent(iconPackPkgName);
            }
        }
        final AppFilterInfo info = readAppFilter(mContext,
                iconPackPkgName,
                NUM_INFINITE,
                ICON_ARRAYLIST_UPPER_BOUND);
        synchronized (mAppFilterInfos) {
            mAppFilterInfos.put(iconPackPkgName, info);
        }
        synchronized (mCallbacks) {
            for (Callback cb : mCallbacks) {
                cb.onFinishToLoadIconPackContent(iconPackPkgName);
            }
        }
        synchronized (mLoadingIconPackList) {
            mLoadingIconPackList.remove(iconPackPkgName);
        }
        if (DEBUG) {
            Log.d(TAG, "loadIconPackContent finish");
        }
    }

    public boolean hasIconPackLoaded(final String iconPackPkgName) {
        synchronized (mAppFilterInfos) {
            return mAppFilterInfos.get(iconPackPkgName) != null;
        }
    }

    public void reloadAllIconPackList() {
        synchronized (mCallbacks) {
            for (Callback cb : mCallbacks) {
                cb.onIconPackListLoadStart();
            }
        }
        synchronized (mIconPackList) {
            mIconPackList.clear();
        }
        synchronized (mAppFilterInfos) {
            mAppFilterInfos.clear();
        }
        mIsLoadingIconPackList.set(true);
        ArrayList<String> iconPackPkgs = new ArrayList<>();
        String[] actionList = mContext.getResources().getStringArray(R.array.icon_pack_intent_action_list);
        String[] categoryList = mContext.getResources().getStringArray(R.array.icon_pack_intent_category_list);
        for (int i = 0; i < actionList.length; ++i) {
            Intent query = new Intent();
            query.setAction(actionList[i]);
            query.addCategory(categoryList[i]);
            List<ResolveInfo> pkgs = mContext.getPackageManager().queryIntentActivities(query, 0);
            for (ResolveInfo info : pkgs) {
                final String pkgName = info.activityInfo.packageName;
                final String appTitle = info.loadLabel(mContext.getPackageManager()).toString();
                final Drawable icon = info.loadIcon(mContext.getPackageManager());
                if (!iconPackPkgs.contains(info.activityInfo.packageName)) {
                    iconPackPkgs.add(info.activityInfo.packageName);
                    synchronized (mIconPackList) {
                        mIconPackList.add(new IconPack(appTitle, pkgName, icon));
                    }
                }
            }
        }
        if (DEBUG) {
            for (String pkg : iconPackPkgs) {
                Log.d(TAG, "pkg: " + pkg);
            }
        }
        mIsLoadingIconPackList.set(false);
        synchronized (mCallbacks) {
            for (Callback cb : mCallbacks) {
                cb.onIconPackListLoadFinish();
            }
        }
    }

    private AppFilterInfo readAppFilter(Context context,
                                        final String iconPackPkgName,
                                        final int specItemsLengthLimit,
                                        final int unspecItemsLengthLimit) {
        AppFilterInfo info = new AppFilterInfo();
        try {
            PackageManager pm = context.getPackageManager();
            Resources res = pm.getResourcesForApplication(iconPackPkgName);
            int resId = res.getIdentifier("appfilter", "xml", iconPackPkgName);
            if (resId != -1) {
                final XmlResourceParser xrp = res.getXml(resId);

                try {
                    while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                        if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                            String name = xrp.getName();
                            if (DEBUG_APPFILTER) {
                                Log.v(TAG, "name= " + name);
                            }
                            if (name.equals("item")) {
                                if (specItemsLengthLimit <= NUM_INFINITE
                                        || info.mSpecIconComponentInfoToResId.size() < specItemsLengthLimit) {
                                    String drawableName = xrp.getAttributeValue(null, "drawable");
                                    if (drawableName != null) {
                                        int drawableResId = res.getIdentifier(drawableName, "drawable", iconPackPkgName);
                                        if (drawableResId != -1) {
                                            String componentInfo = xrp.getAttributeValue(null, "component");
                                            if (DEBUG_APPFILTER) {
                                                Log.v(TAG, "valid componentInfo= "
                                                        + componentInfo + ", drawableName= " + drawableName
                                                        + ", drawableResId= " + drawableResId);
                                            }
                                            if (specItemsLengthLimit <= NUM_INFINITE) {
                                                info.mSpecIconComponentInfoToResId.put(componentInfo, drawableResId);

                                            } else if (!info.mSpecIconComponentInfoToResId.containsValue(drawableResId)) {
                                                try {
                                                    if (getDrawable(res, drawableResId) != null) {
                                                        info.mSpecIconComponentInfoToResId.put(componentInfo, drawableResId);
                                                    }
                                                } catch (Exception e) {
                                                    if (DEBUG_APPFILTER) {
                                                        Log.v(TAG, "NULL drawable, drawableName="
                                                                + drawableName + ", drawableResId= " + drawableResId
                                                                + ", iconPackPkg= " + iconPackPkgName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (name.equals("iconback")
                                    && (unspecItemsLengthLimit == NUM_INFINITE
                                    || info.mIconBackgroundBitmaps.size() < unspecItemsLengthLimit)) {
                                if (DEBUG_APPFILTER) {
                                    Log.v(TAG, "iconback xrp.getAttributeCount()= " + xrp.getAttributeCount());
                                }
                                if (xrp.getAttributeCount() > 0) {
                                    for (int i = 0; i < xrp.getAttributeCount(); i++) {
                                        String drawableName = xrp.getAttributeValue(i);
                                        if (drawableName != null) {
                                            int drawableResId = res.getIdentifier(drawableName, "drawable", iconPackPkgName);
                                            if (drawableResId != -1) {
                                                if (DEBUG_APPFILTER) {
                                                    Log.v(TAG, "valid iconback drawableName= " + drawableName);
                                                }
                                                Bitmap iconBack = getBitmapByResId(res, drawableResId);
                                                if (iconBack != null) {
                                                    info.mIconBackgroundBitmaps.add(iconBack);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (name.equals("iconupon")
                                    && (unspecItemsLengthLimit == NUM_INFINITE
                                    || info.mIconOverlayDrawables.size() < unspecItemsLengthLimit)) {
                                if (DEBUG_APPFILTER) {
                                    Log.v(TAG, "iconupon xrp.getAttributeCount()= " + xrp.getAttributeCount());
                                }
                                if (xrp.getAttributeCount() > 0) {
                                    for (int i = 0; i < xrp.getAttributeCount(); i++) {
                                        String drawableName = xrp.getAttributeValue(i);
                                        if (drawableName != null) {
                                            int drawableResId = res.getIdentifier(drawableName, "drawable", iconPackPkgName);
                                            if (drawableResId != -1) {
                                                if (DEBUG_APPFILTER) {
                                                    Log.v(TAG, "valid iconupon drawableName= " + drawableName);
                                                }
                                                try {
                                                    Drawable drawable = getDrawable(res, drawableResId);
                                                    if (drawable != null) {
                                                        info.mIconOverlayDrawables.add(drawable);
                                                    }
                                                } catch (Resources.NotFoundException e) {
                                                    Log.v(TAG, "get IconUpon/IconOverlay: " + drawableName
                                                            + " FAILED with IconPack: " + iconPackPkgName, e);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (name.equals("iconmask")
                                    && (unspecItemsLengthLimit == NUM_INFINITE
                                    || info.mIconMaskBitmaps.size() < unspecItemsLengthLimit)) {
                                if (DEBUG_APPFILTER) {
                                    Log.v(TAG, "iconmask xrp.getAttributeCount()= " + xrp.getAttributeCount());
                                }
                                if (xrp.getAttributeCount() > 0) {
                                    for (int i = 0; i < xrp.getAttributeCount(); i++) {
                                        String drawableName = xrp.getAttributeValue(i);
                                        if (drawableName != null) {
                                            int drawableResId = res.getIdentifier(drawableName, "drawable", iconPackPkgName);
                                            if (drawableResId != -1) {
                                                if (DEBUG_APPFILTER) {
                                                    Log.v(TAG, "valid iconmask drawableName= " + drawableName);
                                                }
                                                Bitmap iconMask = getBitmapByResId(res, drawableResId);
                                                if (iconMask != null) {
                                                    info.mIconMaskBitmaps.add(iconMask);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (name.equals("scale")) {
                                String scaleFactor = xrp.getAttributeValue(null, "factor");
                                if (scaleFactor != null) {
                                    try {
                                        Float scale = Float.parseFloat(scaleFactor);
                                        if (scale > 0) {
                                            if (DEBUG_APPFILTER) {
                                                Log.v(TAG, "valid iconscale= " + scale);
                                            }
                                            info.mIconScale = scale;
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.i(TAG, "readAppfilter warning info-invalid float scale: " + scaleFactor, e);
                                    }
                                }
                            }
                        }
                        xrp.next();
                    }
                } catch (XmlPullParserException e) {
                    Log.i(TAG, "readAppfilter warning info: ", e);
                } catch (IOException e) {
                    Log.i(TAG, "readAppfilter warning info: ", e);
                }

                if (DEBUG_APPFILTER) {
                    Log.v(TAG, info.toString());
                }
            } else {
                if (DEBUG_APPFILTER) {
                    Log.v(TAG, "Xml not found");
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            if (DEBUG_APPFILTER) {
                Log.v(TAG, "Package not found : " + iconPackPkgName);
            }
        }
        return info;
    }

    private Bitmap getBitmapByResId(Resources res, int resId) {
        InputStream input = null;
        Bitmap bitmap = null;
        try {
            input = new BufferedInputStream(
                    res.openRawResource(resId));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inDither = true;
            bitmap = BitmapFactory.decodeStream(input, null, options);
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, "getBitmapByResId warning info: ", e);
        } catch (NullPointerException e) {
            Log.i(TAG, "getBitmapByResId warning info: ", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    private Drawable getDrawable(final Resources res, final int resId) throws Resources.NotFoundException {
        Drawable drawable = null;
        if (resId != -1) {
            try {
                drawable = res.getDrawable(resId);
                if (drawable instanceof LayerDrawable) {
                    LayerDrawable layerDrawable = (LayerDrawable) drawable;
                    try {
                        layerDrawable.getId(0);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        Log.d(TAG, "Array of LayerDrawable is empty " + layerDrawable);
                        drawable = null;
                    }
                }
                return drawable;
            } catch (Resources.NotFoundException e) {
                Log.d(TAG, "load resid: " + resId + " FAILED! try DENSITY_XXXHIGH to get drawable");

                try {
                    drawable = res.getDrawableForDensity(resId, DisplayMetrics.DENSITY_XXXHIGH);
                    if (drawable instanceof LayerDrawable) {
                        LayerDrawable layerDrawable = (LayerDrawable) drawable;
                        try {
                            layerDrawable.getId(0);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            Log.d(TAG, "Array of LayerDrawable is empty " + layerDrawable);
                            drawable = null;
                        }
                    }
                    return drawable;
                } catch (Resources.NotFoundException e1) {
                    throw new Resources.NotFoundException("load resid: " + resId + " still FAILED under DENSITY_XXXHIGH");
                }
            } catch (StackOverflowError e) {
                return null;
            }
        } else {
            throw new Resources.NotFoundException("resId is 0!!");
        }
    }

    public Drawable getSpecIconPackIcon(Context context, String iconPackPkgName, final ComponentName componentName, IconLoader loader) {
        if (iconPackPkgName != null) {
            try {
                Resources resources = context.getPackageManager().getResourcesForApplication(iconPackPkgName);
                int iconId = getIconResIdViaComponentInfo("ComponentInfo{" + componentName.flattenToString() + "}", iconPackPkgName);
                if (iconId != -1) {
                    return loader.getFullResIcon(resources, iconId, null);
                }
            } catch (PackageManager.NameNotFoundException e) {
            } finally {
            }
        }
        return null;
    }

    public Integer getIconResIdViaComponentInfo(String key, String iconPackPkgName) {
        final Object rtn = mAppFilterInfos.get(iconPackPkgName).mSpecIconComponentInfoToResId.get(key);
        return rtn != null ? (Integer) rtn : -1;
    }

    private void getUnspecIconPackIcon(Context context,
                                       Canvas canvas, final Drawable icon,
                                       final int textureWidth, final int textureHeight,
                                       final int width, final int height,
                                       int iconPackIndex,
                                       AppFilterInfo appfilterInfo) {
        // IconBase
        final int widthBase = Math.round(width * appfilterInfo.mIconScale);
        final int heightBase = Math.round(height * appfilterInfo.mIconScale);
        final int leftMask = (textureWidth - width) / 2;
        final int topMask = (textureHeight - height) / 2;

        if (appfilterInfo.mIconScale > 1) {
            if (icon != null) {
                final int finalCanvasSize = Math.max(canvas.getWidth(), canvas.getHeight());
                final int finalIconSize = Math.max(widthBase, heightBase);
                final int finalStartPosition = (finalCanvasSize - finalIconSize) / 2;
                final int finalEndPosition = finalCanvasSize - finalStartPosition;
                try {
                    canvas.save();
                    canvas.clipRect(new RectF(leftMask, topMask, leftMask + width, topMask + height));
                    icon.setBounds(finalStartPosition, finalStartPosition, finalEndPosition, finalEndPosition);
                    icon.draw(canvas);
                    canvas.restore();
                } catch (Exception e) {
                    Log.w(TAG, "createBitmap failed", e);
                }
            }
        } else {
            final int leftBase = (textureWidth - widthBase) / 2;
            final int topBase = (textureHeight - heightBase) / 2;
            final Rect iconOldBounds = new Rect(icon.getBounds());
            icon.setBounds(leftBase, topBase, leftBase + widthBase, topBase + heightBase);
            icon.draw(canvas);
            icon.setBounds(iconOldBounds);
        }

        // IconMask
        if (!appfilterInfo.mIconMaskBitmaps.isEmpty()) {
            int iconMaskIndex = iconPackIndex % appfilterInfo.mIconMaskBitmaps.size();

            Bitmap iconMask = appfilterInfo.mIconMaskBitmaps.get(iconMaskIndex);
            Paint iconMaskPaint = new Paint();
            iconMaskPaint.setFilterBitmap(true);
            iconMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawBitmap(iconMask, null, new RectF(leftMask, topMask, leftMask + width, topMask + height), iconMaskPaint);

        }

        // IconBackground
        if (!appfilterInfo.mIconBackgroundBitmaps.isEmpty()) {
            final int leftBack = (textureWidth - width) / 2;
            final int topBack = (textureHeight - height) / 2;
            // make background as square
            final int startBack = Math.min(leftBack, topBack);
            final int endBack = startBack + Math.max(width, height);
            int iconBackIndex = iconPackIndex % appfilterInfo.mIconBackgroundBitmaps.size();

            Bitmap iconBack = appfilterInfo.mIconBackgroundBitmaps.get(iconBackIndex);
            Paint iconBackPaint = new Paint();
            iconBackPaint.setFilterBitmap(true);
            iconBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            canvas.drawBitmap(iconBack, null, new RectF(startBack, startBack, endBack, endBack), iconBackPaint);

        }

        // IconOverlay
        if (!appfilterInfo.mIconOverlayDrawables.isEmpty()) {
            final int leftUpon = (textureWidth - width) / 2;
            final int topUpon = (textureHeight - height) / 2;
            final int startUpon = Math.min(leftUpon, topUpon);
            final int endUpon = startUpon + Math.max(width, height);
            int iconUponIndex = iconPackIndex % appfilterInfo.mIconOverlayDrawables.size();
            final Drawable d = appfilterInfo.mIconOverlayDrawables.get(iconUponIndex);
            d.setBounds(startUpon, startUpon, endUpon, endUpon);
            d.draw(canvas);

        }
        canvas.setBitmap(null);
    }

    public Drawable getUnspecIconForDefault(Context context, final Drawable icon, final String iconPackPkgName) {
        final Bitmap iconBitmap = ((BitmapDrawable) icon).getBitmap();
        // make icon as square
        final int iconWidth = Math.min(iconBitmap.getWidth(), iconBitmap.getHeight());
        final int iconHeight = iconWidth;

        Canvas canvas = new Canvas();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
        final Bitmap bitmap = Bitmap.createBitmap(iconWidth, iconHeight,
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        Random random = new Random();

        getUnspecIconPackIcon(context, canvas, icon,
                iconWidth, iconHeight,
                iconWidth, iconHeight,
                random.nextInt(10),
                mAppFilterInfos.get(iconPackPkgName));
        canvas.setBitmap(null);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
