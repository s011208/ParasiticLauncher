package yhh.bj4.parasitic.launcher.utils.images;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/9.
 */
public class ColorChooserDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String COLOR_RED = "color_red";
    public static final String COLOR_BLUE = "color_blue";
    public static final String COLOR_GREEN = "color_green";
    public static final String COLOR_ALPHA = "color_alpha";

    private ColorResultView mResultView;
    private SeekBar mAlpha, mRed, mGreen, mBlue;
    private int mDefaultAlpha, mDefaultRed, mDefaultGreen, mDefaultBlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argus = getArguments();
        if (argus != null) {
            mDefaultAlpha = argus.getInt(COLOR_ALPHA, 255);
            mDefaultRed = argus.getInt(COLOR_RED, 0);
            mDefaultGreen = argus.getInt(COLOR_BLUE, 0);
            mDefaultBlue = argus.getInt(COLOR_GREEN, 0);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.color_chooser_dialog, null);
        mResultView = (ColorResultView) v.findViewById(R.id.color_result);
        mAlpha = (SeekBar) v.findViewById(R.id.seek_bar_alpha);
        mRed = (SeekBar) v.findViewById(R.id.seek_bar_red);
        mGreen = (SeekBar) v.findViewById(R.id.seek_bar_green);
        mBlue = (SeekBar) v.findViewById(R.id.seek_bar_blue);

        mAlpha.setProgress(mDefaultAlpha);
        mRed.setProgress(mDefaultRed);
        mGreen.setProgress(mDefaultGreen);
        mBlue.setProgress(mDefaultBlue);

        mAlpha.setOnSeekBarChangeListener(this);
        mRed.setOnSeekBarChangeListener(this);
        mGreen.setOnSeekBarChangeListener(this);
        mBlue.setOnSeekBarChangeListener(this);

        mResultView.setARGB(mDefaultAlpha, mDefaultRed, mDefaultGreen, mDefaultBlue);

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.app_title_text_color).setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTargetFragment() != null) {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra(COLOR_ALPHA, mAlpha.getProgress());
                            intent.putExtra(COLOR_RED, mRed.getProgress());
                            intent.putExtra(COLOR_GREEN, mGreen.getProgress());
                            intent.putExtra(COLOR_BLUE, mBlue.getProgress());
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mResultView.setARGB(mAlpha.getProgress(), mRed.getProgress(), mGreen.getProgress(), mBlue.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
