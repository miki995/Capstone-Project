package com.inc.miki.minimax.Data;

import android.content.Context;
import android.content.res.Resources;

import com.inc.miki.minimax.MainActivity;
import com.inc.miki.minimax.Objects.Show;
import com.inc.miki.minimax.R;

import java.util.ArrayList;

public class ShowsSingleton {

    private ArrayList<Show> shows;
    private static ShowsSingleton instance;
    private Context mContext;

    private ShowsSingleton(Context context) {
        mContext = context;
        shows = new ArrayList<>();
        shows.add(
                new Show(
                        mContext.getString(R.string.RollName),
                        mContext.getString(R.string.RollDes),
                        R.drawable.rth,
                        mContext.getString(R.string.RollLink)));
        shows.add(
                new Show(
                        mContext.getString(R.string.BVName),
                        mContext.getString(R.string.BVDes),
                        R.drawable.vegans,
                        mContext.getString(R.string.BVLink)));
        shows.add(
                new Show(
                        mContext.getString(R.string.SkyName),
                        mContext.getString(R.string.SkyDes), R.drawable.sky,
                        mContext.getString(R.string.SkyLink)));
        shows.add(
                new Show(
                        mContext.getString(R.string.EpicName),
                        mContext.getString(R.string.EpicDes),
                        R.drawable.epic,
                        mContext.getString(R.string.EpicLink)));
        shows.add(
                new Show(
                        mContext.getString(R.string.SkyCName),
                        mContext.getString(R.string.SkycDes),
                        R.drawable.skyfire,
                        mContext.getString(R.string.SkyCLink)));
        shows.add(
                new Show(
                        mContext.getString(R.string.LooseName),
                        mContext.getString(R.string.LooseDes),
                        R.drawable.loose,
                        mContext.getString(R.string.LooseLink)));


    }
    public static void init(Context context) {
        instance = new ShowsSingleton(context.getApplicationContext());
    }

    public static ShowsSingleton getInstance() {
        return instance;
    }

    public ArrayList<Show> getShows() {
        return this.shows;
    }

}
