package com.example.assignment_2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.VideoView;

public class PracticeGesture extends AppCompatActivity {

    VideoView gestureView;
    String gestureVideoName;
    String getGestureName;

    public static final String VIDEOPATH = "VIDEOPATH";
    public static final String GESTUREVIDEONAME = "com.example.myfirstapp.MESSAGE";
    public static final String GESTURENAME = "GestureName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_gesture);

        Intent intent = getIntent();

        gestureVideoName = intent.getStringExtra(MainActivity.GESTUREVIDEONAME);
        getGestureName = intent.getStringExtra(MainActivity.GESTURENAME);
        gestureView = (VideoView) findViewById(R.id.gestureView);
        int videoId = getResources().getIdentifier(gestureVideoName,
                "raw", getPackageName());
        gestureView.setVideoPath("android.resource://" + getPackageName() + "/" + videoId);
        gestureView.start();
        gestureView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        ActivityResultLauncher<Intent> practiceGestureVideoIntentActivity = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri videoUri = result.getData().getData();
                        String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
                        Cursor cursor = managedQuery(videoUri, projection, null, null, null);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        System.out.println(filePath);
                        Intent newActivityIntent = new Intent(this, UploadActivity.class);
                        newActivityIntent.putExtra(VIDEOPATH,filePath);
                        newActivityIntent.putExtra(GESTUREVIDEONAME, gestureVideoName);
                        newActivityIntent.putExtra(GESTURENAME,getGestureName);
                        startActivity(newActivityIntent);
                    }

                });

        Button practiceButton = findViewById(R.id.praticeButton);
        practiceButton.setOnClickListener(v -> {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                practiceGestureVideoIntentActivity.launch(takeVideoIntent);
            }
        });
    }
}