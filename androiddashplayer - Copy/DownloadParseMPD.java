package com.example.jabir_shabbir.androiddashplayer;
import android.os.AsyncTask;

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
    String basefilePath="";
    String basePath="";
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


    public String DowndoadMPD(HTTPRequests request,boolean failed)
    {
        boolean notSuccess=false;
        try
            {

                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpResponse resp = httpClient.execute(new HttpGet(url));
                DataOutputStream fos = null;

                if (resp.getEntity() != null)
                {
                    FileOutputStream out = new FileOutputStream(basePath + "/"+"MPD.xml");
                    resp.getEntity().writeTo(fos);
                    if(failed)
                    {
                         if(video.startPlayingTime>0)
                         {
                             long firstSentTime=request.requestFirstSentTime;
                             long lastSentTime=request.requestSentTime;
                             long expectedTimeTodownloadInitially= request.expiryTime+video.expectencyDelay;;
                             boolean segPresentInXML=false;
                             if (ParseMPD(request.requestNo))
                             {

                                 segPresentInXML = true;
                                 long expectedTimestartPlaying=expectedTimeTodownloadInitially+6000;
                                 if(segPresentInXML)
                                 {
                                     notSuccess=false;
                                     if(video.startplayingTimeSet==true)
                                     {
                                         if(expectedTimeTodownloadInitially<=p.playWindowStart+(3*video.segmentDuration))
                                         {
                                             long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeHigh/NetSpeed.avgSpeed)));
                                             if(newDownloadTime<p.playWindowStart+(3*video.segmentDuration))
                                             {
                                                 request.quality="High";
                                                 video.synchListrequestFail.remove(0);
                                                 video.synchListrequestFail.add(request);
                                             }
                                             else
                                             {
                                                 long newDownloadTimemed=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeMid/NetSpeed.avgSpeed)));
                                                 if(newDownloadTimemed<p.playWindowStart+(3*video.segmentDuration))
                                                 {
                                                     request.quality="Medium";
                                                     video.synchListrequestFail.remove(0);
                                                     video.synchListrequestFail.add(request);
                                                 }
                                                 else
                                                 {
                                                    // newDownloadTimemed=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeLow/NetSpeed.avgSpeed)));
                                                    // if(newDownloadTimemed<p.playWindowStart+(3*video.segmentDuration))
                                                     //{
                                                         request.quality="Low";
                                                         video.synchListrequestFail.remove(0);
                                                         video.synchListrequestFail.add(request);
                                                    // }
                                                 }

                                                 //likewise check for lower qualities
                                             }

                                         }

                                         else if(expectedTimestartPlaying<=p.playWindowStart+(3*video.segmentDuration)&&expectedTimestartPlaying>System.currentTimeMillis())
                                         {

                                             long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeHigh/NetSpeed.avgSpeed)));
                                             if(newDownloadTime<expectedTimestartPlaying)
                                             {
                                                 //set the request quality
                                                 request.quality="High";
                                                 video.synchListrequestFail.remove(0);
                                                 video.synchListrequestFail.add(request);
                                             }
                                             //likewise check for lower qualities
                                             else
                                             {
                                                 newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeMid/NetSpeed.avgSpeed)));
                                                 if(newDownloadTime<expectedTimestartPlaying)
                                                 {
                                                     request.quality="Medium";
                                                     video.synchListrequestFail.remove(0);
                                                     video.synchListrequestFail.add(request);
                                                 }
                                                 else
                                                 {
                                                    // newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeLow/NetSpeed.avgSpeed)));
                                                    // if(newDownloadTime<expectedTimestartPlaying)
                                                    // {
                                                         request.quality="Low";
                                                         video.synchListrequestFail.remove(0);
                                                         video.synchListrequestFail.add(request);
                                                    // }

                                                 }

                                             }



                                         }

                                         else if(expectedTimeTodownloadInitially>p.playWindowStart+(3*video.segmentDuration))
                                         {
                                             long playWindowNo=(expectedTimestartPlaying-video.startPlayingTime+video.expectencyDelay)/(3*video.segmentDuration);
                                             long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeHigh/NetSpeed.avgSpeed)));
                                             long endWindow=playWindowNo*3*video.segmentDuration+video.startPlayingTime+3*video.segmentDuration;
                                             //if(newDownloadTime<(playWindowNo)*video.segmentDuration*3&&newDownloadTime<(playWindowNo)*video.segmentDuration*3+video.segmentDuration*3)
                                             if(newDownloadTime<endWindow)
                                             {
                                                 //set the request quality
                                                 request.quality="High";
                                                 video.synchListrequestFail.remove(0);
                                                 video.synchListrequestPending.add(request);
                                             }
                                             //likewise check for lower qualities
                                             else
                                             {
                                                 newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeMid/NetSpeed.avgSpeed)));
                                                 if(newDownloadTime<endWindow)
                                                 {
                                                     request.quality="Medium";
                                                     video.synchListrequestFail.remove(0);
                                                     video.synchListrequestPending.add(request);
                                                 }
                                                 else
                                                 {
                                                     newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(segmentSizeLow/NetSpeed.avgSpeed)));
                                                     if(newDownloadTime<endWindow)
                                                     {
                                                         request.quality="Low";
                                                         video.synchListrequestFail.remove(0);
                                                         video.synchListrequestFail.add(request);

                                                     }

                                                 }

                                             }

                                         }
                                         else if(System.currentTimeMillis()>expectedTimestartPlaying+video.expectencyDelay&&!video.buffernotfull)
                                         {
                                             //discard the request
                                             video.synchListrequestFail.remove(i);
                                             //i--;
                                         }

                                     }
                                 }
                                 else
                                     notSuccess=true;
                             }
                             else
                                 notSuccess=true;
                         }
                        else
                         {
                              if(ParseMPD(request.requestNo))
                              {
                                  request.requestFirstSentTime=System.currentTimeMillis();
                                  request.requestSentTime=System.currentTimeMillis();
                                  video.synchListrequestPending.add(request);
                                  video.VideoDownload(,request.requestNo);
                                  notSuccess=false;
                              }
                             else
                                  notSuccess=true;

                         }
                    }
                    else
                    {
                      if(ParseMPD(request.requestNo))
                        {
                            notSuccess=false;
                            request.requestFirstSentTime=System.currentTimeMillis();
                            request.requestSentTime=System.currentTimeMillis();
                            video.synchListrequestPending.add(request);
                            String urlReq="";
                            if(video.startPlayingTime==0)
                                urlReq=basePath+"/"+highFolder+"/"+"Seg"+request.requestNo+".mp4";
                            else
                                urlReq=AdaptationControl(request);
                            video.VideoDownload(urlReq, request.requestNo);

                        }
                        else
                          notSuccess=true;

                    }

                }
                else
                 notSuccess=true;
               //return  basePath + "/"+"MPD.xml";
            }


        catch(Exception ex)
        {
          notSuccess=true;
        }
        if(notSuccess&&failed==false)
        {
            request.requestFailType=0;
            video.synchListrequestFail.add(request);
        }

        return null;
    }

 boolean  ParseMPD(int segmentNo)
    {
        try
        {
            File stocks = new File("C://Users//Jabir_shabbir//AndroidStudioProjects//AndroidDashPlayer//MPD1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stocks);
            doc.getDocumentElement().normalize();
            NodeList segmentsNode1 = doc.getElementsByTagName("Segment");
            for(int k=0;k<segmentsNode1.getLength();k++)
            {
                Node s=segmentsNode1.item(k);
                String segmentName=s.getAttributes().item(0).getTextContent();
                int index=segmentName.indexOf("seg")+3;
                int no=Integer.parseInt(segmentName.substring(index));
                if(no==segmentNo)
                {
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
            File stocks = new File("C://Users//Jabir_shabbir//AndroidStudioProjects//AndroidDashPlayer//MPD1.xml");
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

    String AdaptationControl(HTTPRequests request)
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
                    String url=basefilePath+"/"+midFolder+"/"+String.valueOf(request.requestNo)+".mp4"
                    video.synchListrequestPending.add(request);
                    return url;
                } else {

                    request.quality = "Low";
                    String url=basefilePath+"/"+lowFolder+"/"+String.valueOf(request.requestNo)+".mp4"
                    video.synchListrequestPending.add(request);
                    return url;
                }

                //likewise check for lower qualities


        }


    }
}
