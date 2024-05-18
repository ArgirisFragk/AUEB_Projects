package Event_Delivery_System;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class ConsumerHandler implements Runnable{
    
    private Socket socket;
    private ArrayList<Topic> topicsToSend;
    private HashMap<String,Integer> historySize;
    private int int_chunks;


    public ConsumerHandler(Socket socket,ArrayList<Topic> topicsToSend) {
        this.socket = socket;
        this.topicsToSend = topicsToSend;
        this.historySize = new HashMap<>();
    }

    /*
     We get a MultimediaFile as an input and we output an ArrayList of Multimedia files.
     Each file in the ArrayList is a 512KB chunk of the original MultimediaFile given
    */
    public ArrayList<MultimediaFile> convertFile(MultimediaFile multimediaFile) {
        // We fill the arraylist with MultimediaFile objects
        byte[] file = multimediaFile.getMultimediaFile();
        ArrayList<MultimediaFile> chunks = new ArrayList<>();
        // Last chunk may not be 512KB
        int leftovers = file.length % 524288;
        int numberOfChunks = Math.floorDiv(file.length, 524288);
        if(leftovers == 0) {
            int_chunks = numberOfChunks;
        }
        else {
            int_chunks = numberOfChunks + 1;
        }

        int i = 0;
        while(i < file.length) {
            // We add the last chunk in the arraylist
            if(numberOfChunks == 0) {
                byte[] lastChunk = new byte[leftovers];
                for(int j = 0; j < leftovers; j++) {
                    lastChunk[j] = file[i];
                    i++;
                }
                MultimediaFile tmp = new MultimediaFile();
                tmp.setChunk(lastChunk);
                tmp.setFileName(multimediaFile.getFileName());
                chunks.add(tmp);
                break;
            }
            else {
                byte[] chunk = new byte[524288];
                for(int j = 0; j < 524288; j++) {
                    chunk[j] = file[i];
                    i++;
                }
                numberOfChunks--;
                MultimediaFile temp = new MultimediaFile();
                temp.setChunk(chunk);
                temp.setFileName(multimediaFile.getFileName());
                chunks.add(temp);
            }
        }
        return chunks;
    }

    /*
     This method is responsible for determining what the consumer wants to do
        -Register to a new topic 
        -Listen For new Messages
    */
    private int getUserAction(ObjectInputStream in, ObjectOutputStream out) {
        // We send the topics to the client
        try {
            out.writeObject(topicsToSend);//We send the topics each time and let the user decide if he wants to use them or ignore them
            out.flush();
            // We read the topic in which the client wants to do something
            Message m1 = new Message();
            m1 = (Message)in.readObject();
            int num= Integer.parseInt(m1.getTextValue());
            return num;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return -10;
        }
        
    }

    /*
     We are sent the profileName of the user and update his list of registeredConversasions with the topic he decided to join 
    */
    private void registerTopicToUser(ObjectInputStream in, ObjectOutputStream out,int num) {
        try {
            // We read the user's profile name so we can add it to the appropriate list
            ProfileName p1 = (ProfileName)in.readObject();
            
            p1.registerToConversasion(Broker.getTopics().get(num));
            out.writeObject(p1);
            out.flush();
            Broker.getRegisteredUsers().put(p1.getName(),p1);
            Broker.getUserActiveTopic().put(p1.getName(),"");

        } catch (IOException | ClassNotFoundException e) {
           
        }
        
    }



    // We check if there is a change in the consumer's history and if it does we send the new messages to the appropriate consumer
    private void UpdateConsumerOnChangesInTopicHistory(ObjectInputStream in,ObjectOutputStream out,ProfileName Consumer) {
        for(int l = 0; l < Broker.getOwnedTopics().size(); l++) {// For broker's topic
            Consumer = Broker.getRegisteredUsers().get(Consumer.getName()); 
            for(int u = 0; u < Broker.getOwnedTopics().get(l).getRegisteredProfiles().size(); u++) {//  For every registered profile name in the topic
                if(Broker.getOwnedTopics().get(l).getRegisteredProfiles().get(u).getName().equals(Consumer.getName())) {// If this user is registered in this topic 
                    if(Broker.getUserActiveTopic().get(Consumer.getName()).equals(Broker.getOwnedTopics().get(l).getName())) {
                        // If there is a new message that we have not sent
                        if(!(Broker.getUsersTopicHistory().get(Consumer.getName()).get(Broker.getOwnedTopics().get(l).getName()) == (Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).size()))) {
        
                            for(int j = Broker.getUsersTopicHistory().get(Consumer.getName()).get(Broker.getOwnedTopics().get(l).getName()); j < Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).size(); j++) {
                                
                                if(Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[0].getMessageType()) {
                                    // If it's a text                                              
                                    try {
                                        out.writeObject("Ready to send message - Text");
                                        out.flush();
                                        String syn = (String) in.readObject();
                                        out.writeObject( (Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[0]) );
                                        out.flush();
                                        String syn_ack = (String) in.readObject();
                                        out.writeObject(Broker.getOwnedTopics().get(l).getName());
                                        out.flush();
                                    } catch (IOException | ClassNotFoundException e) {
                                        break;
                                    }
                                }
                                else {
                                    // If it's a photo or a video
                                    for(int k = 0; k < Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j).length; k++) {
                                        Message m = new Message();
                                        if(Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[k].getMessageType()) {
                                            m.setFileText(Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[k].getFileText());
                                            try {
                                                out.writeObject("Ready to send message - File");
                                                out.flush();
                                                String syn = (String) in.readObject();
                                                out.writeObject(m);
                                                out.flush();
                                                String syn_ack = (String) in.readObject();
                                                out.writeObject(Broker.getTopics().get(l).getName());
                                                out.flush();
                                            } catch (IOException | ClassNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            m.setMessageValue(Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[k].getFileValue(),Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[k].getChunks());
                                            m.setFileText(Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).get(j)[k].getFileText());
                                            try {
                                                out.writeObject("Ready to send message - File");
                                                out.flush();
                                                String syn = (String) in.readObject();
                                                out.writeObject(m);
                                                out.flush();
                                                String syn_ack = (String) in.readObject();
                                                out.writeObject(m.getFileText());
                                                out.flush();
                                                out.writeObject(Broker.getTopics().get(l).getName());
                                                out.flush();
                                            } catch (IOException | ClassNotFoundException e) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            Broker.getUsersTopicHistory().get(Consumer.getName()).put(Broker.getOwnedTopics().get(l).getName(),Broker.getTopicsHistory().get(Broker.getOwnedTopics().get(l).getName()).size());
                            break;
                        }
                        else {
                            try {
                                out.writeObject("No New Message");
                                out.flush();
                                out.close();
                                socket.close();
                            } catch (IOException e) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void run() {
        try {
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();

            int num = getUserAction(in, out);
            
            if(!(num == -1)) 
            {
                registerTopicToUser(in, out, num);
                
            }
            else 
            {
                // We read the user's profile name to add to it's list the topic on which he wants to register.
                ProfileName Consumer = (ProfileName) in.readObject();
                Broker.getRegisteredUsers().put(Consumer.getName(), Consumer);
                //registerUserToTopic(Consumer);
                
                if(Broker.getUsersTopicHistory().size() == 0) {
                    Broker.getUsersTopicHistory().put(Consumer.getName(),new HashMap<>());
                    for(Topic t : Broker.getOwnedTopics()) {
                        Broker.getUsersTopicHistory().get(Consumer.getName()).put(t.getName(),0);
                    }
                }
                else {
                    if (Broker.getUsersTopicHistory().get(Consumer.getName()) == null) {
                        Broker.getUsersTopicHistory().put(Consumer.getName(),new HashMap<>());
                        for(Topic t : Broker.getOwnedTopics()) {
                            Broker.getUsersTopicHistory().get(Consumer.getName()).put(t.getName(),0);
                        }
                    }
                }
                

    
                while (!socket.isClosed()) {
                    UpdateConsumerOnChangesInTopicHistory(in,out, Consumer);
                }
            }

        } 
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
