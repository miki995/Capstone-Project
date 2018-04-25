package layout;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.inc.miki.minimax.Adapters.RecyclerAdapter;
import com.inc.miki.minimax.R;

import java.util.ArrayList;
import java.util.Objects;

import com.inc.miki.minimax.Adapters.HostAdapter;
import com.inc.miki.minimax.Data.FeedContract.FeedEntry;
import com.inc.miki.minimax.Objects.FeedItem;
import com.inc.miki.minimax.Objects.Host;
import com.inc.miki.minimax.Objects.Show;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShowPage extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.showListView)
    RecyclerView showList;
    @BindView(R.id.showDescription)
    TextView showDescription;
    @BindView(R.id.logo)
    ImageView imageView;
    @BindView(R.id.hostGrid)
    GridView hostGrid;

    View myFragmentView;
    Show selectedShow;
    ArrayList<FeedItem> items;

    private static final int LOADER = 0;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_EPISODE_TITLE,
                FeedEntry.COLUMN_EPIOSDE_LINK,
                FeedEntry.COLUMN_EPISODE_DESCRIPTION,
                FeedEntry.COLUMN_EPISODE_DATE,
                FeedEntry.COLUMN_EPIOSDE_LENGTH,
                FeedEntry.COLUMN_EPIOSDE_AUDIO,
                FeedEntry.COLUMN_SHOW_NAME
        };
        String selection = FeedEntry.COLUMN_SHOW_NAME + " LIKE ? ";
        String[] selectionArgs = {selectedShow.getName()};

        return new CursorLoader(
                Objects.requireNonNull(getContext()),
                FeedEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //create list from Cursor
        items = new ArrayList<>();
        int nameColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPISODE_TITLE);
        int linkColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPIOSDE_LINK);
        int descColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPISODE_DESCRIPTION);
        int dateColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPISODE_DATE);
        int lengthColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPIOSDE_LENGTH);
        int audioColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_EPIOSDE_AUDIO);
        int showColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_SHOW_NAME);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FeedItem item = new FeedItem();
            item.setTitle(cursor.getString(nameColumnIndex));
            item.setLink(cursor.getString(linkColumnIndex));
            item.setDescription(cursor.getString(descColumnIndex));
            item.setPubDate(cursor.getString(dateColumnIndex));
            item.setLength(cursor.getString(lengthColumnIndex));
            item.setAudioUrl(cursor.getString(audioColumnIndex));
            item.setShow(cursor.getString(showColumnIndex));
            items.add(item);
            cursor.moveToNext();
        }

        RecyclerAdapter feedAdapter = new RecyclerAdapter(getContext(), items);
        showList.setLayoutManager(new LinearLayoutManager(getContext()));
        showList.getLayoutManager().isSmoothScrolling();
        showList.setAdapter(feedAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_show_page, container, false);
        ButterKnife.bind(this, myFragmentView);
        setRetainInstance(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedShow = bundle.getParcelable("show");
        }

        Picasso.get().load(selectedShow.getImage()).into(imageView);

        showDescription.setText(selectedShow.getDescription());


        ArrayList<Host> hosts = new ArrayList<>();
        switch (selectedShow.getName()) {

            case "Roll to Hit (5th Ed. Dungeons and Dragons)":
                hosts.add(new Host(getResources().getString(R.string.david), R.drawable.david));
                hosts.add(new Host(getResources().getString(R.string.josiah), R.drawable.josiah));
                hosts.add(new Host(getResources().getString(R.string.paul), R.drawable.paul));
                hosts.add(new Host(getResources().getString(R.string.rob), R.drawable.rob));
                hosts.add(new Host(getResources().getString(R.string.shawn), R.drawable.shawn));
                break;
            case "The Bearded Vegans":
                hosts.add(new Host(getResources().getString(R.string.andy), R.drawable.andy));
                hosts.add(new Host(getResources().getString(R.string.paul), R.drawable.paul));
                break;
            case "Sky on Fire: A Star Wars RPG":
                hosts.add(new Host(getResources().getString(R.string.wren), R.drawable.wren));
                hosts.add(new Host(getResources().getString(R.string.zem), R.drawable.zem));
                hosts.add(new Host(getResources().getString(R.string.scrapper), R.drawable.scrapper));
                hosts.add(new Host(getResources().getString(R.string.hulo), R.drawable.hulo));
                hosts.add(new Host(getResources().getString(R.string.dewethar), R.drawable.dewie));
                hosts.add(new Host(getResources().getString(R.string.afagella), R.drawable.afagella));
                hosts.add(new Host(getResources().getString(R.string.sollar), R.drawable.sollar));
                break;
            case "Epic Nitpick":
                hosts.add(new Host(getResources().getString(R.string.andy), R.drawable.andy));
                hosts.add(new Host(getResources().getString(R.string.paul), R.drawable.paul));
                break;
            default:
                hosts.add(new Host(getResources().getString(R.string.rob), R.drawable.rob));
                hosts.add(new Host(getResources().getString(R.string.shawn), R.drawable.shawn));
        }

        HostAdapter adapter = new HostAdapter(this.getActivity(), hosts);
        hostGrid.setAdapter(adapter);

        getLoaderManager().initLoader(LOADER, null, this);

        return myFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}

