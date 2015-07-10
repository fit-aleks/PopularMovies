package com.fitaleks.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitaleks.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by alexanderkulikovskiy on 11.07.15.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final int DETAILS_LOADER = 0;

    private long mMovieId;

    private static final String[] DETAILS_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW
    };

    private ImageView poster;
    private TextView title;
    private TextView year;
    private TextView duration;
    private TextView rating;
    private TextView overview;


    public static DetailsFragment newInstance(long movieId) {
        DetailsFragment detailFragment = new DetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.KEY_MOVIE_ID, movieId);
        detailFragment.setArguments(bundle);

        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getLong(DetailsActivity.KEY_MOVIE_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        this.poster = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        this.title = (TextView) rootView.findViewById(R.id.details_movie_title);
        this.year = (TextView) rootView.findViewById(R.id.details_movie_year);
        this.duration = (TextView) rootView.findViewById(R.id.details_movie_duration);
        this.rating = (TextView) rootView.findViewById(R.id.details_movie_rating);
        this.overview = (TextView) rootView.findViewById(R.id.details_movie_overview);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DetailsActivity.KEY_MOVIE_ID)
                && mMovieId != 0) {
            getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieById = MoviesContract.MovieEntry.buildMoviesUri(this.mMovieId);
        return new CursorLoader(getActivity(),
                movieById,
                DETAILS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        if (data == null || !data.moveToFirst()) {
            return;
        }

        String imgUrl = "http://image.tmdb.org/t/p/w185"  + data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
        Picasso.with(getActivity()).load(imgUrl).into(this.poster);

        String title = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
        this.title.setText(title);

        String year = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
        this.year.setText(year);

        double averageRating = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        this.rating.setText(Double.toString(averageRating));

        String overview = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
        this.overview.setText(overview);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
