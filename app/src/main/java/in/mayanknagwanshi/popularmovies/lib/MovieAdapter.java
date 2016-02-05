package in.mayanknagwanshi.popularmovies.lib;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.mayanknagwanshi.popularmovies.R;

/**
 * Created by MayankN on 03-02-2016.
 */
public class MovieAdapter extends ArrayAdapter<MovieData> {
    public MovieAdapter(Activity context,List<MovieData> movieDatas){
        super(context,0,movieDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData md = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item,parent,false);
        }
        ImageView posterView = (ImageView)convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+md.getPosterPath()).placeholder(R.drawable.loader).error(R.drawable.logo).into(posterView);

        return convertView;
    }
}
