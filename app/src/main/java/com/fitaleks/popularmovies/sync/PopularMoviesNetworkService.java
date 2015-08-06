package com.fitaleks.popularmovies.sync;

import com.fitaleks.popularmovies.data.Movie;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by alexanderkulikovskiy on 07.08.15.
 */
public interface PopularMoviesNetworkService {

    @GET("/discover/movie")
    List<Movie> getAllMovies(@Query("api_key") String apiKey, @Query("language") String language);

}
