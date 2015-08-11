package com.fitaleks.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by alexanderkulikovskiy on 07.07.15.
 */
public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.gridview_poster_item, parent, false);
        PosterItemViewHolder viewHolder = new PosterItemViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final PosterItemViewHolder holder = (PosterItemViewHolder)view.getTag();

        final String movieTitle = cursor.getString(MoviesListFragment.COL_TITLE);
        holder.title.setText(movieTitle);

        final String imageUrl = "http://image.tmdb.org/t/p/w185" + cursor.getString(MoviesListFragment.COL_IMAGE_PATH);
        Picasso.with(context).load(imageUrl).into(holder.image);
    }

    public static class PosterItemViewHolder {
        public final ImageView image;
        public final TextView title;

        public PosterItemViewHolder(View v) {
            this.image = (ImageView)v.findViewById(R.id.poster_img);
            this.title = (TextView)v.findViewById(R.id.poster_title);
        }
    }
}
