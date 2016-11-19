package com.example.venkateshwar.tod;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Aravind on 06/11/16.
 */
public class TipOnDemand extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
