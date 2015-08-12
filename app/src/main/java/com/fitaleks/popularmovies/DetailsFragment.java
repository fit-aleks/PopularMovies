package com.fitaleks.popularmovies;

import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fitaleks.popularmovies.data.MoviesContract;
import com.fitaleks.popularmovies.sync.GetMovieDetailsService;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderkulikovskiy on 11.07.15.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final int DETAILS_MOVIE_LOADER = 0;
    private static final int DETAILS_TRAILERS_LOADER = 1;
    private static final int DETAILS_REVIEWS_LOADER = 2;

    private long mMovieId;
    private boolean mIsMovie;

    private static final String[] DETAILS_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_IS_MOVIE
    };

    private static final String[] TRAILERS_COLUMNS = {
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.COLUMN_MOVIE_ID,
            MoviesContract.TrailerEntry.COLUMN_SITE,
            MoviesContract.TrailerEntry.COLUMN_TYPE,
            MoviesContract.TrailerEntry.COLUMN_KEY,
            MoviesContract.TrailerEntry.COLUMN_NAME
    };

    private static final String[] REVIEWS_COLUMNS = {
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT
    };

    @Bind(R.id.details_movie_poster) ImageView poster;
    @Bind(R.id.details_movie_title) TextView title;
    @Bind(R.id.details_movie_year) TextView year;
    @Bind(R.id.details_movie_rating) TextView rating;
    @Bind(R.id.details_movie_overview) TextView overview;
    @Bind(R.id.details_trailers_container) LinearLayout detailsMovieContainer;
    @Bind(R.id.details_reviews_container) LinearLayout detailsReviewContainer;
    @Bind(R.id.details_trailers_card) CardView trailersCard;
    @Bind(R.id.details_reviews_card) CardView reviewsCard;
    @Bind(R.id.details_fab_like) FloatingActionButton fabLike;

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

        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        this.fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.changeMovieFavourite(getActivity(), mMovieId);
                fabLike.setImageResource(Utility.isMovieFavourite(getActivity(), mMovieId) ? R.drawable.fab_heart : R.drawable.fab_heart_dislike);
            }
        });
        this.fabLike.setImageResource(Utility.isMovieFavourite(getActivity(), mMovieId) ? R.drawable.fab_heart : R.drawable.fab_heart_dislike);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateMovieData() {
        Intent intent = new Intent(getActivity(), GetMovieDetailsService.class);
        intent.putExtra(GetMovieDetailsService.MOVIE_ID_QUERY_EXTRA, this.mMovieId);
        intent.putExtra(GetMovieDetailsService.IS_MOVIE_QUERY_EXTRA, this.mIsMovie);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DetailsActivity.KEY_MOVIE_ID)
                && mMovieId != 0) {
            getLoaderManager().restartLoader(DETAILS_MOVIE_LOADER, null, this);
            getLoaderManager().restartLoader(DETAILS_TRAILERS_LOADER, null, this);
            getLoaderManager().restartLoader(DETAILS_REVIEWS_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getLoaderManager().initLoader(DETAILS_MOVIE_LOADER, null, this);
            getLoaderManager().initLoader(DETAILS_TRAILERS_LOADER, null, this);
            getLoaderManager().initLoader(DETAILS_REVIEWS_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DETAILS_MOVIE_LOADER) {
            Uri movieById = MoviesContract.MovieEntry.buildMoviesUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    DETAILS_COLUMNS,
                    null,
                    null,
                    null);
        } else if (id == DETAILS_TRAILERS_LOADER) {
            Uri movieById = MoviesContract.TrailerEntry.buildTrailersUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    TRAILERS_COLUMNS,
                    null,
                    null,
                    null);
        } else if (id == DETAILS_REVIEWS_LOADER) {
            Uri movieById = MoviesContract.ReviewEntry.buildReviewUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    REVIEWS_COLUMNS,
                    null,
                    null,
                    null);
        }
        throw new UnsupportedOperationException("Unknown id: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        if (data == null || !data.moveToFirst()) {
            if (loader.getId() == DETAILS_TRAILERS_LOADER) {
                this.trailersCard.setVisibility(View.GONE);
            } else if (loader.getId() == DETAILS_REVIEWS_LOADER) {
                this.reviewsCard.setVisibility(View.GONE);
            }
            return;
        }
        if (loader.getId() == DETAILS_MOVIE_LOADER) {
            String imgUrl = "http://image.tmdb.org/t/p/w185"  + data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
            Picasso.with(getActivity()).load(imgUrl).into(this.poster);

            String title = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
            this.title.setText(title);

            String year = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
            this.year.setText(year);

            double averageRating = data.getDouble(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            this.rating.setText(String.format(getString(R.string.details_rating), averageRating));

            String overview = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
            this.overview.setText(overview);
            this.mIsMovie = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IS_MOVIE)) == 1;
            updateMovieData();
        } else if (loader.getId() == DETAILS_TRAILERS_LOADER) {
            this.trailersCard.setVisibility(View.VISIBLE);
            detailsMovieContainer.removeAllViews();
            do {
                final String name = data.getString(data.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_NAME));
                final String key = data.getString(data.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_KEY));
                Log.d(LOG_TAG, "site = " + name + " key = " + key);

                TextView textView = (TextView) getLayoutInflater(null).inflate(R.layout.details_trailer_view, null);
                textView.setText(name);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + key));
                            startActivity(intent);
                        }
                    }
                });
                View lineView = new View(getActivity());
                lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lineView.setLayoutParams(layoutParams);

                detailsMovieContainer.addView(textView);
                detailsMovieContainer.addView(lineView);

            } while (data.moveToNext());
        } else if (loader.getId() == DETAILS_REVIEWS_LOADER) {
            this.reviewsCard.setVisibility(View.VISIBLE);
            detailsReviewContainer.removeAllViews();
            do {
                final String content = data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT));
//                final String key = data.getString(data.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_KEY));
//                Log.d(LOG_TAG, "site = " + name + " key = " + key);

                TextView textView = (TextView) getLayoutInflater(null).inflate(R.layout.details_trailer_view, null);
                textView.setText(content);
                textView.setClickable(false);

                View lineView = new View(getActivity());
                lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lineView.setLayoutParams(layoutParams);

                detailsReviewContainer.addView(textView);
                detailsReviewContainer.addView(lineView);

            } while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
