package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
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
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (DEBUG) {
            Log.d(TAG, "mAppWidgetId: " + mAppWidgetId);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.all_apps_widget_configuration_activity);
        getFragmentManager().beginTransaction().replace(R.id.prefs_fragment_container,
                new AllappsWidgetConfigurePreference()).commit();
    }

    @Override
    public void onBackPressed() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        super.onBackPressed();
    }

    @Override
    public void onIconPackSelected(String iconPackPackageName) {
        if (DEBUG) {
            Log.d(TAG, "pkg: " + iconPackPackageName);
        }
    }
}
