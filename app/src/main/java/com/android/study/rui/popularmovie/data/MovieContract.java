package com.android.study.rui.popularmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Rui on 23/05/16.
 * Defines table and column names
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.android.study.rui.popularmovie";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri  CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_YEAR= "year";

        public static final String COLUMN_MOVIE_DETAILS = "details";

        public static final String COLUMN_MOVIE_AVG_VOTE= "average_vote";

        public static final String COLUMN_MOVIE_POSTER= "posters";

        public static final String COLUMN_MOVIE_TRAILORS= "trailors";

        public static final String COLUMN_MOVIE_REVIEW= "reviews";
        public static Uri buildMovieUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}
