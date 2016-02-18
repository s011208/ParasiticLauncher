package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by Yen-Hsun_Huang on 2016/2/16.
 */
public class IconListDialog extends DialogFragment {
    public static final int REQUEST_SELECT_CUSTOMIZED_ICON_LIST = 0;
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
                        if (which == ICON_LIST_CUSTOMIZED) {
//                            SelectIconDialog selectIconDialog = new SelectIconDialog();
//                            selectIconDialog.setTargetFragment(IconListDialog.this, REQUEST_SELECT_CUSTOMIZED_ICON_LIST);
//                            selectIconDialog.show(getFragmentManager(), SelectIconDialog.class.getName());
                            getActivity().startActivity(new Intent(getActivity(), IconSelectActivity.class));
                        } else {
                            if (getTargetFragment() != null) {
                                Intent intent = getActivity().getIntent();
                                if (intent == null) {
                                    intent = new Intent();
                                }
                                intent.putExtra(EXTRA_ICON_LIST, which);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                        }
                    }
                }).create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_SELECT_CUSTOMIZED_ICON_LIST == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                String[] selectedItem = data.getStringArrayExtra(SelectIconDialog.EXTRA_CHECKED_ITEMS);

                Intent intent = null;
                if (getActivity() != null) {
                    intent = getActivity().getIntent();
                }
                if (intent == null) {
                    intent = new Intent();
                }
                intent.putExtra(EXTRA_ICON_LIST, ICON_LIST_CUSTOMIZED);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                for (String item : selectedItem) {
                    Log.e("QQQQ", "item: " + item);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
