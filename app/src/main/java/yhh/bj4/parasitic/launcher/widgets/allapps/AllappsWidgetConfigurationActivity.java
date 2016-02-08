package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class AllappsWidgetConfigurationActivity extends Activity {
    private static final String TAG = "AllappsWidgetConfigure";
    private static final boolean DEBUG = true;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AllappsWidgetConfigurePreference mPreference;

    private TextView mOK, mCancel;

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
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.all_apps_widget_configuration_activity);
        mPreference = new AllappsWidgetConfigurePreference();
        Bundle extra = new Bundle();
        extra.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        mPreference.setArguments(extra);
        getFragmentManager().beginTransaction().replace(R.id.prefs_fragment_container,
                mPreference).commit();

        mOK = (TextView) findViewById(R.id.ok);
        mOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAppWidgetResultOk();
                finish();
            }
        });
        mCancel = (TextView) findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAppWidgetResultOk() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    @Override
    public void onBackPressed() {
        setAppWidgetResultOk();
        super.onBackPressed();
    }
}
