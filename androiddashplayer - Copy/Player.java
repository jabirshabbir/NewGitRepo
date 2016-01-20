package com.example.jabir_shabbir.androiddashplayer;

/**
 * Created by Jabir_shabbir on 27-09-2015.
 */
public class Player extends Thread
{

  DownloadVideo video;
    int missNo=0;
    int previousSize;
    long playWindowStart=0;
    int noOfVideoDownloadedinCurrentWindow=0;
    int currentsegmentplaying;
    int lastsegmentPlayed;
    boolean playWindowSet=false;
    boolean buffernotfull=false;
    long benchmarksegment=-1;
    DownloadParseMPD parseMPD;
    MeasureNetSpeed NetSpeed;
    long segmentToplay=-1;
    PlayMedia media;

Player(DownloadVideo video)
{
  this.video=video;
}

    public void PlayVideo()
    {
        int currentSize=0;
        while(true)
        {
            currentSize=video.videoList.size();
             if(video.startplayingTimeSet) {
                 if (video.segmentbeforestart > 0 && playWindowSet == false) {
                     playWindowStart = video.startPlayingTime;
                     playWindowSet = true;
                 }
             }
            if(playWindowSet==true&&System.currentTimeMillis()>=playWindowStart+3*video.segmentDuration)
            {
               playWindowStart=playWindowStart+3*video.segmentDuration;
            }
                if(System.currentTimeMillis()>video.startPlayingTime)
               {
                  if(currentSize>0||(buffernotfull==true&&currentSize>=3))
                  {
                      //play video asynchronously
                      long segmentToplay=((System.currentTimeMillis()-video.startPlayingTime)-video.expectencyDelay)/video.segmentDuration;
                      if(segmentToplay>=4)
                      {
                          segmentToplay=(segmentToplay-4)+video.segmentbeforestart+1;
                          this.segmentToplay=segmentToplay;
                          //benchmarksegment=segmentToplay;
                          //video.benchmarksegment=(int)segmentToplay;
                          if(video.isDownloadSchedulerModifyingFailList==false&&video.downloadVideoModifyingFailList==false)
                          {
                              video.isPlayerModifyingFailList=true;
                              for (int k = 0; k < video.synchListrequestFail.size(); k++) {
                                  HTTPRequests request = video.synchListrequestFail.get(k);
                                  if (request.requestNo < (int)segmentToplay) {
                                      video.synchListrequestFail.remove(k);
                                      k--;
                                  }
                              }
                              video.isPlayerModifyingFailList=false;
                          }
                          if(buffernotfull==true&&currentSize>=3)
                          {
                             for (int k = 0; k < video.videoList.size(); k++)
                              {
                                  String path=video.videoList.get(k);
                                  String name=path.substring(path.lastIndexOf("/"));
                                  String no=name.substring(3,name.lastIndexOf("."));
                                  if(Integer.parseInt(no)<segmentToplay)
                                  {
                                     video.expectencyDelay=video.expectencyDelay+3000;
                                  }
                              }
                              benchmarksegment=segmentToplay;
                              video.benchmarksegment=(int)segmentToplay;
                              buffernotfull=false;
                          }
                          String path=video.videoList.get(0);
                          String name=path.substring(path.lastIndexOf("/"));
                          String no=name.substring(3,name.lastIndexOf("."));
                          int num=Integer.parseInt(no);
                          if(num<=segmentToplay)
                          {
                           //play it
                              while(media.isplaying==true)
                              {

                              }
                              lastsegmentPlayed=currentsegmentplaying;
                              boolean success=media.SetSourceAndPlay(path);
                              currentsegmentplaying=num;
                              video.videoList.remove(0);

                          }

                      }

                      else if(segmentToplay==3)
                      {
                          this.segmentToplay=segmentToplay;
                          benchmarksegment=video.segmentbeforestart;
                          video.benchmarksegment=video.segmentbeforestart;
                          if(buffernotfull==true&&currentSize>=3)
                          {

                              for (int k = 0; k < video.videoList.size(); k++)
                              {
                                  String path=video.videoList.get(k);
                                  String name=path.substring(path.lastIndexOf("/"));
                                  String no=name.substring(3,name.lastIndexOf("."));
                                  if(Integer.parseInt(no)<video.segmentbeforestart)
                                  {
                                      video.expectencyDelay=video.expectencyDelay+3000;
                                  }
                              }
                              buffernotfull=false;
                          }
                          String path=video.videoList.get(0);
                          String name=path.substring(path.lastIndexOf("/"));
                          String no=name.substring(3,name.lastIndexOf("."));
                          if(Integer.parseInt(no)<video.segmentbeforestart)
                          {
                              //play it
                              while(media.isplaying==true)
                              {

                              }

                              boolean success=media.SetSourceAndPlay(path);

                          }
                          video.videoList.remove(0);
                      }

                      else
                      {
                          String path=video.videoList.remove(0);
                      }

                  }
                  else if(currentSize==0)
                  {
                      video.buffernotfull=true;
                      buffernotfull=true;
                      //reset the window
                     /* video.videoStarted=false;
                      //finding number of pending requests
                      int totalfailPending=video.synchListrequestPending.size()+video.synchListrequestFail.size();
                      //next request
                      video.stopreg=true;
                      long resumereq=totalfailPending*video.segmentDuration;
                      */
                  }

               }

                /*for (int i=0;i<video.synchListrequestFail.size();i++)
                {
                    HTTPRequests req=(HTTPRequests)video.synchListrequestFail.get(i);
                    int reqNo=req.requestNo;
                    long firstSentTime=req.requestFirstSentTime;
                    long lastSentTime=req.requestSentTime;
                    long expectedTimeTodownloadInitially= req.expiryTime+video.expectencyDelay;;
                    parseMPD.segmentRequired=reqNo;
                    int high=-1,low=-1,mid=-1;
                    boolean segPresentInXML=false;
                        if (parseMPD.ParseMPD(reqNo)) {
                            high = parseMPD.segmentSizeHigh;
                            low = parseMPD.segmentSizeLow;
                            mid = parseMPD.segmentSizeMid;
                            segPresentInXML = true;
                        }

                    else
                    {
                       parseMPD.execute();
                       if(parseMPD.ParseMPD(reqNo))
                       {
                         segPresentInXML=true;
                       }
                    }

                    //expectedTimeTodownloadInitially=firstSentTime+video.firstsegmentTimetoDownload;
                    //expectedTimeTodownloadInitially=(reqNo-video.segmentbeforestart)*3000+video.initialBufferingTime+video.segmentbeforestart+video.expectencyDelay;
                    //long timeafterstartPlaying=expectedTimeTodownloadInitially-video.initialBufferingTime;
                    //long expectedTimestartPlaying=(reqNo-1)*video.segmentDuration+video.initialBufferingTime;
                    long expectedTimestartPlaying=expectedTimeTodownloadInitially+6000;
                    if(segPresentInXML)
                    {
                    if(video.startplayingTimeSet==true)
                    {
                    if(expectedTimeTodownloadInitially<=playWindowStart+(3*video.segmentDuration)&&expectedTimeTodownloadInitially>System.currentTimeMillis())
                    {
                        long newDownloadTime=(System.currentTimeMillis()+(200-(System.currentTimeMillis()-lastSentTime)+(long)(high/NetSpeed.avgSpeed)));
                        if(newDownloadTime<playWindowStart+(3*video.segmentDuration))
                        {
                            req.quality="High";
                            video.synchListrequestFail.remove(0);
                            video.synchListrequestFail.add(req);
                        }
                        else
                        {
                            long newDownloadTimemed=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                            if(newDownloadTimemed<playWindowStart+(3*video.segmentDuration))
                            {
                               req.quality="Medium";
                                video.synchListrequestFail.remove(0);
                                video.synchListrequestFail.add(req);
                            }
                            else
                            {
                                newDownloadTimemed=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                                if(newDownloadTimemed<playWindowStart+(3*video.segmentDuration))
                                {
                                    req.quality="Low";
                                    video.synchListrequestFail.remove(0);
                                    video.synchListrequestFail.add(req);
                                }
                            }

                            //likewise check for lower qualities
                        }

                    }

                    else if(expectedTimestartPlaying<=playWindowStart+(3*video.segmentDuration)&&expectedTimestartPlaying>System.currentTimeMillis())
                    {

                        long newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(high/NetSpeed.avgSpeed)));
                        if(newDownloadTime<expectedTimestartPlaying)
                        {
                           //set the request quality
                            req.quality="High";
                            video.synchListrequestFail.remove(0);
                            video.synchListrequestFail.add(req);
                        }
                        //likewise check for lower qualities
                        else
                        {
                            newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                            if(newDownloadTime<expectedTimestartPlaying)
                            {
                               req.quality="Medium";
                                video.synchListrequestFail.remove(0);
                                video.synchListrequestFail.add(req);
                            }
                            else
                            {
                                newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(low/NetSpeed.avgSpeed)));
                                if(newDownloadTime<expectedTimestartPlaying)
                                {
                                   req.quality="Low";
                                    video.synchListrequestFail.remove(0);
                                    video.synchListrequestFail.add(req);
                                }

                            }

                            //video.synchListrequestFail.remove(i);
                            //i--;

                        }



                    }

                    else if(expectedTimeTodownloadInitially>playWindowStart+(3*video.segmentDuration))
                    {
                        long playWindowNo=(expectedTimestartPlaying-video.startPlayingTime+video.expectencyDelay)/(3*video.segmentDuration);
                        long newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+video.averageDownloadTimeHigh));
                        long endWindow=playWindowNo*3*video.segmentDuration+video.startPlayingTime+3*video.segmentDuration;
                        //if(newDownloadTime<(playWindowNo)*video.segmentDuration*3&&newDownloadTime<(playWindowNo)*video.segmentDuration*3+video.segmentDuration*3)
                        if(newDownloadTime<endWindow)
                        {
                            //set the request quality
                            req.quality="High";
                            video.synchListrequestFail.remove(0);
                            video.synchListrequestFail.add(req);
                        }
                        //likewise check for lower qualities
                        else
                        {
                            newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(mid/NetSpeed.avgSpeed)));
                            if(newDownloadTime<endWindow)
                            {
                                req.quality="Medium";
                                video.synchListrequestFail.remove(0);
                                video.synchListrequestFail.add(req);
                            }
                            else
                            {
                                newDownloadTime=(System.currentTimeMillis()+(1000-(System.currentTimeMillis()-lastSentTime)+(long)(low/NetSpeed.avgSpeed)));
                                if(newDownloadTime<endWindow)
                                {
                                    req.quality="Low";
                                    video.synchListrequestFail.remove(0);
                                    video.synchListrequestFail.add(req);

                                }

                            }

                        }

                    }
                    else if(System.currentTimeMillis()>expectedTimestartPlaying+video.expectencyDelay&&!buffernotfull)
                    {
                        //discard the request
                        video.synchListrequestFail.remove(i);
                        i--;
                    }

                }
                }
                }*/

        }
    }
}
