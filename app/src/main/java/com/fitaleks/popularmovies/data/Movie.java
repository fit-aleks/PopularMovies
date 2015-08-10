package com.fitaleks.popularmovies.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by alexanderkulikovskiy on 07.08.15.
 */
public class Movie {
    @SerializedName("id")
    public long movieDbID;
    @SerializedName("adult")
    public boolean isAdult;
    @SerializedName("original_language")
    public String originalLang;
    public String overview;
    public String releaseDate;
    public String title;
    public double voteAverage;
    public String posterPath;
    public double popularity;


}
