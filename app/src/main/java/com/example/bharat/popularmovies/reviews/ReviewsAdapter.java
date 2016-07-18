package com.example.bharat.popularmovies.reviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bharat.popularmovies.R;
import java.util.List;

/**
 * Created by Bharat on 7/15/2016.
 */

public class ReviewsAdapter extends
        RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView authorTextView;
        public TextView contentTextView;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.review_author);
            contentTextView = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    private List<MovieReviews> mReviews;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public ReviewsAdapter(Context context, List<MovieReviews> reviews) {
        mReviews = reviews;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View ReviewView = inflater.inflate(R.layout.reviews, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(ReviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        MovieReviews review = mReviews.get(position);

        // Set item views based on your views and data model
        TextView authorView = viewHolder.authorTextView;
        authorView.setText(review.author);
        TextView contentView = viewHolder.contentTextView;
        contentView.setText(review.content);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

}

