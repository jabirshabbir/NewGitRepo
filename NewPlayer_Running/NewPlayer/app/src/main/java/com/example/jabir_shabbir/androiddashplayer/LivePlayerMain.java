package com.example.jabir_shabbir.androiddashplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

public class LivePlayerMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_player_main);

        String mpd_url = null;
        Boolean isLive = true; //default values

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mpd_url = extras.getString("mpd_url");
        }
        Toast.makeText(getApplicationContext(), mpd_url, Toast.LENGTH_LONG).show();

        if(mpd_url.equals("http://pilatus.d1.comp.nus.edu.sg/~team06/infopk.xml")) isLive = true;
        else isLive = false;

        //TextView tv=(TextView)findViewById(R.id.textView);
        DownloadVideo dv=new DownloadVideo();
        DownloadScheduler ds=new DownloadScheduler(dv);
        ds.isLive=isLive;
        DownloadParseMPD downloadParseMPD=new DownloadParseMPD();
        downloadParseMPD.video=dv;
        downloadParseMPD.basePath=mpd_url;
        downloadParseMPD.ParseInitialParameters();
        ds.parseMPD=downloadParseMPD;
        MeasureNetSpeed ms = new MeasureNetSpeed(this.getApplicationContext());

        ds.NetSpeed=ms;
        ms.start();
        Player p=new Player(dv);
        dv.p=p;
        initMediaPlayer(p.media);
        p.media.initMediaPlayer();
        p.media.mPreview = (SurfaceView)findViewById(R.id.surfaceView);
        p.start();
        ds.start();


    }

    public void initMediaPlayer(PlayMedia mp)
    {
        String PATH_TO_FILE = "/sdcard/Download/tsfile.ts";
        //getWindow().setFormat(PixelFormat.UNKNOWN);
        // if(findViewById(R.id.textView)==null)
        //    Log.i("view not exists","view not exists");
        mp.mPreview = (SurfaceView)findViewById(R.id.surfaceView);
        mp.holder = mp.mPreview.getHolder();
        mp.holder.setFixedSize(800, 480);
        mp.mediaPlayer = new MediaPlayer();
        mp.holder.addCallback(new com.example.jabir_shabbir.androiddashplayer.SurfaceHolder(mp.mediaPlayer));
        mp.holder.setType(android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        /*mp.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
                mp.isPlaying = false;// finish current activity
            }
        });

        mp.holder.setType(android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        try {
           mp.mediaPlayer.setDataSource(PATH_TO_FILE);
            Log.i("initialized player","initialized player");
            mp.mediaPlayer.prepare();
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
        }*/
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
}
