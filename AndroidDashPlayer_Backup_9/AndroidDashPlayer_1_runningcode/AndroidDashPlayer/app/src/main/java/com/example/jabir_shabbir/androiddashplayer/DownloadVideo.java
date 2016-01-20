package com.example.jabir_shabbir.androiddashplayer;

import android.app.VoiceInteractor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
/*import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;*/


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;
import com.loopj.android.http.*;
/**
 * Created by Jabir_shabbir on 26-09-2015.
 */

//to do list
    //1.segments before segment before start remove from the list of fail once segment before start is found
    //2.Consider the segment of xml entry fail as segment segment befor start if found
    //3.segment not present and playing time has arrived and if it comes during its playing slot discard it
public class DownloadVideo /*extends AsyncTask<String ,String, String>*/
{
    public
    int segmentNo=0;
   // int requestNo=0;
    String URL="";
    String quality="";
    long segmentDuration=3000;
    static String basefilePath="/sdcard/Download/";
    boolean segmentdownloaded=false;
    int greatestreceivedsegmentNo=-1;
    int lastsegmentDownloadTime=0;
    int segmentbeforestart=-1;
    long startPlayingTime=0;
    boolean videoStarted=false;
    boolean startplayingTimeSet=false;
    boolean isPlayerExtractingFromList=false;
    boolean isDownloaderModifyingList=false;
    int lastsegmentNoonBufferfull=0;
    long startDownloadTime=0;
    boolean bufferfilledenoughinitially=false;
    boolean buffernotfull=true;
    int numberOfSegmentsDownloaded=0;
    long endDownloadTime=0;
    long requestsentTime=0;
    int numOfVideosinBuffer=0;
    int benchmarksegment=-1;
    long averageDownloadTimeHigh=0;
    long averageDownloadTimeMedium=0;
    long averageDownloadTimeLow=0;
    long initialBufferingTime=0;
    long segmentToPlay=-1;
    boolean stopreg=false;
    boolean isPlayerModifyingFailList=false;
    boolean isModifyingPendingList;
    boolean isDownloadSchedulerModifyingFailList=false;
    boolean isDownloadSchdedulerModifyingPendingList=false;
    boolean downloadVideoModifyingFailList=false;
    boolean downloadVideoModifyingPendingList=false;
    boolean isParseMpdModifyingFailList=false;
    boolean isParseMpdModifyingPendingList=false;
    DownloadParseMPD parseMPD;
    long firstsegmentTimetoDownload=0;
    long expectencyDelay=0;
    Player p;
   // boolean requestFail=false;
    List<String> videoList=new ArrayList<String>();
    List<HTTPRequests> requestPending=new ArrayList<HTTPRequests>();
    List<HTTPRequests> synchListrequestPending = Collections.synchronizedList(requestPending);
    List<HTTPRequests> requestFail=new ArrayList<HTTPRequests>();
    List<HTTPRequests> synchListrequestFail = Collections.synchronizedList(requestFail);
    DownloadVideo()
  {

  }

    protected void onPostExecute(String s) {

    }



    protected void onPreExecute() {
      //  super.onPreExecute();
    }

