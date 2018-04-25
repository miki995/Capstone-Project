package com.inc.miki.minimax.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inc.miki.minimax.Data.FeedContract.FeedEntry;

import java.util.Objects;

public class FeedProvider extends ContentProvider {
    public static final String LOG_TAG = FeedProvider.class.getSimpleName();

    private FeedDbHelper mHelper;

    private static final int FEED = 100;
    private static final int FEED_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FeedContract.CONTENT_AUTHORITY, FeedContract.PATH_FEED, FEED);
        sUriMatcher.addURI(FeedContract.CONTENT_AUTHORITY, FeedContract.PATH_FEED + "/#", FEED_ID);

    }

    @Override
    public boolean onCreate() {
        mHelper = new FeedDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FEED:
                cursor = database.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FEED_ID:
                selection = FeedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FEED:
                return FeedEntry.CONTENT_LIST_TYPE;
            case FEED_ID:
                return FeedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FEED:
                return insertFeed(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFeed(Uri uri, ContentValues values) {
        String name = values.getAsString(FeedEntry.COLUMN_EPISODE_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Invalid Episode title");
        }

        SQLiteDatabase database = mHelper.getWritableDatabase();

        long id = database.insert(FeedEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
