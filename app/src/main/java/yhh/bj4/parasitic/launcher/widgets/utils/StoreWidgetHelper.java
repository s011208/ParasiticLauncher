package yhh.bj4.parasitic.launcher.widgets.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by yenhsunhuang on 2016/2/4.
 */
public class StoreWidgetHelper {
    private static final String STORE_PREFERENCE = "store_widget_helper";
    private static StoreWidgetHelper sInstance = null;

    private final Context mContext;
    private final SharedPreferences mPrefs;

    private final HashMap<Long, StoredPage> mStoredPages = new HashMap<>();
    private final ArrayList<WeakReference<Callback>> mCallbacks = new ArrayList<>();

    public synchronized static final StoreWidgetHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StoreWidgetHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private StoreWidgetHelper(Context context) {
        mContext = context;
        mPrefs = mContext.getSharedPreferences(STORE_PREFERENCE, Context.MODE_PRIVATE);
        loadAllStoredPages();
    }

    public void addCallback(Callback cb) {
        mCallbacks.add(new WeakReference<>(cb));
    }

    public interface Callback {
        void onStoredPageAdded(long id);
        void onStoredPageRemoved(long id);
        void onStoredPageUpdated(long id);
        void onStoredPageReload();
    }

    public void addStoredPage(StoredPage page) {
        long id = 0;
        synchronized (mStoredPages) {
            id = mStoredPages.size();
            mStoredPages.put(id, page);
        }
        mPrefs.edit().putString(String.valueOf(id), page.save()).apply();
        for (WeakReference<Callback> ref : mCallbacks) {
            final Callback cb = ref.get();
            if (cb == null) continue;
            cb.onStoredPageAdded(id);
        }
    }

    public void deleteStoredPage(long id) {
        synchronized (mStoredPages) {
            mStoredPages.remove(id);
        }
        mPrefs.edit().remove(String.valueOf(id)).apply();
        for (WeakReference<Callback> ref : mCallbacks) {
            final Callback cb = ref.get();
            if (cb == null) continue;
            cb.onStoredPageRemoved(id);
        }
    }

    public void updateStoredPage(long id, StoredPage page) {
        if (page != getStoredPage(id)) {
            synchronized (mStoredPages) {
                mStoredPages.put(id, page);
            }
        }
        mPrefs.edit().putString(String.valueOf(id), page.save()).apply();
        for (WeakReference<Callback> ref : mCallbacks) {
            final Callback cb = ref.get();
            if (cb == null) continue;
            cb.onStoredPageUpdated(id);
        }
    }

    public StoredPage getStoredPage(long id) {
        synchronized (mStoredPages) {
            if (mStoredPages.containsKey(id)) {
                return mStoredPages.get(id);
            }
        }
        final String key = String.valueOf(id);
        try {
            final StoredPage page = new StoredPage(new JSONObject(mPrefs.getString(key, new JSONObject().toString())));
            synchronized (mStoredPages) {
                mStoredPages.put(id, page);
            }
            return page;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<Long, StoredPage> getAllStoredPages() {
        return mStoredPages;
    }

    private void loadAllStoredPages() {
        synchronized (mStoredPages) {
            mStoredPages.clear();
        }
        Iterator<String> prefKeys = mPrefs.getAll().keySet().iterator();
        while (prefKeys.hasNext()) {
            final String key = prefKeys.next();
            try {
                final StoredPage page = new StoredPage(new JSONObject(mPrefs.getString(key, new JSONObject().toString())));
                final long longKey = Long.parseLong(key);
                synchronized (mStoredPages) {
                    mStoredPages.put(longKey, page);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (WeakReference<Callback> ref : mCallbacks) {
            final Callback cb = ref.get();
            if (cb == null) continue;
            cb.onStoredPageReload();
        }
    }
}
