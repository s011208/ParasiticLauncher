package yhh.bj4.parasitic.launcher.utils.iconlist;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Iterator;

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
    private View mDraggingView;
    private int mDraggingIndex = -1;
    private int mMovingIndex = -1;
    private final HashMap<Integer, Point> mViewPosition = new HashMap<>();

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
        if (DEBUG) Log.v(TAG, "onFinishInflate");
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
                            startToDrag(v);
                            return true;
                        }
                    });
                }
                if (DEBUG)
                    Log.v(TAG, "mGridView child count: " + mGridView.getCount());
                return false;
            }
        });
    }

    private void startToDrag(View v) {
        IconGridAdapter.ViewHolder holder = (IconGridAdapter.ViewHolder) v.getTag();
        mDraggingIndex = mMovingIndex = holder.getItemPosition();
        if (DEBUG) Log.v(TAG, "startToDrag position: " + holder.getItemPosition());
        ClipData data = ClipData.newPlainText("", "");
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);
        v.setVisibility(View.INVISIBLE);
        mDraggingView = v;
    }

    private int getIndexByXY(final float x, final float y) {
        if (DEBUG)
            Log.v(TAG, "getIndexByXY, x: " + x + ", y: " + y);
        final int width = mGridView.getChildAt(0).getWidth();
        final int height = mGridView.getChildAt(0).getHeight();
        Iterator<Integer> positionIterator = mViewPosition.keySet().iterator();
        while (positionIterator.hasNext()) {
            final int key = positionIterator.next();
            Point point = mViewPosition.get(key);
            if (x > point.x && x < point.x + width
                    && y > point.y && y < point.y + height) {
                if (DEBUG) Log.v(TAG, "return key: " + key);
                return key;
            }
        }
        return mMovingIndex;
    }

    private void animateToChangePosition() {
        if (DEBUG)
            Log.v(TAG, "mDraggingIndex: " + mDraggingIndex + ", mMovingIndex: " + mMovingIndex);
        boolean moveToPrevious = mDraggingIndex < mMovingIndex;
        int startIndex = moveToPrevious ? mDraggingIndex : mMovingIndex;
        int endIndex = moveToPrevious ? mMovingIndex : mDraggingIndex;
        for (int i = 1; i < mGridView.getChildCount() - 1; ++i) {
            final View child = mGridView.getChildAt(i);
            int desIndex = moveToPrevious ? i + 1 : i - 1;
            child.animate().x(mViewPosition.get(desIndex).x).y(mViewPosition.get(desIndex).y).start();
        }
    }

    @Override
    public void onItemListChanged() {
        initDragListener();
    }

    private void fillInViewPositions() {
        mViewPosition.clear();
        for (int i = 0; i < mGridView.getChildCount(); ++i) {
            View child = mGridView.getChildAt(i);
            IconGridAdapter.ViewHolder holder = (IconGridAdapter.ViewHolder) child.getTag();
            mViewPosition.put(holder.getItemPosition(), new Point((int) child.getX(), (int) child.getY()));
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                fillInViewPositions();
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                mMovingIndex = getIndexByXY(x, y);
                Log.e(TAG, "ACTION_DRAG_LOCATION mMovingIndex: " + mMovingIndex);
                if (mMovingIndex == mDraggingIndex) {
                    break;
                }
                animateToChangePosition();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                mDraggingView.setVisibility(View.VISIBLE);
                for (int i = 0; i < mGridView.getChildCount(); ++i) {
                    final View child = mGridView.getChildAt(i);
                    child.setX(mViewPosition.get(i).x);
                    child.setY(mViewPosition.get(i).y);
                }
                break;
            default:
                break;
        }
        return true;
    }
}
