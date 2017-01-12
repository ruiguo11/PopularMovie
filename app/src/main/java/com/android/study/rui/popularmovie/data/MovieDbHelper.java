package com.android.study.rui.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Rui on 12/05/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static String TAG= "MovieDbHelper";

    static final String DATABASE_NAME= "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE= "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME +" ("+
                MovieContract.MovieEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID +" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE+" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_YEAR+" TEXT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_DETAILS +" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_AVG_VOTE +" REAL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER +" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_TRAILORS+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW+" TEXT )";
        Log.d(TAG, SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
