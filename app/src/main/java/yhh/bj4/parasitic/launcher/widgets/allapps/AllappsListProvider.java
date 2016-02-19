package yhh.bj4.parasitic.launcher.widgets.allapps;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Collections;

import yhh.bj4.parasitic.launcher.LauncherProvider;
import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;
import yhh.bj4.parasitic.launcher.loader.IconLoader;
import yhh.bj4.parasitic.launcher.loader.InfoCache;
import yhh.bj4.parasitic.launcher.utils.iconlist.IconListDialog;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPack;
import yhh.bj4.parasitic.launcher.utils.iconpack.IconPackHelper;
import yhh.bj4.parasitic.launcher.utils.iconsorting.SortClickTime;
import yhh.bj4.parasitic.launcher.utils.iconsorting.SortFrequency;
import yhh.bj4.parasitic.launcher.utils.iconsorting.SortFromAToZ;
import yhh.bj4.parasitic.launcher.utils.iconsorting.SortFromZToA;
import yhh.bj4.parasitic.launcher.utils.sizelist.SizeListDialog;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class AllappsListProvider implements RemoteViewsService.RemoteViewsFactory, IconLoader.Callback {
    private static final String TAG = "AllappsListProvider";
    private static final boolean DEBUG = true;
    private ArrayList<InfoCache> mListItem = new ArrayList<>();
    private SparseArray<RemoteViews> mAllappsContainerArray = new SparseArray<>();
    private Context mContext;
    private final int mAppWidgetId;
    private final SharedPreferences mPrefs;
    private String mApplyIconPackPkg;
    private final IconLoader mIconLoader;
    private boolean mShowIcon = true;
    private int mTextSizeIndex = SizeListDialog.SIZE_NORMAL;
    private int mAppLayoutResource;
    private int mTextSize, mIconSize;
    private int mAppTitleTextColor = 0;
    private int mSortingRule = AllappsWidgetConfigurePreference.SORTING_RULE_A_TO_Z;
    private int mIconList = IconListDialog.ICON_LIST_ALL;

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
        Log.e(TAG, "onCreate id: " + mAppWidgetId);
    }

    private void loadConfiguration() {
        mApplyIconPackPkg = mPrefs.getString(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_PACK_PKG, IconLoader.ICON_PACK_DEFAULT);
        mShowIcon = mPrefs.getBoolean(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_VISIBILITY, true);
        mIconSize = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_SIZE, SizeListDialog.SIZE_NORMAL);
        mTextSizeIndex = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_APP_TITLE_TEXT_SIZE, SizeListDialog.SIZE_NORMAL);
        mAppTitleTextColor = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_APP_TITLE_TEXT_COLOR, 0);
        mIconList = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_ICON_LIST, IconListDialog.ICON_LIST_ALL);
        switch (mTextSizeIndex) {
            case SizeListDialog.SIZE_SMALL:
                mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.small_app_icon_layout_title_text_size);
                break;
            case SizeListDialog.SIZE_NORMAL:
                mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.normal_app_icon_layout_title_text_size);
                break;
            case SizeListDialog.SIZE_LARGE:
                mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.large_app_icon_layout_title_text_size);
                break;
            default:
                mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.normal_app_icon_layout_title_text_size);
        }
        switch (mIconSize) {
            case SizeListDialog.SIZE_SMALL:
                mAppLayoutResource = R.layout.small_app_icon_layout;
                break;
            case SizeListDialog.SIZE_NORMAL:
                mAppLayoutResource = R.layout.normal_app_icon_layout;
                break;
            case SizeListDialog.SIZE_LARGE:
                mAppLayoutResource = R.layout.large_app_icon_layout;
                break;
            default:
                mAppLayoutResource = R.layout.normal_app_icon_layout;
        }
        mSortingRule = mPrefs.getInt(AllappsWidgetConfigurePreference.SPREF_KEY_SORTING_RULE, AllappsWidgetConfigurePreference.SORTING_RULE_A_TO_Z);
        if (DEBUG) {
            Log.i(TAG, "loadData, icon pack: " + mApplyIconPackPkg + ", mShowIcon: " + mShowIcon
                    + ", mTextSizeIndex: " + mTextSizeIndex + ", mAppTitleTextColor: " + mAppTitleTextColor
                    + ", mSortingRule: " + mSortingRule
                    + ", mIconList: " + mIconList);
        }
    }

    private void loadData() {
        Log.e(TAG, "loadData id: " + mAppWidgetId);
        loadConfiguration();
        boolean find = false;
        for (IconPack pack : IconPackHelper.getInstance(mContext).getIconPackList()) {
            if (pack.getIconPackPackageName().equals(mApplyIconPackPkg)) {
                find = true;
                break;
            }
        }
        if (!find && mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg) == null) {
            // use default if not find
            mApplyIconPackPkg = IconLoader.ICON_PACK_DEFAULT;
        }
        mListItem.clear();
        mAllappsContainerArray.clear();
        if (mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg) == null || mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg).isEmpty()) {
            ArrayList<InfoCache> providerCache = getInfoCacheFromProvider(mApplyIconPackPkg);
            if (providerCache.isEmpty()) {
                mListItem.addAll(mIconLoader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
            } else {
                mListItem.addAll(providerCache);
            }
            mIconLoader.requestToLoadIconPack(mApplyIconPackPkg, false);
            if (DEBUG) {
                Log.d(TAG, "request to load: " + mApplyIconPackPkg);
            }
        } else {
            mListItem.addAll(mIconLoader.getAllActivitiesInfoCache(mApplyIconPackPkg).values());
        }
        if (mIconList == IconListDialog.ICON_LIST_ALL) {
            // do nothing
        } else if (mIconList == IconListDialog.ICON_LIST_DOWNLOAD) {
            final int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            for (int i = mListItem.size() - 1; i >= 0; --i) {
                InfoCache cache = mListItem.get(i);
                if ((cache.getPackageInfoFlag() & mask) != 0) {
                    mListItem.remove(i);
                }
            }
        } else if (mIconList == IconListDialog.ICON_LIST_SYSTEM) {
            final int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            for (int i = mListItem.size() - 1; i >= 0; --i) {
                InfoCache cache = mListItem.get(i);
                if ((cache.getPackageInfoFlag() & mask) == 0) {
                    mListItem.remove(i);
                }
            }
        }

        IconLoader.fillUpInfoCacheDatabaseData(mListItem, mContext);

        switch (mSortingRule) {
            case AllappsWidgetConfigurePreference.SORTING_RULE_A_TO_Z:
                Collections.sort(mListItem, new SortFromAToZ());
                break;
            case AllappsWidgetConfigurePreference.SORTING_RULE_Z_TO_A:
                Collections.sort(mListItem, new SortFromZToA());
                break;
            case AllappsWidgetConfigurePreference.SORTING_RULE_MOSTLY:
                Collections.sort(mListItem, new SortFrequency());
                break;
            case AllappsWidgetConfigurePreference.SORTING_RULE_RECENTLY:
                Collections.sort(mListItem, new SortClickTime());
                break;
            default:
                Collections.sort(mListItem, new SortFromAToZ());
                break;
        }
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mAllappsContainerArray.clear();
        mListItem.clear();
        mIconLoader.removeCallback(this);
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mListItem.size() <= position) return null;
        final InfoCache info = mListItem.get(position);
        RemoteViews iconContainer = new RemoteViews(mContext.getPackageName(), mAppLayoutResource);
        iconContainer.setImageViewBitmap(R.id.icon, info.getBitmap());
        iconContainer.setTextViewText(R.id.title, info.getTitle());
        iconContainer.setViewVisibility(R.id.title, mShowIcon ? View.VISIBLE : View.GONE);
        iconContainer.setTextViewTextSize(R.id.title, TypedValue.COMPLEX_UNIT_PX, mTextSize);
        Intent intent = new Intent();
        intent.putExtra(AllappsWidgetProvider.ON_ALL_APPS_ITEM_CLICK_INDEX, position);
        intent.putExtra(AllappsWidgetProvider.EXTRA_COMPONENTNAME, info.getComponentName().flattenToShortString());
        iconContainer.setOnClickFillInIntent(R.id.normal_app_icon_container, intent);
        if (mAppTitleTextColor != 0) {
            iconContainer.setTextColor(R.id.title, mAppTitleTextColor);
        }
        return iconContainer;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
    public void onStartLoadingPackageName(String pkg) {

    }

    @Override
    public void onFinishLoadingPackageName(String pkg) {
        if (pkg.equals(mApplyIconPackPkg)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
            manager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.allapps_list);
        }
    }

    private ArrayList<InfoCache> getInfoCacheFromProvider(String iconPack) {
        final ArrayList<InfoCache> rtn = new ArrayList<InfoCache>();
        Cursor c = mContext.getContentResolver().query(LauncherProvider.URI_INFO_CACHE(iconPack), null, null, null, null);
        if (c != null) {
            try {
                while (c.moveToNext()) {
                    rtn.add(new ActivityInfoCache(c));
                }
            } finally {
                c.close();
            }
        }
        return rtn;
    }
}
