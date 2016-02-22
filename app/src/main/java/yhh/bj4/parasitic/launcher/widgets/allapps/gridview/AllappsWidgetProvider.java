package yhh.bj4.parasitic.launcher.widgets.allapps.gridview;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;

import yhh.bj4.parasitic.launcher.LauncherProvider;
import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.Utilities;
import yhh.bj4.parasitic.launcher.utils.images.BackgroundTypeChooserDialog;
import yhh.bj4.parasitic.launcher.utils.sizelist.SizeListDialog;
import yhh.bj4.parasitic.launcher.widgets.BaseWidgetProvider;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsWidgetProvider extends BaseWidgetProvider {
    public static final String ON_ALL_APPS_ITEM_CLICK_INTENT = "yhh.bj4.parasitic.launcher.widgets.allapps.click";
    public static final String ON_ALL_APPS_ITEM_CLICK_INDEX = "yhh.bj4.parasitic.launcher.widgets.allapps.click.index";
    public static final String EXTRA_COMPONENTNAME = "extra_componentname";
    private static final String TAG = "AllappsWidgetProvider";
    private static final boolean DEBUG = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (DEBUG) {
            Log.d(TAG, "action: " + action);
        }
        if (ON_ALL_APPS_ITEM_CLICK_INTENT.equals(action)) {
            int viewIndex = intent.getIntExtra(ON_ALL_APPS_ITEM_CLICK_INDEX, 0);
            String componentNameData = intent.getStringExtra(AllappsWidgetProvider.EXTRA_COMPONENTNAME);
            final ComponentName cn = ComponentName.unflattenFromString(componentNameData);
            if (DEBUG) {
                Log.d(TAG, "click index: " + viewIndex + ", cn: " + cn.flattenToShortString());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentValues cv = new ContentValues();
                    cv.put(LauncherProvider.COLUMN_ACTIVITY_USAGE_CLICK_TIME, String.valueOf(System.currentTimeMillis()));
                    int clickFrequency = 1;
                    Cursor data = context.getContentResolver().query(LauncherProvider.URI_ACTIVITY_USAGE_COMPONENT_INFO(cn), null, null, null, null);
                    if (data != null) {
                        try {
                            if (data.getCount() > 0) {
                                data.moveToFirst();
                                clickFrequency = data.getInt(data.getColumnIndex(LauncherProvider.COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY));
                                ++clickFrequency;
                            }
                        } finally {
                            data.close();
                        }
                    }
                    cv.put(LauncherProvider.COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY, clickFrequency);
                    context.getContentResolver().update(LauncherProvider.URI_ACTIVITY_USAGE_COMPONENT_INFO(cn), cv, null, null);
                }
            }).start();
            Utilities.startActivity(context.getApplicationContext(), cn);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {
        if (DEBUG)
            Log.d(TAG, "onUpdate");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            updateAppWidget(appWidgetManager, context, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateWidgetListView(context, appWidgetId);
        if (DEBUG)
            Log.d(TAG, "onAppWidgetOptionsChanged");
    }

    public static void updateAppWidget(AppWidgetManager appWidgetManager, Context context, int widgetId) {
        RemoteViews remoteViews = updateWidgetListView(context,
                widgetId);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    private static RemoteViews updateWidgetListView(Context context,
                                                    int appWidgetId) {
        if (DEBUG) {
            Log.d(TAG, "updateWidgetListView with id: " + appWidgetId);
        }
        final SharedPreferences mPrefs = context.getSharedPreferences(AllappsWidgetConfigurePreference.class.getSimpleName() + String.valueOf(appWidgetId), Context.MODE_PRIVATE);
        final int backgroundType = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_WIDGET_BACKGROUND_TYPE, BackgroundTypeChooserDialog.TYPE_COLOR);
        int backgroundColor = 0;
        String backgroundPath = null;
        int backgroundPathAlpha = 255;
        int gridViewLayoutRes = 0;
        gridViewLayoutRes = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_SIZE, SizeListDialog.SIZE_NORMAL);
        switch (gridViewLayoutRes) {
            case SizeListDialog.SIZE_SMALL:
                gridViewLayoutRes = R.layout.small_size_all_apps_widget;
                break;
            case SizeListDialog.SIZE_NORMAL:
                gridViewLayoutRes = R.layout.normal_size_all_apps_widget;
                break;
            case SizeListDialog.SIZE_LARGE:
                gridViewLayoutRes = R.layout.large_size_all_apps_widget;
                break;
            default:
                gridViewLayoutRes = R.layout.normal_size_all_apps_widget;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), gridViewLayoutRes);
        Intent svcIntent = new Intent(context, AllappsWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent gridIntent = new Intent();
        gridIntent.setAction(ON_ALL_APPS_ITEM_CLICK_INTENT);
        gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.allapps_list, pendingIntent);

        remoteViews.setRemoteAdapter(R.id.allapps_list,
                svcIntent);

        if (backgroundType == BackgroundTypeChooserDialog.TYPE_COLOR) {
            backgroundColor = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_WIDGET_BACKGROUND_COLOR, 0);
            if (backgroundColor != 0) {
                remoteViews.setInt(R.id.allapps_background, "setBackgroundColor", backgroundColor);
                remoteViews.setImageViewBitmap(R.id.allapps_background, null);
            }
        } else if (backgroundType == BackgroundTypeChooserDialog.TYPE_IMAGE) {
            backgroundPath = mPrefs.getString(AllappsWidgetConfigurePreference.SPREF_KEY_WIDGET_BACKGROUND_PATH, null);
            backgroundPathAlpha = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_WIDGET_BACKGROUND_PATH_ALPHA, 100);
            Bitmap b = Utilities.decodeFile(new File(backgroundPath));
            if (b != null) {
                Bitmap alphaBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
                if (backgroundPathAlpha != 0) {
                    Canvas c = new Canvas();
                    c.setBitmap(alphaBitmap);
                    Paint alphaPaint = new Paint();
                    alphaPaint.setAlpha(backgroundPathAlpha);
                    c.drawBitmap(b, 0, 0, alphaPaint);
                    c.setBitmap(null);
                    b.recycle();
                    b = null;
                }
                remoteViews.setImageViewBitmap(R.id.allapps_background, alphaBitmap);
                remoteViews.setInt(R.id.allapps_background, "setBackgroundColor", Color.TRANSPARENT);
            }
        }
        return remoteViews;
    }
}
