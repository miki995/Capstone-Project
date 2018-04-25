package layout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.inc.miki.minimax.Data.ShowsSingleton;
import com.inc.miki.minimax.MainActivity;
import com.inc.miki.minimax.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import com.inc.miki.minimax.Adapters.ShowAdapter;
import com.inc.miki.minimax.Objects.Show;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowGrid extends Fragment {
    @BindView(R.id.showGrid)
    GridView gridView;
    View myFragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_show_grid, container, false);
        ButterKnife.bind(this, myFragmentView);

        final ArrayList<Show> shows = ShowsSingleton.getInstance().getShows();

        ShowAdapter adapter = new ShowAdapter(this.getActivity(), shows);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Show show = (Show) adapterView.getItemAtPosition(i);
                MainActivity mainActivity = MainActivity.getInstance();
                mainActivity.OnShowSelected(show);

            }
        });

        return myFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
