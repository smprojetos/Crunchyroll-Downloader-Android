package com.hama.crdl;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

public static final String SHARED_PREFS = "hamaCRDSetings";
public static final String HARDSUBS = "hardsubs";
public static final String RESOLUTION = "resolution";


private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = 4000;

public String hardsub_value ;
public String resolution_value;
public static  RecyclerView.Adapter mAdapter;
public static ArrayList<ExampleItem> exampleList;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;





    //@Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

        exampleList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //exampleList.add(new ExampleItem(R.drawable.test1, "RWBY 1", "5/10",50));

       loadData();
        startTimer();
        havePermissionForWriteStorage();
    }

    private boolean havePermissionForWriteStorage() {

        //marshmallow runtime permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.d("Permission Allowed", "true");
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 950);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        hardsub_value = sharedPreferences.getString(HARDSUBS, "\"enUS\"");
        resolution_value = sharedPreferences.getString(RESOLUTION, "720p");

    }

    public  void openBrowser(View v){
        //exampleList.set(0,new ExampleItem(R.drawable.test2, "RWBY 2", "3/10",30));
       Intent i = new Intent(this,BrowserActivity.class);
       startActivity(i);
          }
    public  void openSettings(View v){
        Intent i = new Intent(this,SettingsActivity.class);
        startActivity(i);
    }
    public  void AddButton(View v){
        Intent i = new Intent(this,AddActivity.class);
        i.putExtra("hardsubs",hardsub_value);
        i.putExtra("reso",resolution_value);
        //i.putExtra("listview",exampleList);
        startActivity(i);

    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = 4000;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
               // Log.v("timer : ", " fertig?");
                startTimer();
            }
        }.start();

    }
    private void updateCountDownText() {
        mRecyclerView.setAdapter(mAdapter);
        //Log.v("timer : ", " läuft!");

    }


}


