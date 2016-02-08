package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;
import yhh.bj4.parasitic.launcher.widgets.BaseWidgetProvider;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsWidgetProvider extends BaseWidgetProvider {
    public static final String ON_ALL_APPS_ITEM_CLICK_INTENT = "yhh.bj4.parasitic.launcher.widgets.allapps.click";
    public static final String ON_ALL_APPS_ITEM_CLICK_INDEX = "yhh.bj4.parasitic.launcher.widgets.allapps.click.index";
    public static final String EXTRA_COMPONENTNAME = "extra_componentname";
    private static final String TAG = "AllappsWidgetProvider";
    private static final boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (DEBUG) {
            Log.d(TAG, "action: " + action);
        }
        if (ON_ALL_APPS_ITEM_CLICK_INTENT.equals(action)) {
            int viewIndex = intent.getIntExtra(ON_ALL_APPS_ITEM_CLICK_INDEX, 0);
            String componentNameData = intent.getStringExtra(AllappsWidgetProvider.EXTRA_COMPONENTNAME);
            ComponentName cn = ComponentName.unflattenFromString(componentNameData);
            if (DEBUG) {
                Log.d(TAG, "click index: " + viewIndex + ", cn: " + cn.flattenToShortString());
            }
            context.startActivity(ActivityInfoCache.getStartIntent(cn));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateWidgetListView(context, appWidgetId);
        Log.d(TAG, "onAppWidgetOptionsChanged");
    }

    public static RemoteViews updateWidgetListView(Context context,
                                             int appWidgetId) {
        if (DEBUG) {
            Log.d(TAG, "updateWidgetListView with id: " + appWidgetId);
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.all_apps_widget);
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
        return remoteViews;
    }

    public static void notifyDataSetChanged(Context context, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.allapps_list);
    }
}
