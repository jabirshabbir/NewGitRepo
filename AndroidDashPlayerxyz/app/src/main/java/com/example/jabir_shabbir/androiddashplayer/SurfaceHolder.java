package com.example.jabir_shabbir.androiddashplayer;

/**
 * Created by Jabir_shabbir on 09-10-2015.
 */
import android.media.MediaPlayer;

/**
 * Created by Jabir_shabbir on 22-09-2015.
 */
public class SurfaceHolder implements android.view.SurfaceHolder.Callback
{
    MediaPlayer mp;
    SurfaceHolder(MediaPlayer mp)
    {
        this.mp=mp;
    }
    public void surfaceChanged(android.view.SurfaceHolder holder, int format, int width,
                               int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(android.view.SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        mp.setDisplay(holder);
        //play();
    }

    @Override
    public void surfaceDestroyed(android.view.SurfaceHolder holder)
    {
        // TODO Auto-generated method stub

    }

}

