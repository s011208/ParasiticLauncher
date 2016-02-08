package yhh.bj4.parasitic.launcher.utils.iconpack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/7.
 */
public class IconPackListDialog extends DialogFragment {
    public interface Callback {
        void onIconPackSelected(String iconPackPackageName);
    }

    private IconPackHelper mIconPackHelper;
    private AlertDialog mDialog;
    private IconPackAdapter mAdapter;
    private Callback mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIconPackHelper = IconPackHelper.getInstance(getActivity());
        mAdapter = new IconPackAdapter(getActivity(), mIconPackHelper);
        if (getActivity() instanceof Callback) {
            mCallback = (Callback) getActivity();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.icon_pack_list).setAdapter(mAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mCallback == null) return;
                mCallback.onIconPackSelected(mAdapter.getItem(which).getIconPackPackageName());
            }
        }).create();
        return mDialog;
    }


    private static class IconPackAdapter extends BaseAdapter implements IconPackHelper.Callback {
        private final LayoutInflater mInflater;
        private final IconPackHelper mIconPackHelper;
        private final ArrayList<IconPack> mIconPackList;

        public IconPackAdapter(Context context, IconPackHelper helper) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mIconPackHelper = helper;
            mIconPackHelper.addCallback(this);
            mIconPackList = new ArrayList<>(mIconPackHelper.getIconPackList());
        }

        @Override
        public int getCount() {
            return mIconPackList.size();
        }

        @Override
        public IconPack getItem(int position) {
            return mIconPackList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.icon_pack_dialog_list_adapter, null);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
                holder.mTxt = (TextView) convertView.findViewById(R.id.txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mIcon.setImageDrawable(getItem(position).getIconPackPackageDrawable());
            holder.mTxt.setText(getItem(position).getIconPackPackageTitle());
            return convertView;
        }

        @Override
        public void onLoadStart() {

        }

        @Override
        public void onLoadFinish() {
            mIconPackList.clear();
            mIconPackList.addAll(mIconPackHelper.getIconPackList());
            notifyDataSetChanged();
        }

        private static class ViewHolder {
            TextView mTxt;
            ImageView mIcon;
        }
    }
}
