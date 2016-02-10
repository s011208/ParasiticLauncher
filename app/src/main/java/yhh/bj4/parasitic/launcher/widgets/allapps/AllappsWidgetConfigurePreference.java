package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackListDialog;
import yhh.bj4.parasitic.launcher.utils.iconsize.IconSizeListDialog;
import yhh.bj4.parasitic.launcher.utils.images.BackgroundTypeChooserDialog;
import yhh.bj4.parasitic.launcher.utils.images.ColorChooserDialog;
import yhh.bj4.parasitic.launcher.utils.images.ImageAlphaDialog;
import yhh.bj4.parasitic.launcher.widgets.BaseWidgetPreferenceFragment;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurePreference extends BaseWidgetPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_ICON_PACK = "icon_pack";
    private static final String KEY_ICON_SIZE = "icon_size";
    private static final String KEY_APP_TITLE_VISIBILITY = "app_title_visibility";
    private static final String KEY_APP_TITLE_TEXT_COLOR = "title_text_color";
    private static final String KEY_WIDGET_BACKGROUND_TYPE = "widget_background_type";

    private static final int REQUEST_ICON_PACK = 1;
    private static final int REQUEST_ICON_SIZE = 2;
    private static final int REQUEST_APP_TITLE_TEXT_COLOR = 3;
    private static final int REQUEST_WIDGET_BACKGROUND_TYPE = 4;
    private static final int REQUEST_WIDGET_BACKGROUND_IMAGE_DATA = 5;
    private static final int REQUEST_WIDGET_BACKGROUND_IMAGE_ALPHA = 6;

    public static final String SPREF_KEY_ICON_SIZE = "icon_size";
    public static final String SPREF_KEY_ICON_PACK_PKG = "icon_pack_pkg";
    public static final String SPREF_KEY_ICON_PACK_TITLE = "icon_pack_title";
    public static final String SPREF_KEY_ICON_VISIBILITY = KEY_APP_TITLE_VISIBILITY;
    public static final String SPREF_KEY_APP_TITLE_TEXT_COLOR = "app_title_text_color";
    public static final String SPREF_KEY_WIDGET_BACKGROUND_TYPE = "widget_background_type";
    public static final String SPREF_KEY_WIDGET_BACKGROUND_COLOR = "widget_background_color";
    public static final String SPREF_KEY_WIDGET_BACKGROUND_PATH = "widget_background_path";
    public static final String SPREF_KEY_WIDGET_BACKGROUND_PATH_ALPHA = "widget_background_path_alpha";

    private int mIconSize = IconSizeListDialog.ICON_SIZE_NORMAL;
    private String mIconPackPackageName;
    private String mIconPackTitle;
    private int mWidgetBackgroundType = BackgroundTypeChooserDialog.TYPE_COLOR;
    private int mWidgetBackgroundColor = 0;
    private String mWidgetBackgroundPath = null;
    private int mWidgetBackgroundPathAlpha = 255;
    private boolean mIconVisibility;
    private int mAppTitleTextColor = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_allapps_widget_configuration);
        setIconPackSummary();
        setIconSizeSummary();
        setWidgetBackgroundTypeSummary();
        initIconVisibilityPreference();
    }

    private void setWidgetBackgroundTypeSummary() {
        Preference pref = findPreference(KEY_WIDGET_BACKGROUND_TYPE);
        if (pref == null) return;
        pref.setSummary(mWidgetBackgroundType == BackgroundTypeChooserDialog.TYPE_COLOR ? R.string.widget_background_type_color : R.string.widget_background_type_image);
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

    private void initIconVisibilityPreference() {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(KEY_APP_TITLE_VISIBILITY);
        if (pref == null) return;
        mIconVisibility = pref.isChecked();
        pref.setOnPreferenceChangeListener(this);
    }

    @Override
    public void initSharedPreferenceValues() {
        mIconPackPackageName = (String) getPreferenceValue(SPREF_KEY_ICON_PACK_PKG, null);
        mIconPackTitle = (String) getPreferenceValue(SPREF_KEY_ICON_PACK_TITLE, null);
        mIconSize = (Integer) getPreferenceValue(SPREF_KEY_ICON_SIZE, IconSizeListDialog.ICON_SIZE_NORMAL);
        mAppTitleTextColor = (Integer) getPreferenceValue(SPREF_KEY_APP_TITLE_TEXT_COLOR, 0);
        mWidgetBackgroundType = (Integer) getPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_TYPE, BackgroundTypeChooserDialog.TYPE_COLOR);
        mWidgetBackgroundColor = (Integer) getPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_COLOR, 0);
        mWidgetBackgroundPath = (String) getPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_PATH, null);
        mWidgetBackgroundPathAlpha = (Integer) getPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_PATH_ALPHA, 255);
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
        } else if (KEY_APP_TITLE_TEXT_COLOR.equals(key)) {
            ColorChooserDialog dialog = new ColorChooserDialog();
            dialog.setTargetFragment(this, REQUEST_APP_TITLE_TEXT_COLOR);
            Bundle argus = new Bundle();
            argus.putInt(ColorChooserDialog.COLOR_ALPHA, mAppTitleTextColor == 0 ? 255 : Color.alpha(mAppTitleTextColor));
            argus.putInt(ColorChooserDialog.COLOR_RED, mAppTitleTextColor == 0 ? 0 : Color.red(mAppTitleTextColor));
            argus.putInt(ColorChooserDialog.COLOR_GREEN, mAppTitleTextColor == 0 ? 0 : Color.green(mAppTitleTextColor));
            argus.putInt(ColorChooserDialog.COLOR_BLUE, mAppTitleTextColor == 0 ? 0 : Color.blue(mAppTitleTextColor));
            dialog.setArguments(argus);
            dialog.show(getFragmentManager(), ColorChooserDialog.class.getName());
        } else if (KEY_WIDGET_BACKGROUND_TYPE.equals(key)) {
            BackgroundTypeChooserDialog dialog = new BackgroundTypeChooserDialog();
            dialog.setTargetFragment(this, REQUEST_WIDGET_BACKGROUND_TYPE);
            Bundle argus = new Bundle();
            argus.putInt(ColorChooserDialog.COLOR_ALPHA, mWidgetBackgroundColor == 0 ? 255 : Color.alpha(mWidgetBackgroundColor));
            argus.putInt(ColorChooserDialog.COLOR_RED, mWidgetBackgroundColor == 0 ? 0 : Color.red(mWidgetBackgroundColor));
            argus.putInt(ColorChooserDialog.COLOR_GREEN, mWidgetBackgroundColor == 0 ? 0 : Color.green(mWidgetBackgroundColor));
            argus.putInt(ColorChooserDialog.COLOR_BLUE, mWidgetBackgroundColor == 0 ? 0 : Color.blue(mWidgetBackgroundColor));
            dialog.setArguments(argus);
            dialog.show(getFragmentManager(), BackgroundTypeChooserDialog.class.getName());
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
            } else if (requestCode == REQUEST_APP_TITLE_TEXT_COLOR) {
                mAppTitleTextColor = Color.argb(
                        data.getIntExtra(ColorChooserDialog.COLOR_ALPHA, 255),
                        data.getIntExtra(ColorChooserDialog.COLOR_RED, 0),
                        data.getIntExtra(ColorChooserDialog.COLOR_GREEN, 0),
                        data.getIntExtra(ColorChooserDialog.COLOR_BLUE, 0)
                );
                if (DEBUG) {
                    Log.d(TAG, "title text color: " + mAppTitleTextColor);
                }
                putPreferenceValue(SPREF_KEY_APP_TITLE_TEXT_COLOR, mAppTitleTextColor);
            } else if (requestCode == REQUEST_WIDGET_BACKGROUND_TYPE) {
                mWidgetBackgroundType = data.getIntExtra(BackgroundTypeChooserDialog.BACKGROUND_TYPE, BackgroundTypeChooserDialog.TYPE_COLOR);
                if (mWidgetBackgroundType == BackgroundTypeChooserDialog.TYPE_COLOR) {
                    mWidgetBackgroundColor = Color.argb(
                            data.getIntExtra(ColorChooserDialog.COLOR_ALPHA, 255),
                            data.getIntExtra(ColorChooserDialog.COLOR_RED, 0),
                            data.getIntExtra(ColorChooserDialog.COLOR_GREEN, 0),
                            data.getIntExtra(ColorChooserDialog.COLOR_BLUE, 0)
                    );
                } else if (mWidgetBackgroundType == BackgroundTypeChooserDialog.TYPE_IMAGE) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_WIDGET_BACKGROUND_IMAGE_DATA);
                }
                if (DEBUG) {
                    Log.d(TAG, "mBackgroundType: " + mWidgetBackgroundType + ", mBackgroundColor: " + mWidgetBackgroundColor);
                }
                putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_TYPE, mWidgetBackgroundType);
                putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_COLOR, mWidgetBackgroundColor);
                setWidgetBackgroundTypeSummary();
            } else if (requestCode == REQUEST_WIDGET_BACKGROUND_IMAGE_DATA) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mWidgetBackgroundPath = cursor.getString(columnIndex);

                    if (DEBUG) {
                        Log.d(TAG, "mWidgetBackgroundPath: " + mWidgetBackgroundPath);
                    }
                    putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_PATH, mWidgetBackgroundPath);
                    ImageAlphaDialog dialog = new ImageAlphaDialog();
                    Bundle argus = new Bundle();
                    argus.putString(ImageAlphaDialog.IMAGE_PATH, mWidgetBackgroundPath);
                    dialog.setArguments(argus);
                    dialog.setTargetFragment(this, REQUEST_WIDGET_BACKGROUND_IMAGE_ALPHA);
                    dialog.show(getFragmentManager(), ImageAlphaDialog.class.getName());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else if (requestCode == REQUEST_WIDGET_BACKGROUND_IMAGE_ALPHA) {
                mWidgetBackgroundPathAlpha = data.getIntExtra(ImageAlphaDialog.IMAGE_ALPHA, 255);
                if (DEBUG) {
                    Log.d(TAG, "mWidgetBackgroundPathAlpha: " + mWidgetBackgroundPathAlpha);
                }
                putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_PATH_ALPHA, mWidgetBackgroundPathAlpha);
            }
        } else {
            if (requestCode == REQUEST_WIDGET_BACKGROUND_IMAGE_DATA) {
                putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_TYPE, BackgroundTypeChooserDialog.TYPE_COLOR);
                putPreferenceValue(SPREF_KEY_WIDGET_BACKGROUND_COLOR, 0);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (KEY_APP_TITLE_VISIBILITY.equals(key)) {
            mIconVisibility = (Boolean) newValue;
            putPreferenceValue(SPREF_KEY_ICON_VISIBILITY, mIconVisibility);
            return true;
        }
        return false;
    }
}
