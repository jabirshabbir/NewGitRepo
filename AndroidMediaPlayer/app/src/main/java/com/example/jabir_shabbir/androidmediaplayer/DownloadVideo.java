package com.example.jabir_shabbir.androidmediaplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;
/**
 * Created by Jabir_shabbir on 23-09-2015.
 */
public class DownloadVideo extends Thread /*AsyncTask<String, String, String>*/
{
     String url="";
      DownloadVideo(String url)
      {
          this.url=url;
      }
       int count=0;
   // protected String doInBackground(String... f_url)
    public void run()
    {
         count++;
         InputStream input = null;
         OutputStream output = null;
         HttpURLConnection connection = null;
         try
         {
             // URL url = new URL("http://www-itec.uni-klu.ac.at/ftp/datasets/mmsys13/video/redbull_4sec/100kbps/redbull_240p_100kbps_4sec_segment2.m4s");
           /*  URL url = new URL("http://download.netresec.com/pcap/maccdc-2012/maccdc2012_00002.pcap.gz");
            // http:download.netresec.com/pcap/maccdc-2012/maccdc2012_00002.pcap.gz
             Log.i(url.toString(),"hello");
             connection = (HttpURLConnection) url.openConnection();
             Log.i("connecting","connecting");
             connection.connect();
             Log.i("after connect", "after connect");
             // expect HTTP 200 OK, so we don't mistakenly save error report
             // instead of the file
             if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                 return "Server returned HTTP " + connection.getResponseCode()
                         + " " + connec tion.getResponseMessage();
             }

             // this will be useful to display download percentage
             // might be -1: server did not report the length
             int fileLength = connection.getContentLength();

             // download the file
             input = connection.getInputStream();
             output = new FileOutputStream("/sdcard/Download/Download1.m4s");

             byte data[] = new byte[4096];
             long total = 0;
             int count;
             while ((count = input.read(data)) != -1) {
                 // allow canceling with back button
                 if (isCancelled()) {
                     input.close();
                     return null;
                 }
                 total += count;
                 // publishing the progress....
              //   if (fileLength > 0) // only if total length is known
               //      publishProgress((int) (total * 100 / fileLength));
                 output.write(data, 0, count);
             }
             */
            // for (int i = 1; i < 10; i++)
             while(true)
             {

                 HttpClient httpClient = HttpClientBuilder.create().build();
                 //HttpResponse resp = httpClient.execute(new HttpGet("http://pilatus.d1.comp.nus.edu.sg/~team06/upl/Seg" + String.valueOf(i) + "/high" + "/output" + String.valueOf(1) + ".mp4"));
                 HttpResponse resp = httpClient.execute(new HttpGet("http://pilatus.d1.comp.nus.edu.sg/~team06" + "/"+"info.xml"));
                 /*URL u = new URL("http://download.netresec.com/pcap/maccdc-2012/maccdc2012_00002.pcap.gz");
             URLConnection conn = u.openConnection();
             Log.i("openingconn","openingopen");
             int contentLength = conn.getContentLength();
             Log.i("getting len","getting len");
             DataInputStream stream = new DataInputStream(u.openStream());
             Log.i("getting len1","getting len1");
             byte[] buffer = new byte[contentLength];
             stream.readFully(buffer);
             stream.close();*/
                 DataOutputStream fos = null;
                 if (resp.getEntity() != null)
                 {
                   /*  if (count == 1)
                         fos = new DataOutputStream(new FileOutputStream("/sdcard/Download/"));
                     else if (count == 2)
                     */
                     fos = new DataOutputStream(new FileOutputStream("/sdcard/Download/info.xml"));
                     resp.getEntity().writeTo(fos);
                     // fos.write(buffer);
                     fos.flush();
                     fos.close();
                 }
                 //Log.i("count is", String.valueOf(count));
                 /*while (true) {
                     if (count == 1)
                         Log.i("hello", "hello");
                     else if (count == 2)
                         Log.i("hi", "hi");
                         */
                 //}
             }
         }
         catch (Exception e)
         {
             Log.i("hello",e.toString());
            // return e.toString();
         }
         finally
         {
           /*  try {
               /*  if (output != null)
                     output.close();
                 if (input != null)
                     input.close();
             } catch (/*IOException ignored) {
             }*/

             //if (connection != null)
                 //connection.disconnect();
         }
         //return null;
     }
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
       // dismissDialog(progress_bar_type);

    }
    protected void onPreExecute() {
        //super.onPreExecute();
       // showDialog(progress_bar_type);
    }


}
