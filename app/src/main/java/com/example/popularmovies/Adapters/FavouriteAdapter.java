package com.example.popularmovies.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.Database.MovieContract;
import com.example.popularmovies.R;
import com.example.popularmovies.utils.DatabaseUtils;
import com.example.popularmovies.utils.DateUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteAdapterViewHolder> {
    private Cursor mCursor;
    private FavouriteAdapterOnClickListener onClickHandler;
    private Context context;

    public interface FavouriteAdapterOnClickListener{
        void onClick(int movieNumber);
    }

    public FavouriteAdapter(FavouriteAdapterOnClickListener onClickListener){
        onClickHandler = onClickListener;
    }

    @NonNull
    @Override
    public FavouriteAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layout = R.layout.favourites_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout,parent,false);
        return new FavouriteAdapterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapterViewHolder holder, int position) {
        if (!(mCursor.moveToPosition(position))){ return; }
        String movieTitle = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.TITLE_COLUMN));
        String rating = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.RATING_COLUMN));
        String releaseDate = DateUtils.getDateFormat(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE_COLUMN)));

        holder.mMovieName.setText(movieTitle);
        holder.mRating.setText(rating);
        holder.mReleaseDate.setText(releaseDate);

        URL posterPath = NetworkUtils.buildPosterUrl(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH_COLUMN)));

        Picasso.with(context)
                .load(posterPath.toString())
                .placeholder(R.drawable.movie)
                .error(R.drawable.movie)
                .into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        if(mCursor==null){return 0;}
        else {return mCursor.getCount();}
    }

    public void setData(Cursor cursor){
        mCursor=cursor;
        notifyDataSetChanged();
    }


    public class FavouriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mMovieName;
        private TextView mReleaseDate;
        private TextView mRating;
        private ImageView mPoster;

        public FavouriteAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_title);
            mReleaseDate = (TextView) itemView.findViewById(R.id.release_date);
            mRating = (TextView) itemView.findViewById(R.id.rating);
            mPoster = (ImageView) itemView.findViewById(R.id.poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onClickHandler.onClick(position);
        }
    }
}
