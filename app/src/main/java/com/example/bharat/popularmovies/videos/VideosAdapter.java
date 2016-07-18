package com.example.bharat.popularmovies.videos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
//The onclick portion of this class was referenced from this post.
//http://stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener-and-how-recyclerview-is-dif/24933117#24933117

public class VideosAdapter extends
        RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView videoTextView;
        public IMyViewHolderClicks mListener;

        public ViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);
            videoTextView = (TextView) itemView.findViewById(R.id.videoName);
            videoTextView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.videoLink(view, getPosition());
        }

        public static interface IMyViewHolderClicks {
            public void videoLink(View video, int pos);
        }
    }

    private List<MovieVideos> mVideos;
    private Context mContext;

    public VideosAdapter(Context context, List<MovieVideos> videos) {
        mVideos = videos;
        mContext = context;
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos, parent, false);

        VideosAdapter.ViewHolder vh = new ViewHolder(v, new VideosAdapter.ViewHolder.IMyViewHolderClicks() {
            public void videoLink(View video, int pos) {
                String videoKey = mVideos.get(pos).key;
                if (null != videoKey) {
                    String youtubeLink = "https://www.youtube.com/watch?v=" + videoKey;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                    mContext.startActivity(i);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(VideosAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        MovieVideos video = mVideos.get(position);
        // Set item views based on your views and data model
        TextView videoView = viewHolder.videoTextView;
        videoView.setText(video.name);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }
}

