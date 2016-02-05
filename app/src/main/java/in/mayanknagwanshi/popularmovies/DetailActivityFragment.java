package in.mayanknagwanshi.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by MayankN on 04-02-2016.
 */
public class DetailActivityFragment extends Fragment{

    //private TextView year,time,vote,desc,title;
    //private ImageView poster;

    @Bind(R.id.textViewDesc) TextView desc;
    @Bind(R.id.textViewYear) TextView year;
    @Bind(R.id.textViewTime) TextView time;
    @Bind(R.id.textViewVote) TextView vote;
    @Bind(R.id.textViewTitle) TextView title;
    @Bind(R.id.imageView) ImageView poster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        /*year = (TextView)rootView.findViewById(R.id.textViewYear);
        time = (TextView)rootView.findViewById(R.id.textViewTime);
        vote = (TextView)rootView.findViewById(R.id.textViewVote);
        desc = (TextView)rootView.findViewById(R.id.textViewDesc);
        title = (TextView)rootView.findViewById(R.id.textViewTitle);
        poster = (ImageView)rootView.findViewById(R.id.imageView);*/
        ButterKnife.bind(this, rootView);

        String temp = getActivity().getIntent().getStringExtra("detail");
        try{
            JSONObject movieDetail = new JSONObject(temp);
            year.setText(movieDetail.getString("release_date").substring(0,4));
            vote.setText(movieDetail.getDouble("vote_average")+"/10");
            desc.setText(movieDetail.getString("overview"));
            title.setText(movieDetail.getString("original_title"));
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/"+movieDetail.getString("poster_path")).placeholder(R.drawable.loader).error(R.drawable.logo).into(poster);
        }catch(JSONException e){
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
