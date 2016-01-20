package com.example.jabir_shabbir.mediaextractor;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.nio.ByteBuffer;

/**
 * Created by Jabir_shabbir on 24-09-2015.
 */
public class ExtractorThread extends Thread
{
   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public void run()
   {
       MediaExtractor extractor = new MediaExtractor();
       //MediaCodec codec=MediaCodec.createDecoderByType()
       try {
           Log.i("hello", "hello");
           extractor.setDataSource("/sdcard/Download/tsfile.ts");
           //Log.i("hello","hello");
           int numTracks = extractor.getTrackCount();
           //TextView tv=(TextView) findViewById(R.id.pTime);
           for (int i = 0; i < numTracks; ++i)
           {
               MediaFormat format = extractor.getTrackFormat(i);
               String mime = format.getString(MediaFormat.KEY_MIME);
               //  if (weAreInterestedInThisTrack) {
                     extractor.selectTrack(i);
               //}
               //tv.setText(mime);
               //int x=0;
              // MediaCodecList ls=new MediaCodecList(ALL_CODECS);
               int count=MediaCodecList.getCodecCount();
               for(int j=0;j<count;j++)
               {
                   MediaCodecInfo info=MediaCodecList.getCodecInfoAt(j);
                   for(int k=0;k<info.getSupportedTypes().length;k++) {
                       Log.i("The codec info is" + info.getSupportedTypes()[k].toString(), "The codec info is" + info.toString());
                   }
               }
               MediaCodec codec=MediaCodec.createDecoderByType(mime);
               Log.i("Creating decoder","creating decoder");
               codec.configure(format,null,null,0);
               int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
               Log.i(mime+"mine is",mime+"mine is");
               Log.i("The format is" + extractor.getTrackFormat(i),"The format is" + extractor.getTrackFormat(i));
               MediaCodecCallBack cBack=new MediaCodecCallBack(codec,extractor,sampleRate);
               codec.setCallback(cBack);
               codec.start();
           }
          /* ByteBuffer inputBuffer = ByteBuffer.allocate(100);

           while (extractor.readSampleData(inputBuffer, 0) >= 0) {
               Log.i("next step","next step");
               int trackIndex = extractor.getSampleTrackIndex();
               long presentationTimeUs = extractor.getSampleTime();
               Log.i("presentation time is"+presentationTimeUs,"presentation time is"+presentationTimeUs);
               extractor.advance();
           }*/
       }
       catch (Exception ex)
       {

           Log.i(ex.toString()+"exception is",ex.toString()+"exception is");
       }
   }

}
