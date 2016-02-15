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
import yhh.bj4.parasitic.launcher.loader.IconLoader;
import yhh.bj4.parasitic.launcher.loader.InfoCache;

/**
 * Created by yenhsunhuang on 2016/2/6.
 */
public class IconGridAdapter extends BaseAdapter implements IconLoader.Callback {
    private static final boolean DEBUG = true;
    private static final String TAG = "IconGridAdapter";
    private final WeakReference<Context> mContext;
    private final WeakReference<LayoutInflater> mInflater;
    private final ArrayList<InfoCache> mItems = new ArrayList<>();
    private final WeakReference<IconLoader> mLoader;

    public IconGridAdapter(Context context) {
        mContext = new WeakReference<Context>(context);
        mInflater = new WeakReference<LayoutInflater>((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        IconLoader loader = IconLoader.getInstance(context);
        mLoader = new WeakReference(loader);
        loader.addCallback(this);
        mItems.addAll(loader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
        if (DEBUG) {
            Log.e(TAG, "mItems size: " + mItems.size());
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public InfoCache getItem(int position) {
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
            convertView = inflater.inflate(R.layout.icon_adapter_layout, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final InfoCache activityCache = mItems.get(position);
        holder.icon.setImageBitmap(activityCache.getBitmap());
        holder.title.setText(activityCache.getTitle());
        return convertView;
    }

    @Override
    public void onStartLoadingPackageName(String pkg) {
        if (DEBUG) {
            Log.d(TAG, "onStartLoadingPackageName pkg: " + pkg);
        }
    }

    @Override
    public void onFinishLoadingPackageName(String pkg) {
        if (DEBUG) {
            Log.d(TAG, "onFinishLoadingPackageName pkg: " + pkg);
        }
        final IconLoader loader = mLoader.get();
        if (loader == null) return;
        if (IconLoader.ICON_PACK_DEFAULT.equals(pkg) == false) return;
        mItems.clear();
        mItems.addAll(loader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
    }
}
