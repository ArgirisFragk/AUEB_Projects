package com.example.edsui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Event_Delivery_System.Message;

public class MultiMediaPlayer extends AppCompatActivity {

    private String fileType;
    private Event_Delivery_System.Message file;
    private String videoFileName;

    private ImageView imageView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_media_player);
        Intent intent = getIntent();
        fileType = intent.getExtras().getString("FILETYPE");
        file = (Message) intent.getExtras().getSerializable("FILEMESSAGE");
        imageView = findViewById(R.id.MultiMediaImage);
        videoView = findViewById(R.id.MultiMeidaVideo);

        if(fileType.equals("IMAGE")) {
            videoView.setVisibility(View.INVISIBLE);
            byte[] fileBytes = file.getFileValue().getMultimediaFile();
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBytes,0,fileBytes.length);
            imageView.setImageBitmap(bitmap);
            imageView.setMaxWidth(412);
            imageView.setMaxHeight(332);
            imageView.setVisibility(View.VISIBLE);
        }
        else {
            videoFileName = intent.getExtras().getString("FILENAME");
            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);

            File dir = getApplicationContext().getFilesDir();
            Uri uri = Uri.fromFile(new File(dir,videoFileName));
            videoView.setVideoURI(uri);

            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);


        }

    }
}