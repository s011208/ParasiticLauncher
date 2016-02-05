package yhh.bj4.parasitic.launcher.widgets.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yenhsunhuang on 2016/2/4.
 */
public abstract class Item {
    private static final String TAG = "Item";
    public static final String CLASS = "class";
    public static final String ID = "ID";
    public static final String X = "X";
    public static final String Y = "Y";
    public static final String ORDER = "order";
    public static final String TITLE = "title";
    public static final String INTENT = "intent";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String COMPONENT = "component";

    private long mId = -1;
    private int mOrder = -1;

    /**
     * get child-class's data only
     */
    public abstract JSONObject getData();

    /**
     * set child-class's data only
     */
    public abstract void readData(JSONObject json);

    public String getItemName() {
        return getClass().getName();
    }

    public long getItemId() {
        return mId;
    }

    public int getOrder() {
        return mOrder;
    }

    /**
     * get class's data (child included)
     */
    public String save() {
        JSONObject json = getData();
        try {
            json.put(ID, getItemId());
            json.put(ORDER, getOrder());
            json.put(CLASS, getItemName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * set class's data (child included)
     */
    public void read(final JSONObject json) {
        try {
            mId = json.getLong(ID);
            mOrder = json.getInt(ORDER);
            readData(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Item createItemInstance(JSONObject json) {
        try {
            if (json == null) return null;
            final String className = json.getString(CLASS);
            Item rtn = null;
            if (AppIcon.class.getName().equals(className)) {
                rtn = new AppIcon(json);
            } else {
                Log.d(TAG, "cannot instantiate class with: " + json.toString());
            }
            return rtn;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
