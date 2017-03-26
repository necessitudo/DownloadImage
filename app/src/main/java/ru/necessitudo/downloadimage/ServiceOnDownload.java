package ru.necessitudo.downloadimage;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceOnDownload  extends IntentService{

    public static final String ACTION_MYINTENTSERVICE = "ACTION_MYINTENTSERVICE";
    private String path;
    private String url;

    public ServiceOnDownload() {
        super("ServiceOnDownload");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String path = intent.getStringExtra("path");
        String url  = intent.getStringExtra("url");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.stat_notify_sync);

        Response responce = null;
        try {
            responce = client.newCall(request).execute();

            InputStream is = (responce.body().byteStream());

            int allByte = Integer.parseInt(responce.header("Content-Length"));

            int incr=0;
            mBuilder.setProgress(allByte, incr, false);

            FileOutputStream f = new FileOutputStream(new File(path));

            byte[] buffer = new byte[50*1024];
            int len1 = 0;
            while ( (len1 = is.read(buffer)) > 0 ) {
                f.write(buffer,0, len1);
                incr+=len1;
                mBuilder.setProgress(allByte, incr, false);
                mNotifyManager.notify(R.id.SET_PICTURE_ID, mBuilder.build());
                Thread.sleep(1000);
            }
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("setPicture", true);

        PendingIntent picturePendingIntent = PendingIntent.getActivity(
                this, R.id.SET_PICTURE_ID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentText("Download complete").setProgress(0, 0, false);
        mBuilder.setContentIntent(picturePendingIntent).setAutoCancel(true);
        mBuilder.setSmallIcon(android.R.drawable.star_on);
        mNotifyManager.notify(R.id.SET_PICTURE_ID, mBuilder.build());

    }
}
