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
    float lastTime=0;
    MeasureNetSpeed()
    {

    }

    public void run()
    {
        if(System.currentTimeMillis()-lastTime>1000)
        {
            float currentSpeed = 100;
            //get current speed
            avgSpeed = avgSpeed * (interval / (interval + 1)) + currentSpeed / (interval + 1);
            //update netspeed
            lastTime=System.currentTimeMillis();
            interval++;
        }
    }

}
