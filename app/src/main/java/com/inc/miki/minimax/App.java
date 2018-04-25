package com.inc.miki.minimax;

import android.app.Application;

import com.inc.miki.minimax.Data.ShowsSingleton;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShowsSingleton.init(this);
    }
}