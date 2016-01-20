package com.example.jabir_shabbir.androiddashplayer;

import android.util.Log;

//import ch.boye.httpclientandroidlib.HttpResponse;
//import ch.boye.httpclientandroidlib.client.HttpClient;
//import ch.boye.httpclientandroidlib.client.methods.HttpGet;
//import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

/**
 * Created by Jabir_shabbir on 27-09-2015.
 */
public class DownloadScheduler extends Thread {
    DownloadVideo downloadVideo;
    Player p;
    MeasureNetSpeed NetSpeed;
    int requestNo=-1;
    boolean downloadStarted=false;
    long startPlaying=0;
    int firstSegmentDownloaded=0;
    int initialnumberOffoundSegments=0;
    DownloadParseMPD parseMPD;
    boolean isLive=false;
    //MeasureNetSpeed speed=null;

    DownloadScheduler(DownloadVideo video)
    {

        this.downloadVideo = video;
       // NetSpeed=new MeasureNetSpeed();
        //NetSpeed.start();
        //speed.start();
    }

    public void run()
    {
        long lastTime=0;
        while (true)
        {

      //      if (downloadVideo.videoList.size() < 3&&downloadVideo.startPlayingTime==0)
      //      {
                if (downloadStarted == false)
                {
                    //get the lastest or appropriate segment no in xml firstSegmentDownloaded
                    // Log.i("scheduler running","scheduler running");

                    if(parseMPD.DownloadMPD(-1))
                    {
                        int initialsegment =-1; //parseMPD.GetInitialSegment();
                        if(isLive)
                           initialsegment=1;
                        else
                            initialsegment=parseMPD.GetInitialSegment();
                        if(initialsegment!=-1)
                        {
                              Log.i("foundSegScheduler","foundSegScheduler");
                            String res="high/output1";
                            if (NetSpeed.netspeed <55.00 && NetSpeed.netspeed >52.00)
                            {
                                res = "mid/output1";
                            }
                            else if(NetSpeed.netspeed<52)
                            {
                                res = "low/output1";
                            }
                            else
                            {

                            }
                            String firstSegmentPath = parseMPD.basePath + "/"+"Seg"+String.valueOf(initialsegment)+"/" +res+ ".mp4";
                            //Log.i("basepath",firstSegmentPath);
                            HTTPRequests req=new HTTPRequests();
                            req.requestFirstSentTime=System.currentTimeMillis();
                            req.requestSentTime=req.requestFirstSentTime;
                            req.requestNo=initialsegment;
                            req.quality="High";
                            requestNo=req.requestNo;
                            downloadVideo.startDownloadTime=System.currentTimeMillis()+3000;
                            MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(firstSegmentPath, downloadVideo,req);
                            th.start();
                            lastTime = System.currentTimeMillis();
                            initialnumberOffoundSegments++;
                            downloadStarted = true;
                        }
                    }
                }
                else
                {
                    if (true/*System.currentTimeMillis() >= lastTime + 200*/)
                    {
                        //download next segment sequence no wise
                        //if not available
                        //check all segments failed and request them
                        /*if (downloadVideo.isModifyingPendingList == false && downloadVideo.isParseMpdModifyingPendingList == false&&downloadVideo.downloadVideoModifyingFailList==false&&downloadVideo.isParseMpdModifyingFailList==false) {
                            downloadVideo.isDownloadSchdedulerModifyingPendingList = true;
                            downloadVideo.isDownloadSchedulerModifyingFailList = true;
                            for (int i = 0; i < downloadVideo.synchListrequestFail.size(); i++) {
                                //check what type if request has failed
                                //either no record in xml or download fail
                                HTTPRequests r = downloadVideo.synchListrequestFail.get(i);
                                int failType = r.requestFailType;
                                if (failType == 0) {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(requestNo)) {

                                        downloadVideo.synchListrequestFail.remove(i);
                                        downloadVideo.synchListrequestPending.add(r);
                                        i--;
                                        //String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                        String path = parseMPD.basePath + "/" + "Seg" + String.valueOf(r.requestNo) + "/" + "high" + "/" + "output1" + ".mp4";
                                        //download high quality
                                        MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo, r);
                                        th.start();

                                    } else {
                                        //download xml
                                        downloadVideo.synchListrequestFail.remove(i);
                                        i--;
                                        MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(r.requestNo, r, true, true, downloadVideo);
                                        th.start();


                                    }
                                } else if (failType == 1) {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(r.requestNo)) {

                                        String path = parseMPD.basePath + "/" + "Seg" + String.valueOf(r.requestNo) + "/" + "high" + "/" + "output1" + ".mp4";
                                        //download high quality
                                        downloadVideo.synchListrequestFail.remove(r);
                                        downloadVideo.synchListrequestPending.add(r);
                                        i--;
                                 success parsed seg2       MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo, r);
                                        th.start();
                                        //downloadVideo.execute(path);
                                        // initialnumberOffoundSegments++;
                                    }

                                }
                            }
                        }*/
                        if (true/*isLive//*initialnumberOffoundSegments < 3*/)
                        {



                            if(System.currentTimeMillis()>downloadVideo.startDownloadTime&&downloadVideo.startDownloadTime!=0)
                            {


                                if(isLive)
                                {
                                    requestNo++;
                                    DownloadParseSegmentsBeforePlay(requestNo);
                                    downloadVideo.startDownloadTime=System.currentTimeMillis()+3000;

                                    //increment the number of segments found
                                  //
                                }
                                else
                                {
                                    requestNo++;
                                    if(DownloadParseSegmentsBeforePlay(requestNo))
                                    {
                                        downloadVideo.startDownloadTime=System.currentTimeMillis()+3000;
                                    }

                                }

                               // downloadVideo.startDownloadTime=System.currentTimeMillis()+5000;
                                initialnumberOffoundSegments++;
                            }
                        }

                        else
                        {
                            requestNo++;
                            DownloadParseSegmentsBeforePlay(requestNo);
                            initialnumberOffoundSegments++;
                        }

                        // downloadVideo.isDownloadSchdedulerModifyingPendingList=false;
                        // downloadVideo.isDownloadSchedulerModifyingFailList=false;
                        //  }

                    }
                }

       //     }
      //      else
      //      {
                // Log.i("List size > than 3","List size greater than 3");
                if (downloadVideo.videoStarted == false)
                {
                    if (true/*System.currentTimeMillis() >= startPlaying && startPlaying != 0*/) {
                        downloadVideo.videoStarted = true;
                        downloadVideo.startPlayingTime=System.currentTimeMillis();
                        Log.i("start video...","start video...");
                        //System.out.println("List size in sch"+downloadVideo.videoList.size());
                    }
                /*    if(downloadVideo.isParseMpdModifyingFailList==false&&downloadVideo.isDownloadSchedulerModifyingFailList==false)
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
                    }*/
                }

                //request for next segment no
                // Log.i("download video"+String.valueOf(downloadVideo.videoStarted),String.valueOf(downloadVideo.segmentbeforestart));
                /*if (downloadVideo.videoStarted==false&&downloadVideo.segmentbeforestart ==-1)
                {
                    requestNo++;
                    HTTPRequests req=new HTTPRequests();
                 //   Log.i("request seg b4 start",String.valueOf(requestNo));
                    if (parseMPD.ParseMPD(requestNo))
                    {

                        req.requestNo=requestNo;
                        req.quality="High";
                        req.requestFirstSentTime= System.currentTimeMillis();
                        req.requestSentTime=req.requestFirstSentTime;
                        Log.i("Segment b4 start found","Segment before start found");
                        downloadVideo.startDownloadTime = System.currentTimeMillis();
                        int size = parseMPD.segmentSizeHigh;
                        float avgSpeed = NetSpeed.avgSpeed;
                        float avgTime = size / avgSpeed;
                        startPlaying = System.currentTimeMillis() + (long) avgTime - downloadVideo.segmentDuration;
                        downloadVideo.segmentbeforestart = requestNo;
                        downloadVideo.benchmarksegment=requestNo;
                        String path=parseMPD.basePath + "/" +"Seg"+String.valueOf(req.requestNo)+"/" + "high" + "/" + "output1"  + ".mp4";
                        MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo,req);
                        th.start();
                    }
                    else
                    {
                        boolean downloadSuccess=false;
                        try
                        {
                            HttpClient httpClient = HttpClientBuilder.create().build();
                        HttpResponse resp = httpClient.execute(new HttpGet(parseMPD.basePath+"/"+"info.xml"));
                        DataOutputStream fos = null;
                        if (resp.getEntity() != null)
                        {

                                FileOutputStream out = new FileOutputStream(parseMPD.basefilePath + "/"+"MPD1.xml");
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
                                float avgSpeed = NetSpeed.avgSpeed;
                                float avgTime = size / avgSpeed;
                                startPlaying = System.currentTimeMillis() + (long) avgTime - downloadVideo.segmentDuration;
                                downloadVideo.segmentbeforestart = requestNo;
                                downloadVideo.benchmarksegment=requestNo;
                                String path=parseMPD.basePath + "/" +"Seg"+String.valueOf(req.requestNo)+"/" + "high" + "/" + "output1" + ".mp4";
                                MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo,req);
                                th.start();
                            }
                            else
                            {
                                while(downloadVideo.isParseMpdModifyingFailList==false&&downloadVideo.isDownloadSchedulerModifyingFailList==false) {
                                }
                                downloadVideo.isDownloadSchedulerModifyingFailList=true;
                                req.requestFailType=0;
                                downloadVideo.synchListrequestFail.add(req);
                                downloadVideo.isDownloadSchedulerModifyingFailList=false;
                            }
                        }
                        else
                        {
                            req.requestFailType=0;
                            downloadVideo.isDownloadSchedulerModifyingFailList=true;
                            downloadVideo.synchListrequestFail.add(req);
                            downloadVideo.isDownloadSchedulerModifyingFailList=false;
                        }


                    }

                }*/

            }


            /*if (downloadVideo.segmentbeforestart > 0)
            {
                if (System.currentTimeMillis() - downloadVideo.startDownloadTime >= downloadVideo.segmentDuration) {
                    downloadVideo.startDownloadTime = System.currentTimeMillis();
                    // downloadVideo.requestsentTime=System.currentTimeMillis();
                    HTTPRequests req = new HTTPRequests();
                    requestNo++;
                    req.requestNo = requestNo;
                    req.requestSentTime = System.currentTimeMillis();
                    req.requestFirstSentTime=req.requestSentTime;
                    MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(req.requestNo,req,false,true,downloadVideo);
                    while(downloadVideo.isModifyingPendingList==true||downloadVideo.isParseMpdModifyingPendingList==true)
                    {
                        //wait to be free
                    }
                    downloadVideo.isDownloadSchdedulerModifyingPendingList=true;
                    downloadVideo.synchListrequestPending.add(req);
                    downloadVideo.isDownloadSchdedulerModifyingPendingList=false;
                    downloadVideo.startDownloadTime=System.currentTimeMillis();
                    th.start();
                    //call execute

                }
                else
                {
                    if (downloadVideo.downloadVideoModifyingFailList == false&&downloadVideo.isParseMpdModifyingFailList==false&&downloadVideo.downloadVideoModifyingPendingList==false&&downloadVideo.isParseMpdModifyingPendingList==false)
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
                                                /*String folder="";
                                                if(re.quality=="High")
                                                  folder=parseMPD.highFolder;
                                                else if(re.quality=="Medium")
                                                    folder=parseMPD.midFolder;
                                                else if(re.quality=="Low")
                                                    folder=parseMPD.lowFolder;

                                               // if(!folder.equals(""))
                                                */

                                             /*String path=AdaptationControlOfFail(req,parseMPD.segmentSizeLow,parseMPD.segmentSizeMid,parseMPD.segmentSizeHigh);
                                                {
                                                    downloadVideo.synchListrequestFail.remove(i);
                                                   // String path = parseMPD.basefilePath + "/" + folder + "/" + "Seg" + req.requestNo + ".mp4";
                                                    MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo,req);
                                                    th.start();
                                                }

                                            }
                                            else
                                            {

                                                downloadVideo.synchListrequestFail.remove(i);
                                                MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(re.requestNo, re, true, true, downloadVideo);
                                                th.start();
                                            }
                                        }
                                        else if(req.requestFailType==1)
                                        {
                                            String folder="",path="";
                                            if (parseMPD.ParseMPD(requestNo))
                                            {
                                                path=AdaptationControl(req,parseMPD.segmentSizeHigh,parseMPD.segmentSizeMid,parseMPD.segmentSizeLow);
                                            }
                                            else
                                            {
                                            if(re.quality=="High")
                                                folder=parseMPD.highFolder;
                                            else if(re.quality=="Medium")
                                                folder=parseMPD.midFolder;
                                            else if(re.quality=="Low")
                                                folder=parseMPD.lowFolder;
                                                //if(!folder.equals(""))
                                                path = parseMPD.basefilePath + "/" +"Seg"+String.valueOf(req.requestNo)+"/" + "high" + "/" + "output" + req.requestNo + ".mp4";
                                            }
                                            {
                                                downloadVideo.synchListrequestFail.remove(i);
                                                //String path = parseMPD.basefilePath + "/" + folder + "/" + "Seg" + req.requestNo + ".mp4";
                                                MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(path, downloadVideo,req);
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
         */
  //      }
    }

