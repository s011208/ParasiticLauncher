package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.widgets.BaseWidgetProvider;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsWidgetProvider extends BaseWidgetProvider {
    private static final String TAG = "AllappsWidgetProvider";
    private static final boolean DEBUG = true;

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

    private RemoteViews updateWidgetListView(Context context,
                                             int appWidgetId) {
        if (DEBUG) {
            Log.d(TAG, "updateWidgetListView with id: " + appWidgetId);
        }
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.all_apps_widget);

        Intent svcIntent = new Intent(context, AllappsWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.allapps_list,
                svcIntent);
        return remoteViews;
    }
}
