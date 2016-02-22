package yhh.bj4.parasitic.launcher.utils.iconsorting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.widgets.allapps.gridview.AllappsWidgetConfigurePreference;

/**
 * Created by yenhsunhuang on 2016/2/11.
 */
public class IconSortingDialog extends DialogFragment {
    public static final String SORTING_RULE = "sorting_rule_extra";
    private int mSortingRule = AllappsWidgetConfigurePreference.SORTING_RULE_A_TO_Z;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argus = getArguments();
        if (argus != null) {
            mSortingRule = argus.getInt(SORTING_RULE, AllappsWidgetConfigurePreference.SORTING_RULE_A_TO_Z);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.sorting_rule)
                .setSingleChoiceItems(R.array.sorting_rule_list, mSortingRule, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSortingRule = which;
                        if (getTargetFragment() != null) {
                            Intent intent = getActivity().getIntent();
                            intent.putExtra(SORTING_RULE, mSortingRule);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        }
                        dismiss();
                    }
                }).create();
    }
}
