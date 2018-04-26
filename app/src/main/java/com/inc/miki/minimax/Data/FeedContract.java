package com.inc.miki.minimax.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class FeedContract {

    public static final String CONTENT_AUTHORITY = "com.inc.miki.minimax";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FEED = "feed";

    public static final class FeedEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FEED);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;

        public static final String TABLE_NAME = "feed";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SHOW_NAME = "show_name";
        public static final String COLUMN_EPISODE_TITLE = "episode_title";
        public static final String COLUMN_EPIOSDE_LINK = "episode_link";
        public static final String COLUMN_EPISODE_DESCRIPTION = "episode_description";
        public static final String COLUMN_EPISODE_DATE = "episode_date";
        public static final String COLUMN_EPIOSDE_AUDIO = "episode_audio";
        public static final String COLUMN_EPIOSDE_LENGTH = "episode_length";
    }
}