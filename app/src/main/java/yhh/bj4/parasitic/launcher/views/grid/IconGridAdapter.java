package yhh.bj4.parasitic.launcher.views.grid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    public interface Callback {
        void onItemListChanged();
    }

    private static final boolean DEBUG = true;
    private static final String TAG = "IconGridAdapter";
    private final WeakReference<Context> mContext;
    private final WeakReference<LayoutInflater> mInflater;
    private final ArrayList<InfoCache> mItems = new ArrayList<>();
    private final WeakReference<IconLoader> mLoader;
    private boolean mShowCheckBox = true;
    private final ArrayList<Boolean> mCheckedItem = new ArrayList<>();
    private boolean mUseCustomizedList = false;
    private WeakReference<Callback> mCallback;

    public IconGridAdapter(Context context) {
        mContext = new WeakReference<Context>(context);
        mInflater = new WeakReference<LayoutInflater>((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        IconLoader loader = IconLoader.getInstance(context);
        mLoader = new WeakReference(loader);
        loader.addCallback(this);
        if (!mUseCustomizedList) {
            mItems.addAll(loader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
        }
        setCheckBoxVisibility(mShowCheckBox);
        if (DEBUG) {
            Log.e(TAG, "mItems size: " + mItems.size() + ", mUseCustomizedList: " + mUseCustomizedList);
        }
    }

    public void setCallback(Callback cb) {
        mCallback = new WeakReference<Callback>(cb);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mInflater.get();
        if (inflater == null) return null;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.icon_adapter_layout, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final InfoCache activityCache = mItems.get(position);
        holder.icon.setImageBitmap(activityCache.getBitmap());
        holder.title.setText(activityCache.getTitle());
        holder.position = position;

        if (!mCheckedItem.isEmpty()) {
            holder.checkbox.setOnCheckedChangeListener(null);
            holder.checkbox.setVisibility(mShowCheckBox ? View.VISIBLE : View.INVISIBLE);
            holder.checkbox.setChecked(mCheckedItem.get(position));
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCheckedItem.set(position, isChecked);
                    if (DEBUG) Log.v(TAG, "position: " + position + ", checked: " + isChecked);
                }
            });
        }
        return convertView;
    }

    public void setCheckBoxVisibility(boolean show) {
        mShowCheckBox = show;
        mCheckedItem.clear();
        for (int i = 0; i < mItems.size(); ++i) {
            mCheckedItem.add(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onStartLoadingPackageName(String pkg) {
        if (DEBUG) {
            Log.d(TAG, "onStartLoadingPackageName pkg: " + pkg);
        }
    }

    public void setCustomizedList(ArrayList<InfoCache> cache) {
        mUseCustomizedList = true;
        mItems.clear();
        mItems.addAll(cache);
        setCheckBoxVisibility(mShowCheckBox);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mCallback == null) return;
        final Callback cb = mCallback.get();
        if (cb == null) return;
        cb.onItemListChanged();
    }

    @Override
    public void onFinishLoadingPackageName(String pkg) {
        if (mUseCustomizedList) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "onFinishLoadingPackageName pkg: " + pkg);
        }
        final IconLoader loader = mLoader.get();
        if (loader == null) return;
        if (IconLoader.ICON_PACK_DEFAULT.equals(pkg) == false) return;
        mItems.clear();
        mItems.addAll(loader.getAllActivitiesInfoCache(IconLoader.ICON_PACK_DEFAULT).values());
        setCheckBoxVisibility(mShowCheckBox);
        notifyDataSetChanged();
    }

    public ArrayList<InfoCache> getCheckedItems() {
        final ArrayList<InfoCache> rtn = new ArrayList<>();
        for (int i = 0; i < mItems.size(); ++i) {
            if (mCheckedItem.get(i)) {
                rtn.add(mItems.get(i));
            }
        }
        return rtn;
    }

    public static class ViewHolder {
        ImageView icon;
        TextView title;
        CheckBox checkbox;
        int position;

        public int getItemPosition() {
            return position;
        }
    }
}
