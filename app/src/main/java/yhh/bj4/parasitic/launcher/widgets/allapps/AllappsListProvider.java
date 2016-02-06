package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;
import yhh.bj4.parasitic.launcher.loader.IconLoader;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsListProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "AllappsListProvider";
    private static final boolean DEBUG = true;
    private static final int ITEMS_PER_ROW = 4;
    private final ArrayList<ActivityInfoCache> listItemList = new ArrayList<>();
    private final SparseArray<RemoteViews> mAllappsContainerArray = new SparseArray<>();
    private final Context mContext;
    private final int mAppWidgetId;

    public AllappsListProvider(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }
    }

    private void loadData() {
        if (DEBUG) {
            Log.d(TAG, "loadData");
        }
        listItemList.clear();
        mAllappsContainerArray.clear();
        listItemList.addAll(IconLoader.getInstance(mContext).getAllActivitiesInfoCache().values());
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        int rows = listItemList.size() / ITEMS_PER_ROW;
        return listItemList.size() % ITEMS_PER_ROW == 0 ? rows : rows + 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews allAppListContainer = mAllappsContainerArray.get(position);
        if (allAppListContainer == null) {
            allAppListContainer = new RemoteViews(mContext.getPackageName(), R.layout.all_apps_widget_list_container);
            for (int i = position * ITEMS_PER_ROW; i < (position + 1) * ITEMS_PER_ROW && i < listItemList.size(); ++i) {
                RemoteViews icon = new RemoteViews(mContext.getPackageName(), R.layout.base_image_button);
                icon.setImageViewBitmap(R.id.base_icon, listItemList.get(i).getBitmap());
                allAppListContainer.addView(R.id.allapps_list_item_container, icon);
            }
            if (DEBUG) {
                Log.d(TAG, "add position: " + position);
            }
            mAllappsContainerArray.put(position, allAppListContainer);
        }
        return allAppListContainer;
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
}
