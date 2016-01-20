package com.example.jabir_shabbir.androiddashplayer;

import android.util.Log;

/**
 * Created by Jabir_shabbir on 22-10-2015.
 */
public class MPDVideoDownloaderThread extends Thread
{
    String url;
    String mpdUrl;
    int segmentNo=-1;
    DownloadParseMPD parseMPD;
    HTTPRequests request;
    DownloadVideo video;
    boolean downloadMPD=false;
    boolean isFailed=false;
    MPDVideoDownloaderThread(String url,DownloadVideo video,HTTPRequests req)
    {
        this.url=url;
        this.video=video;
        this.request=req;
        this.segmentNo=req.requestNo;
    }
    MPDVideoDownloaderThread(int segmentNo,HTTPRequests request,boolean isFailed,boolean downloadMPD,DownloadVideo video)
    {
        this.segmentNo=segmentNo;
        this.request=request;
        this.isFailed=isFailed;
        this.downloadMPD=downloadMPD;
        this.video=video;
    }
    public synchronized void run()
    {
        Log.i("Thread id is", String.valueOf(this.getId()));
        DownloadParseMPD parseMPD=new DownloadParseMPD();
        parseMPD.ParseInitialParameters();
        parseMPD.video=video;
        this.parseMPD=parseMPD;
        if(downloadMPD)
            parseMPD.DowndoadMPD(request,isFailed);
        else
        {
            if (segmentNo==2)
                Log.i("seg 2 thread","seg 2 thread");
            video.VideoDownload(url, segmentNo);
        }
    }

}
