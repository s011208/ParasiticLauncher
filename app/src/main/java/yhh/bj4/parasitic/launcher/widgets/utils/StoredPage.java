package yhh.bj4.parasitic.launcher.widgets.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yenhsunhuang on 2016/2/4.
 */
public class StoredPage {
    public static final String PAGE_NAME = "page_name";
    public static final String PAGE_TYPE = "page_type";
    public static final String PAGE_ITEMS = "page_items";

    public static final int PAGE_TYPE_DEFAULT = 0;

    private final ArrayList<Item> mItems = new ArrayList<>();

    private int mPageType = PAGE_TYPE_DEFAULT;

    private String mPageName = null;

    public void addItem(Item item) {
        mItems.add(item);
    }

    public void removeItem(Item item) {
        mItems.remove(item);
    }

    public int getPageType() {
        return mPageType;
    }

    public String getPageName() {
        return mPageName;
    }

    public String save() {
        JSONObject json = new JSONObject();
        try {
            json.put(PAGE_NAME, mPageName);
            json.put(PAGE_TYPE, mPageType);
            JSONArray jArray = new JSONArray();
            for (Item item : mItems) {
                jArray.put(item.save());
            }
            json.put(PAGE_ITEMS, jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void read(String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            mPageName = json.getString(PAGE_NAME);
            mPageType = json.getInt(PAGE_TYPE);
            JSONArray jArray = json.getJSONArray(PAGE_ITEMS);
            for (int i = 0; i < jArray.length(); ++i) {
                JSONObject itemRaw = jArray.getJSONObject(i);
                Item newItem = Item.createItemInstance(itemRaw);
                if (newItem == null) continue;
                mItems.add(newItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
