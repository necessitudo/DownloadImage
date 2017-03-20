package ru.necessitudo.downloadimage;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    public static final String TAG = "happy";
    int id = 1;
    private  String pathToPicture;

    private  ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.picture);

        pathToPicture = getFilesDir()+"/picture.jpg";

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
                        Request request = new Request.Builder().url("https://farm3.staticflickr.com/2904/33143941260_5332f8ff6f_q.jpg").build();

                        String path = pathToPicture;

                        Response responce = client.newCall(request).execute();

                        BufferedInputStream  bis = new BufferedInputStream(responce.body().byteStream());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));

                        long allByte = bis.available();

                        int len;

                        while ((len = bis.read())!=-1) {
                            bos.write(len);
                        }

                        bis.close();
                        bos.close();

                    }



                    @Override
                    public void run()   {
                        int incr;

                        try {
                            download();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }






                        //}
                        //catch (Exception e){
                         //   Log.d("fail", "Not success");
                       // }

                        /*// Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr += 5) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(id, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                                Log.d(TAG, "sleep failure");
                            }
                        }*/
                        // When the loop is finished, updates the notification
                        /*mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());*/
                    }
                }
        // Starts the thread by calling the run() method in its Runnable
        ).start();

    }

    public void onClick(View view) {
        File file = new File(pathToPicture);

        if(file.exists()){
            Toast.makeText(this,"file exist!", Toast.LENGTH_SHORT).show();
        }

        Picasso.with(this).load(file).into(image);
    }
}



