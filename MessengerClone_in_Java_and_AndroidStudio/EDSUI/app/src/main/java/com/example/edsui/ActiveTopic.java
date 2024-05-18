package com.example.edsui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;

import Event_Delivery_System.Consumer;
import Event_Delivery_System.Message;
import Event_Delivery_System.MultimediaFile;
import Event_Delivery_System.ProfileName;
import Event_Delivery_System.Publisher;
import Event_Delivery_System.Topic;

public class ActiveTopic extends AppCompatActivity {

    private ProfileName profileName;
    private Consumer consumer;
    private Publisher publisher;
    private Topic ActiveTopic;

    private ArrayList<Event_Delivery_System.Message> history;//List holding all the message history
    private RecyclerView messageList;//List to view the Messages

    private ConsumerMessageListener consumerMessageListener;//Responsible for retrieving and displaying the messages sent from broker
    private Button refreshMessages;//Invokes the use of the consumerMessageListener

    private Button sendMessage;//Responsible for sending the message typed in the typeMessage edit text
    private Button sendImage;//Responsible for opening the Gallery and giving the user the option to choose a photo or a video and send it
    private EditText typeMessage;//This is where the user types his message

    private Button returnBack;//Exits from the topic and goes back to RegisterTopic

    private Uri selectedImage;//Used to reference the image selected

    private Uri selectedVideo;//Used to reference the video selected

    private Button camera;//Responsible for opening the camera app and letting the user send the photo he took

    private ActiveTopicListAdapter.ActiveTopicListClickListener activeTopicListClickListener;//Responsible for the opening of videos and photos in a new activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_topic);

        Intent intent = getIntent();
        profileName = (ProfileName) intent.getExtras().getSerializable("ProfileName1");
        ActiveTopic = (Topic) intent.getExtras().getSerializable("ActiveTopic");
        profileName.setActiveTopic(ActiveTopic);
        consumer = new Consumer(profileName);
        publisher = new Publisher(profileName);

        refreshMessages = findViewById(R.id.RefreshMessages);
        messageList = findViewById(R.id.Active_Topic_List);
        history = new ArrayList<>();

        refreshMessages.setOnClickListener(new View.OnClickListener() {//Retrieves Messages
            @Override
            public void onClick(View v) {
                consumerMessageListener = new ConsumerMessageListener();
                consumerMessageListener.execute();
            }
        });

        sendMessage = findViewById(R.id.SendMessage);
        sendImage = findViewById(R.id.SendImage);
        typeMessage = findViewById(R.id.TypeMessage);

        new Thread(new Runnable() {
            @Override
            public void run() {
                publisher.initConv(ActiveTopic);
            }
        }).start();

