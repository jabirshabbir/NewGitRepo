package com.example.jabir_shabbir.androidmediaplayer;

import android.media.MediaPlayer;
import android.os.Environment;
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


import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button buttonPlayPause, buttonQuit;
    TextView textState;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private int stateMediaPlayer;
    private final int stateMP_Error = 0;
    private final int stateMP_NotStarter = 1;
    private final int stateMP_Playing = 2;
    private final int stateMP_Pausing = 3;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlayPause = (Button) findViewById(R.id.playpause);
        buttonQuit = (Button) findViewById(R.id.quit);
        textState = (TextView) findViewById(R.id.state);

       // buttonPlayPause.setOnClickListener(buttonPlayPauseOnClickListener);
       // buttonQuit.setOnClickListener(buttonQuitOnClickListener);
        DownloadVideo v = new DownloadVideo("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s");
        v.count = 0;
        v.start();
       /* try {
            long t=System.currentTimeMillis();
            while(System.currentTimeMillis()-t<6000)
            {

            }
        }
        catch(Exception ex)
        {
            Log.i("exc in sleep","excp in sleep");
        }*/
        DownloadVideo v1 = new DownloadVideo("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s");
        v1.count = 1;
        v1.start();
        //new DownloadVideo("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s").execute("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s");
        /*try
        {
            URL u = new URL("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s");
            InputStream is=u.openStream();
            DataInputStream dStream=new DataInputStream(is);
            byte[] buffer=new byte[1024];
            FileOutputStream fs=new FileOutputStream("Download.m4s");
            int length=0;
            while((length=dStream.read(buffer))>0)
            {
                fs.write(buffer,0,length);
            }
            fs.close();
        }
        catch(Exception ex)
        {
             Log.i(ex.toString(), "exception in download");
        }*/
        File f = new File(Environment.getExternalStorageDirectory() + "/Download/output.mp4");
        Log.i(String.valueOf(f.length()), String.valueOf(f.length()));
        Toast.makeText(this, String.valueOf(f.length()), Toast.LENGTH_LONG).show();
        initMediaPlayer();

    }

    private void initMediaPlayer() {
        String PATH_TO_FILE = Environment.getExternalStorageDirectory() + "/Download/output.mp4";
        //getWindow().setFormat(PixelFormat.UNKNOWN);
        mPreview = (SurfaceView) findViewById(R.id.surfaceView);
        holder = mPreview.getHolder();
        holder.setFixedSize(800, 480);
        mediaPlayer = new MediaPlayer();
        holder.addCallback(new com.example.jabir_shabbir.androidmediaplayer.SurfaceHolder(mediaPlayer));


        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


       /* try
        {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            Log.i("initialized player","initialized player");
            mediaPlayer.prepare();
            Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
            mediaPlayer.start();
            Log.i("hello","hello");
            stateMediaPlayer = stateMP_NotStarter;
            textState.setText("- IDLE -");
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            textState.setText("- ERROR!!! -");
        } catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            textState.setText("- ERROR!!! -");
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = stateMP_Error;
            textState.setText("- ERROR!!! -");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    Button.OnClickListener buttonPlayPauseOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(stateMediaPlayer){
                case stateMP_Error:
                    break;
                case stateMP_NotStarter:
                    mediaPlayer.start();
                    buttonPlayPause.setText("Pause");
                    textState.setText("- PLAYING -");
                    stateMediaPlayer = stateMP_Playing;
                    break;
                case stateMP_Playing:
                    mediaPlayer.pause();
                    buttonPlayPause.setText("Play");
                    textState.setText("- PAUSING -");
                    stateMediaPlayer = stateMP_Pausing;
                    break;
                case stateMP_Pausing:
                    mediaPlayer.start();
                    buttonPlayPause.setText("Pause");
                    textState.setText("- PLAYING -");
                    stateMediaPlayer = stateMP_Playing;
                    break;
            }

        }
    };

    Button.OnClickListener buttonQuitOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        }
    };*/

    }
}
