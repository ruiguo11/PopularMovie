package com.android.study.rui.popularmovie.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.study.rui.popularmovie.R;
import com.android.study.rui.popularmovie.Utilies.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Rui on 25/01/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder>  {
    //private LayoutInflater layoutInflater;
    private ArrayList<Movie> gridData;
    private Context mContext;
    private int itemLayouId;

    private static final String TAG= "MovieListAdapter";

    public MovieListAdapter( Context context, ArrayList<Movie> movies) {
        mContext = context;
        this.gridData = movies;
    }
    public void setGridData (ArrayList<Movie> movielist){
        this.gridData = movielist;
        notifyDataSetChanged();
        Log.d(TAG, "setGridData"+movielist.size());
    }

    public void swap(ArrayList list){
        if (gridData!= null) {
            gridData.clear();
            gridData.addAll(list);
        }
        else {
            gridData = list;
        }
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = gridData.get(position);
        Picasso.with(mContext).load(movie.getPoster()).into(holder.thumbnail);



    }

    @Override
    public int getItemCount() {
        return gridData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;


        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.grid_item_image);
            view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    //Log.d(TAG, Integer.toString(pos));

                }
            });

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "item CLicked"+getItemId());

            //Toast.makeText(v.getContext(), "position"+getItemId(),Toast.LENGTH_LONG).show();
        }
    }
}



