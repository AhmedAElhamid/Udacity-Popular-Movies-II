package com.example.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.popularmovies.R;
import com.example.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsArrayAdapter extends ArrayAdapter<Review> {
    private List<Review> mReviews;
    public ReviewsArrayAdapter(@NonNull Context context, List<Review> reviews) {
        super(context,0, reviews);
        mReviews=reviews;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Review review = mReviews.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reviews_list_layout, parent, false);
        }

        TextView tvAuthor = (TextView) convertView.findViewById(R.id.review_author);
        TextView tvContent = (TextView) convertView.findViewById(R.id.review_content);

        tvAuthor.setText(review.getAuthor());
        tvContent.setText(review.getContent());

        return convertView;
    }
}
