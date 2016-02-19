package yhh.bj4.parasitic.launcher.loader;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Yen-Hsun_Huang on 2016/2/5.
 */
public class ActivityInfoCache extends InfoCache {
    public ActivityInfoCache() {
        super();
    }

    public ActivityInfoCache(Cursor c) {
        super(c);
    }

    @Override
    public void onSave(ContentValues cv) {

    }
}
