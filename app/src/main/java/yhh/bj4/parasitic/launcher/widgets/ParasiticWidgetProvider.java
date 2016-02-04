package yhh.bj4.parasitic.launcher.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
public class ParasiticWidgetProvider extends AppWidgetProvider {

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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().contentEquals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
            handleTouchWiz(context, intent);
        }
        Log.d(TAG, "intent: " + intent.toUri(0));
    }

    private void handleTouchWiz(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int appWidgetId = intent.getIntExtra("widgetId", 0);
        int widgetSpanX = intent.getIntExtra("widgetspanx", 0);
        int widgetSpanY = intent.getIntExtra("widgetspany", 0);

        if (appWidgetId > 0 && widgetSpanX > 0 && widgetSpanY > 0) {
            Bundle newOptions = new Bundle();
            // We have to convert these numbers for future use
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, widgetSpanY * 74);
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widgetSpanX * 74);

            onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Iterator<String> iter = newOptions.keySet().iterator();
        while(iter.hasNext()) {
            final String key = iter.next();
            Log.d(TAG, "key: " + key + ", value: " + newOptions.get(key));
        }
    }
}
