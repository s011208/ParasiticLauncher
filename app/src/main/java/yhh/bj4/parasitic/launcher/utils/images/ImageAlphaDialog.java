package yhh.bj4.parasitic.launcher.utils.images;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/10.
 */
public class ImageAlphaDialog extends DialogFragment {
    public static final String IMAGE_PATH = "image_path";
    public static final String IMAGE_ALPHA = "image_alpha";
    private String mImagePath = null;
    private SeekBar mAlphaBar;
    private ImageView mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mImagePath = args.getString(IMAGE_PATH, null);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View content = getActivity().getLayoutInflater().inflate(R.layout.image_alpha_dialog, null);
        mAlphaBar = (SeekBar) content.findViewById(R.id.seek_bar_alpha);
        mAlphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPreview.setImageAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPreview = (ImageView) content.findViewById(R.id.preview);
        mPreview.setImageBitmap(BitmapFactory.decodeFile(mImagePath));
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.widget_background_image_alpha_dialog_title)
                .setView(content).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTargetFragment() != null) {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra(IMAGE_ALPHA, mAlphaBar.getProgress());
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).create();
        return dialog;
    }
}
