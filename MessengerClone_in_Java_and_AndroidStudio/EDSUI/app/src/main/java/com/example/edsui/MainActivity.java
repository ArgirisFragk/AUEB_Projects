package com.example.edsui;
import Event_Delivery_System.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button LoginButton;
    private EditText UserNameInput;


    private ProfileName profileName;
    private Consumer consumer;
    private Publisher publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton = findViewById(R.id.LoginButton);
        UserNameInput = findViewById(R.id.LoginUserName);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login() {
        String username = UserNameInput.getText().toString();
        if(!(username.equals("") || username.equals(null))) {
            initUser(username);
            Intent intent = new Intent(this, RegisterTopic.class);
            intent.putExtra("ProfileName", profileName);
            startActivity(intent);
        }
    }

    private void initUser(String username) {
        profileName = new ProfileName(username);
        consumer = new Consumer(profileName);
        publisher = new Publisher(profileName);
    }
}