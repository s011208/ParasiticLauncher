package yhh.bj4.parasitic.launcher;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by yenhsunhuang on 2016/2/11.
 */
public class LauncherProvider extends ContentProvider {
    public static final String PROVIDER_AUTHORITY = "yhh.bj4.parasitic.launcher.LauncherProvider";

    public static final String COLUMN_ID = "id";
    public static final String TABLE_ACTIVITY_USAGE = "activity_usage";
    public static final String COLUMN_ACTIVITY_USAGE_PACKAGE_NAME = "pkg";
    public static final String COLUMN_ACTIVITY_USAGE_CLASSNAME = "clz";
    public static final String COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY = "click_frequency";
    public static final String COLUMN_ACTIVITY_USAGE_CLICK_TIME = "click_time";

    public static final Uri URI_ACTIVITY_USAGE = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + TABLE_ACTIVITY_USAGE);

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MATCHER_ACTIVITY_USAGE = 0;

    static {
        URI_MATCHER.addURI(PROVIDER_AUTHORITY, TABLE_ACTIVITY_USAGE, MATCHER_ACTIVITY_USAGE);
    }

    private ContentProviderDatabase mContentProviderDatabase;
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mContentProviderDatabase = new ContentProviderDatabase(getContext());
        mDatabase = mContentProviderDatabase.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_ACTIVITY_USAGE:
                return mDatabase.query(TABLE_ACTIVITY_USAGE, projection, selection, selectionArgs, null, null, sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = 0;
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_ACTIVITY_USAGE:
                rowId = mDatabase.insert(TABLE_ACTIVITY_USAGE, null, values);
                uri = ContentUris.withAppendedId(uri, rowId);
                return uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_ACTIVITY_USAGE:
                return mDatabase.delete(TABLE_ACTIVITY_USAGE, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_ACTIVITY_USAGE:
                return mDatabase.update(TABLE_ACTIVITY_USAGE, values, selection, selectionArgs);
        }
        return 0;
    }

    private static class ContentProviderDatabase extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String DATABASE_NAME = "ContentProviderDatabase.db";
        public static final String CREATE_TABLE_ACTIVITY_USAGE_CMD = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ACTIVITY_USAGE + " ("
                + COLUMN_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACTIVITY_USAGE_PACKAGE_NAME + " TEXT,"
                + COLUMN_ACTIVITY_USAGE_CLASSNAME + " TEXT,"
                + COLUMN_ACTIVITY_USAGE_CLICK_TIME + " INTEGER,"
                + COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY + " INTEGER)";

        public ContentProviderDatabase(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
            getWritableDatabase().execSQL(CREATE_TABLE_ACTIVITY_USAGE_CMD);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
