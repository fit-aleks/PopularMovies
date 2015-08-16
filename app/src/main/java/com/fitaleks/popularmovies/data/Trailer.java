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
 * Created by alexanderkulikovskiy on 09.08.15.
 */
public class Trailer {
    @SerializedName("id")
    public String trailerId;
    @SerializedName("iso_639_1")
    public String iso639;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;
}
