package com.fitaleks.popularmovies.sync;

import com.fitaleks.popularmovies.data.Movie;
import com.fitaleks.popularmovies.data.Review;
import com.fitaleks.popularmovies.data.Trailer;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by alexanderkulikovskiy on 07.08.15.
 */
public interface PopularMoviesNetworkService {

    @GET("/discover/movie")
    List<Movie> getAllMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("/movie/{movie_id}/videos")
    List<Trailer> getTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("/movie/{movie_id}/reviews")
    List<Review> getReviews(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language);

}
