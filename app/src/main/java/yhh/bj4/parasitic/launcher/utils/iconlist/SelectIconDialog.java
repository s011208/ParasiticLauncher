package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.loader.InfoCache;
import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;
import yhh.bj4.parasitic.launcher.views.grid.IconGridView;

/**
 * Created by Yen-Hsun_Huang on 2016/2/16.
 */
public class SelectIconDialog extends DialogFragment {
    public static final String EXTRA_CHECKED_ITEMS = "checked_items";
    private IconGridView mIconGridView;
    private IconGridAdapter mIconGridAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIconGridView = new IconGridView(getActivity());
        mIconGridAdapter = new IconGridAdapter(getActivity());
        mIconGridView.setAdapter(mIconGridAdapter);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.select_icons).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getTargetFragment() != null) {
                    Intent intent = getActivity().getIntent();
                    if (intent == null) {
                        intent = new Intent();
                    }
                    ArrayList<InfoCache> checkedItem = mIconGridAdapter.getCheckedItems();
                    String[] cns = new String[checkedItem.size()];
                    for (int i = 0; i < cns.length; ++i) {
                        InfoCache cache = checkedItem.get(i);
                        cns[i] = cache.getComponentName().flattenToShortString();
                    }
                    intent.putExtra(EXTRA_CHECKED_ITEMS, cns);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setView(mIconGridView).create();
    }
}
