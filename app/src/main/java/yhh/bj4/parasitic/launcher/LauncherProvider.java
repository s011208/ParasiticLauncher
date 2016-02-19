package yhh.bj4.parasitic.launcher;

import android.content.ComponentName;
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
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by yenhsunhuang on 2016/2/11.
 */
public class LauncherProvider extends ContentProvider {
    private static final String TAG = "LauncherProvider";
    public static final String PROVIDER_AUTHORITY = "yhh.bj4.parasitic.launcher.LauncherProvider";

    public static final String COLUMN_ID = "id";

    // TABLE_ACTIVITY_USAGE
    public static final String TABLE_ACTIVITY_USAGE = "activity_usage";
    public static final String COLUMN_ACTIVITY_USAGE_PACKAGE_NAME = "pkg";
    public static final String COLUMN_ACTIVITY_USAGE_CLASSNAME = "clz";
    public static final String COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY = "click_frequency";
    public static final String COLUMN_ACTIVITY_USAGE_CLICK_TIME = "click_time";

    // TABLE_WIDGET_ICONS
    public static final String TABLE_WIDGET_ICONS = "widget_icons";
    public static final String COLUMN_WIDGET_ICONS_PACKAGE_NAME = "pkg";
    public static final String COLUMN_WIDGET_ICONS_CLASSNAME = "clz";
    public static final String COLUMN_WIDGET_ICONS_WIDGET_ID = "widget_id";
    public static final String COLUMN_WIDGET_ICONS_ICON_ORDER = "icon_order";
    public static final String COLUMN_WIDGET_ICONS_CONTAINER_ID = "container_id";

    // TABLE_CACHE_INFO_CACHE
    public static final String TABLE_INFO_CACHE = "info_cache";
    public static final String COLUMN_INFO_CACHE_ICON_PACK = "icon_pack";
    public static final String COLUMN_INFO_CACHE_PACKAGE_NAME = "pkg";
    public static final String COLUMN_INFO_CACHE_CLASSNAME = "clz";
    public static final String COLUMN_INFO_CACHE_ICON_LABEL = "icon_label";
    public static final String COLUMN_INFO_CACHE_ICON_BITMAP = "bitmap";
    public static final String COLUMN_INFO_CACHE_PACKAGE_INFO_FLAG = "package_info_flag";

    private static final String URI_ACTIVITY_USAGE_PATTERN = TABLE_ACTIVITY_USAGE;
    private static final String URI_ACTIVITY_USAGE_COMPONENT_INFO_PATTERN = TABLE_ACTIVITY_USAGE + "/" + COLUMN_ACTIVITY_USAGE_PACKAGE_NAME;
    private static final String URI_INFO_CACHE_PATTERN = TABLE_INFO_CACHE;

    private static final String URI_WIDGET_ICONS_PATTERN = TABLE_WIDGET_ICONS;

