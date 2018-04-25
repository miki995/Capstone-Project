package com.inc.miki.minimax;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Objects;

import com.inc.miki.minimax.Data.ShowsSingleton;
import com.inc.miki.minimax.Objects.FeedItem;
import com.inc.miki.minimax.Objects.Show;
import com.inc.miki.minimax.Utilities.PlayerService;
import com.inc.miki.minimax.Utilities.Rss;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import layout.EpisodePage;
import layout.ShowGrid;
import layout.ShowPage;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.playpause)
    FloatingActionButton playPauseButton;
    @BindView(R.id.playerEpisodeName)
    TextView playerEpisodeName;
    @BindView(R.id.playerShowLogo)
    ImageView playerShowLogo;

    Show selectedShow;
    FeedItem selectedEpisode;
    PlayerService playerService;
    BottomSheetBehavior bottomSheetBehavior;

    View bottomSheet;
    private static MainActivity sMainActivty;

    public static MainActivity getInstance() {
        return sMainActivty;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
        } else {
            sMainActivty = this;

            Intent startPlayer = new Intent(this, PlayerService.class);
            startService(startPlayer);

            Fragment fragment = new ShowGrid();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }


        final ArrayList<Show> shows = ShowsSingleton.getInstance().getShows();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            //get feed
            Rss rss = new Rss(this);
            rss.execute(shows);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.no_rss), Toast.LENGTH_LONG).show();
        }
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        playPauseButton.setVisibility(GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void OnShowSelected(Show show) {

        ShowPage showFragment = new ShowPage();
        Bundle args = new Bundle();
        args.putParcelable("show", show);
        showFragment.setArguments(args);

        selectedShow = show;

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, showFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void OnEpisodeSelected(FeedItem feedItem) {

        EpisodePage episodeFragment = new EpisodePage();
        Bundle args = new Bundle();
        args.putParcelable("episode", feedItem);
        episodeFragment.setArguments(args);

        selectedEpisode = feedItem;

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, episodeFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName myappWidget = new ComponentName(this.getPackageName(), WidgetProvider.class.getName());

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(myappWidget);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.appwidget_layout);
            views.setTextViewText(R.id.widgetEpisodeName, getString(R.string.widget_play_episode));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void PlayEpisode(FeedItem feedItem) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            final FeedItem selectedItem = feedItem;
            playerEpisodeName.setText(feedItem.getTitle());
            playerEpisodeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EpisodePage episodeFragment = new EpisodePage();
                    Bundle args = new Bundle();
                    args.putParcelable("episode", selectedItem);
                    episodeFragment.setArguments(args);

                    selectedEpisode = selectedItem;

                    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_container, episodeFragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            });
            playerService = PlayerService.get();
            playerService.LoadUrl(selectedItem, MainActivity.this);

            playPauseButton.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setPeekHeight(350);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            int showLogo;
            switch (Objects.requireNonNull(selectedItem.getShow())) {
                case "Roll to Hit (5th Ed. Dungeons and Dragons)":
                    showLogo = R.drawable.rth;
                    break;
                case "The Bearded Vegans":
                    showLogo = R.drawable.vegans;
                    break;
                case "Sky on Fire: A Star Wars RPG":
                    showLogo = R.drawable.sky;
                    break;
                case "Epic Nitpick":
                    showLogo = R.drawable.epic;
                    break;
                case "SkyFyre Comics":
                    showLogo = R.drawable.skyfire;
                    break;
                case "Loose Endz":
                    showLogo = R.drawable.loose;
                    break;

                default:
                    showLogo = R.drawable.sky;
            }
            Picasso.get().load(showLogo).into(playerShowLogo);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
