package nus.cs5248.project.mr;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

@SuppressWarnings("deprecation")
public class MediaRecorderRecipe extends Activity implements SurfaceHolder.Callback {
    private static final String VIDEO_PATH_NAME = "/Pictures/test.mp4";

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private View mToggleButton;
    private boolean mInitSuccesful;
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder_recipe);

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_PATH_NAME);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
        mToggleButton.setOnClickListener(new OnClickListener() {
            @Override
            // toggle video recording
            public void onClick(View v) {
                if (((ToggleButton)v).isChecked())
                    mMediaRecorder.start();
                else {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_PATH_NAME);
                    if(file.exists()) System.out.println("EXISTS");
                    else System.out.println("NAH");
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    try {
                        initRecorder(mHolder.getSurface());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /* Init the MediaRecorder, the order the methods are called is vital to
     * its correct functioning */
   // Replace this function with below and build please

    private void initRecorder(Surface surface) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.unlock();
        }

        if(mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        //mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setVideoSize(720, 480);
        mMediaRecorder.setVideoEncodingBitRate(3000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setMaxDuration(3600000);
        mMediaRecorder.setMaxFileSize(2000000000);

    /*mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

    //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
    //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

    mMediaRecorder.setVideoEncodingBitRate(3000000);

    //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_PATH_NAME);

    // "touch" the file
    if(!file.exists()) {
        File parent = file.getParentFile();
        if(parent != null)
            if(!parent.exists())
                if(!parent.mkdirs())
                    throw new IOException("Cannot create " +
                            "parent directories for file: " + file);

        file.createNewFile();
        System.out.println("********CREATED FILE********");
    }
    else System.out.println("File ALREADY found");

    mMediaRecorder.setOutputFile(file.getAbsolutePath());
    */
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }

        mInitSuccesful = true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if(!mInitSuccesful)
                initRecorder(mHolder.getSurface());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {  }


    private void shutdown()
    {
        // Release MediaRecorder and especially the Camera as it's a shared
        // object that can be used by other applications
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mCamera.release();

        // once the objects have been released they can't be reused
        mMediaRecorder = null;
        mCamera = null;
    }
}