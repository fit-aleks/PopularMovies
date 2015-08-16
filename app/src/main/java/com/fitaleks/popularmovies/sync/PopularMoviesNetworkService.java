package com.fitaleks.popularmovies.sync;

import com.fitaleks.popularmovies.data.Movie;
import com.fitaleks.popularmovies.data.Review;
import com.fitaleks.popularmovies.data.TVSeries;
import com.fitaleks.popularmovies.data.Trailer;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by alexanderkulikovskiy on 07.08.15.
 */
public interface PopularMoviesNetworkService {

    @GET("/discover/movie")
    void getAllMovies(@Query("api_key") String apiKey, @Query("language") String language, Callback<List<Movie>> callback);

    @GET("/discover/tv")
    void getAllTvSeries(@Query("api_key") String apiKey, @Query("language") String language, Callback<List<TVSeries>> callback);

    @GET("/movie/{movie_id}/videos")
    void getTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Trailer>> callback);

    @GET("/movie/{movie_id}/reviews")
    void getReviews(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Review>> callback);

    @GET("/tv/{movie_id}/videos")
    void getTvTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Trailer>> callback);

}
