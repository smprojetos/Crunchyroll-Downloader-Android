package com.hama.crdl;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import java.util.Random;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import static com.hama.crdl.MainActivity.exampleList;
import static com.hama.crdl.MainActivity.mAdapter;
public class AddActivity extends AppCompatActivity {
    private static AddActivity instance;
    WebView Browser;
    Spinner CB_Season;
    Spinner CB_EpisodeStart;
    Spinner CB_EpisodeStop;
    Button bt_mass_add;
    Button bt_add;
    TextView URLBox;
    TextView StatusLabel;
  public  TextView htmlCache;
    public String hardsub_value;
    public String resolution_value;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            resolution_value = extras.getString("reso");
            hardsub_value = extras.getString("hardsubs");
            //get the value based on the key
        }
        //Toast.makeText(AddActivity.this, resolution_value, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_add);
        instance = this;
        //Toast.makeText(AddActivity.this, hardsub_value, Toast.LENGTH_LONG).show();
        CB_Season = (Spinner) findViewById(R.id.CB_Season);
        CB_EpisodeStart = (Spinner) findViewById(R.id.CB_EpisodeStart);
        CB_EpisodeStop = (Spinner) findViewById(R.id.CB_EpisodeStop);
        CB_Season.setEnabled(false);
        CB_EpisodeStart.setEnabled(false);
        CB_EpisodeStop.setEnabled(false);
        bt_mass_add = (Button) findViewById(R.id.btadd_mass);
        bt_add = (Button) findViewById(R.id.btadd_dl);
        //bt_mass_add.setEnabled(false);
        URLBox = (TextView) findViewById(R.id.urlTextBox2);
        StatusLabel = (TextView) findViewById(R.id.StatusText);
        htmlCache = (TextView) findViewById(R.id.htmlCache);
        htmlCache.addTextChangedListener(new TextWatcher() {


            @Override
                                             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                             }

                                             @Override
                                             public void onTextChanged(CharSequence s, int start, int before, int count) {

                                             }

                                             @Override
                                             public void afterTextChanged(Editable s) {
                                                    ProcessHTML(htmlCache.getText().toString());
                                             }
                                         });
                Browser = (WebView) findViewById(R.id.Browser);

    }


    public void dl_bt(View v) {
     Browser.getSettings().setJavaScriptEnabled(true);

        Browser.addJavascriptInterface(new AddActivity.MyJavaScriptInterface(), "HTMLOUT");
        Browser.setWebViewClient(new WebViewClient() {
            // do your stuff here
            @Override
            public void onPageFinished(WebView view, String url) {
                Browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


            }
        });

        Browser.loadUrl(URLBox.getText().toString());

    }

    public static Context getContext() {
        return instance;
    }

    public String getReso() {
        return resolution_value;
    }

    public String gethardsub() {
        return hardsub_value;
    }
    public void LoadHTML(String html) {
        htmlCache.setText(html);
    }

    public void ProcessHTML(String html) {
    BackgroudThread thread = new BackgroudThread(html, resolution_value,hardsub_value);
    thread.start();
 }


        class MyJavaScriptInterface extends AddActivity {
            @JavascriptInterface
             @SuppressWarnings("unused")

            public void processHTML(final String html) {

                AddActivity.this.ProcessHTML(html);
            }


        }

        class BackgroudThread extends Thread{
            String[] CR_Anime_Titel;
            String[] CR_Anime_Titel2;
            String[] CR_Anime_Titel3;
            String CR_Final_Anime_Titel;
            String Bug_Deutsch;
            String[] CR_Uri_Sub;
            String[] CR_Uri_Sub2;
            String[] CR_Uri_Sub3;
            String CR_Uri_Sub_Final_Error;
            String CR_Uri_Sub_Final;
            String DL_URL;
            String reso;
            String hardsub;
            String html;
            BackgroudThread(String html, String reso, String hardsub){
                this.hardsub = hardsub;
                this.reso = reso;
                this.html = html;


            }

            @Override
            public void run() {
                if (html.contains("\"format\":\"adaptive_hls\"")) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                          StatusLabel.setText("Status: looking for video file");

                        }
                    });
                    hardsub = hardsub_value;
                    Bug_Deutsch = "-";
                    CR_Anime_Titel = html.split("<title>");
                    CR_Anime_Titel2 = CR_Anime_Titel[1].split("</title>");

                    if (CR_Anime_Titel2[0].contains("Anschauen auf Crunchyroll")) {
                        Bug_Deutsch = ":";
                    }
                    CR_Anime_Titel3 = CR_Anime_Titel2[0].split(Bug_Deutsch);
                    CR_Final_Anime_Titel = CR_Anime_Titel3[0].replace(",", "") + "-temp.mp4";

                    if (CR_Anime_Titel3.length > 2) {
                        CR_Final_Anime_Titel = CR_Anime_Titel3[0].replace(",", "") + CR_Anime_Titel3[1].replace(",", "") + "-temp.mp4";
                    }


                    List<String> list = new ArrayList<String>();

                    CR_Uri_Sub = html.split("\"format\":\"adaptive_hls\"");

                    for (int i = 0; i < CR_Uri_Sub.length; i++) {

                        if (CR_Uri_Sub[i].contains("\",\"resolution\":\"adaptive\"")) {
                            CR_Uri_Sub2 = CR_Uri_Sub[i].split("\",\"resolution\":\"adaptive\"");
                            list.add(CR_Uri_Sub2[0]);
                            CR_Uri_Sub_Final_Error = CR_Uri_Sub2[0];
                            //i = CR_Uri_Sub.length;
                            //CR_Uri_Sub_Final = CR_Uri_Sub2[0].replace("\\/", "/");
                        } else {

                        }

                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).toString().contains("\"hardsub_lang\":" + hardsub + ",\"url\":\"")) {
                            //CR_Uri_Sub_Final_Error = list.get(i).toString();
                            CR_Uri_Sub3 = list.get(i).toString().split("\"hardsub_lang\":" + hardsub + ",\"url\":\"");

                            //i = CR_Uri_Sub.length;
                            CR_Uri_Sub_Final = CR_Uri_Sub3[1].replace("\\/", "/");
                        }

                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            StatusLabel.setText("Status: m3u8 found, looking for resolution");

                        }
                    });

                    String downloadPath = Environment.getExternalStorageDirectory() + "/" + "Download/";
                    File dir = new File(downloadPath);
                    if (!dir.exists()) {

                        dir.mkdirs();
                    }
                    try {

                        DL_URL = PlaylistDownloader.getdl_url(CR_Uri_Sub_Final, "x" + resolution_value.replace("p", ","));

                        PlaylistDownloader downloader =
                                new PlaylistDownloader(DL_URL);
                        exampleList.add(new ExampleItem(R.drawable.test1, CR_Final_Anime_Titel, "Starting the download...", 0));

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                StatusLabel.setText("Status: Starting the download...");

                            }
                        });
                        downloader.download(dir.toString() + "/" + CR_Final_Anime_Titel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            StatusLabel.setText("Status: something looks wrong, check the url.");

                        }
                    });

                }



            }
        }


}