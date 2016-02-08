package yhh.bj4.parasitic.launcher.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by yenhsunhuang on 2016/2/8.
 */
public abstract class BaseWidgetPreferenceFragment extends PreferenceFragment {
    public static final boolean DEBUG = true;
    public static final String TAG = "AllappsWidgetConfigure";
    private final HashMap<String, Object> mPrefMap = new HashMap<String, Object>();

    private SharedPreferences mPrefs;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argus = getArguments();
        if (argus != null) {
            mAppWidgetId = argus.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (DEBUG) {
                Log.d(TAG, "mAppWidgetId: " + mAppWidgetId);
            }
        }
        mPrefs = getActivity().getSharedPreferences(getClass().getSimpleName() + String.valueOf(mAppWidgetId), Context.MODE_PRIVATE);
        readAllPreferenceValue();
    }

    public void putPreferenceValue(String key, Object value) {
        mPrefMap.put(key, value);
    }

    public void saveAllPreferenceValue() {
        if (mPrefMap.size() == 0) return;
        SharedPreferences.Editor editor = mPrefs.edit();
        Iterator<String> iterator = mPrefMap.keySet().iterator();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            Object value = mPrefMap.get(key);
            if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            }
        }
        editor.commit();
    }

    public void readAllPreferenceValue() {
        mPrefMap.putAll(mPrefs.getAll());
        initSharedPreferenceValues();
    }

    public Object getPreferenceValue(String key, Object defaultValue) {
        Object rtn = mPrefMap.get(key);
        return rtn == null ? defaultValue : rtn;
    }

    public abstract void initSharedPreferenceValues();
}
