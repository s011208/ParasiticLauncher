package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by Yen-Hsun_Huang on 2016/2/16.
 */
public class IconListDialog extends DialogFragment {
    public static final String EXTRA_ICON_LIST = "icon_list";
    public static final int ICON_LIST_ALL = 0;
    public static final int ICON_LIST_DOWNLOAD = 1;
    public static final int ICON_LIST_SYSTEM = 2;
    public static final int ICON_LIST_CUSTOMIZED = 3;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.icon_list)
                .setItems(R.array.icon_list_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTargetFragment() != null) {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra(EXTRA_ICON_LIST, which);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).create();
    }
}
