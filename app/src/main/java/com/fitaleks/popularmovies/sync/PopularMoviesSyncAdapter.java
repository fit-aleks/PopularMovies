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
import com.fitaleks.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

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

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final Uri builtUri = Uri.parse(MOVIEDB_URL).buildUpon()
                .appendQueryParameter("api_key", MOVIEDB_API_KEY)
                .build();

        final String userJsonString = sendResponse(builtUri.toString());

        try {
            final JSONObject userJson = new JSONObject(userJsonString);
            final JSONArray cinemasArray = userJson.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<>(cinemasArray.length());
            for (int i = 0; i < cinemasArray.length(); ++i) {
                JSONObject cinema = cinemasArray.getJSONObject(i);

                long movieDbId = cinema.getLong("id");
                String origLanguage = cinema.getString("original_language");
                String origOverview = cinema.getString("overview");
                String releaseDate = cinema.getString("release_date");
                String title = cinema.getString("title");
                double averageVote = cinema.getDouble("vote_average");
                boolean isAdult = cinema.getBoolean("adult");
                String posterPath = cinema.getString("poster_path");
                double popularity = cinema.getDouble("popularity");

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID, movieDbId);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, origLanguage);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, origOverview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, averageVote);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_ADULT, isAdult);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, popularity);

                cVVector.add(movieValues);
            }
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
            }

        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json", ex);
        }
    }

    private String sendResponse(@NonNull final String urlString) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse = null;
        try {
            URL url = new URL(urlString);
            Log.v(LOG_TAG, "Built URI " + urlString);

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonResponse = buffer.toString();

            Log.v(LOG_TAG, "Response string: " + jsonResponse);
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error", ex);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try{
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonResponse;
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
