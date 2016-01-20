package com.example.jabir_shabbir.androiddashplayer;

/**
 * Created by Jabir_shabbir on 02-10-2015.
 */
public class MeasureNetSpeed extends Thread
{
   public
    float netspeed=0;
    float avgSpeed=0;
    static int interval=0;
    MeasureNetSpeed()
   {

   }

    public void run()
    {
        float currentSpeed=0;
        //get current speed
        avgSpeed=avgSpeed*(interval/(interval+1))+currentSpeed/(interval+1);
        //update netspeed
    }

}
