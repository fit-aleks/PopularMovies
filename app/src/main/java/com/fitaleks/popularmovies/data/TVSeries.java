package com.fitaleks.popularmovies.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexanderkulikovskiy on 12.08.15.
 */
public class TVSeries {
    @SerializedName("id")
    public long movieDbID;
//    @SerializedName("adult")
//    public boolean isAdult;
    @SerializedName("original_language")
    public String originalLang;
    public String overview;
    public String releaseDate;
    @SerializedName("name")
    public String title;
    public double voteAverage;
    public String posterPath;
    public double popularity;
}
