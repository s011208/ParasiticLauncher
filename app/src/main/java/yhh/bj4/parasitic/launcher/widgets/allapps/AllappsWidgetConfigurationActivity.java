package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackListDialog;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurationActivity extends Activity implements IconPackListDialog.Callback {
    private static final String TAG = "AllappsWidgetConfigure";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_apps_widget_configuration_activity);
        getFragmentManager().beginTransaction().replace(R.id.prefs_fragment_container,
                new AllappsWidgetConfigurePreference()).commit();
    }

    @Override
    public void onIconPackSelected(String iconPackPackageName) {
        if (DEBUG) {
            Log.d(TAG, "pkg: " + iconPackPackageName);
        }
    }
}
