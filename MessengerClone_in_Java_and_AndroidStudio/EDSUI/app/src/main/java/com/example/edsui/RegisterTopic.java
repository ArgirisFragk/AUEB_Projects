package com.example.edsui;
import Event_Delivery_System.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class RegisterTopic extends AppCompatActivity {

    private ProfileName profileName;
    private Consumer consumer;
    private Publisher publisher;
    private RecyclerView unregisteredTopicsList; //List to view all the topics
    private UnregisteredListAdapter.UnregisterdListClickListener unregisterdListClickListener;//Adapter used to display the topics in the recycler view
    private ConsumerRegisterToConv consumerRegisterToConv;//Used to get the topics from the broker and initialize the recycler view with the data
    ArrayList<Event_Delivery_System.Topic> unregisteredTopics; //List to contain the topics received

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_topic);

        //Get User from MainActivity
        consumerRegisterToConv = new ConsumerRegisterToConv();
        Intent intent = getIntent();
        profileName = (ProfileName) intent.getExtras().getSerializable("ProfileName");
        consumer = new Consumer(profileName);
        publisher = new Publisher(profileName);
        unregisteredTopicsList = findViewById(R.id.unregisteredTopicsList);
        consumerRegisterToConv.execute();
    }

    private void setOnClickListener() { //click listener for every topic so that we are able to connect to it
        unregisterdListClickListener = new UnregisteredListAdapter.UnregisterdListClickListener() {
            @Override
            public void onClick(View v, int position) {
                ConsumerChooseActiveTopic consumerChooseActiveTopic = new ConsumerChooseActiveTopic(position);
                consumerChooseActiveTopic.start();
                while(!consumerChooseActiveTopic.isFlag()) {

                }
                profileName = consumerChooseActiveTopic.getProfileName();

                Intent intent = new Intent(getApplicationContext(), ActiveTopic.class);
                intent.putExtra("ProfileName1", profileName);
                intent.putExtra("Publisher1", publisher);
                intent.putExtra("ActiveTopic",unregisteredTopics.get(position));
                startActivity(intent);
            }
        };
    }


    class ConsumerRegisterToConv extends AsyncTask<Void,Void,ArrayList<Event_Delivery_System.Topic>> { //init Recycler view with topics received from Brokers


        @Override
        protected ArrayList<Event_Delivery_System.Topic> doInBackground(Void... voids) {
            unregisteredTopics = consumer.registerToConversasion();
            return unregisteredTopics;
        }

        @Override
        protected void onPostExecute(ArrayList<Event_Delivery_System.Topic> unregisteredTopics) {
            setOnClickListener();
            UnregisteredListAdapter adapter = new UnregisteredListAdapter(unregisteredTopics,unregisterdListClickListener);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
            unregisteredTopicsList.setLayoutManager(layoutManager);
            unregisteredTopicsList.setItemAnimator(new DefaultItemAnimator());
            unregisteredTopicsList.setAdapter(adapter);
        }
    }

    class ConsumerChooseActiveTopic extends Thread {//Is responsible for running all the backround tasks needed to connect to the active topic and then let the program open the new activity
        private int position;
        private ProfileName profileName;
        private boolean flag = false;

        ConsumerChooseActiveTopic(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            profileName = consumer.ConnectToActiveTopic(position);
            flag = true;
        }

        public ProfileName getProfileName() {
            return profileName;
        }

        public boolean isFlag() {
            return flag;
        }
    }

}