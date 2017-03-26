package ru.necessitudo.downloadimage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
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
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private  String pathToPicture;
    private BroadcastReceiver mMyBroadcastReceiver;


    private  ImageView image;
    private String url = "https://farm3.staticflickr.com/2904/33143941260_5332f8ff6f_q.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.picture);

        pathToPicture = getFilesDir()+"/picture.jpg";


        if(getIntent()!=null && getIntent().hasExtra("setPicture")){
            File file = new File(pathToPicture);
            if(file.exists()) {
                Picasso.with(MainActivity.this).load(file).into(image);
            }
        }


    }

    public void onClick(View view) {
        Intent intent = new Intent(this, ServiceOnDownload.class);
        intent.putExtra("path", pathToPicture);
        intent.putExtra("url", url);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMyBroadcastReceiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent!=null && intent.hasExtra("setPicture")){
                File file = new File(pathToPicture);
                if(file.exists()) {
                    Picasso.with(MainActivity.this).load(file).into(image);
                }
            }

        }
    }

}



