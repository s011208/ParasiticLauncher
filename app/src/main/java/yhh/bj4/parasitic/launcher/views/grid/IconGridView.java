package yhh.bj4.parasitic.launcher.views.grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridView;

import yhh.bj4.parasitic.launcher.R;

/**
 * Created by yenhsunhuang on 2016/2/15.
 */
public class IconGridView extends GridView {
    private int mIconSize;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    public IconGridView(Context context) {
        this(context, null);
    }

    public IconGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context);
        initLayoutParams();
    }

    private void initParams(Context context) {
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.normal_icon_size);
        mHorizontalSpacing = context.getResources().getDimensionPixelSize(R.dimen.icon_grid_view_h_spacing);
        mVerticalSpacing = context.getResources().getDimensionPixelSize(R.dimen.icon_grid_view_v_spacing);
    }

    private void initLayoutParams() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setLayoutParams(layoutParams);
        }
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setGravity(Gravity.CENTER);
        setColumnWidth(mIconSize);
        setHorizontalSpacing(mHorizontalSpacing);
        setVerticalSpacing(mVerticalSpacing);
        setNumColumns(GridView.AUTO_FIT);
        setVerticalScrollBarEnabled(false);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }
}
