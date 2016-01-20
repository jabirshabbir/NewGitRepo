package com.example.jabir_shabbir.mediaextractor;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by Jabir_shabbir on 24-09-2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MediaCodecCallBack extends MediaCodec.Callback
{
    MediaCodec codec;
    MediaFormat mOutputFormat;
    MediaExtractor extractor;
    int sampleRate;
    AudioTrack mAudioTrack;
    boolean sawOutputEOS=false;
    boolean sawInputEOS=true;
    MediaCodecCallBack(MediaCodec codec,MediaExtractor extractor,int sampleRate)
    {
        this.codec=codec;
        this.extractor=extractor;
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize (
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT
                ),
                AudioTrack.MODE_STREAM
        );
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  void onInputBufferAvailable(MediaCodec mc, int inputBufferId)
    {
        ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
        // fill inputBuffer with valid data
        int sampleSize=0;
        sampleSize=extractor.readSampleData(inputBuffer, 0);
        long presentationTime=extractor.getSampleTime();
        if(sampleSize>0) {
            codec.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTime, 0);
            extractor.advance();
        }
        else {
            if(!sawInputEOS) {
                codec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                sawInputEOS=true;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onOutputBufferAvailable(MediaCodec mc, int outputBufferId,MediaCodec.BufferInfo binfo)
    {
        if(!sawOutputEOS) {
            int outputBufferIndex = codec.dequeueOutputBuffer(binfo, -1);

            if (outputBufferIndex > 0) {
                ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);

                // MediaFormat bufferFormat = codec.getOutputFormat(outputBufferId); // option A
                final byte[] chunk = new byte[binfo.size];
                if (chunk.length > 0) {
                    outputBuffer.get(chunk); // Read the buffer all at once
                    outputBuffer.clear(); //
                }
                codec.releaseOutputBuffer(outputBufferIndex, false);
                if ((binfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    sawOutputEOS = true;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                final MediaFormat oformat = codec.getOutputFormat();
                // Log.d(LOG_TAG, "Output format has changed to " + oformat);
                mAudioTrack.setPlaybackRate(oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
            }

        }
        // bufferFormat is equivalent to mOutputFormat
        // outputBuffer is ready to be processed or rendered.
        //codec.releaseOutputBuffer(outputBufferId);
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
