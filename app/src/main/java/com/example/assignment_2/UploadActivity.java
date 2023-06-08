package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {

    VideoView gestureView;
    String videoPath;
    String gestueName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra(PracticeGesture.VIDEOPATH);
        gestueName = intent.getStringExtra(PracticeGesture.GESTURENAME);

        gestureView = (VideoView) findViewById(R.id.userGestureView);
        gestureView.setVideoPath(videoPath);
        gestureView.start();
        gestureView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

    }

    public void uploadVideoToServer(View view) throws InterruptedException {
        Button practiceButton = findViewById(R.id.uploadButton);
        practiceButton.setEnabled(false);
        final boolean[] videoUploaded = {false};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    // Your code goes here
                    uploadVideo(videoPath);
                    videoUploaded[0] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        if(videoUploaded[0]) {
            Toast.makeText(this, "Video Uploaded", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Some error occurred while uploading", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private int uploadVideo(String sourceFileUri) throws ParseException, IOException {

        String upLoadServerUri = "http://192.168.0.220:5000/upload";
        String fileName = gestueName+".mp4";

        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e(Thread.currentThread().getStackTrace()[1].getMethodName(), " Source File Does not exist");
            return 0;
        }
        int serverResponseCode = 0;
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("video", fileName);
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\""+ fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i(Thread.currentThread().getStackTrace()[1].getMethodName(), "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = httpURLConnection.getResponseCode();
            String serverResponseMessage = httpURLConnection.getResponseMessage();

            Log.i(Thread.currentThread().getStackTrace()[1].getMethodName() + " Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            Log.i(Thread.currentThread().getStackTrace()[1].getMethodName() + " Upload file to server", fileName + " File is written");

            fileInputStream.close();
            Log.i(Thread.currentThread().getStackTrace()[1].getMethodName() + " Upload file to server", fileName + " File is written");
            dataOutputStream.flush();
            dataOutputStream.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e(Thread.currentThread().getStackTrace()[1].getMethodName()+ " Upload file to server", " Error: " + ex.getMessage(), ex);
            throw  ex;
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }

        //Reponse link
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpURLConnection
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.i(Thread.currentThread().getStackTrace()[1].getMethodName(), " RES Message: " + line);
            }
            rd.close();
        } catch (IOException ioex) {
            Log.e(Thread.currentThread().getStackTrace()[1].getMethodName(), " Error: " + ioex.getMessage(), ioex);
            throw ioex;
        }
        return serverResponseCode;
    }

}