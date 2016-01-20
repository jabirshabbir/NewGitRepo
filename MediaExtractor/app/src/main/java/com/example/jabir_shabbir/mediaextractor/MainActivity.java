package com.example.jabir_shabbir.mediaextractor;

import android.annotation.TargetApi;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaExtractor extractor = new MediaExtractor();
        /*try {
            Log.i("hello","hello");
            extractor.setDataSource("/sdcard/Download/source1.mp4");
            //Log.i("hello","hello");
            int numTracks = extractor.getTrackCount();
            TextView tv=(TextView) findViewById(R.id.pTime);
            for (int i = 0; i < numTracks; ++i)
            {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                //  if (weAreInterestedInThisTrack) {
                //      extractor.selectTrack(i);
                //}
                tv.setText(mime);

            }
        }
        catch (Exception ex)
        {

            Log.i(ex.toString()+"exception is",ex.toString()+"exception is");
        }
        */
        ExtractorThread th=new ExtractorThread();
        th.start();
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
