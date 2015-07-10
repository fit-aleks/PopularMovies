package com.fitaleks.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alexanderkulikovskiy on 07.06.15.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.fitaleks.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_MOVIES = "movies";

    /* Class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "movies";

        // ID in terms of themoviedb.com
        public static final String COLUMN_MOVIEDB_ID = "id";

        // is this movie adult
        public static final String COLUMN_IS_ADULT = "is_adult";

        // original language
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_lang";

        // title
        public static final String COLUMN_TITLE = "original_title";

        // overview
        public static final String COLUMN_OVERVIEW = "original_overview";

        // release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // average vote
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // poster url path
        public static final String COLUMN_POSTER_PATH = "poster_url";


        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
