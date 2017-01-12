package com.android.study.rui.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

//import com.android.study.rui.popularmovie.dummy.DummyContent;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements ActionBar.OnNavigationListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public MovieListAdapter movieListAdapter;
    public ArrayList <Movie> movieList = new ArrayList<>();
    public static String TAG = "MovieListActivity";
    public static final String BASE_URL="http://api.themoviedb.org/3/";
    public static final String API_Key = "e335ca134d15bc62d9769b280cce35ce";
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185";
    private static final String MOVIE_KEY = "movies";
    private int select_nav_index ;
    private ArrayAdapter<String> actionBar_adapter;
    private String nav_string = "now_showing";
    private static final String NAV_INDEX = "Nav_index";
    private boolean nav_changed;
    private RecyclerView recyclerView;

    private boolean mIsLoading;
    public int totalPage;
    public int currentPage=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupActionBar();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        movieList = new ArrayList<>();
        movieList.clear();
        //savedInstanceState.toString();
        recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

        });


        if(savedInstanceState!=null){

            movieList=savedInstanceState.getParcelableArrayList(MOVIE_KEY);
           // nav_changed = false;

            Log.d(TAG, "onCreate After saved MovieListAdapter"+movieList.size());
            movieListAdapter = new MovieListAdapter(getApplicationContext(), movieList );

            movieListAdapter.setGridData(movieList);
            recyclerView.setAdapter(movieListAdapter);

            MovieDetailFragment fragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);

            if (fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }
        else{
            String ADD_URL = "movie/popular?api_key=";
            new AsyncHttpTask().execute(BASE_URL + ADD_URL + API_Key+"&page="+Integer.toString(currentPage));
            movieListAdapter = new MovieListAdapter(getApplicationContext(), movieList );
            recyclerView.setAdapter(movieListAdapter);

            mIsLoading = true;

            setupRecyclerView(recyclerView);
        }


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    private  void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        String[] nav_list = getResources().getStringArray(R.array.nav_list);
        actionBar_adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        actionBar_adapter.addAll(nav_list);
        actionBar.setListNavigationCallbacks(actionBar_adapter, this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MovieListAdapter(getApplicationContext(), movieList));

    }

    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        nav_string = new String();
        nav_changed = select_nav_index != position;

        select_nav_index = position;
        currentPage = 1;
        //movieList.clear();

        String[] nav_list = getResources().getStringArray(R.array.nav_list);
        String nav = nav_list[position];

        Log.d(TAG, Boolean.toString(nav_changed));


        switch (nav) {
            case "Most Popular": {
                Log.d(TAG, "Most Popular");
                nav_string = "movie/popular";

                break;
            }
            case "Top rated": {
                Log.d(TAG, "Top rated");
                nav_string = "movie/top_rated";
                break;
            }
            case "Favourites": {
                movieList.clear();
                Toast.makeText(getBaseContext(), "Favourites", Toast.LENGTH_LONG).show();
                Cursor cursor = getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                Log.d(TAG, "no of favouritess" + Integer.toString(cursor.getCount()));

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    Movie amovie = new Movie();
                    amovie.setId(cursor.getInt(1));
                    amovie.setTitle(cursor.getString(2));
                    amovie.setYear(cursor.getString(3));
                    amovie.setMovie_details(cursor.getString(4));
                    amovie.setVote_average(Double.toString(cursor.getDouble(5)));
                    amovie.setPoster(cursor.getString(6));

                    Log.d(TAG, "favourts" + amovie.getTitle());
                    Log.d(TAG, amovie.getPoster());
                    cursor.moveToNext();
                    movieList.add(amovie);
                }
                movieListAdapter.setGridData(movieList);
                recyclerView.setAdapter(movieListAdapter);
                MovieDetailFragment fragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);

                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();


                return true;
            }

        }

        Log.d(TAG, Boolean.toString(nav_changed)+movieList.size());
        if (nav_changed == true && nav != "Favourites" ) {

            String url = BASE_URL + nav_string + "?&api_key=" + API_Key + "&page=" + Integer.toString(currentPage);
            new AsyncHttpTask().execute(url);
            Log.d(TAG + " onCreate", url);
            movieList.clear();
        }

        Log.d(TAG, nav_string);
        return true;

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            movieList =savedInstanceState.getParcelableArrayList(MOVIE_KEY);
            //select_nav_index = savedInstanceState.getInt("Navgition select");

            Log.d(TAG, "on RestoredInstanceState After saved MovieListAdapter"+movieList.size()+select_nav_index);
            movieListAdapter.setGridData(movieList);

            recyclerView.setAdapter(movieListAdapter);

            MovieDetailFragment fragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);

            if (fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            Toast.makeText(getApplicationContext(),"onRestorInstanceState", Toast.LENGTH_LONG).show();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelableArrayList(MOVIE_KEY, movieList);
        outState.putInt("Navgition select", select_nav_index);
        super.onSaveInstanceState(outState);
    }


    public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder>  {
        //private LayoutInflater layoutInflater;
        private ArrayList<Movie> gridData;
        //private Context mContext;


        private static final String TAG= "MovieListAdapter";

        public MovieListAdapter( Context context, ArrayList<Movie> movies) {

            this.gridData = movies;
        }
        public void setGridData (ArrayList<Movie> movielist){
            this.gridData = movielist;
            notifyDataSetChanged();
            Log.d(TAG, "setGridData"+movielist.size());
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

            Picasso.with(getApplicationContext()).load(movie.getPoster()).into(holder.thumbnail);

        }

        @Override
        public int getItemCount() {
            return gridData.size();
        }

        public void swap(ArrayList movies) {
            if (gridData!= null) {
                gridData.clear();
                gridData.addAll(movies);
            }
            else {
                gridData = movies;
            }
            notifyDataSetChanged();

        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public ImageView thumbnail;
            Movie selectedMovie = new Movie();


            public MyViewHolder(View view) {
                super(view);
                thumbnail = (ImageView) view.findViewById(R.id.grid_item_image);
                view.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Log.d(TAG, Integer.toString(pos));
                        if (mTwoPane) {
                            selectedMovie= (Movie) movieList.get(getPosition());
                            Log.d(TAG, "select no"+getItemCount());
                            Bundle arguments = new Bundle();
                            if(selectedMovie!=null) {
                                arguments.putString("title", selectedMovie.getTitle());
                                arguments.putInt("id", selectedMovie.getId());
                                arguments.putString("poster_url", selectedMovie.getPoster());
                                arguments.putString("release_year", selectedMovie.getYear());
                                arguments.putString("vote_average", selectedMovie.getVote_average());
                                arguments.putString("movie_details", selectedMovie.getMovie_details());
                                MovieDetailFragment fragment = new MovieDetailFragment();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.movie_detail_container, fragment)
                                        .commit();
                            }
                            else{
                                Log.d(TAG, "passing object is null");
                                Toast.makeText(getApplicationContext(), "passing object is null", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            Context context = v.getContext();

                            Movie selectedMovie = (Movie) movieList.get(getPosition());
                            Log.d(TAG, "select no"+getItemCount());

                            Intent intent = new Intent(context, MovieDetailActivity.class);
                            intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, selectedMovie.getId());
                            intent.putExtra("title", selectedMovie.getTitle());
                            intent.putExtra("id", selectedMovie.getId());
                            intent.putExtra("poster_url", selectedMovie.getPoster());
                            intent.putExtra("release_year", selectedMovie.getYear());
                            intent.putExtra("vote_average", selectedMovie.getVote_average());
                            intent.putExtra("movie_details", selectedMovie.getMovie_details());
                            context.startActivity(intent);
                        }

                    }
                });

            }

            @Override
            public void onClick(View v) {
                Log.d(TAG, "item CLicked"+getItemId());
            }
        }
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
                //Log.d(TAG+"AsyncHttpTask", Integer.toString(statusCode));

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
            //Log.d(TAG+"AsyncHttpTask", Integer.toString(result));
            return result;
        }
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            MovieDetailFragment fragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);

            if(fragment!=null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            if (result == 1) {
                movieListAdapter.setGridData(movieList);
                Log.d(TAG+"onPostExecute", Integer.toString(movieListAdapter.getItemCount()));
                recyclerView.setAdapter(movieListAdapter);


            } else {
                //Log.d(TAG, BASE_URL + "discover/movie?sort_by=popularity.asc"+"&api_key="+API_Key );
                Log.d(TAG, "Failed to get data"+movieList.size());

                movieListAdapter.setGridData(movieList);
                Log.d(TAG+"onPostExecute", Integer.toString(movieListAdapter.getItemCount()));
                recyclerView.setAdapter(movieListAdapter);
                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected== false) {
                    Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_LONG).show();
                }

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

        try {
            JSONObject json_movies = new JSONObject(inputString);
            JSONArray jsonArray = json_movies.optJSONArray("results");
            for(int i =0;i<jsonArray.length();i++) {
                Log.d(TAG, Integer.toString(i));

                Movie movie = new Movie();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                movie.setTitle(jsonObject.optString("title"));
                Log.d(TAG, movie.getTitle());
                if(jsonObject.optString("poster_path")=="null"){
                    movie.setPoster("https://assets.tmdb.org/assets/7f29bd8b3370c71dd379b0e8b570887c/images/no-poster-w185-v2.png");
                }
                else {

                    movie.setPoster(BASE_POSTER_URL + jsonObject.optString("poster_path"));
                }
                movie.setId(jsonObject.optInt("id"));
                String temp_release_date= jsonObject.optString("release_date");
                if(temp_release_date.length()>=4) {
                    movie.setYear(temp_release_date.substring(0, 4));
                }
                else{
                    movie.setYear("unKnown");
                }
                movie.setVote_average(jsonObject.getString("vote_average"));


                movie.setMovie_details(jsonObject.optString("overview"));
                movie.setTitle(jsonObject.optString("title"));

                movieList.add(movie);
               // movieListAdapter.notifyDataSetChanged();
            }
            String pages = json_movies.getString("total_pages");
            Log.d(TAG,"totalpages = "+pages);
            totalPage = Integer.parseInt(pages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
