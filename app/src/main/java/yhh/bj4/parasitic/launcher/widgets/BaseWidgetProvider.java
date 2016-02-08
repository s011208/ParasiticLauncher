package yhh.bj4.parasitic.launcher.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by yenhsunhuang on 2016/2/2.
 */
public class BaseWidgetProvider extends AppWidgetProvider {
    public static final boolean DEBUG = false;
    private static final String TAG = "BaseWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().contentEquals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
            handleTouchWiz(context, intent);
        }
        if (DEBUG)
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
    }
}
