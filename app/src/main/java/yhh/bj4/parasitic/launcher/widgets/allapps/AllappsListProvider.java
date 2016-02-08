package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;
import yhh.bj4.parasitic.launcher.loader.IconLoader;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsListProvider implements RemoteViewsService.RemoteViewsFactory, IconLoader.Callback {
    private static final String TAG = "AllappsListProvider";
    private static final boolean DEBUG = true;
    private ArrayList<ActivityInfoCache> listItemList = new ArrayList<>();
    private SparseArray<RemoteViews> mAllappsContainerArray = new SparseArray<>();
    private Context mContext;
    private final int mAppWidgetId;
    private final SharedPreferences mPrefs;
    private String mApplyIconPackPkg;
    private final IconLoader mIconLoader;

    public AllappsListProvider(Context context, Intent intent) {
        mContext = context;
        mIconLoader = IconLoader.getInstance(mContext);
        mIconLoader.addCallback(this);
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mPrefs = context.getSharedPreferences(AllappsWidgetConfigurePreference.class.getSimpleName() + String.valueOf(mAppWidgetId), Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate() {
        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }
    }

    private void loadData() {
        mApplyIconPackPkg = mPrefs.getString(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_PACK_PKG, IconLoader.ICON_PACK_DEFAULT);
        if (DEBUG) {
            Log.d(TAG, "loadData, icon pack: " + mApplyIconPackPkg);
        }

        listItemList.clear();
        mAllappsContainerArray.clear();
        if (mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg) == null) {
            listItemList.addAll(mIconLoader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
            mIconLoader.requestToLoadIconPack(mApplyIconPackPkg);
            if (DEBUG) {
                Log.d(TAG, "request to load: " + mApplyIconPackPkg);
            }
        } else {
            listItemList.addAll(mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg).values());
        }
        Collections.sort(listItemList, new Comparator<ActivityInfoCache>() {
            @Override
            public int compare(ActivityInfoCache lhs, ActivityInfoCache rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mAllappsContainerArray.clear();
        listItemList.clear();
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final ActivityInfoCache info = listItemList.get(position);
        RemoteViews iconContainer = new RemoteViews(mContext.getPackageName(), R.layout.normal_app_icon_layout);
        iconContainer.setImageViewBitmap(R.id.icon, info.getBitmap());
        iconContainer.setTextViewText(R.id.title, info.getTitle());
        Intent intent = new Intent();
        intent.putExtra(AllappsWidgetProvider.ON_ALL_APPS_ITEM_CLICK_INDEX, position);
        intent.putExtra(AllappsWidgetProvider.EXTRA_COMPONENTNAME, info.getComponentName().flattenToShortString());
        iconContainer.setOnClickFillInIntent(R.id.normal_app_icon_container, intent);
        return iconContainer;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onRefresh(String iconPackPkgName) {
        if (iconPackPkgName.equals(mApplyIconPackPkg)) {
            AllappsWidgetProvider.notifyDataSetChanged(mContext, mAppWidgetId);
        }
    }
}
