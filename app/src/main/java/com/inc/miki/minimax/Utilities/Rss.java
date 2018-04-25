package com.inc.miki.minimax.Utilities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import com.inc.miki.minimax.Data.FeedContract;
import com.inc.miki.minimax.Data.FeedDbHelper;
import com.inc.miki.minimax.Objects.FeedItem;
import com.inc.miki.minimax.Objects.Show;
import com.inc.miki.minimax.R;


public class Rss extends AsyncTask<ArrayList<Show>, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ProgressDialog progressDialog;
    private static final String BASE_URL = "http://thecommentist.com/feed/";

    public Rss(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(R.string.get_episodes);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(ArrayList<Show>... params) {
        for (int i = 0; i < params[0].size(); i++) {
            Show show = params[0].get(i);
            Document feedUrl = Getdata(BASE_URL + show.getFeed());
            ProcessXML(feedUrl);
        }
        return null;
    }

    private void ProcessXML(Document data) {
        if (data != null) {
            ArrayList<FeedItem> feedItems = new ArrayList<>();
            String showTitle = "";
            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node currentChild = items.item(i);
                if (currentChild.getNodeName().equalsIgnoreCase("title")) {
                    NodeList itemchilds = currentChild.getChildNodes();
                    Node current = itemchilds.item(0);
                    showTitle = current.getTextContent();
                }
                if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                    FeedItem item = new FeedItem();
                    NodeList itemchilds = currentChild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node current = itemchilds.item(j);

                        item.setShow(showTitle);
                        if (current.getNodeName().equalsIgnoreCase("title")) {

                            item.setTitle(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("itunes:summary")) {

                            item.setDescription(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {

                            item.setPubDate(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("link")) {

                            item.setLink(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("enclosure")) {

                            String url = current.getAttributes().item(0).getTextContent();
                            item.setAudioUrl(url);
                        } else if (current.getNodeName().equalsIgnoreCase("itunes:duration")) {

                            item.setLength(current.getTextContent());
                        }
                    }
                    feedItems.add(item);
                }
            }
            saveFeedItems(feedItems);
        }
    }

    private Document Getdata(String feed) {
        return new HttpUtils(feed).getDocument();
    }

    private void saveFeedItems(ArrayList<FeedItem> feedItems) {
        FeedDbHelper mDbHelper = new FeedDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT * FROM " + FeedContract.FeedEntry.TABLE_NAME + " WHERE " + FeedContract.FeedEntry.COLUMN_EPIOSDE_AUDIO
                + " =?";

        for (FeedItem item : feedItems) {

            Cursor cursor = db.rawQuery(query, new String[]{item.getAudioUrl()});

            if (cursor.getCount() <= 0) {
                ContentValues values = new ContentValues();
                values.put(FeedContract.FeedEntry.COLUMN_SHOW_NAME, item.getShow());
                values.put(FeedContract.FeedEntry.COLUMN_EPISODE_TITLE, item.getTitle());
                values.put(FeedContract.FeedEntry.COLUMN_EPIOSDE_LINK, item.getLink());
                values.put(FeedContract.FeedEntry.COLUMN_EPISODE_DESCRIPTION, item.getDescription());
                values.put(FeedContract.FeedEntry.COLUMN_EPISODE_DATE, item.getPubDate());
                values.put(FeedContract.FeedEntry.COLUMN_EPIOSDE_LENGTH, item.getLength());
                values.put(FeedContract.FeedEntry.COLUMN_EPIOSDE_AUDIO, item.getAudioUrl());

                db.insert(FeedContract.FeedEntry.TABLE_NAME, null, values);

            }
            cursor.close();
        }

        db.close();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.dismiss();
    }
}
