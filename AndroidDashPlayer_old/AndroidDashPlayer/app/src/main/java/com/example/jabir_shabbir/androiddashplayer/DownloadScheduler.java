package com.example.jabir_shabbir.androiddashplayer;

import android.os.AsyncTask;

/**
 * Created by Jabir_shabbir on 27-09-2015.
 */
public class DownloadScheduler extends Thread {
    DownloadVideo downloadVideo;
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
                if (downloadStarted == false) {
                    //get the lastest or appropriate segment no in xml firstSegmentDownloaded
                    lastTime = System.currentTimeMillis();
                    initialnumberOffoundSegments++;
                    downloadStarted = true;
                } else {
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
                                if (failType == 0) {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(requestNo)) {

                                        String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                        //download high quality
                                        downloadVideo.execute(path);

                                    } else {
                                        //download xml
                                        String xmlPath = "";
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


                                        }

                                    }
                                } else if (failType == 1) {
                                    parseMPD.segmentRequired = r.requestNo;
                                    if (parseMPD.ParseMPD(r.requestNo)) {

                                        String path = parseMPD.basePath + "/" + parseMPD.highFolder + "/" + "Seg" + String.valueOf(r.requestNo) + ".mp4";
                                        //download high quality
                                        downloadVideo.synchListrequestFail.remove(r);
                                        downloadVideo.synchListrequestPending.add(r);
                                        i--;
                                        downloadVideo.execute(path);
                                        // initialnumberOffoundSegments++;
                                    }


                                }
                            }
                            if (initialnumberOffoundSegments < 3) {

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
                if (downloadVideo.videoStarted==false&&downloadVideo.segmentbeforestart ==-1) {
                    requestNo++;
                    if (DownloadParseSegmentsBeforePlay(requestNo)) {
                        downloadVideo.startDownloadTime = System.currentTimeMillis();
                        int size = parseMPD.segmentSizeHigh;
                        float avgSpeed = speed.avgSpeed;
                        float avgTime = size / avgSpeed;
                        startPlaying = System.currentTimeMillis() + (long) avgTime - 3000;
                        downloadVideo.segmentbeforestart = requestNo;
                        downloadVideo.benchmarksegment=requestNo;
                    }

                }

            }


            if (downloadVideo.segmentbeforestart > 0) {
                if (System.currentTimeMillis() - downloadVideo.startDownloadTime >= downloadVideo.segmentDuration) {
                    downloadVideo.startDownloadTime = System.currentTimeMillis();
                    // downloadVideo.requestsentTime=System.currentTimeMillis();
                    HTTPRequests req = new HTTPRequests();
                    requestNo++;
                    req.requestNo = requestNo;
                    req.requestSentTime = System.currentTimeMillis();
                    while(downloadVideo.isModifyingPendingList==false)
                    {
                        //wait to be free
                    }
                    downloadVideo.synchListrequestPending.add(req);
                    downloadVideo.startDownloadTime=System.currentTimeMillis();

                    //call execute

                } else {
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

                                        downloadVideo.synchListrequestPending.add(req);
                                        //call execute


                                        downloadVideo.synchListrequestFail.remove(i);

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
            downloadVideo.execute(path);
            return true;
        }
        else
        {
            //download xml
            String xmlPath="";
            AsyncTask<String,String,String>task= downloadVideo.execute();

            if(xmlPath!=null&&!xmlPath.equals(""))
            {
                parseMPD.segmentRequired=segmentNo;
                if(parseMPD.ParseMPD(segmentNo))
                {

                    String path=parseMPD.basePath+"/"+parseMPD.highFolder+"/"+"Seg"+segmentNo+".mp4";
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
                    downloadVideo.execute(path);

                    return true;
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
            }

        }

    }
}
