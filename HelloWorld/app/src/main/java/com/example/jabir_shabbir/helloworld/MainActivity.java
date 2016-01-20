package com.example.jabir_shabbir.helloworld;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv=(TextView) findViewById(R.id.first_program);
       // RelativeLayout newLayout = new RelativeLayout(getApplicationContext());
        //tv.setText("JustTrial");
        //newLayout.addView(tv);
        try {
            File current = new java.io.File("/sdcard/Download/photo.html");
            String s[]=current.list();
            //String s = f.getName();
            if(current.isFile()) {
                tv.setText("Hello World"+Environment.getExternalStorageDirectory());
                //tv.setText(String.valueOf(current.getTotalSpace()));

            }
        }

        catch(Exception e)
        {

        }
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
