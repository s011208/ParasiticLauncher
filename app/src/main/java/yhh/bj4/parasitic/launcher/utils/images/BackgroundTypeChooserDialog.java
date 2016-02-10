package yhh.bj4.parasitic.launcher.utils.images;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/9.
 */
public class BackgroundTypeChooserDialog extends DialogFragment {
    public static final String BACKGROUND_TYPE = "background_type";
    public static final int TYPE_COLOR = 0;
    public static final int TYPE_IMAGE = 1;
    private static final int REQUEST_GET_COLOR = 0;
    private static final int REQUEST_GET_IMAGE = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] items = getActivity().getResources().getStringArray(R.array.widget_background_type);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.widget_background_choose_type_title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case TYPE_COLOR:
                                ColorChooserDialog openDialog = new ColorChooserDialog();
                                openDialog.setTargetFragment(BackgroundTypeChooserDialog.this, REQUEST_GET_COLOR);
                                if (getArguments() != null) {
                                    openDialog.setArguments(getArguments());
                                }
                                openDialog.show(getFragmentManager(), ColorChooserDialog.class.getName());
                                break;
                            case TYPE_IMAGE:
                                Intent intent = null;
                                if (getActivity() == null || getActivity().getIntent() == null) {
                                    intent = new Intent();
                                } else {
                                    intent = getActivity().getIntent();
                                }
                                intent.putExtra(BACKGROUND_TYPE, REQUEST_GET_IMAGE);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                                break;
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GET_COLOR) {
                if (getTargetFragment() != null) {
                    Intent intent = null;
                    if (getActivity() == null || getActivity().getIntent() == null) {
                        intent = new Intent();
                    } else {
                        intent = getActivity().getIntent();
                    }
                    intent.putExtra(ColorChooserDialog.COLOR_ALPHA, data.getIntExtra(ColorChooserDialog.COLOR_ALPHA, 100));
                    intent.putExtra(ColorChooserDialog.COLOR_RED, data.getIntExtra(ColorChooserDialog.COLOR_RED, 0));
                    intent.putExtra(ColorChooserDialog.COLOR_GREEN, data.getIntExtra(ColorChooserDialog.COLOR_GREEN, 0));
                    intent.putExtra(ColorChooserDialog.COLOR_BLUE, data.getIntExtra(ColorChooserDialog.COLOR_BLUE, 0));
                    intent.putExtra(BACKGROUND_TYPE, TYPE_COLOR);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
            } else if (requestCode == REQUEST_GET_IMAGE) {
            }
        }
    }
}
