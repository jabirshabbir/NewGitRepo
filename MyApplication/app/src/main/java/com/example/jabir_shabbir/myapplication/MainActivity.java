package com.example.jabir_shabbir.myapplication;

import android.media.MediaFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import 	android.media.MediaCodec;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //codec
        final MediaCodec codec = MediaCodec.createByCodecName(name);
        MediaFormat mOutputFormat; // member variable
        codec.setCallback(new MediaCodec.Callback() {
            @Override
            void onInputBufferAvailable(MediaCodec mc, int inputBufferId) {
                ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
                // fill inputBuffer with valid data
                codec.queueInputBuffer(inputBufferId, …);
            }

            @Override
            void onOutputBufferAvailable(MediaCodec mc, int outputBufferId, …) {
                ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
                MediaFormat bufferFormat = codec.getOutputFormat(outputBufferId); // option A
                // bufferFormat is equivalent to mOutputFormat
                // outputBuffer is ready to be processed or rendered.
                codec.releaseOutputBuffer(outputBufferId, …);
            }

            @Override
            void onOutputFormatChanged(MediaCodec mc, MediaFormat format) {
                // Subsequent data will conform to new format.
                // Can ignore if using getOutputFormat(outputBufferId)
                mOutputFormat = format; // option B
            }

            @Override
            void onError() {
            }
        });
        codec.configure(format, …);
        mOutputFormat = codec.getOutputFormat(); // option B
        codec.start();
        // wait for processing to complete
        codec.stop();
        codec.release();
        //end
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
