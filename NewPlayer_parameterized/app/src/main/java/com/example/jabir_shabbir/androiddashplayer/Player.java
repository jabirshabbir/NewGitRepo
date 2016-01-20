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
    boolean buffernotfull=true;
    long benchmarksegment=-1;
    DownloadParseMPD parseMPD;
    MeasureNetSpeed NetSpeed;
    long segmentToplay=-1;
    PlayMedia media;
    long printTime;
    long segmentToplayStartPlayingTime=0;

    Player(DownloadVideo video)
    {
        this.video=video;
        media=new PlayMedia();
    }

    public void run() {
        int currentSize = 0;
        while (true)
        {
            currentSize=video.videoList.size();
            //  Log.i("list size player",""+currentSize);
          /*   if(video.startplayingTimeSet)
             {
                 if (video.segmentbeforestart > 0 && playWindowSet == false)
                 {
                     playWindowStart = video.startPlayingTime;
                     playWindowSet = true;
                     System.out.println("Player ready picking");
                 }
             }*/

            /*if(playWindowSet==true&&System.currentTimeMillis()>=playWindowStart+3*video.segmentDuration)
            {
               playWindowStart=playWindowStart+3*video.segmentDuration;
            }*/

              /*  if(System.currentTimeMillis()>video.startPlayingTime)
               {
                  */

            if (currentSize >= 1 || (buffernotfull == false && currentSize > 0))
            {
                System.out.print("Player list size" + " " + video.videoList.size());
                //play video asynchronously
                // System.out.println("difference"+String.valueOf(System.currentTimeMillis()-printTime));
                /*      if(printTime==0||System.currentTimeMillis()-printTime>1000)
                      {
                          System.out.println("video list size" + video.videoList.size());
                          for (int d = 0; d < video.videoList.size(); d++) {
                              System.out.println("video list " + video.videoList.get(d));
                          }
                          printTime=System.currentTimeMillis();
                      }
                      long segmentToplay=((System.currentTimeMillis()-video.startPlayingTime)-video.expectencyDelay)/video.segmentDuration;

                      this.segmentToplayStartPlayingTime=System.currentTimeMillis();
                      Log.i("Segment  parameters","Segment  parameters");
                      Log.i(String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()));
                      Log.i(String.valueOf(video.startPlayingTime),String.valueOf(video.startPlayingTime));
                      Log.i(String.valueOf(video.expectencyDelay),String.valueOf(video.expectencyDelay));
                      Log.i(String.valueOf(video.segmentDuration),String.valueOf(video.segmentDuration));
                       */
                      /*if(segmentToplay>=4)
                      {
                          segmentToplay=(segmentToplay-4)+video.segmentbeforestart+1;
                          this.segmentToplay=segmentToplay;
                          video.segmentToPlay=segmentToplay;
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
                              //video.segmentToPlay=(int)segmentToplay;
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
                                   Log.i("Segment still playing","segment still playing");
                              }
                              lastsegmentPlayed=currentsegmentplaying;
                              boolean success=media.SetSourceAndPlay(path);
                              currentsegmentplaying=num;
                              video.videoList.remove(0);

                          }

                      }*/

                      /*else if(segmentToplay==3)
                      {
                          this.segmentToplay=video.segmentbeforestart;
                          video.segmentToPlay=video.segmentbeforestart;
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

                      }*/

                // else
                {
                    while(video.isDownloaderModifyingList)
                    {

                    }
                    video.isPlayerExtractingFromList=true;
                    String path = video.videoList.get(0);
                    //System.out.println("seg b4 start"+video.segmentbeforestart);
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    String no = name.substring(3, name.lastIndexOf("."));
                    if (true/*Integer.parseInt(no)<video.segmentbeforestart*/)
                    {
                        //play it
                        Log.i("gonna play", "gonna play"+video.videoList.get(0));
                        while (media.isPlaying == true)
                        {
                            //Log.i("still playing", "still playing");
                        }

                        boolean success = media.SetSourceAndPlay(path);
                        /*while(video.isDownloaderModifyingList)
                        {

                        }*/
                        Log.i("sunny_video.videoList!!",""+video.videoList);
                        video.videoList.remove(0);
                        Log.i("sunny_video.videoList", "" + video.videoList);

                        video.isPlayerExtractingFromList=false;
                    }

                }

            }

                  /*else(currentSize==0)
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

                  }*/

        }

    }

}
