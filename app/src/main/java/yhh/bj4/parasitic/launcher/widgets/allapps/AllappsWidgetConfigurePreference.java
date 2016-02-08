package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackListDialog;
import yhh.bj4.parasitic.launcher.utils.iconsize.IconSizeListDialog;
import yhh.bj4.parasitic.launcher.widgets.BaseWidgetPreferenceFragment;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurePreference extends BaseWidgetPreferenceFragment {
    private static final String KEY_ICON_PACK = "icon_pack";
    private static final String KEY_ICON_SIZE = "icon_size";

    private static final int REQUEST_ICON_PACK = 1;
    private static final int REQUEST_ICON_SIZE = 2;

    public static final String SPREF_KEY_ICON_SIZE = "icon_size";
    public static final String SPREF_KEY_ICON_PACK_PKG = "icon_pack_pkg";
    public static final String SPREF_KEY_ICON_PACK_TITLE = "icon_pack_title";

    private int mIconSize = IconSizeListDialog.ICON_SIZE_NORMAL;
    private String mIconPackPackageName;
    private String mIconPackTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_allapps_widget_configuration);
        setIconPackSummary();
        setIconSizeSummary();
    }

    private void setIconPackSummary() {
        if (mIconPackTitle == null) return;
        Preference pref = findPreference(KEY_ICON_PACK);
        if (pref == null) return;
        pref.setSummary(mIconPackTitle);
    }

    private void setIconSizeSummary() {
        Preference pref = findPreference(KEY_ICON_SIZE);
        if (pref == null) return;
        int summaryRes = R.string.icon_size_normal;
        switch (mIconSize) {
            case IconSizeListDialog.ICON_SIZE_SMALL:
                summaryRes = R.string.icon_size_small;
                break;
            case IconSizeListDialog.ICON_SIZE_NORMAL:
                summaryRes = R.string.icon_size_normal;
                break;
            case IconSizeListDialog.ICON_SIZE_LARGE:
                summaryRes = R.string.icon_size_large;
                break;
        }
        pref.setSummary(summaryRes);
    }

    @Override
    public void initSharedPreferenceValues() {
        mIconPackPackageName = (String) getPreferenceValue(SPREF_KEY_ICON_PACK_PKG, null);
        mIconPackTitle = (String) getPreferenceValue(SPREF_KEY_ICON_PACK_TITLE, null);
        mIconSize = (Integer) getPreferenceValue(SPREF_KEY_ICON_SIZE, IconSizeListDialog.ICON_SIZE_NORMAL);
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
                mIconPackPackageName = data.getStringExtra(IconPackListDialog.EXTRA_ICON_PACK_PACKAGE);
                mIconPackTitle = data.getStringExtra(IconPackListDialog.EXTRA_ICON_PACK_TITLE);
                if (DEBUG) {
                    Log.d(TAG, "icon pack pkg: " + mIconPackPackageName + ", title: " + mIconPackTitle);
                }
                setIconPackSummary();
                putPreferenceValue(SPREF_KEY_ICON_PACK_PKG, mIconPackPackageName);
                putPreferenceValue(SPREF_KEY_ICON_PACK_TITLE, mIconPackTitle);
            } else if (requestCode == REQUEST_ICON_SIZE) {
                mIconSize = data.getIntExtra(IconSizeListDialog.EXTRA_ICON_SIZE, IconSizeListDialog.ICON_SIZE_NORMAL);
                if (DEBUG) {
                    Log.d(TAG, "icon size: " + mIconSize);
                }
                setIconSizeSummary();
                putPreferenceValue(SPREF_KEY_ICON_SIZE, mIconSize);
            }
        }
    }
}