    public String  VideoDownload(String url,int segmentNo)
    {
       // segmentdownloaded=false;
        synchronized(this) {
            Log.i("prateek_ur", "" + url);
            Log.i("prateek_segmentNo", "" + url);
            int requestNo = -1;
            InputStream input = null;
            OutputStream output = null;
            boolean filedownloaded = false;
            HttpURLConnection connection = null;
            try {

                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpResponse resp = httpClient.execute(new HttpGet(url));
                DataOutputStream fos = null;
                resp.getEntity().getContent();
                if (resp.getEntity() != null) {
                    //String segmentString = params[0].substring(0, params[0].lastIndexOf("."));
                    //   int localsegmentNo = segmentNo;//Integer.parseInt(segmentString);
                    //  segmentNo = localsegmentNo;
                    //figure out if the playing time has already passed out...
                    boolean segmentdiscarded = false;
                    boolean delayed = false;
                    //if((segmentNo)*segmentDuration+initialBufferingTime<System.currentTimeMillis()&&buffernotfull==false&&bufferfilledenoughinitially==true)
                    //  Log.i("download url", "" + url);

                   /* if (buffernotfull == false)
                    {
                        if (segmentToPlay == localsegmentNo) {
                            // if (System.currentTimeMillis() > segmentToPlay * segmentDuration + expectencyDelay + startPlayingTime)
                            //segmentdiscarded = true;
                            Log.i("discarded", "discarded");
                        }

                    }*/

                    /*if (segmentNo<p.lastsegmentPlayed)
                    {
                        //segmentdiscarded=true;
                        Log.i("discarded","discarded");
                    }*/

              /*      if (segmentNo < benchmarksegment && buffernotfull == false && bufferfilledenoughinitially == true)
                    {

                        if (p.lastsegmentPlayed > segmentNo || p.currentsegmentplaying > segmentNo)
                        {
                          //  segmentdiscarded = true;
                            Log.i("discarded","discarded");
                        }
                        else
                        {
                            expectencyDelay = expectencyDelay + 3000;
                            delayed = true;
                        }
                    }*/
                    String segNo = "";
                    segmentdiscarded = false;
                    if (segmentdiscarded == false) {
                        segNo = String.valueOf(segmentNo);
                        Log.i("Segment going to write", "Segment going to write");
                        if (segNo.equals("6"))
                            Log.i("6 seg downloaded", "6 seg downloaded");
                        Log.i("writing to fileSystem", "" + segNo);
                        InputStream is = resp.getEntity().getContent();
                        Log.i("prateek_content",""+resp.getEntity().getContentLength());
                        fos = new DataOutputStream(new FileOutputStream("/sdcard/Download/"+"Seg"+segNo+".mp4"));
                        //fos = new DataOutputStream(new FileOutputStream("/sdcard/Download/" + "Seg" + segNo + ".mp4"));
                        //resp.getEntity().writeTo(fos);
                        byte [] buffer = new byte[1024];
                        int bl =0;
                        while((bl=is.read(buffer))>0)
                        {
                            fos.write(buffer,0,bl);
                        }
                        Log.i("Segment no", " " + segNo);
                        filedownloaded = true;
                        fos.flush();
                        fos.close();
                        fos=null;
                     /*   AsyncHttpClient client = new AsyncHttpClient();
                        client.get(url,new AsyncHttpResponseHandler())
                        {

                            @Override
                            public void onStart()
                            {
                                // called before request is started
                            }


                            public void onSuccess(int statusCode, Header[] headers, byte[] response)
                            {
                                // called when response HTTP status is "200 OK"
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                            {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            }

                            @Override
                            public void onRetry(int retryNo) {
                                // called when request is retried
                            }
                        });*/
                        //reorder the segment
                        if (segmentNo < greatestreceivedsegmentNo) {
                           /* if (segmentNo < segmentbeforestart)
                            {
                                if (delayed == false)
                                {
                                    synchronized (this)
                                    {
                                        expectencyDelay = expectencyDelay + 3000;
                                    }
                                }
                            }
                            //delayed=false;
                            //copying it to another list can be an option
                            //doing the later part only when file is created successfully.
                            */

                            synchronized (this)
                            {
                                while (isPlayerExtractingFromList)
                                {

                                }
                                isDownloaderModifyingList = true;
                      /*      for (int k = 0; k < videoList.size(); k++)
                            {
                                String pathName = videoList.get(k);
                                String listsegNo = pathName.substring(pathName.lastIndexOf("/") + 4, pathName.lastIndexOf("."));
                                int no = Integer.parseInt(listsegNo);
                                if (no > segmentNo)
                                {

                                    List<String> backupList = new ArrayList<String>();
                                    for (int l = k; l < videoList.size(); l++)
                                    {
                                        String w = (String) videoList.get(l);
                                        backupList.add(w);
                                        Log.i("sunny_back",""+backupList);
                                        videoList.remove(l);
                                    }

                               // videoList.add(basefilePath + "/" + quality + "/" + filename);
*/
                                videoList.add(basefilePath + "/" + "Seg" + segNo + ".mp4");
                                //Collections.sort(videoList);

  /*                                for (int l = 0; l < backupList.size(); l++)
                                    {
                                        videoList.add(backupList.get(l));
                                    }

                                    Log.i("sunny_videoListsort", "" + videoList);
                                    Log.i("sunny_videoListsort", "" + backupList);
                                    Log.i("sunny_new no",""+segNo);
                                  break;
                                  }
                                   }*/
                                isDownloaderModifyingList = false;
                            }

                        } else {
                            greatestreceivedsegmentNo = segmentNo;
                            while (isPlayerExtractingFromList) {

                            }
                            isDownloaderModifyingList = true;
                            Log.i("sunny_videoList", "" + videoList);
                            videoList.add(basefilePath + "/" + "Seg" + segNo + ".mp4");
                            isDownloaderModifyingList = false;
                        }
                        System.out.println("video list in downloader " + " " + videoList.size());
                        for (int x = 0; x < videoList.size(); x++) {
                            System.out.println("list entry" + " " + videoList.get(x));
                        }
                        //  segmentdownloaded = true;
                        /*runOnUiThread(new Runnable() {
                                          public void run() {
                                              Toast.makeText(this,  + " seconds read", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                        );*/
                        /*synchronized (this) {
                            numberOfSegmentsDownloaded++;
                            if (bufferfilledenoughinitially != true) {
                                if (numberOfSegmentsDownloaded >= 3) {

                                    lastsegmentNoonBufferfull = localsegmentNo;
                                    bufferfilledenoughinitially = true;
                                }
                            }
                        }
                        synchronized (this) {
                            while (isDownloadSchdedulerModifyingPendingList) {

                            }
                            isModifyingPendingList = true;
                            /*for (int k = 0; k < synchListrequestPending.size(); k++) {
                                HTTPRequests done = (HTTPRequests) synchListrequestPending.get(k);

                                if (done.requestNo == requestNo) {
                                    synchListrequestPending.remove(k);
                                }
                            }*/
                          /*  isModifyingPendingList = false;
                        }*/
                    }

                }
                /*else
                {
                    //Closes the connection.
                    try
                    {
                        resp.getEntity().getContent().close();
                        //throw new IOException(statusLine.getReasonPhrase());
                    }
                    catch (Exception ex)
                    {

                    }
                }
*/
            } catch (Exception e) {

            }
       /* MainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity, "Hello", Toast.LENGTH_SHORT).show();
            }
        });*/

       /* if(filedownloaded==false)
        {
            synchronized (this)
            {
                while(isDownloadSchdedulerModifyingPendingList&&isPlayerModifyingFailList&&isDownloadSchedulerModifyingFailList)
                {

                }
                isModifyingPendingList=true;
                isDownloadSchedulerModifyingFailList=true;
               /* for (int i = 0; i < synchListrequestPending.size(); i++)
                {
                    HTTPRequests r = (HTTPRequests) synchListrequestPending.get(i);
                    if (((HTTPRequests) synchListrequestPending.get(i)).requestNo == requestNo)
                    {
                        synchListrequestPending.remove(i);
                        //r.expiryTime=System.currentTimeMillis();
                        r.requestFailType = 1;
                        synchListrequestFail.add(r);
                    }
                }
                isModifyingPendingList=false;
                isDownloadSchedulerModifyingFailList=true;
                // HTTPRequests r=synchListrequestPending.
                // requestFail=true;
            }*/
            return null;
        }
        }

       // return null;



    public void KeepDownloading()
    {
        while(true)
        {
           //send HTTP request


        }

    }
    public int GetSegmentNo()
    {
        return segmentNo;
    }
    public void  SetSegmentDuration(int segmentduration)
    {
        this.segmentDuration=segmentduration;
    }


    public void setQuality(String quality)
    {
        this.quality=quality;
    }
}
