package com.fitaleks.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fitaleks.popularmovies.data.MoviesContract;

/**
 * Created by alexanderkulikovskiy on 12.08.15.
 */
public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    public enum FragmentListTypes {
        MOVIES,
        TV_SERIES,
        FAVOURITES
    }

    public interface MovieSelectedCallback {
        void onItemSelected(long id);
    }

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID,
            MoviesContract.MovieEntry.COLUMN_IS_MOVIE
    };

    public static final int COL_MOVIE_ID    = 0;
    public static final int COL_TITLE       = 1;
    public static final int COL_IMAGE_PATH  = 2;
    public static final int COL_MOVIEDB_ID  = 3;

    private FragmentListTypes listType;

    public static MoviesListFragment newInstance(FragmentListTypes fragmentListType) {
        MoviesListFragment moviesListFragment = new MoviesListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("", fragmentListType.ordinal());
        moviesListFragment.setArguments(bundle);

        return moviesListFragment;
    }


    private MoviesAdapter moviesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.listType = FragmentListTypes.values()[ bundle.getInt("", 0) ];
        }
        final View rootView = inflater.inflate(R.layout.fragment_grid_movies, container, false);
        final GridView gridLayout = (GridView) rootView.findViewById(R.id.gridview);
        moviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        gridLayout.setAdapter(moviesAdapter);
        gridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = moviesAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((MovieSelectedCallback)getActivity()).onItemSelected(cursor.getLong(COL_MOVIEDB_ID));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final String sortOrder;

        String sortCriteria = Utility.getPrefferedMoviesList(getActivity());
        if (sortCriteria.equals(getString(R.string.pref_list_popular))) {
            sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        final String selection = MoviesContract.MovieEntry.COLUMN_IS_MOVIE  + " = "
                + (this.listType == FragmentListTypes.MOVIES ? 1 : 0);

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                selection,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.moviesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.moviesAdapter.swapCursor(null);
    }
}
