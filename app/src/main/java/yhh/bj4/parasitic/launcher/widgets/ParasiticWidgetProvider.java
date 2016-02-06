package yhh.bj4.parasitic.launcher.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Iterator;

import yhh.bj4.parasitic.launcher.MainActivity;
import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/2.
 */
public class ParasiticWidgetProvider extends BaseWidgetProvider {

    private static final String TAG = "QQQQ";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.parasitic_widget);
            for (int i = 0; i < 20; i++) {
                RemoteViews txt = new RemoteViews(context.getPackageName(), R.layout.base_image_button);
                txt.setImageViewResource(R.id.base_icon, R.mipmap.ic_launcher);
                txt.setViewPadding(R.id.base_icon, i * 10, i * 10, 0, 0);
                views.addView(R.id.main_container, txt);
            }
            views.setOnClickPendingIntent(R.id.main_content, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Iterator<String> iter = newOptions.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            Log.d(TAG, "key: " + key + ", value: " + newOptions.get(key));
        }
    }
}
