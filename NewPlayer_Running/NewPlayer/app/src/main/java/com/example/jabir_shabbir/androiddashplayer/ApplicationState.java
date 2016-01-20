package com.example.jabir_shabbir.androiddashplayer;

import java.util.LinkedList;
import android.app.Application;
import android.util.Log;

// This is the main application class. It maintains the global state of the app which is
// available to all components of the application.
public class ApplicationState extends Application
{
    // List of all videos downloaded
    private LinkedList<String> videoBuffer;
    private boolean canPlay;
    private int numberOfVideos;
    private int numberPlayed;

    // List of all the videos available
    private String[] itemsArray;

    public String[] getItemsArray() {
        return itemsArray;
    }

    public void setItemsArray(String[] itemsArray) {
        this.itemsArray = itemsArray;
    }

    @Override
    public void onCreate()
    {
        initVideo();
        Log.i("DashPlayer", "The create method has been called and the buffer is ready");
    }

    public void initVideo()
    {
        itemsArray = null;
        canPlay = false;
        videoBuffer = new LinkedList<String>();
        numberOfVideos = 0;
        numberPlayed = 0;
    }





}