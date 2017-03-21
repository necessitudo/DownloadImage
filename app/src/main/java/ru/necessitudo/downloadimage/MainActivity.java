package ru.necessitudo.downloadimage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import android.view.View;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    public static final String TAG = "happy";
    private  String pathToPicture;

    private  ImageView image;
    private String url = "https://farm3.staticflickr.com/2904/33143941260_5332f8ff6f_q.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.picture);

        pathToPicture = getFilesDir()+"/picture.jpg";

        Intent pictureIntent = getIntent();
        if(pictureIntent!=null && pictureIntent.hasExtra("setPicture")){
            File file = new File(pathToPicture);
            if(file.exists()) {
                Picasso.with(this).load(file).into(image);
            }
        }
    }

    public void onClick(View view) {


        mNotifyManager =
                (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.stat_notify_sync);

        mNotifyManager =
                (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.btn_star);
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {

                    private void download() throws  Exception{
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).build();

                        String path = pathToPicture;

                        Response responce = client.newCall(request).execute();

                        BufferedInputStream  bis = new BufferedInputStream(responce.body().byteStream());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));

                        int allByte = Integer.parseInt(responce.header("Content-Length"));
                        int len;

                        byte[] buffer = new byte[50*1024];

                        int incr=0;
                        //mBuilder.setProgress(allByte, incr, false);

                        while ((len = bis.read())!=-1) {
                            bos.write(len);
                            incr+=len;

                            //mBuilder.setProgress(allByte, incr, false);
                            //mNotifyManager.notify(R.id.SET_PICTURE_ID, mBuilder.build());
                        }

                        Context context = getApplicationContext();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("setPicture", true);

                        PendingIntent picturePendingIntent = PendingIntent.getActivity(
                                context, R.id.SET_PICTURE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.setContentText("Download complete").setProgress(0, 0, false);
                        mBuilder.setContentIntent(picturePendingIntent).setAutoCancel(true);
                        mNotifyManager.notify(R.id.SET_PICTURE_ID, mBuilder.build());

                        bis.close();
                        bos.close();


                    }


                    @Override
                    public void run()   {

                        try {
                            download();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    public void setPicture(View view) {
        File file = new File(pathToPicture);
        if(file.exists()) {
            Picasso.with(this).load(file).into(image);
        };
    }
}



