package com.inc.miki.minimax.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.inc.miki.minimax.MainActivity;
import com.inc.miki.minimax.Objects.FeedItem;
import com.inc.miki.minimax.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    ArrayList<FeedItem> feedItems;
    Context context;

    public RecyclerAdapter(Context context, ArrayList<FeedItem> feedItems) {
        this.feedItems = feedItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final FeedItem current = feedItems.get(position);
        holder.Title.setText(current.getTitle());
        holder.Date.setText(current.getPubDate());
        holder.Length.setText(current.getLength());

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = MainActivity.getInstance();
                mainActivity.PlayEpisode(current);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.episodeName)
        TextView Title;
        @BindView(R.id.episodeLength)
        TextView Length;
        @BindView(R.id.episodeDate)
        TextView Date;
        @BindView(R.id.playEpisode)
        ImageButton playButton;
        @BindView(R.id.episodeCard)
        CardView cardView;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FeedItem selected = feedItems.get(getAdapterPosition());
            MainActivity mainActivity = MainActivity.getInstance();
            mainActivity.OnEpisodeSelected(selected);
        }
    }
}