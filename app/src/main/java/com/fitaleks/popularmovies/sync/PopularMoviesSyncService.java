package com.fitaleks.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by alexanderkulikovskiy on 10.07.15.
 */
public class PopularMoviesSyncService extends Service {
    private static final String LOG_TAG = PopularMoviesSyncService.class.getSimpleName();

    public static final Object sSyncAdapterLock = new Object();
    private static PopularMoviesSyncAdapter sMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - PopularMoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMoviesSyncAdapter == null) {
                sMoviesSyncAdapter = new PopularMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
