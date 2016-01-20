package com.example.jabir_shabbir.mediaplayer;
import android.content.Context;
import 	android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Jabir_shabbir on 21-09-2015.
 */


public class RunPlayer extends Thread
{

    boolean prepared=false;
    MediaPlayer m;
    private class PreparedListner implements android.media.MediaPlayer.OnPreparedListener
    {
        public void  onPrepared(MediaPlayer m)
        {
           prepared=true;
            Log.i("in prepared done","in prepared done");
        }
    }
    RunPlayer()
   {

   }

    public void run()
    {
       try
       {
           m.setOnPreparedListener(new PreparedListner());

           m.prepareAsync();
           while(!prepared)
           {
               Log.i("hello","hello");
           }
           Log.i("prepared123","prepared123");
           m.start();
       }
       catch(Exception ex)
       {
           Log.i("Exce in running", "Exception");
       }

    }

    public void InitializeState(Context c)
    {
        Uri myUri = Uri.parse("/sdcard/Download/source1.mp4");
        m=MediaPlayer.create(c,myUri);
        m.start();
        try
        {
           // m.setDataSource("/sdcard/Download/source1.mp4");

        }
        catch (Exception ex)
        {
            Log.i(ex.toString()+"Exce in iniatialization", ex.toString());
        }

    }

}
