package com.android.study.rui.popularmovie;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.study.rui.popularmovie.Utilies.Movie;
import com.android.study.rui.popularmovie.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = "MovieDetailFragment";

    private Movie movie;
    private ImageView imageView;

    private TextView ratingView;
    private TextView yearView;
    private TextView detailsView;
    private ImageButton favouritetButton;


    private String poster_url;
    private String title;
    private int id;
    private String release_year;
    private String vote_average;
    private String movie_details;
    private String video_title;
    private String video_url;
    private String review_username;
    private String review_content;

    private String video_link_string= new String();
    private String comments_string=new String();
    private TableLayout layout;
    private TableLayout reviewLayout;


    private static final String BASE_URL="http://api.themoviedb.org/3/movie/";
    public static final String API_Key = "e335ca134d15bc62d9769b280cce35ce";
    private static final String YOUTUBE_URL="https://www.youtube.com/watch?v=";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            movie = new Movie();
            movie.setTitle(getArguments().getString("title"));
            movie.setId(getArguments().getInt("id"));
            movie.setPoster(getArguments().getString("poster_url"));
            movie.setMovie_details(getArguments().getString("movie_details"));
            movie.setVote_average(getArguments().getString("vote_average"));
            movie.setYear(getArguments().getString("release_year"));

            Log.d("onCreate", movie.getTitle());
        }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                // appBarLayout.setTitle(mItem.content);
            }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);


        yearView =(TextView) rootView.findViewById(R.id.movie_details_year);
        ratingView = (TextView) rootView.findViewById(R.id.movie_details_rating);
        detailsView = (TextView) rootView.findViewById(R.id.movie_details_overview);

        imageView = (ImageView) rootView.findViewById(R.id.movie_details_poster);

        //Picasso.with(getApplicationContext()).load(movie.getPoster()).into(imageView);

        yearView =(TextView) rootView.findViewById(R.id.movie_details_year);

        ratingView = (TextView) rootView.findViewById(R.id.movie_details_rating);

        detailsView = (TextView) rootView.findViewById(R.id.movie_details_overview);


        favouritetButton = (ImageButton)rootView.findViewById(R.id.button_favourite);
        layout =(TableLayout)rootView.findViewById(R.id.layout_trailors);
        reviewLayout= (TableLayout)rootView.findViewById(R.id.layout_comments);
        setupUI();

        return rootView;
    }

    protected void setupUI(){

        movie.setPoster(movie.getPoster());
        movie.setTitle(movie.getTitle());
        poster_url = movie.getPoster();

        id = movie.getId();
        release_year = movie.getYear();
        vote_average = movie.getVote_average();
        movie_details = movie.getMovie_details();
        title = movie.getTitle();


        Log.d(TAG+"id=",Integer.toString(id));
        Log.d(TAG+" year=", release_year);
        Log.d(TAG+" ratingBar=", vote_average);

        Picasso.with(getContext()).load(movie.getPoster()).into(imageView);

        yearView.setText(release_year);

        ratingView.setText(vote_average);

        detailsView.setText(movie_details);
        getTailors();
        getReviews();


        if(isFavourts()==true) {
            favouritetButton.setImageResource(R.mipmap.ic_favourites);
        }
        else
            favouritetButton.setImageResource(R.mipmap.ic_not_favourites);
        favouritetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFavourts()==false) {
                    favouritetButton.setImageResource(R.mipmap.ic_favourites);
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_AVG_VOTE, vote_average);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_DETAILS, movie_details);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, poster_url);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILORS, video_link_string);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_YEAR, release_year);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, comments_string);
                    Log.d(TAG, MovieContract.MovieEntry.CONTENT_URI.toString());
                    getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);


                    Log.d(TAG+ "insert values", values.toString());
                    Log.d(TAG, " ADD_to_favourt");
                }
                else{
                    favouritetButton.setImageResource(R.mipmap.ic_not_favourites);
                    Log.d(TAG, "already in favourts will be removed from favourts");
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID+" = ?",
                            new String[]{Integer.toString(id)});

                }

            }
        });

    }
    public boolean isFavourts(){

        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { Integer.toString(id) },
                null
        );
        Log.d(TAG,"favourt id="+Integer.toString(id)+cursor.toString());


        Log.d(TAG, MovieContract.MovieEntry.CONTENT_URI.toString());

        int numRows = cursor.getCount();
        Log.d(TAG, Integer.toString(numRows));

        cursor.close();
        if (numRows==1)
            return true;
        else
            return false;

    }

    private void getTailors(){

        String videos_url =BASE_URL + String.valueOf(id) +"/videos?api_key="+ API_Key;
        Log.d("getTailors", videos_url);
        new AsyncHttpTask().execute(videos_url);
    }

    private void getReviews(){

        String reviews_url = BASE_URL + String.valueOf(id) +"/reviews?api_key="+ API_Key;
        Log.d("getReviews", reviews_url);
        new AsyncHttpTask().execute(reviews_url);
    }
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                Log.d(TAG+"AsyncHttpTask", "statusCode"+Integer.toString(statusCode));

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = parseToString(httpResponse.getEntity().getContent());
                    parseJSON(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            Log.d(TAG+"AsyncHttpTask", "result"+Integer.toString(result));
            return result;
        }
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                Log.d(TAG, "Got data");
            } else {
                Log.d(TAG, "Failed to get data");
            }
        }
    }
    private String parseToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != inputStream) {
            inputStream.close();
        }
        return result;
    }
    private void parseJSON(String inputString) {
        Log.d(TAG, "parseJSON()");
        Log.d(TAG, inputString);
       // final TableLayout layout =(TableLayout)findViewById(R.id.layout_trailors);
        //final TableLayout reviewLayout= (TableLayout)findViewById(R.id.layout_comments);

        try {
            JSONObject json_review = new JSONObject(inputString);
            JSONArray jsonArray = json_review.optJSONArray("results");


            for(int i =0;i<jsonArray.length();i++){
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


                Log.d(TAG, Integer.toString(i));
                JSONObject jsonObject = jsonArray.optJSONObject(i);

                if(jsonObject.has("name")==true){
                    video_title=jsonObject.getString("name");
                }
                if(jsonObject.has("key")==true)
                    video_url = jsonObject.getString("key");
                video_link_string = video_link_string+"{'"+video_title+"';'"+video_url+"'}";
                Log.d(TAG+"trailors", video_link_string);


                if(jsonObject.has("author")==true) {
                    review_username = jsonObject.getString("author");
                }

                if(jsonObject.has("content")==true) {
                    review_content = jsonObject.getString("content");
                    Log.d(TAG+"review", review_content);
                    comments_string = comments_string+ "{'"+review_username+"';'"+review_content+"'}";
                }


                TextView aTrailor = new TextView(getContext());
                aTrailor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                aTrailor.setPadding(10,20,0,0);



                aTrailor.setText(video_title);




                final TableRow aRow = new TableRow(getContext());
                ImageView trailor_icon = new ImageView(getContext());
                trailor_icon.setImageResource(R.mipmap.ic_trailor);

                aRow.addView(trailor_icon);
                aRow.addView(aTrailor);
                aRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG+" Youtube", video_url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL+video_url));
                        startActivity(intent);
                    }
                });


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        layout.addView(aRow);
                    }
                });

                if(review_username!=null & review_content!=null) {

                    final TableRow commentRow = new TableRow(getContext());
                    ImageView review_icon = new ImageView(getContext());
                    review_icon.setImageResource(R.mipmap.ic_reviews);

                    TextView reviewedBy = new TextView(getContext());
                    reviewedBy.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    reviewedBy.setText("Reviewd by " + review_username);


                    reviewedBy.setSingleLine(false);
                    reviewedBy.setPadding(10, 20, 0, 0);
                    commentRow.addView(review_icon);
                    commentRow.addView(reviewedBy);
                    commentRow.setPadding(0, 20, 0, 20);


                    final TextView commentView = new TextView(getContext());
                    commentView.setText(review_content);
                    commentView.setPadding(10, 10, 0, 30);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            reviewLayout.addView(commentRow);
                            reviewLayout.addView(commentView);
                        }
                    });
                }

                //Log.d(TAG, video_title);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}