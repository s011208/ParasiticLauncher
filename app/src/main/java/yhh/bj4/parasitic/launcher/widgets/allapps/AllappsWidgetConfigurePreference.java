package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackListDialog;
import yhh.bj4.parasitic.launcher.utils.iconsize.IconSizeListDialog;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurePreference extends PreferenceFragment {
    private static final boolean DEBUG = true;
    private static final String TAG = "AllappsWidgetConfigure";
    private static final String KEY_ICON_PACK = "icon_pack";
    private static final String KEY_ICON_SIZE = "icon_size";

    private static final int REQUEST_ICON_PACK = 1;
    private static final int REQUEST_ICON_SIZE = 2;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_allapps_widget_configuration);
        Bundle argus = getArguments();
        if (argus != null) {
            mAppWidgetId = argus.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (DEBUG) {
                Log.d(TAG, "mAppWidgetId: " + mAppWidgetId);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        if (DEBUG) {
            Log.d(TAG, "key: " + key);
        }
        if (KEY_ICON_PACK.equals(key)) {
            IconPackListDialog dialog = new IconPackListDialog();
            dialog.setTargetFragment(this, REQUEST_ICON_PACK);
            dialog.show(getFragmentManager(), IconPackListDialog.class.getName());
            return true;
        } else if (KEY_ICON_SIZE.equals(key)) {
            IconSizeListDialog dialog = new IconSizeListDialog();
            dialog.setTargetFragment(this, REQUEST_ICON_SIZE);
            dialog.show(getFragmentManager(), IconSizeListDialog.class.getName());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ICON_PACK) {
                final String iconPackPackageName = data.getStringExtra(IconPackListDialog.EXTRA_ICON_PACK_PACKAGE);
                final String iconPackTitle = data.getStringExtra(IconPackListDialog.EXTRA_ICON_PACK_TITLE);
                if (DEBUG) {
                    Log.d(TAG, "icon pack pkg: " + iconPackPackageName);
                }
                Preference pref = findPreference(KEY_ICON_PACK);
                if (pref != null) {
                    pref.setSummary(iconPackTitle);
                }
            } else if (requestCode == REQUEST_ICON_SIZE) {
                final int iconSize = data.getIntExtra(IconSizeListDialog.EXTRA_ICON_SIZE, IconSizeListDialog.ICON_SIZE_NORMAL);
                if (DEBUG) {
                    Log.d(TAG, "icon size: " + iconSize);
                }
            }
        }
    }
}
