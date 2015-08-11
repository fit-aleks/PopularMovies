package com.fitaleks.popularmovies.sync;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fitaleks.popularmovies.data.MoviesContract;
import com.fitaleks.popularmovies.data.Review;
import com.fitaleks.popularmovies.data.Trailer;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by alexanderkulikovskiy on 11.08.15.
 */
public class GetMovieDetailsService extends IntentService {
    private static final String LOG_TAG = GetMovieDetailsService.class.getSimpleName();
    public static final String MOVIE_ID_QUERY_EXTRA = "movie_id";

    public GetMovieDetailsService() {
        super("GetMovieDetailsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long movieDbID = intent.getLongExtra(MOVIE_ID_QUERY_EXTRA, 0);
        if (movieDbID <= 0) {
            return;
        }
        final PopularMoviesNetworkService popularMoviesNetworkService = NetworkHelper.getMovieRESTAdapter();
        final Vector<ContentValues> cVTrailersVector = new Vector<>();
        final List<Trailer> allTrailers = popularMoviesNetworkService.getTrailers(movieDbID, NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage());
        for (final Trailer trailer : allTrailers) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movieDbID);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.trailerId);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_ISO_639, trailer.iso639);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_KEY, trailer.key);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, trailer.name);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SITE, trailer.site);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, trailer.size);
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, trailer.type);
            cVTrailersVector.add(trailerValues);
        }
        saveContentValuesToDB(MoviesContract.TrailerEntry.CONTENT_URI, cVTrailersVector);

        final List<Review> allReviews = popularMoviesNetworkService.getReviews(movieDbID, NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage());
        final Vector<ContentValues> cVReviewsVector = new Vector<>();
        for (final Review review : allReviews) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieDbID);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.author);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.content);
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, review.reviewID);
            cVReviewsVector.add(reviewValues);
        }
        saveContentValuesToDB(MoviesContract.ReviewEntry.CONTENT_URI, cVReviewsVector);
    }

    private void saveContentValuesToDB(@NonNull Uri tableUri, @NonNull Vector<ContentValues> cVVector) {
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContentResolver().bulkInsert(tableUri, cvArray);
        }
    }
}
