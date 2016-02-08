package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackListDialog;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurePreference extends PreferenceFragment {
    private static final boolean DEBUG = true;
    private static final String TAG = "AllappsWidgetConfigure";
    private static final String KEY_ICON_PACK = "icon_pack";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_allapps_widget_configuration);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        if (KEY_ICON_PACK.equals(key)) {
            if (DEBUG) {
                Log.d(TAG, KEY_ICON_PACK + " clicked");
            }
            IconPackListDialog dialog = new IconPackListDialog();
            dialog.show(getFragmentManager(), IconPackListDialog.class.getName());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
