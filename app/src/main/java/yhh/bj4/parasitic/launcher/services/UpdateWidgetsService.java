package yhh.bj4.parasitic.launcher.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import yhh.bj4.parasitic.launcher.widgets.allapps.gridview.AllappsWidgetProvider;

/**
 * Created by Yen-Hsun_Huang on 2016/2/15.
 */
public class UpdateWidgetsService extends IntentService {
    private static final int ALARM_TIME = 60 * 60 * 1000;

    public UpdateWidgetsService() {
        super(UpdateWidgetsService.class.getName());
    }

    private void updateAllappsWidgets() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] widgetIds = manager.getAppWidgetIds(new ComponentName(getPackageName(), AllappsWidgetProvider.class.getName()));
        for (int widgetId : widgetIds) {
            AllappsWidgetProvider.updateAppWidget(manager, this, widgetId);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateAllappsWidgets();
    }

    public static void scheduleAlarm(Context context) {
        Intent intent = new Intent(context, UpdateWidgetsService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ALARM_TIME, pendingIntent);
    }
}
