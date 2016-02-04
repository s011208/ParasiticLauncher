package yhh.bj4.parasitic.launcher.widgets.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yenhsunhuang on 2016/2/4.
 */
public class AppIcon extends Item {

    private int mX = -1;
    private int mY = -1;
    private int mWidth = -1;
    private int mHeight = -1;
    private String mTitle = null;
    private String mIntent = null;
    private String mComponent = null;

    public AppIcon() {

    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        try {
            json.put(X, mX);
            json.put(Y, mY);
            json.put(WIDTH, mWidth);
            json.put(HEIGHT, mHeight);
            json.put(TITLE, mTitle);
            json.put(INTENT, mIntent);
            json.put(COMPONENT, mComponent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void readData(JSONObject json) {
        if (json == null) {
            throw new RuntimeException("null json in AppIcon");
        }
        try {
            mX = json.getInt(X);
            mY = json.getInt(Y);
            mWidth = json.getInt(WIDTH);
            mHeight = json.getInt(HEIGHT);
            mTitle = json.getString(TITLE);
            mIntent = json.getString(INTENT);
            mComponent = json.getString(COMPONENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
