package com.example.jabir_shabbir.mediacodechandson;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by Jabir_shabbir on 24-09-2015.
 */
public class MediaCodecCallBack extends MediaCodec.Callback
{
   MediaCodec codec;
    MediaFormat mOutputFormat;
    MediaCodecCallBack(MediaCodec codec)
    {
        this.codec=codec;
    }
   public  void onInputBufferAvailable(MediaCodec mc, int inputBufferId)
    {
        ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
        // fill inputBuffer with valid data
        inputBuffer.
        codec.queueInputBuffer(inputBufferId);
    }

    public void onOutputBufferAvailable(MediaCodec mc, int outputBufferId,MediaCodec.BufferInfo binfo)
    {
        ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
        MediaFormat bufferFormat = codec.getOutputFormat(outputBufferId); // option A
        // bufferFormat is equivalent to mOutputFormat
        // outputBuffer is ready to be processed or rendered.
        codec.releaseOutputBuffer(outputBufferId);
    }

    public void onOutputFormatChanged(MediaCodec mc, MediaFormat format)
    {
        // Subsequent data will conform to new format.
        // Can ignore if using getOutputFormat(outputBufferId)

        mOutputFormat = format; // option B
    }

    public void onError(MediaCodec m,MediaCodec.CodecException exp)
    {
       // …
    }

}
