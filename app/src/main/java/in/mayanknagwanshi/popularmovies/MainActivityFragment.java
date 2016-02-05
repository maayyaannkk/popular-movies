package in.mayanknagwanshi.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.mayanknagwanshi.popularmovies.lib.JSONParser;
import in.mayanknagwanshi.popularmovies.lib.MovieAdapter;
import in.mayanknagwanshi.popularmovies.lib.MovieData;

/**
 * Created by MayankN on 03-02-2016.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private ArrayList<MovieData> listMovieData;
    @Bind(R.id.movie_grid) GridView gridView;
    String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    private String sort_order = "";
    public MainActivityFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        if(unitType.equals(getString(R.string.pref_sort_rating))){
            sort_order = getString(R.string.pref_sort_rating);
        }else{
            sort_order = getString(R.string.pref_sort_popularity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        if(!sort_order.equals(unitType)){
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", listMovieData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            listMovieData = new ArrayList<MovieData>();
            new SyncMovie().execute();
        } else {
            listMovieData = savedInstanceState.getParcelableArrayList("movies");
            if(movieAdapter!=null){
                movieAdapter.clear();
            }
            movieAdapter = new MovieAdapter(getActivity(),listMovieData);
        }

        //gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        ButterKnife.bind(this,rootView);
        if(movieAdapter!=null){
            gridView.setAdapter(movieAdapter);
            setOnItemClickOnGridView(gridView);
        }
        return rootView;
    }

    private class SyncMovie extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... params) {
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popularity));
            Uri builtUri = null;
            if(unitType.equals(getString(R.string.pref_sort_rating))){
                builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by","vote_average.desc")
                        .appendQueryParameter("api_key",JSONParser.API_KEY)
                        .build();
            }else{
                builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by","popularity.desc")
                        .appendQueryParameter("api_key",JSONParser.API_KEY)
                        .build();
            }
            return JSONParser.parseFrom(builtUri);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(result!=null){
                try {
                    JSONArray movieArray = result.getJSONArray("results");
                    for(int i=0;i<movieArray.length();i++){
                        MovieData md = new MovieData();
                        md.setMovieId(movieArray.getJSONObject(i).getInt("id"));
                        md.setPosterPath(movieArray.getJSONObject(i).getString("poster_path"));
                        md.setMetaData(movieArray.getJSONObject(i).toString());
                        listMovieData.add(md);
                    }
                    if(movieAdapter!=null) {
                        movieAdapter.clear();
                    }
                    movieAdapter = new MovieAdapter(getActivity(),listMovieData);
                    gridView.setAdapter(movieAdapter);
                    setOnItemClickOnGridView(gridView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void setOnItemClickOnGridView(GridView gv){
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieData clicked = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("detail",clicked.getMetaData());
                startActivity(intent);
            }
        });
    }
}
