package com.example.jabir_shabbir.androiddashplayer;
import android.os.AsyncTask;
import android.util.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;

/**
 * Created by Jabir_shabbir on 07-10-2015.
 */

public class DownloadParseMPD /*extends AsyncTask<String ,String, String>*/
{
    public
    String url;
    String basefilePath="/sdcard/Download";
    String basePath="http://pilatus.d1.comp.nus.edu.sg/~team06/";
    String lowFolder="";
    String midFolder="";
    String highFolder="";
    int segmentRequired=-1;
    MeasureNetSpeed NetSpeed;
    Player p;
    //String qualityRequired="";
    int segmentSizeLow=-1;
    int segmentSizeMid=-1;
    int segmentSizeHigh=-1;
    DownloadVideo video;
    DownloadScheduler scheduler;
    DownloadParseMPD()
    {

    }


    protected void onPreExecute()
    {
       // super.onPreExecute();
    }

    protected void onPostExecute(String s)
    {

    }

    boolean DownloadMPD()
    {
        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();
            System.out.println(basePath+"/"+"info.xml");
            HttpResponse resp = httpClient.execute(new HttpGet("http://pilatus.d1.comp.nus.edu.sg/~team06" + "/" + "info.xml"));
            DataOutputStream fos = null;
            Log.i("basepath is",""+basePath);
            if (resp.getEntity() != null)
            {
                Log.i("response not null","response not null");
                DataOutputStream out = new DataOutputStream(new FileOutputStream(basefilePath + "/" + "MPD1.xml"));
                resp.getEntity().writeTo(out);
                this.ParseInitialParameters();
                return true;
            }
        }
        catch(Exception ex)
        {
          Log.i("excep in mpd download","exception in mpd download");
        }
        return false;
    }

    public String DowndoadMPD(HTTPRequests request,boolean failed)
    {
        boolean notSuccess=false;
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse resp = httpClient.execute(new HttpGet(basePath+"/"+"info.xml"));
            DataOutputStream fos = null;

            if (resp.getEntity() != null)
            {
                DataOutputStream out = new DataOutputStream(new FileOutputStream(basefilePath + "/" + "info.xml"));
                resp.getEntity().writeTo(out);
                if (failed)
                {
                    if (video.startPlayingTime > 0)
                    {
                        long firstSentTime = request.requestFirstSentTime;
                        long lastSentTime = request.requestSentTime;
                        long expectedTimeTodownloadInitially = request.expiryTime + video.expectencyDelay;
                        ;
                        boolean segPresentInXML = false;
                        if (ParseMPD(request.requestNo))
                        {

                            segPresentInXML = true;
                            long expectedTimestartPlaying = expectedTimeTodownloadInitially + 6000;
                            notSuccess = false;
                            String url = scheduler.AdaptationControlOfFail(request,segmentSizeLow,segmentSizeMid,segmentSizeHigh);
                            if (!url.equals("") && url != null)
                            {
                               synchronized(this)
                               {
                                   video.isParseMpdModifyingPendingList=true;
                                   video.synchListrequestPending.add(request);
                                   video.isParseMpdModifyingPendingList=false;
                               }
                                video.VideoDownload(url, request.requestNo);
                            }
                        }
                    }
                    else
                    {
                        if (ParseMPD(request.requestNo))
                        {
                            request.requestFirstSentTime = System.currentTimeMillis();
                            request.requestSentTime = System.currentTimeMillis();
                            synchronized (this)
                            {
                                while(video.isDownloadSchdedulerModifyingPendingList||video.isModifyingPendingList)
                                {
                                    //do nothing
                                }
                                video.isParseMpdModifyingPendingList=true;
                                video.synchListrequestPending.add(request);
                                video.isParseMpdModifyingPendingList=false;
                            }
                            String urlReq = basePath + "/" + highFolder + "/" + "Seg" + request.requestNo + ".mp4";
                            video.VideoDownload(urlReq, request.requestNo);
                            notSuccess = false;
                        } else
                            notSuccess = true;

                    }
                }
                else
                {
                            if (ParseMPD(request.requestNo)) {
                                notSuccess = false;
                                request.requestFirstSentTime = System.currentTimeMillis();
                                request.requestSentTime = System.currentTimeMillis();
                                synchronized (this)
                                {
                                    while(video.isDownloadSchdedulerModifyingPendingList||video.isModifyingPendingList)
                                    {
                                        //do nothing
                                    }
                                        video.isParseMpdModifyingPendingList=true;
                                        video.synchListrequestPending.add(request);
                                        video.isParseMpdModifyingPendingList=false;
                                }
                                String urlReq = "";
                                if (video.startPlayingTime == 0)
                                    urlReq = basePath + "/" + highFolder + "/" + "Seg" + request.requestNo + ".mp4";
                                else
                                    urlReq = AdaptationControl(request,segmentSizeHigh,segmentSizeMid,segmentSizeLow);
                                video.VideoDownload(urlReq, request.requestNo);

                            } else
                                notSuccess = true;

                        }

                    }

                     //else
                     //   notSuccess = true;
        }

        catch(Exception ex)
        {
          notSuccess=true;
        }
        if(notSuccess&&failed==false)
        {
            request.requestFailType=0;
            synchronized (this)
            {
                while(video.isDownloadSchedulerModifyingFailList||video.downloadVideoModifyingFailList)
                {
                    //do nothing
                }
                video.isParseMpdModifyingFailList=true;
                video.synchListrequestFail.add(request);
                video.isParseMpdModifyingFailList=false;
            }
        }

        return null;
    }

 boolean  ParseMPD(int segmentNo)
    {
        try
        {
            File stocks = new File("/sdcard/Download/MPD1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stocks);
            doc.getDocumentElement().normalize();
            NodeList segmentsNode1 = doc.getElementsByTagName("Segment");
            for(int k=0;k<segmentsNode1.getLength();k++)
            {
                Node s=segmentsNode1.item(k);
                String segmentName=s.getAttributes().item(0).getTextContent();
                int index=segmentName.indexOf("Seg")+3;//getting the index of segment number in string
                int no=Integer.parseInt(segmentName.substring(index));
                if(no==segmentNo)
                {

                    NodeList segmentsNode = s.getChildNodes();
                    for (int i = 0; i <segmentsNode.getLength(); i++)
                    {
                        Node seg = segmentsNode.item(i);
                        NodeList qualities = seg.getChildNodes();
                        for (int j = 0; j < qualities.getLength(); j++)
                        {
                            Node x = qualities.item(j);

                            if (x.getNodeName().equals("High"))
                            {
                                segmentSizeHigh=Integer.parseInt(x.getTextContent());
                            }   else if (x.getNodeName().equals("Low"))
                            {

                                segmentSizeLow=Integer.parseInt(x.getTextContent());
                            } else if (x.getNodeName().equals("Medium"))
                            {

                                segmentSizeMid=Integer.parseInt(x.getTextContent());
                            }
                        }
                    }
                    return true;
                }
            }
        }

        catch (Exception ex)
        {

        }
return false;

    }

    void ParseInitialParameters()
    {
        try {
            File stocks = new File("/sdcard/Download/MPD1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stocks);
            doc.getDocumentElement().normalize();
            NodeList baseUrlNode = doc.getElementsByTagName("Template");
            NodeList segmentsNode1 = doc.getElementsByTagName("Segment");
            Node bNode = baseUrlNode.item(0);
            System.out.println(bNode.getNodeName());
            NodeList l = bNode.getChildNodes();
            for (int i = 0; i < l.getLength(); i++) {
                Node x = l.item(i);
                if (x.getNodeName().equals("BasePath")) {
                    basePath = x.getTextContent();
                    System.out.println(basePath);
                } else if (x.getNodeName().equals("QualitiesFolder")) {
                    NodeList qList = x.getChildNodes();
                    for (int j = 0; j < qList.getLength(); j++) {
                        Node q = qList.item(j);
                        if (q.getNodeName().equals("Low")) {
                            lowFolder = q.getTextContent();
                        } else if (q.getNodeName().equals("Medium")) {
                            midFolder = q.getTextContent();
                        } else if (q.getNodeName().equals("High")) {
                            highFolder = q.getTextContent();
                        }
                    }
                }
            }
        }

        catch (Exception ex)
        {

        }

    }

    String AdaptationControl(HTTPRequests request,int low,int mid,int high)
    {
        long expectedTimeTodownloadInitially = System.currentTimeMillis() + 3 * video.segmentDuration;
            long newDownloadTime = System.currentTimeMillis() + (long) (segmentSizeHigh / NetSpeed.avgSpeed);
            if (newDownloadTime < p.playWindowStart + (3 * video.segmentDuration)) {
                request.quality = "High";
                video.synchListrequestPending.add(request);
                String url=basefilePath+"/"+highFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                return url;
            } else {
                long newDownloadTimemed = System.currentTimeMillis() + (long) (segmentSizeMid / NetSpeed.avgSpeed);
                if (newDownloadTimemed < p.playWindowStart + (3 * video.segmentDuration)) {
                    request.quality = "Medium";
                    String url=basefilePath+"/"+midFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                    video.synchListrequestPending.add(request);
                    return url;
                } else
                {

                    request.quality = "Low";
                    String url=basefilePath+"/"+lowFolder+"/"+String.valueOf(request.requestNo)+".mp4";
                    video.synchListrequestPending.add(request);
                    return url;
                }

                //likewise check for lower qualities


        }


    }

    int GetInitialSegment()
    {
        try
        {
            File stocks = new File("/sdcard/Download/MPD1.xml");
            Log.i("ininitialseg","ininitialseg");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stocks);
            doc.getDocumentElement().normalize();
            NodeList segmentsNode1 = doc.getElementsByTagName("Segment");
            for(int k=segmentsNode1.getLength()-1;k>=0;k--)
            {
                Log.i("loopinginitialseg","loopinginitialseg");
                Node s=segmentsNode1.item(k);
                String segmentName=s.getAttributes().item(0).getTextContent();
                Log.i("SegmentName",segmentName);
                int index=segmentName.indexOf("Seg")+3;
                int no=Integer.parseInt(segmentName.substring(index,index+1));
                Log.i("iniSegNois",String.valueOf(no));
                if(true/*k==segmentsNode1.getLength()-5*/)
                {
                    Log.i("foundinitialsegment", "foundinitialsegment");
                    NodeList segmentsNode = s.getChildNodes();
                    for (int i = 0; i < segmentsNode.getLength(); i++)
                    {
                        Node seg = segmentsNode.item(i);
                        NodeList qualities = seg.getChildNodes();
                        for (int j = 0; j < qualities.getLength(); j++)
                        {
                            Node x = qualities.item(j);
                       /*     if(x.getNodeName().equals(qualityRequired))
                            {
                                segmentSize=Integer.parseInt(x.getTextContent());
                            }*/
                            if (x.getNodeName().equals("High"))
                            {
                                segmentSizeHigh=Integer.parseInt(x.getTextContent());
                            } else if (x.getNodeName().equals("Low"))
                            {

                                segmentSizeLow=Integer.parseInt(x.getTextContent());
                            } else if (x.getNodeName().equals("Medium"))
                            {

                                segmentSizeMid=Integer.parseInt(x.getTextContent());
                            }
                        }
                      //  return no;
                    }
                    return no;
                }
            }
        }

        catch (Exception ex)
        {

        }
        return -1;

    }
}
