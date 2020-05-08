package com.hama.crdl;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.apache.commons.lang3.ArrayUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static com.hama.crdl.MainActivity.exampleList;

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
    TextView StatusMaxDLLabel;
    public TextView htmlCache;
    public String hardsub_value;
    public String resolution_value;
    private volatile boolean WaitThreadBool;


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
        CB_Season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                CB_Lang_Class SeasonDropDown = (CB_Lang_Class) CB_Season.getSelectedItem();
                String[] DropDownSplitWrapper =  SeasonDropDown.getValue().split("wrapper container-shadow hover-classes");


                List<CB_Lang_Class> CB_LangList = new ArrayList<>();
                ArrayUtils.reverse(DropDownSplitWrapper);
                for (int i = 0; i < DropDownSplitWrapper.length -1; i++) {


                    String[] Titel = DropDownSplitWrapper[i].split("title=\"");
                    String[] Titel2 = Titel[1].split("\"");
                    String[] URL = DropDownSplitWrapper[i].split("<a href=\"");
                    String[] URL2 = URL[1].split("\" title=");
                    CB_Lang_Class TitelDropDown = new CB_Lang_Class(Titel2[0], URL2[0]);
                    CB_LangList.add(TitelDropDown);
                    //i = CR_Uri_Sub.length;
                    //CR_Uri_Sub_Final = CR_Uri_Sub2[0].replace("\\/", "/");
                    Log.i("URLs", URL2[0]);
                }
                ArrayAdapter<CB_Lang_Class> adapter = new ArrayAdapter<CB_Lang_Class>(getContext(),android.R.layout.simple_spinner_item, CB_LangList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                StatusLabel.setText("Status: selected");
                CB_EpisodeStart.setAdapter(adapter);
                CB_EpisodeStop.setAdapter(adapter);
                bt_mass_add.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        CB_EpisodeStart = (Spinner) findViewById(R.id.CB_EpisodeStart);
        CB_EpisodeStop = (Spinner) findViewById(R.id.CB_EpisodeStop);
        CB_Season.setEnabled(false);
        CB_EpisodeStart.setEnabled(false);
        CB_EpisodeStop.setEnabled(false);
        bt_mass_add = (Button) findViewById(R.id.btadd_mass);
        bt_add = (Button) findViewById(R.id.btadd_dl);
        bt_mass_add.setEnabled(false);
        CB_Season.setEnabled(false);
        CB_EpisodeStart.setEnabled(false);
        CB_EpisodeStop.setEnabled(false);
        URLBox = (TextView) findViewById(R.id.urlTextBox2);
        StatusLabel = (TextView) findViewById(R.id.StatusText);
        StatusMaxDLLabel = (TextView) findViewById(R.id.statusTextMassDL);
        htmlCache = (TextView) findViewById(R.id.htmlCache);
        Browser = (WebView) findViewById(R.id.Browser);
        Browser.getSettings().setJavaScriptEnabled(true);

        Browser.addJavascriptInterface(new AddActivity.MyJavaScriptInterface(), "HTMLOUT");
        Browser.setWebViewClient(new WebViewClient() {
            // do your stuff here
            @Override
            public void onPageFinished(WebView view, String url) {
                Browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


            }
        });
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


    }


    public void dl_bt(View v) {
        StatusLabel.setText("Status: loading website ...");
        bt_add.setEnabled(false);
        CB_Season.setEnabled(false);
        CB_EpisodeStart.setEnabled(false);
        CB_EpisodeStop.setEnabled(false);


        Browser.loadUrl(URLBox.getText().toString());


    }
    public void dl_mass_bt(View v) {
        StatusMaxDLLabel.setText("Status: Starting ...");
        bt_add.setEnabled(false);
        if (CB_EpisodeStart.getSelectedItemPosition() > CB_EpisodeStop.getSelectedItemPosition()) {
            int stop = CB_EpisodeStart.getSelectedItemPosition();
            int start = CB_EpisodeStop.getSelectedItemPosition();
            CB_EpisodeStart.setSelection(start);
            CB_EpisodeStop.setSelection(stop);
        }

        ExampleRunnable runnable = new ExampleRunnable();
        new Thread(runnable).start();

        //Browser.loadUrl(URLBox.getText().toString());

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

    public void LoadHTML(final String html) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                htmlCache.setText(html);
            }
        });

    }

    public void ProcessHTML(String html) {
       //LoadHTML(html);

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
    class ExampleRunnable implements Runnable {
         String EpisodeURL;
         int Current;
        List<String> Episodes = new ArrayList<String>();
         @Override
        public void run() {



            runOnUiThread(new Runnable() {

                @Override
                public void run() {



                    for (int i = CB_EpisodeStart.getSelectedItemPosition(); i <= CB_EpisodeStop.getSelectedItemPosition(); i++) {

                        CB_Lang_Class EpisodeURLs = (CB_Lang_Class) CB_EpisodeStop.getAdapter().getItem(i);
                        Episodes.add(EpisodeURLs.getValue());
                        Log.i("multi DL", "value: " + EpisodeURLs.getValue());
                    }
                }
            });
             try {
                 Thread.sleep(5000);

             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             Log.i("multi DL", "anzahl: " + Episodes.size());

            for (int i = 0; i < Episodes.size() ; i++) {
                Current = i;
                Log.i("multi DL", "i number on start: " + i);
                for (int ii = 0; ii < Integer.MAX_VALUE; ii++) {


                if (WaitThreadBool == true){
                    try {
                        Thread.sleep(2000);
                        Log.i("multi DL", "sleep");
                        Log.i("multi DL", Boolean.toString(WaitThreadBool));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                if (WaitThreadBool == false){
                     WaitThreadBool = true;

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            StatusMaxDLLabel.setText("Status: " + Integer.toString(Current +1) + "/" + Integer.toString(Episodes.size()));
                            Log.i("multi DL", "dummy DL");
                            Browser.loadUrl("https://www.crunchyroll.com/"+Episodes.get(Current));
                            Log.i("multi DL", "dummy DL : https://www.crunchyroll.com" + Episodes.get(Current));
                            Log.i("multi DL", "Current numbe: "+ Current);
                        }

                    });
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                }




            }



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
            String[] Thumbnail;
            String[] Thumbnail2;
            String Thumbnail3;
            String[] SeasonDropDown;

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
                    Thumbnail = html.split(Pattern.quote("\"thumbnail\":{\"url\":\""));
                    Thumbnail2 = Thumbnail[1].split(Pattern.quote("\"}"));
                    Thumbnail3 = Thumbnail2[0].replace("\\/", "/");
                    Log.i("thumbnail", "thumbnail: " + Thumbnail3);

                    String downloadPath = Environment.getExternalStorageDirectory() + "/" + "Download/";
                    File dir = new File(downloadPath);
                    if (!dir.exists()) {

                        dir.mkdirs();
                    }
                    try {




                        DL_URL = PlaylistDownloader.getdl_url(CR_Uri_Sub_Final, "x" + resolution_value.replace("p", ","));

                        PlaylistDownloader downloader =
                                new PlaylistDownloader(DL_URL);
                        //exampleList.add(new ExampleItem(R.drawable.test1, CR_Final_Anime_Titel, "Starting the download...", 0));

                        exampleList.add(new ExampleItem(Thumbnail3, CR_Final_Anime_Titel, "Starting the download...", 0));

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                StatusLabel.setText("Status: idle");
                                URLBox.setText(null);
                                bt_add.setEnabled(true);
                                WaitThreadBool = false;

                            }
                        });
                        downloader.download(dir.toString() + "/" + CR_Final_Anime_Titel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (html.contains("season-dropdown content-menu block"))
                    {
                      runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                SeasonDropDown = html.split("season-dropdown content-menu block");
                                ArrayUtils.reverse(SeasonDropDown);
                                List<CB_Lang_Class> CB_LangList = new ArrayList<>();
                                for (int i = 0; i < SeasonDropDown.length -1; i++) {


                                    String[] Titel = SeasonDropDown[i].split("</a>");
                                    String[] Titel2 = Titel[0].split(">");
                                    CB_Lang_Class TitelDropDown = new CB_Lang_Class(Titel2[1], SeasonDropDown[i]);
                                    CB_LangList.add(TitelDropDown);
                                    //i = CR_Uri_Sub.length;
                                    //CR_Uri_Sub_Final = CR_Uri_Sub2[0].replace("\\/", "/");


                                }
                                ArrayAdapter<CB_Lang_Class> adapter = new ArrayAdapter<CB_Lang_Class>(getContext(),android.R.layout.simple_spinner_item, CB_LangList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                CB_Season.setEnabled(true);
                                CB_EpisodeStart.setEnabled(true);
                                CB_EpisodeStop.setEnabled(true);
                                StatusLabel.setText("Status: multi download detected - select below!");
                                CB_Season.setAdapter(adapter);

                            }
                        });



                    }

                else if (html.contains("wrapper container-shadow hover-classes"))
                {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                             String[] DropDownSplitWrapper =  html.split("wrapper container-shadow hover-classes");


                            List<CB_Lang_Class> CB_LangList = new ArrayList<>();
                            ArrayUtils.reverse(DropDownSplitWrapper);
                            for (int i = 0; i < DropDownSplitWrapper.length -1; i++) {


                                String[] Titel = DropDownSplitWrapper[i].split("title=\"");
                                String[] Titel2 = Titel[1].split("\"");
                                String[] URL = DropDownSplitWrapper[i].split("<a href=\"");
                                String[] URL2 = URL[1].split("\" title=");
                                CB_Lang_Class TitelDropDown = new CB_Lang_Class(Titel2[0], URL2[0]);
                                CB_LangList.add(TitelDropDown);
                                //i = CR_Uri_Sub.length;
                                //CR_Uri_Sub_Final = CR_Uri_Sub2[0].replace("\\/", "/");
                                Log.i("URLs", URL2[0]);
                            }
                            ArrayAdapter<CB_Lang_Class> adapter = new ArrayAdapter<CB_Lang_Class>(getContext(),android.R.layout.simple_spinner_item, CB_LangList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            CB_Season.setEnabled(false);
                            CB_EpisodeStart.setEnabled(true);
                            CB_EpisodeStop.setEnabled(true);
                            StatusLabel.setText("Status: selected");
                            CB_EpisodeStart.setAdapter(adapter);
                            CB_EpisodeStop.setAdapter(adapter);
                            bt_mass_add.setEnabled(true);
                        }
                    });

                }
                else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            StatusLabel.setText("Status: something looks wrong, check the url.");
                            bt_add.setEnabled(true);
                        }
                    });

                }



            }
        }


}