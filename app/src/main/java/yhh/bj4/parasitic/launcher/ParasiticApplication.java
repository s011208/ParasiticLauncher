package yhh.bj4.parasitic.launcher;

import android.app.Application;
import android.content.Intent;

import yhh.bj4.parasitic.launcher.loader.IconLoader;
import yhh.bj4.parasitic.launcher.services.UpdateWidgetsService;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class ParasiticApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IconLoader.getInstance(this);
        startUpdateWidgetsService();
    }

    private void startUpdateWidgetsService() {
        Intent intent = new Intent(this, UpdateWidgetsService.class);
        startService(intent);
        UpdateWidgetsService.scheduleAlarm(this);
    }
}
