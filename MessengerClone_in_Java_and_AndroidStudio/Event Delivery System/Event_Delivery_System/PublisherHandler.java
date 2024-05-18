package Event_Delivery_System;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class PublisherHandler implements Runnable {
    
    private Socket socket;
    private ObjectInputStream in;
    private String userSending;

    public PublisherHandler(Socket socket) {
        this.socket = socket;
        try  {
            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            initStreams(read,write);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initStreams(BufferedReader read,BufferedWriter write) {
        // synchronize object streams
        try {
            write.write("open outputstream");
            write.newLine();
            write.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
           
            e.printStackTrace();
        }
    }

    /*
     We check if the user that is sending a message is connected to the Broker
     If not we add him to our registed users since he is already registered to the topic becasue otherwise he 
     could not have sent us a message(ss ConsumerHandler -> registerTopicToUser and registerUserToTopic)
     (This method is used when we are sent a SYNC Message and is used if we connect to a topic and this topic is owned by a Broker that we connected to earlier via another Topic)
    */
    private void checkIfUserConnectedToTopic(String topicName,ProfileName sender) {
        for(int i = 0; i < Broker.getOwnedTopics().size(); i++) {
            if(Broker.getOwnedTopics().get(i).getName().equals(topicName)) {
                if(Broker.getOwnedTopics().get(i).getRegisteredProfiles().size() == 0) {
                    Broker.getOwnedTopics().get(i).getRegisteredProfiles().add(sender);
                }
                else {
                    boolean exists = false;
                    for(int j = 0; j < Broker.getOwnedTopics().get(i).getRegisteredProfiles().size(); j++) {
                        if(Broker.getOwnedTopics().get(i).getRegisteredProfiles().get(j).getName().equals(sender.getName())) {
                            exists = true;
                        }
                    }
                    if(!exists) {
                         Broker.getOwnedTopics().get(i).getRegisteredProfiles().add(sender);
                    }
                }
            }
        }
    }
    
    //Read a text Message that is sent
    //If the sent Message is SYNC then we call the method checkIfUserConnectedToTopic
    private void ReadText(ObjectInputStream in,Message m) {
        try {
            String topicName = (String) in.readObject();
            ProfileName sender = (ProfileName) in.readObject();
            if(!m.getTextValue().equals("SYNC")) {
                Message[] tmp = new Message[1];
                tmp[0] = m;
                Broker.updateTopicHistory(topicName,tmp);
                Broker.getRegisteredUsers().put(sender.getName(),sender);
                System.out.println("Received Text on Topic " + topicName + " from " + sender.getName());
            }
            else if(m.getTextValue().equals("SYNC")) {
                checkIfUserConnectedToTopic(topicName, sender);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void ReadFile(ObjectInputStream in,Message m) {
        try {   
            String topicName_tmp = (String) in.readObject();
            ProfileName sender = (ProfileName) in.readObject();
            ArrayList<Message> tmp = new ArrayList<>();
            tmp.add(m);
            for(int i = 1; i < m.getChunks() + 1; i++) {
                String notify_tmp = (String) in.readObject();
                Message m_tmp = (Message) in.readObject();
                topicName_tmp = (String) in.readObject();
                sender = (ProfileName) in.readObject();
                tmp.add(m_tmp);
            }
            Message[] fileChunks = new Message[tmp.size()];
            for(int i = 0; i < fileChunks.length - 1; i++) {
                fileChunks[i] = tmp.get(i);
                fileChunks[i].setFileText(tmp.get(i).getFileText());
            }
            System.out.println("Received File on Topic " + topicName_tmp + " from " + sender.getName());
            fileChunks[fileChunks.length-1] = tmp.get(tmp.size()-1);

            checkIfUserConnectedToTopic(topicName_tmp, sender);
            Broker.updateTopicHistory(topicName_tmp, fileChunks);
           
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void SetActiveTopic(ObjectInputStream in ) {
        try {
            String userName = (String) in.readObject();
            String activeTopic = (String) in.readObject();
            Broker.getUserActiveTopic().put(userName,activeTopic);
            userSending = userName;
            
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                // We check if we will read Message object or the user wants to exit
                String notify = (String) in.readObject();

                if(notify.equals("Active Topic")) {
                    SetActiveTopic(in);
                }

                if(notify.equals("exit")) {
                    break;
                }
                
                Message m = (Message) in.readObject();    

                if(m.getMessageType()) {
                    // We call ReadText if the user just sent us a text message
                    ReadText(in, m);
                }
                else {
                    // We call ReadFile if the user just sent us a photo or a video file
                    ReadFile(in, m);
                }   
            }
            Broker.getUserActiveTopic().put(userSending,"");
            in.close();
            socket.close();
        }
        catch (IOException | ClassNotFoundException  e) {
            
        }
       
    }
}