        sendMessage.setOnClickListener(new View.OnClickListener() {//Sends message type in the EditText
            @Override
            public void onClick(View v) {
                String message = typeMessage.getText().toString();
                typeMessage.setText("");
                if(!message.equals("") && !message.equals(null)) {
                    Sender s = new Sender(message);
                    s.start();
                }
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {//Sends image or video from the gallery
            @Override
            public void onClick(View v) {

                Intent PickMedia = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                PickMedia.setType("image/* video/*");

                ImageResultLauncher.launch(Intent.createChooser(PickMedia,"Select Media"));
            }
        });

        returnBack = findViewById(R.id.ReturnBack);

        returnBack.setOnClickListener(new View.OnClickListener() {//Goes back to the RegisterTopic activity
            @Override
            public void onClick(View v) {
                Sender sender = new Sender(true);
                sender.start();
                Intent intent = new Intent(getApplicationContext(),RegisterTopic.class);
                intent.putExtra("ProfileName", profileName);
                startActivity(intent);
            }
        });

        camera = findViewById(R.id.Camera);

        camera.setOnClickListener(new View.OnClickListener() {//Opens the camera app to take a photo and then send it
            @Override
            public void onClick(View v) {
                Intent OpenCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                CameraLauncher.launch(OpenCamera);
            }
        });

    }

    private void setOnClickListener() {//Opens videos and photos in a new activity
        activeTopicListClickListener = new ActiveTopicListAdapter.ActiveTopicListClickListener() {
            @Override
            public void onClick(View v, int position) {
                Message message = history.get(position);
                if(!message.getMessageType()) {
                    if(message.getFileText().split("\\|")[2].contains("image")) {
                        Intent intent = new Intent(getApplicationContext(),MultiMediaPlayer.class);
                        intent.putExtra("FILETYPE","IMAGE");
                        intent.putExtra("FILEMESSAGE", history.get(position));
                        startActivity(intent);
                    }
                    else {
                        File path = getApplicationContext().getFilesDir();
                        try {
                            FileOutputStream writer = new FileOutputStream(new File(path, ActiveTopic.getName()+String.valueOf(position)));
                            writer.write(history.get(position).getFileValue().getMultimediaFile());
                            writer.close();
                        } catch (IOException e) {

                        }

                        Intent intent = new Intent(getApplicationContext(),MultiMediaPlayer.class);
                        intent.putExtra("FILETYPE","VIDEO");
                        intent.putExtra("FILENAME",ActiveTopic.getName()+String.valueOf(position));
                        startActivity(intent);
                    }
                }
            }
        };
    }

    private byte[] UriToBytes(Uri toConvert) {
        try {
            InputStream input = getContentResolver().openInputStream(toConvert);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] bytes = new byte[bufferSize];
            int len = 0;
            while ((len = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            return outputStream.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }

    private void setAdapter(ArrayList<Message> msgs) {//Adapter for the recycler view
        ActiveTopicListAdapter adapter = new ActiveTopicListAdapter(msgs,activeTopicListClickListener);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        messageList.setLayoutManager(layoutManager);
        messageList.setItemAnimator(new DefaultItemAnimator());
        messageList.setAdapter(adapter);
    }

    class ConsumerMessageListener extends AsyncTask<Void,Message,ArrayList<Message>> {

        @Override
        protected ArrayList<Message> doInBackground(Void... voids) {
            ArrayList<Message> tmp = consumer.openConnectionToBroker(ActiveTopic);

            if(tmp != null) {
                history.addAll(tmp);
                return history;
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Message> msgs) {
            if(msgs != null) {
                setOnClickListener();
                setAdapter(msgs);
            }
        }
    }

    class Sender extends Thread {//sends file or message
        String message;
        byte[] file;
        String fileExtension;
        boolean exit;
        Sender(String message) {
            this.message = message;
        }

        Sender(byte[] file,String fileExtension) {this.file = file;this.fileExtension = fileExtension;}

        Sender(boolean exit) {
            this.exit = exit;
        }
        @Override
        public void run() {

            if(message == null && exit == false) {
                Log.d("FILE","READY TO SEND FILE ");
                publisher.sendMessage(file,fileExtension);
            }
            else if(exit == true) {
                publisher.sendMessage(exit);
            }
            else {
                publisher.sendMessage(message);
            }
        }

    }

    ActivityResultLauncher<Intent> ImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            // do your operation from here....
            if (data != null  && data.getData() != null) {
                Uri selectedMedia = data.getData();
                if(selectedMedia.toString().contains("image")) {
                    selectedImage = selectedMedia;
                    String fileExtension = getContentResolver().getType(selectedImage);
                    Log.d("IMAGE FILE TYPE",fileExtension);
                    byte[] imageBytes = UriToBytes(selectedImage);
                    Sender s = new Sender(imageBytes,fileExtension);
                    s.start();
                }
                else {
                    selectedVideo = selectedMedia;
                    byte[] videoBytes = UriToBytes(selectedVideo);
                    String fileExtension = getContentResolver().getType(selectedVideo);
                    Log.d("VIDEO FILE TYPE",fileExtension);
                    Sender s = new Sender(videoBytes,fileExtension);
                    s.start();
                }

            }
        }
    });

    ActivityResultLauncher<Intent> CameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            // do your operation from here....

            if (data != null  && data.getExtras().get("data") != null) {

                Bitmap photoTaken = (Bitmap) data.getExtras().get("data");

                File path = getApplicationContext().getFilesDir();
                File image = new File(path,"capturedImage");
                try {
                    FileOutputStream outputStream = new FileOutputStream(image);
                    photoTaken.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch(IOException e) {

                }
                Uri capturedImage = Uri.fromFile(new File(path,"capturedImage"));
                selectedImage = capturedImage;
                Log.d("CAPTURE IMAGE",String.valueOf(selectedImage == null));
                String fileExtension = "image/jpeg";
                Log.d("IMAGE FILE TYPE", String.valueOf(fileExtension == null));
                Log.d("STOP",fileExtension);
                byte[] imageBytes = UriToBytes(selectedImage);
                Sender s = new Sender(imageBytes, fileExtension);
                s.start();
            }
        }
    });
}