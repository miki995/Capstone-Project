package layout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inc.miki.minimax.MainActivity;
import com.inc.miki.minimax.R;

import com.inc.miki.minimax.Objects.FeedItem;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EpisodePage extends Fragment {
    @BindView(R.id.episodeDescription)
    TextView showDescription;
    @BindView(R.id.episodeName)
    TextView showTitle;
    @BindView(R.id.episodeLength)
    TextView showLength;
    @BindView(R.id.episodeDate)
    TextView showDate;
    @BindView(R.id.playEpisodeButton)
    FloatingActionButton playButton;

    View myFragmentView;
    FeedItem selectedEpisode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_episode_page, container, false);
        ButterKnife.bind(this, myFragmentView);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedEpisode = bundle.getParcelable("episode");
        }
        if (selectedEpisode != null) {
            showDescription.setText(selectedEpisode.getDescription());
            showTitle.setText(selectedEpisode.getTitle());
            showLength.setText(selectedEpisode.getLength());
            showDate.setText(selectedEpisode.getPubDate());
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = MainActivity.getInstance();
                    mainActivity.PlayEpisode(selectedEpisode);
                }
            });
        }

        return myFragmentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}