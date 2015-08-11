package com.fitaleks.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by alexanderkulikovskiy on 11.07.15.
 */
public class Utility {
    public static String getPrefferedMoviesList(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_list_key), context.getString(R.string.pref_list_popular));
    }

    public static void changeMovieFavourite(final Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean isCurFavorite = isMovieFavourite(context, movieId);
        prefs.edit().putBoolean("MOVIE_ID_"+ movieId, !isCurFavorite).apply();
    }

    public static boolean isMovieFavourite(final Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("MOVIE_ID_" + movieId, false);
    }
}
