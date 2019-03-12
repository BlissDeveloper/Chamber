package com.example.avery.chamberofwizards;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ChamberOfWizards3 extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