    boolean DownloadParseSegmentsBeforePlay(int segmentNo)
    {
        if(segmentNo==2)
            Log.i("seg2 in dpsbp","seg2 in dpsbp");
        parseMPD.segmentRequired=segmentNo;
        long reqsentTime=System.currentTimeMillis();
        if(isLive||parseMPD.ParseMPD(segmentNo))
        {
            int lowSize = parseMPD.segmentSizeLow;
            int midSize = parseMPD.segmentSizeMid;
            int highSize = parseMPD.segmentSizeHigh;
            String res="high/output1";
            if (NetSpeed.netspeed <55.00 && NetSpeed.netspeed >52.00)
            {
                res = "mid/output1";
            }
            else if(NetSpeed.netspeed<52)
            {
                res = "low/output1";
            }
            else
            {

            }
            String path=parseMPD.basePath + "/" +"Seg"+String.valueOf(segmentNo)+"/" + res +".mp4";
            //download high quality
            HTTPRequests req=new HTTPRequests();
            req.requestNo=segmentNo;
            req.requestSentTime=reqsentTime;
            req.quality="High";
            req.requestFirstSentTime=reqsentTime;
            req.requestFailType=-1;
            while(downloadVideo.downloadVideoModifyingPendingList==true||downloadVideo.isParseMpdModifyingPendingList==true)
            {

            }
            downloadVideo.downloadVideoModifyingPendingList=true;
            downloadVideo.synchListrequestPending.add(req);
            downloadVideo.downloadVideoModifyingPendingList=false;
            MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo,req);
           Log.i("the path is",""+path);
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
            if(parseMPD.DownloadMPD(segmentNo))
            {
                Log.i("download xml success",String.valueOf(segmentNo));
                if(parseMPD.ParseMPD(segmentNo))
                {
                    if(segmentNo==2)
                        Log.i("success parsed seg2","success parsed seg2");
                    Log.i("gotaSegmentbeforeStart","gotaSegmentbeforeStart");
                    String path = parseMPD.basePath + "/" +"Seg"+String.valueOf(segmentNo)+"/" + "high" + "/" + "output1" + ".mp4";
                    //download high quality
                    HTTPRequests req = new HTTPRequests();
                    req.requestNo = segmentNo;
                    req.requestSentTime = reqsentTime;
                    req.quality = "High";
                    req.requestFirstSentTime = reqsentTime;
                    req.requestFailType = -1;
                    //MPDVideoDownloaderThread th = new MPDVideoDownloaderThread(req.requestNo, req, true, true, downloadVideo);
                    MPDVideoDownloaderThread th=new MPDVideoDownloaderThread(path,downloadVideo,req);
                    th.start();
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
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

    String AdaptationControl(HTTPRequests request,int high,int mid,int low)
    {
        long expectedTimeTodownloadInitially = System.currentTimeMillis() + 3 * downloadVideo.segmentDuration;
        long newDownloadTime = System.currentTimeMillis() + (long) (high / NetSpeed.avgSpeed);
        if (newDownloadTime < p.playWindowStart + (3 * downloadVideo.segmentDuration)) {
            request.quality = "High";
            //downloadVideo.synchListrequestPending.add(request);
            String url=parseMPD.basefilePath + "/" +"Seg"+String.valueOf(request.requestNo)+"/" + "high" + "/" + "output" +String.valueOf(request.requestNo)+".mp4";
            return url;
        } else {
            long newDownloadTimemed = System.currentTimeMillis() + (long) (mid / NetSpeed.avgSpeed);
            if (newDownloadTimemed < p.playWindowStart + (3 * downloadVideo.segmentDuration)) {
                request.quality = "Medium";
                String url=parseMPD.basefilePath+ "/" +"Seg"+String.valueOf(request.requestNo)+"/" + "high" + "/" + "output" +String.valueOf(request.requestNo)+".mp4";
                //downloadVideo.synchListrequestPending.add(request);
                return url;
            } else
            {

                request.quality = "Low";
                String url=parseMPD.basefilePath+ "/" +"Seg"+String.valueOf(request.requestNo)+"/" + "high" + "/" + "output" +String.valueOf(request.requestNo)+".mp4";
                //downloadVideo.synchListrequestPending.add(request);
                return url;
            }

            //likewise check for lower qualities
        }

    }

    String AdaptationControlOfFail(HTTPRequests req,int low,int mid,int high)
    {

        int reqNo=req.requestNo;
        long firstSentTime=req.requestFirstSentTime;
        long lastSentTime=req.requestSentTime;
        long expectedTimeTodownloadInitially= req.expiryTime+downloadVideo.expectencyDelay;;
        //expectedTimeTodownloadInitially=firstSentTime+video.firstsegmentTimetoDownload;
        //expectedTimeTodownloadInitially=(reqNo-video.segmentbeforestart)*3000+video.initialBufferingTime+video.segmentbeforestart+video.expectencyDelay;
        //long timeafterstartPlaying=expectedTimeTodownloadInitially-video.initialBufferingTime;
        //long expectedTimestartPlaying=(reqNo-1)*video.segmentDuration+video.initialBufferingTime;
        long expectedTimestartPlaying=expectedTimeTodownloadInitially+6000;

        if(downloadVideo.startplayingTimeSet==true)
        {
            if(expectedTimeTodownloadInitially<=p.playWindowStart+(3*downloadVideo.segmentDuration)&&expectedTimeTodownloadInitially>System.currentTimeMillis())
            {
                long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(high/NetSpeed.avgSpeed)));
                if(newDownloadTime<p.playWindowStart+(3*downloadVideo.segmentDuration))
                {
                    req.quality="High";
                    //downloadVideo.synchListrequestFail.remove(0);
                    //downloadVideo.synchListrequestFail.add(req);
                    return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                }
                else
                {
                    long newDownloadTimemed=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                    if(newDownloadTimemed<p.playWindowStart+(3*downloadVideo.segmentDuration))
                    {
                        req.quality="Medium";
                        //downloadVideo.synchListrequestFail.remove(0);
                        //downloadVideo.synchListrequestFail.add(req);
                        return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                    }
                    else
                    {
                        // newDownloadTimemed=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                        // if(newDownloadTimemed<p.playWindowStart+(3*downloadVideo.segmentDuration))
                        {
                            req.quality="Low";
                            //downloadVideo.synchListrequestFail.remove(0);
                            //downloadVideo.synchListrequestFail.add(req);
                            return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                        }
                    }

                    //likewise check for lower qualities
                }

            }

            else if(expectedTimestartPlaying<=p.playWindowStart+(3*downloadVideo.segmentDuration)&&expectedTimestartPlaying>System.currentTimeMillis())
            {

                long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(high/NetSpeed.avgSpeed)));
                if(newDownloadTime<expectedTimestartPlaying)
                {
                    //set the request quality
                    req.quality="High";
                    downloadVideo.synchListrequestFail.remove(0);
                    downloadVideo.synchListrequestFail.add(req);
                    return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                }
                //likewise check for lower qualities
                else
                {
                    newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                    if(newDownloadTime<expectedTimestartPlaying)
                    {
                        req.quality="Medium";
                        downloadVideo.synchListrequestFail.remove(0);
                        downloadVideo.synchListrequestFail.add(req);
                        return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                    }
                    else
                    {
                        // newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(low/NetSpeed.avgSpeed)));
                        // if(newDownloadTime<expectedTimestartPlaying)
                        {
                            req.quality="Low";
                            downloadVideo.synchListrequestFail.remove(0);
                            downloadVideo.synchListrequestFail.add(req);
                            return parseMPD.basePath+ "/" +"Seg"+String.valueOf(reqNo)+"/" + "high" + "/" + "output" +String.valueOf(reqNo)+".mp4";
                        }

                    }

                    //video.synchListrequestFail.remove(i);
                    //i--;

                }



            }

            else if(expectedTimeTodownloadInitially>p.playWindowStart+(3*downloadVideo.segmentDuration))
            {
                long playWindowNo=(expectedTimestartPlaying-downloadVideo.startPlayingTime+downloadVideo.expectencyDelay)/(3*downloadVideo.segmentDuration);
                long newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+downloadVideo.averageDownloadTimeHigh));
                long endWindow=playWindowNo*3*downloadVideo.segmentDuration+downloadVideo.startPlayingTime+3*downloadVideo.segmentDuration;
                //if(newDownloadTime<(playWindowNo)*video.segmentDuration*3&&newDownloadTime<(playWindowNo)*video.segmentDuration*3+video.segmentDuration*3)
                if(newDownloadTime<endWindow)
                {
                    //set the request quality
                    req.quality="High";
                    downloadVideo.synchListrequestFail.remove(0);
                    downloadVideo.synchListrequestFail.add(req);
                    return parseMPD.basePath+"/"+parseMPD.highFolder+"/"+"SegNo"+String.valueOf(reqNo)+".mp4";
                }
                //likewise check for lower qualities
                else
                {
                    newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                    if(newDownloadTime<endWindow)
                    {
                        req.quality="Medium";
                        downloadVideo.synchListrequestFail.remove(0);
                        downloadVideo.synchListrequestFail.add(req);
                        return parseMPD.basePath+"/"+parseMPD.midFolder+"/"+"SegNo"+String.valueOf(reqNo)+".mp4";
                    }
                    else
                    {
                        newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(low/NetSpeed.avgSpeed)));
                        if(newDownloadTime<endWindow)
                        {
                            req.quality="Low";
                            downloadVideo.synchListrequestFail.remove(0);
                            downloadVideo.synchListrequestFail.add(req);
                            return parseMPD.basePath+"/"+parseMPD.lowFolder+"/"+"SegNo"+String.valueOf(reqNo)+".mp4";
                        }

                    }

                }

            }
            else if(System.currentTimeMillis()>expectedTimestartPlaying+downloadVideo.expectencyDelay&&!p.buffernotfull)
            {
                //discard the request
                //    downloadVideo.synchListrequestFail.remove(i);
                return null;
            }

        }
        return parseMPD.basePath+"/"+parseMPD.highFolder+"/"+"SegNo"+String.valueOf(reqNo)+".mp4";
    }

}

