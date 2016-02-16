package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;
import yhh.bj4.parasitic.launcher.views.grid.IconGridView;

/**
 * Created by Yen-Hsun_Huang on 2016/2/16.
 */
public class SelectIconDialog extends DialogFragment {
    private IconGridView mIconGridView;
    private IconGridAdapter mIconGridAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIconGridView = new IconGridView(getActivity());
        mIconGridAdapter = new IconGridAdapter(getActivity());
        mIconGridView.setAdapter(mIconGridAdapter);
        return new AlertDialog.Builder(getActivity()).setView(mIconGridView).create();
    }
}
