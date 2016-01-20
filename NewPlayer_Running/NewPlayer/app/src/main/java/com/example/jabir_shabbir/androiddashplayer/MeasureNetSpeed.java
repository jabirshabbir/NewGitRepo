package com.example.jabir_shabbir.androiddashplayer;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.Random;

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
    Context ct;
    MeasureNetSpeed(Context ct)    {
    this.ct=ct;
    }

    public void run()
    {
        while(true)
        {
            WifiManager wifiManager = (WifiManager) ct.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null)
                netspeed = wifiInfo.getLinkSpeed(); //Mbps
                netspeed = new Random().nextInt(100);
            try {
                Thread.sleep(5000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //lastTime=System.currentTimeMillis();
        }
    }

}
