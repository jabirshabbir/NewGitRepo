package com.example.jabir_shabbir.androiddashplayer;

import android.app.VoiceInteractor;
import android.os.AsyncTask;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * Created by Jabir_shabbir on 26-09-2015.
 */

//to do list
    //1.segments before segment before start remove from the list of fail once segment before start is found
    //2.Consider the segment of xml entry fail as segment segment befor start if found
    //3.segment not present and playing time has arrived and if it comes during its playing slot discard it
public class DownloadVideo extends AsyncTask<String ,String, String>
{
    public
    int segmentNo=0;
   // int requestNo=0;
    String URL="";
    String quality="";
    int segmentDuration=0;
    static String basefilePath="";
    boolean segmentdownloaded=false;
    int greatestreceivedsegmentNo=0;
    int lastsegmentDownloadTime=0;
    int segmentbeforestart=0;
    long startPlayingTime=0;
    boolean videoStarted=false;
    boolean startplayingTimeSet=false;
    boolean isPlayerExtractingFromList=false;
    boolean isDownloaderModifyingList=false;
    int lastsegmentNoonBufferfull=0;
    long startDownloadTime=0;
    boolean bufferfilledenoughinitially=false;
    boolean buffernotfull=false;
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

    @Override
    protected void onPostExecute(String s) {

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params)
    {
       // segmentdownloaded=false;
        try
        {
            int requestNo=-1;
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(params[0]));
            StatusLine statusLine = response.getStatusLine();
            String filename = "";
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                Header h[] = response.getAllHeaders();
                for (Header header : h)
                {
                    if (header.getName().equals("filename"))
                        filename = header.getValue();

                }

               /* InputStream is= u.openStream();
                DataInputStream dStream=new DataInputStream(is);
                byte[] buffer=new byte[1024];*/
                FileOutputStream out = new FileOutputStream(basefilePath + "/" + quality + "/" + filename);
                /*int length=0;
                while((length=dStream.read(buffer))>0)
                {
                    out.write(buffer,0,length);
                }
                out.close();*/
                String segmentString = filename.substring(0, filename.lastIndexOf("."));
                int localsegmentNo = Integer.parseInt(segmentString);
                segmentNo=localsegmentNo;
                //figure out if the playing time has already passed out...
                boolean segmentdiscarded=false;
                boolean delayed=false;
                //if((segmentNo)*segmentDuration+initialBufferingTime<System.currentTimeMillis()&&buffernotfull==false&&bufferfilledenoughinitially==true)
                if(buffernotfull==false)
                {
                   if(segmentToPlay==localsegmentNo)
                   {
                       if(System.currentTimeMillis()>segmentToPlay*segmentDuration+expectencyDelay+startPlayingTime)
                        segmentdiscarded=true;
                   }


                }
                 if(segmentNo<benchmarksegment&&buffernotfull==false&&bufferfilledenoughinitially==true)
                {

                    if(p.lastsegmentPlayed>segmentNo||p.currentsegmentplaying>segmentNo)
                    {
                        //discard the response
                        //expectencyDelay=expectencyDelay+3000;
                        segmentdiscarded=true;
                    }
                    expectencyDelay=expectencyDelay+3000;
                    delayed=true;
                }
                if(segmentdiscarded==false) {
                    response.getEntity().writeTo(out);
                    out.close();
                    //reorder the segment
                    if (segmentNo < greatestreceivedsegmentNo) {
                        if (segmentNo < segmentbeforestart) {
                            if(delayed==false)
                            {
                                synchronized (this)
                                {
                                    expectencyDelay = expectencyDelay + 3000;
                                }
                            }
                        }
                        //delayed=false;
                        //copying it to another list can be an option
                        synchronized (this)
                        {
                            while(isPlayerExtractingFromList)
                            {

                            }
                            isDownloaderModifyingList=true;
                            for (int k = 0; k < videoList.size(); k++)
                            {
                                String pathName = videoList.get(k);
                                String segNo = pathName.substring(pathName.lastIndexOf("/") + 1, pathName.lastIndexOf("."));
                                int no = Integer.parseInt(segNo);
                                if (no > segmentNo) {
                                    List<String> backupList = new ArrayList<String>();
                                    for (int l = k; l < videoList.size(); l++) {
                                        String w = (String) videoList.get(l);
                                        backupList.add(w);
                                        videoList.remove(l);
                                    }
                                    videoList.add(basefilePath + "/" + quality + "/" + filename);
                                    for (int l = 0; l < backupList.size(); l++) {
                                        videoList.add(backupList.get(l));
                                    }
                                }
                            }
                            isDownloaderModifyingList=false;
                        }

                    }
                    else
                    {
                        while(isPlayerExtractingFromList)
                        {

                        }
                        isDownloaderModifyingList=true;
                        videoList.add(basefilePath + "/" + quality + "/" + filename);
                        isDownloaderModifyingList=false;
                    }
                  //  segmentdownloaded = true;

                    synchronized (this) {
                        numberOfSegmentsDownloaded++;
                        if (bufferfilledenoughinitially != true) {
                            if (numberOfSegmentsDownloaded >= 3) {

                                lastsegmentNoonBufferfull = localsegmentNo;
                                bufferfilledenoughinitially = true;
                            }
                        }
                    }
                    synchronized (this)
                    {
                        while(isDownloadSchdedulerModifyingPendingList)
                        {

                        }
                        isModifyingPendingList=true;
                        for (int k = 0; k < synchListrequestPending.size(); k++) {
                            HTTPRequests done = (HTTPRequests) synchListrequestPending.get(k);

                            if (done.requestNo == requestNo)
                            {
                                synchListrequestPending.remove(k);
                            }
                        }
                        isModifyingPendingList=false;
                    }
                }

            } else {
                //Closes the connection.
                try {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                } catch (Exception e)
                {
                    synchronized (this)
                    {
                        while(isDownloadSchdedulerModifyingPendingList&&isPlayerModifyingFailList&&isDownloadSchedulerModifyingFailList)
                        {

                        }
                        isModifyingPendingList=true;
                        isDownloadSchedulerModifyingFailList=true;
                        for (int i = 0; i < synchListrequestPending.size(); i++) {
                            HTTPRequests r = (HTTPRequests) synchListrequestPending.get(i);
                            if (((HTTPRequests) synchListrequestPending.get(i)).requestNo == requestNo) {
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
                    }
                }
            }

        }

        catch(Exception ex)
        {

        }


        return null;
    }

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
