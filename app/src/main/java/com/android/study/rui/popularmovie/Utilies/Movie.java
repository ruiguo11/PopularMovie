package com.android.study.rui.popularmovie.Utilies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rui on 13/01/16.
 */
public class Movie implements Parcelable {
    private String title;
    private String year;
    private String length;
    private String poster;
    private String movie_details;
    private String videos;
    private int id;
    private String vote_average;


    public Movie(){
        title = new String();
        year = new String();
        length = new String();
        poster = new String();
        movie_details = new String();
        videos = new String();
        id = 0;
        vote_average = new String();
    }

    public void setId(int i){id= i;}
    public void setTitle(String t){title = t;}
    public void setYear(String y) {year= y;}
    public void setLength(String l){length = l;}
    public void setPoster(String p){poster= p;}
    public void setMovie_details(String d){movie_details = d;}
    public void setVideos (String v){videos = v;}
    public void setVote_average(String s){vote_average = s;}


    public String getTitle(){return title;}
    public String getYear(){return year;}
    public String getLength(){return  length;}
    public String getPoster (){return poster;}
    public String getMovie_details(){return movie_details;}
    public String getVideos(){return videos;}
    public int getId(){return id;}
    public String getVote_average(){return vote_average;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(length);
        dest.writeString(poster);
        dest.writeString(movie_details);
        dest.writeString(videos);
        dest.writeString(vote_average);

    }
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private Movie (Parcel in) {
        id = in.readInt();
        title = in.readString();
        year = in.readString();
        length= in.readString();
        poster = in.readString();
        movie_details = in.readString();
        videos= in.readString();
        vote_average= in.readString();

    }
}
