package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import yhh.bj4.parasitic.launcher.R;
import yhh.bj4.parasitic.launcher.views.grid.IconGridAdapter;
import yhh.bj4.parasitic.launcher.views.grid.IconGridView;

/**
 * Created by Yen-Hsun_Huang on 2016/2/18.
 */
public class IconSelectView extends RelativeLayout implements IconGridAdapter.Callback, View.OnDragListener {
    private static final boolean DEBUG = true;
    private static final String TAG = "IconSelectView";

    private ImageButton mAddFolderButton;
    private IconGridView mGridView;
    private IconGridAdapter mGridAdapter;
    private Context mContext;
    private int mDragingIndex = -1;
    private int mMovingIndex = -1;

    public IconSelectView(Context context) {
        this(context, null);
    }

    public IconSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (DEBUG) Log.d(TAG, "onFinishInflate");
        mAddFolderButton = (ImageButton) findViewById(R.id.add_folder);
        mGridView = (IconGridView) findViewById(R.id.gridview);
        mGridAdapter = new IconGridAdapter(mContext);
        mGridAdapter.setCallback(this);
        mGridView.setAdapter(mGridAdapter);
        setOnDragListener(this);
        initDragListener();
    }

    private void initDragListener() {
        mGridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!mGridView.getViewTreeObserver().isAlive()) return false;
                mGridView.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i = 0; i < mGridView.getChildCount(); ++i) {
                    final View child = mGridView.getChildAt(i);

                    child.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            IconGridAdapter.ViewHolder holder = (IconGridAdapter.ViewHolder) v.getTag();
                            mDragingIndex = mMovingIndex = holder.getItemPosition();
                            Log.d(TAG, "position: " + holder.getItemPosition());
                            ClipData data = ClipData.newPlainText("", "");
                            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(child);
                            child.startDrag(data, shadowBuilder, child, 0);
                            child.setVisibility(View.INVISIBLE);
                            return true;
                        }
                    });
                }
                Log.i(TAG, "mGridView child count: " + mGridView.getCount());
                return false;
            }
        });
    }

    @Override
    public void onItemListChanged() {
        initDragListener();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        Log.v(TAG, "action: " + action);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.v(TAG, "x: " + x + ", y: " + y);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
            default:
                break;
        }
        return true;
    }
}
