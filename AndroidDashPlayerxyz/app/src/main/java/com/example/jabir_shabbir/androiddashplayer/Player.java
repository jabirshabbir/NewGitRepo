package com.example.jabir_shabbir.androiddashplayer;

import android.util.Log;

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
    long printTime;

Player(DownloadVideo video)
{
    this.video=video;
    media=new PlayMedia();
}

    public void run()
    {
        int currentSize=0;
        while(true)
        {
            currentSize=video.videoList.size();
             if(video.startplayingTimeSet)
             {
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
                  if(currentSize>=3||(buffernotfull==false&&currentSize>0))
                  {
                      //play video asynchronously
                     // System.out.println("difference"+String.valueOf(System.currentTimeMillis()-printTime));
                      if(printTime==0||System.currentTimeMillis()-printTime>1000) {
                          System.out.println("video list size" + video.videoList.size());
                          for (int d = 0; d < video.videoList.size(); d++) {
                              System.out.println("video list " + video.videoList.get(d));
                          }
                          printTime=System.currentTimeMillis();
                      }
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
                                  String name=path.substring(path.lastIndexOf("/")+1);
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
                          String name=path.substring(path.lastIndexOf("/")+1);
                          String no=name.substring(3,name.lastIndexOf("."));
                          System.out.println("seg to play"+segmentToplay);
                          System.out.println("path is"+ path);
                          int num=Integer.parseInt(no);
                          if(num<=segmentToplay)
                          {
                           //play it
                              while(media.isPlaying==true)
                              {
                                 Log.i("still playing","still playing");
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
                                  String name=path.substring(path.lastIndexOf("/")+1);
                                  String no=name.substring(3,name.lastIndexOf("."));
                                  if(Integer.parseInt(no)<video.segmentbeforestart)
                                  {
                                      video.expectencyDelay=video.expectencyDelay+3000;
                                  }
                              }
                              buffernotfull=false;
                          }
                          String path=video.videoList.get(0);
                          String name=path.substring(path.lastIndexOf("/")+1);
                          String no=name.substring(3,name.lastIndexOf("."));
                          System.out.println("seg to play"+segmentToplay);
                          System.out.println("path is"+ path);
                          if(Integer.parseInt(no)<=video.segmentbeforestart)
                          {
                              //play it

                              while(media.isPlaying==true)
                              {
                                Log.i("still playing", "still playing");
                              }

                              boolean success=media.SetSourceAndPlay(path);
                              video.videoList.remove(0);
                          }

                      }

                      else
                      {
                          String path=video.videoList.get(0);

                          //System.out.println("seg b4 start"+video.segmentbeforestart);
                          String name=path.substring(path.lastIndexOf("/")+1);
                          String no=name.substring(3,name.lastIndexOf("."));
                          if(Integer.parseInt(no)<video.segmentbeforestart)
                          {
                              //play it
                              Log.i("gonna play","gonna play");
                              while(media.isPlaying==true)
                              {
                                  Log.i("still playing", "still playing");
                              }

                              boolean success=media.SetSourceAndPlay(path);
                              video.videoList.remove(0);
                          }

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

        }
    }
}