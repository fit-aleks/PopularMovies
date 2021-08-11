package com.fitaleks.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fitaleks.popularmovies.sync.PopularMoviesSyncAdapter;


public class MainActivity extends AppCompatActivity implements MoviesListFragment.MovieSelectedCallback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_details_container) != null) {
            this.mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_details_container, new DetailsFragment())
                        .commit();
            }
        } else {
            this.mTwoPane = false;
        }

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            PopularMoviesSyncAdapter.syncImmediately(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(long id) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, DetailsFragment.newInstance(id))
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsActivity.KEY_MOVIE_ID, id);
            startActivity(intent);
        }
    }
}
