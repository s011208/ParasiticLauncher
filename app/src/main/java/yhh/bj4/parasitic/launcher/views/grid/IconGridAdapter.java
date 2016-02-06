package yhh.bj4.parasitic.launcher.views.grid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.ActivityInfoCache;
import yhh.bj4.parasitic.launcher.loader.IconLoader;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class IconGridAdapter extends BaseAdapter implements IconLoader.Callback {
    private static final boolean DEBUG = true;
    private static final String TAG = "IconGridAdapter";
    private final WeakReference<Context> mContext;
    private final WeakReference<LayoutInflater> mInflater;
    private final ArrayList<ActivityInfoCache> mItems = new ArrayList<ActivityInfoCache>();
    private final WeakReference<IconLoader> mLoader;

    public IconGridAdapter(Context context) {
        mContext = new WeakReference<Context>(context);
        mInflater = new WeakReference<LayoutInflater>((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        IconLoader loader = IconLoader.getInstance(context);
        mLoader = new WeakReference(loader);
        loader.addCallback(this);
        mItems.addAll(loader.getAllActivitiesInfoCache().values());
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ActivityInfoCache getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mInflater.get();
        if (inflater == null) return null;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.normal_app_icon_layout, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ActivityInfoCache activityCache = mItems.get(position);
        holder.icon.setImageBitmap(activityCache.getBitmap());
        holder.title.setText(activityCache.getTitle());
        return convertView;
    }

    @Override
    public void onRefresh() {
        if (DEBUG)
            Log.d(TAG, "onRefresh called");
        final IconLoader loader = mLoader.get();
        if (loader == null) return;
        mItems.clear();
        mItems.addAll(loader.getAllActivitiesInfoCache().values());
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
    }
}
