package yhh.bj4.parasitic.launcher.utils.sizelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/8.
 */
public class SizeListDialog extends DialogFragment {
    public static final String EXTRA_SIZE = "extra_size";
    public static final int SIZE_SMALL = 0;
    public static final int SIZE_NORMAL = 1;
    public static final int SIZE_LARGE = 2;

    public interface Callback {
        void onIconSizeSelected(int iconSize);
    }

    private Callback mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof Callback) {
            mCallback = (Callback) getActivity();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.size_list_dialog_title).setItems(R.array.icon_size_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int size = SIZE_NORMAL;
                switch (which) {
                    case 0:
                        size = SIZE_SMALL;
                        break;
                    case 1:
                        size = SIZE_NORMAL;
                        break;
                    case 2:
                        size = SIZE_LARGE;
                        break;
                }
                if (mCallback != null) {
                    mCallback.onIconSizeSelected(size);
                }
                if (getTargetFragment() != null) {
                    Intent intent = getActivity().getIntent();
                    intent.putExtra(EXTRA_SIZE, size);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
            }
        }).create();
    }

}
