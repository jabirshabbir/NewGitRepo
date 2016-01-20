package com.example.jabir_shabbir.androiddashplayer;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

/**
 * Created by Jabir_shabbir on 27-09-2015.
 */
public class DownloadScheduler extends Thread {
    DownloadVideo downloadVideo;
    Player p;
    MeasureNetSpeed NetSpeed;
    int requestNo=0;
    boolean downloadStarted=false;
    long startPlaying=0;
    int firstSegmentDownloaded=0;
    int initialnumberOffoundSegments=0;
    DownloadParseMPD parseMPD;
    MeasureNetSpeed speed=null;

    DownloadScheduler(DownloadVideo video)
    {

        this.downloadVideo = video;
        speed.start();
    }

    public void run()
    {
        long lastTime=0;
        while (true) {

            if (downloadVideo.videoList.size() < 3&&downloadVideo.startPlayingTime==0) {
                if (downloadStarted == false)
                {
                    //get the lastest or appropriate segment no in xml firstSegmentDownloaded
                    lastTime = System.currentTimeMillis();
                    initialnumberOffoundSegments++;
                    downloadStarted = true;
                }
                else
                {
                    if (System.currentTimeMillis() >= lastTime + 200) {
                        //download next segment sequence no wise
                        //if not available
                        //check all segments failed and request them
                        if (downloadVideo.isModifyingPendingList == false && downloadVideo.isPlayerModifyingFailList == false)
                        {
                            downloadVideo.isDownloadSchdedulerModifyingPendingList=true;
                            downloadVideo.isDownloadSchedulerModifyingFailList=true;
                            for (int i = 0; i < downloadVideo.synchListrequestFail.size(); i++) {
                                //check what type if request has failed
                                //either no record in xml or download fail
                                HTTPRequests r = downloadVideo.synchListrequestFail.get(i);
                                int failType = r.requestFailType;
                                if (failType == 0)
                                {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(requestNo)) {

                                        downloadVideo.synchListrequestFail.remove(i);
                                        downloadVideo.synchListrequestPending.add(r);
                                        i--;
                                        String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                        //download high quality
                                        MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo);
                                        th.start();

                                    }
                                    else
                                    {
                                        //download xml
                                        downloadVideo.synchListrequestFail.remove(i);
                                        i--;
                                        MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(r.requestNo,r,true,true,downloadVideo);
                                        th.start();
                                        /*String xmlPath = "";
                                        AsyncTask<String, String, String> task = downloadVideo.execute();
                                        if (xmlPath != null && !xmlPath.equals("")) {
                                            parseMPD.segmentRequired = r.requestNo;
                                            if (parseMPD.ParseMPD(r.requestNo)) {

                                                String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                                //download high quality
                                                downloadVideo.synchListrequestPending.remove(i);
                                                downloadVideo.synchListrequestPending.add(r);
                                                i--;
                                                downloadVideo.execute(path);
                                                initialnumberOffoundSegments++;
                                            }


                                        }*/

                                    }
                                }
                                else if (failType == 1)
                                {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(r.requestNo))
                                    {

                                        String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                        //download high quality
                                        downloadVideo.synchListrequestFail.remove(r);
                                        downloadVideo.synchListrequestPending.add(r);
                                        i--;
                                        MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo);
                                        th.start();
                                        //downloadVideo.execute(path);
                                        // initialnumberOffoundSegments++;
                                    }

                                }
                            }
                            if (initialnumberOffoundSegments < 3)
                            {

                                requestNo++;
                                if (DownloadParseSegmentsBeforePlay(requestNo)) {
                                    //increment the number of segments found
                                    initialnumberOffoundSegments++;
                                }
                            }

                            downloadVideo.isDownloadSchdedulerModifyingPendingList=false;
                            downloadVideo.isDownloadSchedulerModifyingFailList=false;
                        }

                    }
                }

            } else {

                if (downloadVideo.videoStarted == false) {
                    if (System.currentTimeMillis() >= startPlaying && startPlaying != 0) {
                        downloadVideo.videoStarted = true;
                    }
                    if(downloadVideo.isPlayerModifyingFailList==false&&downloadVideo.isDownloadSchedulerModifyingFailList==false)
                    {
                        downloadVideo.isDownloadSchedulerModifyingFailList=true;
                        for (int i = 0; i < downloadVideo.synchListrequestFail.size(); i++) {
                            HTTPRequests req = downloadVideo.synchListrequestFail.get(i);
                            if (req.requestNo < downloadVideo.lastsegmentNoonBufferfull) {
                                downloadVideo.synchListrequestFail.remove(i);
                                i--;
                            }
                        }
                        downloadVideo.isDownloadSchedulerModifyingFailList=false;
                    }
                }

                //request for next segment no
                if (downloadVideo.videoStarted==false&&downloadVideo.segmentbeforestart ==-1)
                {
                    requestNo++;
                    HTTPRequests req=new HTTPRequests();
                    req.requestNo=requestNo;
                    req.quality="High";
                    req.requestFirstSentTime= req.requestSentTime;
                    if (parseMPD.ParseMPD(requestNo))
                    {

                        downloadVideo.startDownloadTime = System.currentTimeMillis();
                        int size = parseMPD.segmentSizeHigh;
                        float avgSpeed = speed.avgSpeed;
                        float avgTime = size / avgSpeed;
                        startPlaying = System.currentTimeMillis() + (long) avgTime - downloadVideo.segmentDuration;
                        downloadVideo.segmentbeforestart = requestNo;
                        downloadVideo.benchmarksegment=requestNo;
                        String path=parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + req.requestNo + ".mp4";
                        MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo);
                        th.start();
                    }
                    else
                    {
                        boolean downloadSuccess=false;
                        try
                        {

                            HttpClient httpClient = HttpClientBuilder.create().build();
                        HttpResponse resp = httpClient.execute(new HttpGet(parseMPD.url));
                        DataOutputStream fos = null;
                        if (resp.getEntity() != null)
                        {

                                FileOutputStream out = new FileOutputStream(parseMPD.basefilePath + "/"+"MPD.xml");
                                resp.getEntity().writeTo(fos);
                                downloadSuccess=true;
                        }
                        }
                        catch(Exception ex)
                        {

                        }
                        if(downloadSuccess)
                        {
                            if (parseMPD.ParseMPD(requestNo))
                            {
                                downloadVideo.startDownloadTime = System.currentTimeMillis();
                                int size = parseMPD.segmentSizeHigh;
                                float avgSpeed = speed.avgSpeed;
                                float avgTime = size / avgSpeed;
                                startPlaying = System.currentTimeMillis() + (long) avgTime - downloadVideo.segmentDuration;
                                downloadVideo.segmentbeforestart = requestNo;
                                downloadVideo.benchmarksegment=requestNo;
                                String path=parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + req.requestNo + ".mp4";
                                MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo);
                                th.start();
                            }
                            else
                            {
                                req.requestFailType=0;
                                downloadVideo.synchListrequestFail.add(req);
                            }
                        }
                        else
                        {
                            req.requestFailType=0;
                            downloadVideo.synchListrequestFail.add(req);
                        }


                    }

                }

            }


            if (downloadVideo.segmentbeforestart > 0)
            {
                if (System.currentTimeMillis() - downloadVideo.startDownloadTime >= downloadVideo.segmentDuration) {
                    downloadVideo.startDownloadTime = System.currentTimeMillis();
                    // downloadVideo.requestsentTime=System.currentTimeMillis();
                    HTTPRequests req = new HTTPRequests();
                    requestNo++;
                    req.requestNo = requestNo;
                    req.requestSentTime = System.currentTimeMillis();
                    MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(req.requestNo,req,false,true,downloadVideo);
                    th.start();
                    while(downloadVideo.isModifyingPendingList==false)
                    {
                        //wait to be free
                    }
                    downloadVideo.synchListrequestPending.add(req);
                    downloadVideo.startDownloadTime=System.currentTimeMillis();

                    //call execute

                }
                else
                {
                    if (downloadVideo.downloadVideoModifyingFailList == false&&downloadVideo.isPlayerModifyingFailList==false&&downloadVideo.downloadVideoModifyingPendingList==false)
                    {
                        downloadVideo.isDownloadSchedulerModifyingFailList=true;
                        downloadVideo.isDownloadSchdedulerModifyingPendingList=true;
                        if (downloadVideo.synchListrequestFail.size() > 0) {
                            for (int i = 0; i < downloadVideo.synchListrequestFail.size(); i++) {
                                HTTPRequests re = (HTTPRequests) downloadVideo.synchListrequestFail.get(i);
                                if (re.requestNo > downloadVideo.lastsegmentNoonBufferfull) {


                                    if (System.currentTimeMillis() - re.requestSentTime >= 200) {
                                        HTTPRequests req = new HTTPRequests();
                                        //requestNo;
                                        req.requestNo = re.requestNo;
                                        req.requestSentTime = System.currentTimeMillis();

                                        //downloadVideo.synchListrequestPending.add(req);
                                        //call execute

                                        if(req.requestFailType==0)
                                        {
                                            if (parseMPD.ParseMPD(requestNo))
                                            {
                                                String folder="";
                                                if(re.quality=="High")
                                                  folder=parseMPD.highFolder;
                                                else if(re.quality=="Medium")
                                                    folder=parseMPD.midFolder;
                                                else if(re.quality=="Low")
                                                    folder=parseMPD.lowFolder;
                                                if(!folder.equals(""))
                                                {
                                                    downloadVideo.synchListrequestFail.remove(i);
                                                    String path = parseMPD.basefilePath + "/" + folder + "/" + "Seg" + req.requestNo + ".mp4";
                                                    MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo);
                                                    th.start();
                                                }

                                            }
                                            else {
                                                downloadVideo.synchListrequestFail.remove(i);
                                                MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(re.requestNo, re, true, true, downloadVideo);
                                                th.start();
                                            }
                                        }
                                        else if(req.requestFailType==1)
                                        {
                                            String folder="";
                                            if(re.quality=="High")
                                                folder=parseMPD.highFolder;
                                            else if(re.quality=="Medium")
                                                folder=parseMPD.midFolder;
                                            else if(re.quality=="Low")
                                                folder=parseMPD.lowFolder;
                                            if(!folder.equals("")) {
                                                downloadVideo.synchListrequestFail.remove(i);
                                                String path = parseMPD.basefilePath + "/" + folder + "/" + "Seg" + req.requestNo + ".mp4";
                                                MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo);
                                                th.start();
                                            }

                                        }
                                        break;

                                    }
                                }
                            }
                        }
                        downloadVideo.isDownloadSchedulerModifyingFailList=false;
                        downloadVideo.isDownloadSchdedulerModifyingPendingList=false;
                    }
                }

            }
        }
    }

    boolean DownloadParseSegmentsBeforePlay(int segmentNo)
    {
        parseMPD.segmentRequired=segmentNo;
        long reqsentTime=System.currentTimeMillis();
        if(parseMPD.ParseMPD(segmentNo))
        {
            int lowSize = parseMPD.segmentSizeLow;
            int midSize = parseMPD.segmentSizeMid;
            int highSize = parseMPD.segmentSizeHigh;
            String path=parseMPD.basePath+"/"+parseMPD.highFolder+"/"+"Seg"+String.valueOf(segmentNo)+".mp4";
            //download high quality
            HTTPRequests req=new HTTPRequests();
            req.requestNo=segmentNo;
            req.requestSentTime=reqsentTime;
            req.quality="High";
            req.requestFirstSentTime=reqsentTime;
            req.requestFailType=-1;
            while(downloadVideo.downloadVideoModifyingPendingList==true)
            {

            }
            downloadVideo.downloadVideoModifyingPendingList=true;
            downloadVideo.synchListrequestPending.add(req);
            downloadVideo.downloadVideoModifyingPendingList=false;
            MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo);
            th.start();
            //downloadVideo.execute
            return true;
        }
        else
        {
            //download xml
           /* String xmlPath="";
            AsyncTask<String,String,String>task= downloadVideo.execute();

            if(xmlPath!=null&&!xmlPath.equals(""))
            {
                parseMPD.segmentRequired=segmentNo;
                if(parseMPD.ParseMPD(segmentNo))
                {*/

                    String path=parseMPD.basePath+"/"+parseMPD.highFolder+"/"+"Seg"+segmentNo+".mp4";
                    //download high quality
                    HTTPRequests req=new HTTPRequests();
                    req.requestNo=segmentNo;
                    req.requestSentTime=reqsentTime;
                    req.quality="High";
                    req.requestFirstSentTime=reqsentTime;
                    req.requestFailType=-1;
                    MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(req.requestNo,req,true,true,downloadVideo);
                    th.start();
                   /* while(downloadVideo.downloadVideoModifyingPendingList==true)
                    {

                    }
                    downloadVideo.downloadVideoModifyingPendingList=true;
                    downloadVideo.synchListrequestPending.add(req);
                    downloadVideo.downloadVideoModifyingPendingList=false;
                    MPDVideoDownloaderThread th=new MPDVideoDownloaderThread();
                    downloadVideo.execute(path);*/

                    //return true;
                }
               /* else
                {
                    HTTPRequests req=new HTTPRequests();
                    req.requestNo=segmentNo;
                    req.requestSentTime=reqsentTime;
                    req.quality="High";
                    req.requestFirstSentTime=reqsentTime;
                    req.requestFailType=0;
                    while(downloadVideo.downloadVideoModifyingFailList==true)
                    {

                    }
                    downloadVideo.downloadVideoModifyingFailList=true;
                    downloadVideo.synchListrequestFail.add(req);
                    downloadVideo.downloadVideoModifyingFailList=false;
                     return false;
                }

            }
            else
            {
                HTTPRequests req=new HTTPRequests();
                req.requestNo=segmentNo;
                req.requestSentTime=reqsentTime;
                req.quality="High";
                req.requestFirstSentTime=reqsentTime;
                req.requestFailType=0;
                while(downloadVideo.downloadVideoModifyingFailList==true)
                {

                }
                downloadVideo.downloadVideoModifyingFailList=true;
                downloadVideo.synchListrequestFail.add(req);
                downloadVideo.downloadVideoModifyingFailList=false;

                return false;
            }*/

    }

    String AdaptationControl(HTTPRequests request)
    {
        long expectedTimeTodownloadInitially = System.currentTimeMillis() + 3 * downloadVideo.segmentDuration;
            long newDownloadTime = System.currentTimeMillis() + (long) (parseMPD.segmentSizeHigh / NetSpeed.avgSpeed);
            if (newDownloadTime < p.playWindowStart + (3 * downloadVideo.segmentDuration)) {
                request.quality = "High";
                downloadVideo.synchListrequestPending.add(request);
                String url=parseMPD.basefilePath+"/"+parseMPD.highFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                return url;
            } else {
                long newDownloadTimemed = System.currentTimeMillis() + (long) (parseMPD.segmentSizeMid / NetSpeed.avgSpeed);
                if (newDownloadTimemed < p.playWindowStart + (3 * downloadVideo.segmentDuration)) {
                    request.quality = "Medium";
                    String url=parseMPD.basefilePath+"/"+parseMPD.midFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                    downloadVideo.synchListrequestPending.add(request);
                    return url;
                } else
                {

                    request.quality = "Low";
                    String url=parseMPD.basefilePath+"/"+parseMPD.lowFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                    downloadVideo.synchListrequestPending.add(request);
                    return url;
                }

                //likewise check for lower qualities
            }

        }

}
