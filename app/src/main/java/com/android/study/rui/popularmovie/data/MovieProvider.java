package com.android.study.rui.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Rui on 26/05/16.
 */
public class MovieProvider extends ContentProvider{
    private static final String TAG = "ContentProvider";
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;
    static final int MOVIE = 1;

    static UriMatcher buildUriMatcher(){
        Log.d(TAG, "buildUriMateher()");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query()");
        Cursor retCursor;

        switch (uriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri  finalUri;
        switch (uriMatcher.match(uri)){
            case MOVIE:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id>0) {
                    finalUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return finalUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int rowsDeleted;

        switch (uriMatcher.match(uri)){
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }

        if (rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriMatcher.match(uri)){
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,  values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
