package com.example.jabir_shabbir.androiddashplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jabir_shabbir on 09-10-2015.
 */
public class PlayMedia extends AppCompatActivity
{
    MediaPlayer mediaPlayer;
    public SurfaceView mPreview;
    public SurfaceHolder holder;
    public boolean isPlaying=false;
    public void initMediaPlayer()
    {
        String PATH_TO_FILE = "/sdcard/Download/tsfile.ts";
        //getWindow().setFormat(PixelFormat.UNKNOWN);
        // if(findViewById(R.id.textView)==null)
        //    Log.i("view not exists","view not exists");
        //mPreview = (SurfaceView)findViewById(R.id.surfaceView);
        //holder = mPreview.getHolder();
        //holder.setFixedSize(800, 480);
        //mediaPlayer = new  MediaPlayer();
        //holder.addCallback(new com.example.jabir_shabbir.androiddashplayer.SurfaceHolder(mediaPlayer));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPlaying = false;// finish current activity
            }
        });

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        /*try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            Log.i("initialized player","initialized player");
            mediaPlayer.prepare();
            //Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }*/
    }

    boolean SetSourceAndPlay(String path)
    {
        try
        {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            isPlaying=true;
            mediaPlayer.start();
            return true;
        }
        catch(Exception ex)
        {
            Log.i("execption on attach","execption on attach");
            return false;
        }
    }

}
