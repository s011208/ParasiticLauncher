package yhh.bj4.parasitic.launcher;

import android.app.Application;

import yhh.bj4.parasitic.launcher.loader.IconLoader;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class ParasiticApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IconLoader.getInstance(this);
    }
}
