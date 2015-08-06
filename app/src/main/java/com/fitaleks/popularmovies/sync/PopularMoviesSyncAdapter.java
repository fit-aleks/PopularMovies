package com.fitaleks.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fitaleks.popularmovies.R;
import com.fitaleks.popularmovies.Utility;
import com.fitaleks.popularmovies.data.Movie;
import com.fitaleks.popularmovies.data.MoviesContract;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by alexanderkulikovskiy on 10.07.15.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();

    private static final String MOVIEDB_API_KEY     = "375f92998282f1a4bb47492812dc0123";
    private static final String MOVIEDB_URL         = "http://api.themoviedb.org/3/discover/movie";

    public static final int SYNC_INTERVAL = 60 * 3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final long THREE_HOURS_IN_MILLIS = 1000 * 60 * 60 * 3;
    private static final int MOVIES_NOTIFICATION_ID = 7006; // Just because 7*6 is 42 :)

    private PopularMoviesNetworkService popularMoviesNetworkService;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(new Movie.MovieTypeAdapterFactory())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setConverter(new GsonConverter(gson))
                .build();

        popularMoviesNetworkService = restAdapter.create(PopularMoviesNetworkService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final List<Movie> allMovies = popularMoviesNetworkService.getAllMovies(MOVIEDB_API_KEY, Locale.getDefault().getLanguage());
        Log.d(LOG_TAG, "allMovies = " + allMovies.toString());
        Vector<ContentValues> cVVector = new Vector<>(allMovies.size());
        for (int i = 0; i < allMovies.size(); ++i) {
            Movie movie = allMovies.get(i);

            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID, movie.movieDbID);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.originalLang);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.title);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_ADULT, movie.isAdult);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movie.popularity);

            cVVector.add(movieValues);
        }
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
        }

    }

    public static void initializeSyncAdapter(final Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     * @param context
     * @param syncInterval
     * @param flexTime
     */
    public static void configurePeriodSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        PopularMoviesSyncAdapter.configurePeriodSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }
}
