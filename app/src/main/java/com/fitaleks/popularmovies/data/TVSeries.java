package com.fitaleks.popularmovies.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexanderkulikovskiy on 12.08.15.
 */
public class TVSeries {
    @SerializedName("id")
    public long movieDbID;
    @SerializedName("original_language")
    public String originalLang;
    public String overview;
    @SerializedName("first_air_date")
    public String firstAirDate;
    @SerializedName("name")
    public String title;
    public double voteAverage;
    public String posterPath;
    public double popularity;
}
