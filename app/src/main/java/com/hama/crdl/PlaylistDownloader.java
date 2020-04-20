/*
 * Copyright (c) Christopher A Longo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hama.crdl;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import static com.hama.crdl.MainActivity.exampleList;

public class PlaylistDownloader {
    private URL url;
    private List<String> playlist;
    private static List<String> masterContent;
    private Crypto crypto;
    private static String dl_url;
    private boolean FFMPEG_Run = false;
    private static String EXT_X_KEY = "#EXT-X-KEY";
    private static final String BANDWIDTH = "BANDWIDTH";



    public PlaylistDownloader(String playlistUrl) throws MalformedURLException {
        this.url = new URL(playlistUrl);
        this.playlist = new ArrayList<String>();
    }

    public static String getdl_url(String masterurl, String resolution_value) throws MalformedURLException {
       try {
         URL uri = new URL(masterurl);
           Log.v("m3u8 master URL : ", masterurl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(uri.openStream()));
        String line;
        String textcompleed = null;


           try {
               OkHttpClient client = new OkHttpClient();
               Request request = new Request.Builder()
                       .url(uri)
                       .build();
               Response responses = null;

               try {
                   responses = client.newCall(request).execute();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               textcompleed = responses.body().string();

           }
           catch (Exception e) {
           e.printStackTrace();
       }


        Log.v("m3u8 master : ", textcompleed);
        //System.out.printf("m3u8 master : ", textcompleed);
        String[] TextSplit = textcompleed.split(resolution_value);
        String[] TextSplit2  = TextSplit[1].split("\"");
        String[] TextSplit3  = TextSplit2[2].split("#");
        dl_url = TextSplit3[0].replace(System.getProperty("line.separator"), "");
         System.out.printf("finale m3u8: ", dl_url);
       } catch (Exception e) {
           e.printStackTrace();
       }
        return dl_url;
    }

    public void download(String outfile) throws IOException {
        this.download(outfile, null);
    }

    int countStringOccurance(List<String> arr, String str){
        int count = 1;
        for (int i=0; i<arr.size(); i++) {
            if (arr.get(i).indexOf(str)!=-1? true: false){
             count += 1;
            }
        }
        count = count -2;
        return count;
    }

    public void download(String outfile, String key) throws IOException {
        fetchPlaylist();

        this.crypto = new Crypto(getBaseUrl(this.url), key);

        int segmentsAll = countStringOccurance(playlist, "https");
        int segmentsDone = 0;
        for (String line : playlist) {
            line = line.trim();

            if (line.startsWith(EXT_X_KEY)) {
                crypto.updateKeyString(line);

                System.out.printf("\rCurrent Key: %s                                  \n", crypto.getCurrentKey());
                System.out.printf("Current IV:  %s\n", crypto.getCurrentIV());
            } else if (line.length() > 0 && !line.startsWith("#")) {
                URL segmentUrl;

                if (!line.startsWith("https")) {
                    String baseUrl = getBaseUrl(this.url);
                    segmentUrl = new URL(baseUrl + line);
                } else {
                    segmentUrl = new URL(line);
                }

                downloadInternal(segmentUrl, outfile);
                segmentsDone++;
                        double segmentsDonedouble = segmentsDone;
                        double segmentsAlldouble = segmentsAll;
                        double progress = segmentsDonedouble / segmentsAlldouble ;
                        double progessProzent = progress *100;
                        Log.i("progessbar", "value: " + progessProzent);

                int progressint = (int) progessProzent;
                    //Toast.makeText(this, hardsub_value, Toast.LENGTH_SHORT).show();
                    //int spinnerPosition = CB_LangList.indexOf();

                    for (int i=0;i<exampleList.size();i++){
                        if (outfile.indexOf(exampleList.get(i).getTextTitle())!=-1? true: false){
                           exampleList.set(i,new ExampleItem(exampleList.get(i).getImageResource(), exampleList.get(i).getTextTitle(), segmentsDone+ "/" +segmentsAll,progressint));
                        }
                    }

            }
        }

        System.out.println("\nDone.");

        if (FFMPEG_Run == true)
        {
            for (int i=0; i<2000000; i++) {
                if (FFMPEG_Run == false){
                    i= 2000000;
                }else{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        try {
            FFMPEG_Run = true;
            for (int i=0;i<exampleList.size();i++){
                if (outfile.indexOf(exampleList.get(i).getTextTitle())!=-1? true: false){
                    exampleList.set(i,new ExampleItem(exampleList.get(i).getImageResource(), exampleList.get(i).getTextTitle(), "converting...",100));
                }
            }
        String cmd = "-i \""+outfile+"\" -c copy -bsf:a aac_adtstoasc \""+outfile.replace("-temp","")+"\"" ;//String.format("-i " +dir.toString() + "/test.mp4" +" -acodec %s -bsf:a aac_adtstoasc -vcodec %s %s", INPUT_FILE, "copy", "copy", dir.toString() + "/bigBuckBunny.mp4");
        System.out.println("cmd string "+cmd);
        String[] command = new String[7];//cmd.split(" ");
            command[0] = "-i";
            command[1] = outfile;//"\""+outfile + "\"";
            command[2] = "-c";
            command[3] = "copy";
            command[4] = "-bsf:a";
            command[5] = "aac_adtstoasc";
            command[6] = outfile.replace("-temp","");//"\""+outfile.replace("-temp","") + "\"";
            execFFmpegBinary(command);
         } catch (Exception e) {
             e.printStackTrace();
            }
    }

    private void downloadInternal(URL segmentUrl, String outFile) throws IOException {
        byte[] buffer = new byte[512];
        System.out.printf(String.valueOf(crypto.hasKey()));
        InputStream is = crypto.hasKey()

                ? crypto.wrapInputStream(segmentUrl.openStream())
                : segmentUrl.openStream();

        FileOutputStream out;

        if (outFile != null) {
            File file = new File(outFile);
            out = new FileOutputStream(outFile, file.exists());
        } else {
            String path = segmentUrl.getPath();
            int pos = path.lastIndexOf('/');
            out = new FileOutputStream(path.substring(++pos), false);
        }

        System.out.printf(" Downloading segment: %s\r", segmentUrl);

        int read;

        while ((read = is.read(buffer)) >= 0) {
            out.write(buffer, 0, read);
        }

        is.close();
        out.close();
    }

    private String getBaseUrl(URL url) {
        String urlString = url.toString();
        int index = urlString.lastIndexOf('/');
        return urlString.substring(0, ++index);
    }

    private void fetchPlaylist() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        boolean isMaster = false;
        long maxRate = 0L;
        int maxRateIndex = 0;

        String line;
        int index = 0;

        while ((line = reader.readLine()) != null) {
            playlist.add(line);

            if (line.contains(BANDWIDTH))
                isMaster = true;

            if (isMaster && line.contains(BANDWIDTH)) {
                try {
                    int pos = line.lastIndexOf("=");
                    long bandwidth = Long.parseLong(line.substring(++pos));

                    maxRate = Math.max(bandwidth, maxRate);

                    if (bandwidth == maxRate)
                        maxRateIndex = index + 1;
                } catch (NumberFormatException ignore) {}
            }

            index++;
        }

        reader.close();

        if (isMaster) {
            System.out.printf("Found master playlist, fetching highest stream at %dKb/s\n", maxRate / 1024);
            this.url = updateUrlForSubPlaylist(playlist.get(maxRateIndex));
            this.playlist.clear();

            fetchPlaylist();
        }
    }

    private URL updateUrlForSubPlaylist(String sub) throws MalformedURLException {
        String newUrl;

        if (!sub.startsWith("http")) {
            newUrl = getBaseUrl(this.url) + sub;
        } else {
            newUrl = sub;
        }

        return new URL(newUrl);
    }

    private void execFFmpegBinary(String[] command) {
        try {
            FFmpeg ffmpeg = FFmpeg.getInstance(AddActivity.getContext());
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure() {
                    }

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFinish() {
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                // Handle if FFmpeg is not supported by device
            }
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {


                    FFMPEG_Run = false;
                    Log.i("ffmpeg", "onSuccess: " + message);
                    String[] TextSplit = message.split("Input #0, mpegts, from '");
                    String[] TextSplit2  = TextSplit[1].split("':");
                    Log.i("ffmpeg-del", "The End: " + TextSplit2[0]);

                    for (int i=0;i<exampleList.size();i++){
                        if ( TextSplit2[0].indexOf(exampleList.get(i).getTextTitle())!=-1? true: false){
                            exampleList.set(i,new ExampleItem(exampleList.get(i).getImageResource(), exampleList.get(i).getTextTitle(), "finished!",100));
                        }
                    }

                    File file = new File(TextSplit2[0]);
                    boolean deleted = file.delete();


                }

                @Override
                public void onProgress(String message) {
                    Log.i("ffmpeg", "onProgress: " + message);
                    //progressBar.setMessage("Progressing: \n " + message);


                }

                @Override
                public void onFailure(String message) {
                    Log.i("ffmpeg", "onFailure: " + message);
                    // progressBar.dismiss();
                    FFMPEG_Run = false;
                }

                @Override
                public void onStart() {

                    //progressBar.show();
                }

                @Override
                public void onFinish() {
                    // progressBar.dismiss();
                    FFMPEG_Run = false;

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }


}
