package com.fitaleks.popularmovies.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexanderkulikovskiy on 10.08.15.
 */
public class Review {
    @SerializedName("id")
    public String reviewID;
    public String author;
    public String content;
}
