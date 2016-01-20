package com.example.jabir_shabbir.androiddashplayer;

import android.support.v7.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Jabir_shabbir on 09-10-2015.
 */
public class PlayMedia extends AppCompatActivity
{
    MediaPlayer mediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    boolean isplaying=false;
    private void initMediaPlayer()
    {
        String PATH_TO_FILE = "/sdcard/Download/tsfile.ts";
        //getWindow().setFormat(PixelFormat.UNKNOWN);
        mPreview = (SurfaceView)findViewById(R.id.surfaceView);
        holder = mPreview.getHolder();
        holder.setFixedSize(800, 480);
        mediaPlayer = new  MediaPlayer();
        holder.addCallback(new com.example.jabir_shabbir.androiddashplayer.SurfaceHolder(mediaPlayer));



        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            Log.i("initialized player","initialized player");
            mediaPlayer.prepare();
            Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    boolean SetSourceAndPlay(String path)
    {
        try
        {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

}
