package com.example.jabir_shabbir.androiddashplayer;
import android.os.AsyncTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

/**
 * Created by Jabir_shabbir on 07-10-2015.
 */

public class DownloadParseMPD extends AsyncTask<String ,String, String>
{
    public
    String URL;
    String basefilePath="";
    String basePath="";
    String lowFolder="";
    String midFolder="";
    String highFolder="";
    int segmentRequired=-1;
    //String qualityRequired="";
    int segmentSizeLow=-1;
    int segmentSizeMid=-1;
    int segmentSizeHigh=-1;
    DownloadParseMPD()
    {

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    protected void onPostExecute(String s)
    {

    }


    protected String doInBackground(String... params)
    {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();
            String filename = "";
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                Header h[] = response.getAllHeaders();
                for (Header header : h)
                {
                    if (header.getName().equals("filename"))
                        filename = header.getValue();
                }

                /* InputStream is= u.openStream();
                DataInputStream dStream=new DataInputStream(is);
                byte[] buffer=new byte[1024];*/
                FileOutputStream out = new FileOutputStream(basePath + "/"  + filename);
                /*int length=0;
                while((length=dStream.read(buffer))>0)
                {
                    out.write(buffer,0,length);
                }
                out.close();*/
               return  basePath + "/"  + filename;
            }

        }
        catch(Exception ex)
        {

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



}
