package com.example.jabir_shabbir.androiddashplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    Button live;
    String[] values;
    private static final String ns = null; //parser needs this for some reason
    String vod_url = "http://pilatus.d1.comp.nus.edu.sg/~team06/vod_xml/playlist.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ApplicationState appState = (ApplicationState)getApplicationContext();
        appState.initVideo();

        GetPlaylistAsync getPlaylist = new GetPlaylistAsync(vod_url, getApplicationContext());
        getPlaylist.execute(); //HAVE to do this in async task as otherwise you get networkOnMainThreadException

        live = (Button) findViewById(R.id.live);
        live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.this, "Starting Live Streaming", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), LivePlayerMain.class);
                i.putExtra("mpd_url", "http://pilatus.d1.comp.nus.edu.sg/~team06/infopk.xml");
                startActivity(i);
            }
        });

        while (appState.getItemsArray() == null)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String[] display = new String[appState.getItemsArray().length]; int i = 0;
        for(String v : appState.getItemsArray()) {
            File f = new File(v);
            display[i++] = new String(f.getName().substring(0, f.getName().lastIndexOf(".")));
        }
        appState.setItemsArray(display);
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        values = appState.getItemsArray();

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Playing " + itemValue, Toast.LENGTH_LONG)
                        .show();

                Intent i = new Intent(getApplicationContext(), LivePlayerMain.class);
                i.putExtra("mpd_url", "http://pilatus.d1.comp.nus.edu.sg/~team06/vod_xml/"+itemValue+".xml");
                startActivity(i);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