    public static final Uri URI_ACTIVITY_USAGE = Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + URI_ACTIVITY_USAGE_PATTERN);

    public static final Uri URI_ACTIVITY_USAGE_COMPONENT_INFO(ComponentName cn) {
        return URI_ACTIVITY_USAGE_COMPONENT_INFO(cn.getPackageName(), cn.getClassName());
    }

    public static final Uri URI_ACTIVITY_USAGE_COMPONENT_INFO(String packageName, String className) {
        return Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + URI_ACTIVITY_USAGE_COMPONENT_INFO_PATTERN
                + "?" + COLUMN_ACTIVITY_USAGE_PACKAGE_NAME + "=" + packageName
                + "&" + COLUMN_ACTIVITY_USAGE_CLASSNAME + "=" + className);
    }

    public static final Uri URI_WIDGET_ICONS_DATA(long widgetId) {
        return Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + URI_WIDGET_ICONS_PATTERN
                + "?" + COLUMN_WIDGET_ICONS_WIDGET_ID + "=" + widgetId);
    }

    public static final Uri URI_INFO_CACHE(String iconPack) {
        return Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + URI_INFO_CACHE_PATTERN
                + "?" + COLUMN_INFO_CACHE_ICON_PACK + "=" + iconPack);
    }

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MATCHER_ACTIVITY_USAGE = 0;
    private static final int MATCHER_ACTIVITY_USAGE_COMPONENT_INFO = 1;
    private static final int MATCHER_WIDGET_ICONS_DATA = 2;
    private static final int MATCHER_INFO_CACHE = 3;

    static {
        URI_MATCHER.addURI(PROVIDER_AUTHORITY, URI_ACTIVITY_USAGE_PATTERN, MATCHER_ACTIVITY_USAGE);
        URI_MATCHER.addURI(PROVIDER_AUTHORITY, URI_ACTIVITY_USAGE_COMPONENT_INFO_PATTERN, MATCHER_ACTIVITY_USAGE_COMPONENT_INFO);
        URI_MATCHER.addURI(PROVIDER_AUTHORITY, URI_WIDGET_ICONS_PATTERN, MATCHER_WIDGET_ICONS_DATA);
        URI_MATCHER.addURI(PROVIDER_AUTHORITY, URI_INFO_CACHE_PATTERN, MATCHER_INFO_CACHE);
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
            case MATCHER_ACTIVITY_USAGE_COMPONENT_INFO:
                final String pkg = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_PACKAGE_NAME);
                final String clz = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_CLASSNAME);
                return mDatabase.query(TABLE_ACTIVITY_USAGE, null
                        , COLUMN_ACTIVITY_USAGE_PACKAGE_NAME + "='" + pkg + "' and " + COLUMN_ACTIVITY_USAGE_CLASSNAME + "='" + clz + "'"
                        , null, null, null, null);
            case MATCHER_WIDGET_ICONS_DATA:
                long widgetId = Long.valueOf(uri.getQueryParameter(COLUMN_WIDGET_ICONS_WIDGET_ID));
                return mDatabase.query(TABLE_WIDGET_ICONS, null
                        , COLUMN_WIDGET_ICONS_WIDGET_ID + "=" + widgetId
                        , null, null, null, COLUMN_WIDGET_ICONS_ICON_ORDER);
            case MATCHER_INFO_CACHE:
                final String iconPack = uri.getQueryParameter(COLUMN_INFO_CACHE_ICON_PACK);
                return mDatabase.query(TABLE_INFO_CACHE, null
                        , COLUMN_INFO_CACHE_ICON_PACK + "='" + iconPack + "'", null, null, null, null);
            default:
                Log.e(TAG, "no match with uri: " + uri.getEncodedPath());
                break;
        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_WIDGET_ICONS_DATA:
                bulkInsertIntoDatabase(TABLE_WIDGET_ICONS, values);
                break;
            case MATCHER_INFO_CACHE:
                final String iconPack = uri.getQueryParameter(COLUMN_INFO_CACHE_ICON_PACK);
                mDatabase.delete(TABLE_INFO_CACHE, COLUMN_INFO_CACHE_ICON_PACK + "='" + iconPack + "'", null);
                bulkInsertIntoDatabase(TABLE_INFO_CACHE, values);
                break;
        }
        return super.bulkInsert(uri, values);
    }

    private int bulkInsertIntoDatabase(String table, ContentValues[] values) {
        int numInserted = 0;
        mDatabase.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = mDatabase.insertOrThrow(table, null, cv);
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row");
                }
            }
            mDatabase.setTransactionSuccessful();
            numInserted = values.length;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
        }
        return numInserted;
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
            case MATCHER_ACTIVITY_USAGE_COMPONENT_INFO:
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
            case MATCHER_ACTIVITY_USAGE_COMPONENT_INFO:
                final String pkg = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_PACKAGE_NAME);
                final String clz = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_CLASSNAME);
                return mDatabase.delete(TABLE_ACTIVITY_USAGE
                        , COLUMN_ACTIVITY_USAGE_PACKAGE_NAME + "='" + pkg + "' and " + COLUMN_ACTIVITY_USAGE_CLASSNAME + "='" + clz + "'"
                        , null);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case MATCHER_ACTIVITY_USAGE:
                return mDatabase.update(TABLE_ACTIVITY_USAGE, values, selection, selectionArgs);
            case MATCHER_ACTIVITY_USAGE_COMPONENT_INFO:
                final String pkg = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_PACKAGE_NAME);
                final String clz = uri.getQueryParameter(COLUMN_ACTIVITY_USAGE_CLASSNAME);
                return mDatabase.update(TABLE_ACTIVITY_USAGE, values
                        , COLUMN_ACTIVITY_USAGE_PACKAGE_NAME + "='" + pkg + "' and " + COLUMN_ACTIVITY_USAGE_CLASSNAME + "='" + clz + "'"
                        , null);
            case MATCHER_INFO_CACHE:
                final String iconPack = uri.getQueryParameter(COLUMN_INFO_CACHE_ICON_PACK);
                final String packageName = values.getAsString(COLUMN_INFO_CACHE_PACKAGE_NAME);
                final String className = values.getAsString(COLUMN_INFO_CACHE_CLASSNAME);
                return mDatabase.update(TABLE_INFO_CACHE, values, COLUMN_INFO_CACHE_ICON_PACK + "='" + iconPack + "' and "
                        + COLUMN_INFO_CACHE_PACKAGE_NAME + "='" + packageName + "' and " + COLUMN_INFO_CACHE_CLASSNAME + "='" + className + "'", null);
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
                + COLUMN_ACTIVITY_USAGE_CLICK_TIME + " TEXT NOT NULL DEFAULT '0',"
                + COLUMN_ACTIVITY_USAGE_CLICK_FREQUENCY + " INTEGER NOT NULL DEFAULT 0)";

        public static final String CREATE_TABLE_WIDGET_ICONS = "CREATE TABLE IF NOT EXISTS "
                + TABLE_WIDGET_ICONS + " ("
                + COLUMN_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WIDGET_ICONS_PACKAGE_NAME + " TEXT NOT NULL,"
                + COLUMN_WIDGET_ICONS_CLASSNAME + " TEXT NOT NULL,"
                + COLUMN_WIDGET_ICONS_CONTAINER_ID + " INTEGER NOT NULL DEFAULT -1,"
                + COLUMN_WIDGET_ICONS_ICON_ORDER + " INTEGER NOT NULL DEFAULT 0,"
                + COLUMN_WIDGET_ICONS_WIDGET_ID + "INTEGER NOT NULL DEFAULT 0)";

        public static final String CREATE_TABLE_WIDGET_INFO_CACHE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_INFO_CACHE + " ("
                + COLUMN_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_INFO_CACHE_ICON_PACK + " TEXT NOT NULL,"
                + COLUMN_INFO_CACHE_PACKAGE_NAME + " TEXT NOT NULL,"
                + COLUMN_INFO_CACHE_CLASSNAME + " TEXT NOT NULL,"
                + COLUMN_INFO_CACHE_ICON_LABEL + " TEXT NOT NULL,"
                + COLUMN_INFO_CACHE_PACKAGE_INFO_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + COLUMN_INFO_CACHE_ICON_BITMAP + " BLOB)";

        public ContentProviderDatabase(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
            getWritableDatabase().execSQL(CREATE_TABLE_ACTIVITY_USAGE_CMD);
            getWritableDatabase().execSQL(CREATE_TABLE_WIDGET_ICONS);
            getWritableDatabase().execSQL(CREATE_TABLE_WIDGET_INFO_CACHE);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
