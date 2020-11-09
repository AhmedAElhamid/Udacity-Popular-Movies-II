package com.example.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.LoadMoviesIntentService;
import com.example.popularmovies.MainActivity;
import com.example.popularmovies.R;
import com.example.popularmovies.utils.DatabaseUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private MovieAdapterOncClickListener mClickHandler;
    private Cursor mCursor;
    private Context context;


    public interface MovieAdapterOncClickListener{
        void onClick(int movieNumber);
    }

    public MovieAdapter(MovieAdapterOncClickListener onClickHandler){
        mClickHandler = onClickHandler;

    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutForList = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForList,parent,false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieAdapterViewHolder holder,final int position) {

        if (!(mCursor.moveToPosition(position))){ return; }

        if(position==mCursor.getCount()-1){
            sendBroadcast(context);
        }

        String movieName = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN));
        holder.mMovieName.setText(movieName);

        URL posterPath = NetworkUtils.buildPosterUrl(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH_COLUMN)));

        Picasso.with(context)
                .load(posterPath.toString())
                .placeholder(R.drawable.movie)
                .error(R.drawable.movie)
                .into(holder.mPoster);

    }



    @Override
    public int getItemCount() {
        if(null == mCursor){return 0;}
        else {return mCursor.getCount();}
    }

    public void swapCursor(Cursor cursor){
        mCursor=cursor;
        notifyDataSetChanged();
    }

    public void clearData(){
        mCursor=null;
        notifyDataSetChanged();
    }


    private void sendBroadcast(Context context){
        Log.i(MovieAdapter.class.getSimpleName(),"sending broadcast...");
        Intent intent = new Intent(MainActivity.LOAD_MORE_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPoster;
        public final TextView mMovieName;

        public MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.image_iv);
            mMovieName = (TextView) itemView.findViewById(R.id.tv_movie_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);
        }
    }

}
