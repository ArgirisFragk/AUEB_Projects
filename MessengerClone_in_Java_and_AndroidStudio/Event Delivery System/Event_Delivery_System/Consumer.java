package Event_Delivery_System;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;

public class Consumer implements Serializable {
    private static final long serialVersionUID = -1L;
	private Socket socket;
    private Socket listener;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final ProfileName profileName;
    private int numberOfFileReceived = 0;
    private HashMap<Integer,String> brokerAddreses = new HashMap<>();//Keeps the ports and ip's of the different brokers we are connected to
    private ArrayList<Topic> topics = new ArrayList<>();

	public Consumer(ProfileName profileName) {
        this.profileName = profileName;
    }

    /*
      This method is used to register to a conversation. We are connecting to a "random" Broker(the broker's ip and port are hard coded)
    */
    public ArrayList<Topic> registerToConversasion() {
        try {
            socket = new Socket("192.168.3.7",1234);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            wr.flush();
            wr.write("1");
            wr.newLine();
            wr.flush();
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ArrayList<Topic> topics =(ArrayList<Topic>) in.readObject();
            this.topics = topics;
            return this.topics;

        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        }
    }

    //This method is used to Complete the registration to a profile.
    public ProfileName ConnectToActiveTopic(int position) {
        try {
            Message m1 = new Message();
            m1.setMessageValue(String.valueOf(position));

            out.writeObject(m1);
            out.flush();

            out.writeObject(this.profileName);
            out.flush();

            ProfileName temp = (ProfileName) in.readObject();
            this.profileName.setRegisteredConversasion(temp.getRegisteredConversasion());
            this.profileName.setRegisterToNewTopic(false);
            socket.close();
            in = null;
            out = null;
            return profileName;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    //This method is used to retrieve all the unreceived messages from the active topic.
    public ArrayList<Message> openConnectionToBroker(Topic activeTopic) {
        try {
            ArrayList<Message> msgs = new ArrayList<>();

            listener = new Socket(activeTopic.getIP(),activeTopic.getPort());
            //Connect to the broker as a Consumer

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(listener.getOutputStream()));
            wr.flush();

            wr.write("1");
            wr.newLine();
            wr.flush();

            ObjectInputStream in = new ObjectInputStream(listener.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(listener.getOutputStream());
            out.flush();
            ArrayList<Topic> gb = (ArrayList<Topic>)in.readObject();//This is sent when we want to register to a topic but now we are already registerd in a topic so we just ignore this message

            //We use code -1 to say to the ConsumerHandler that we want to listen for messages and not register to a topic
            Message flag = new Message();
            flag.setMessageValue("-1");
            out.writeObject(flag);
            out.flush();

            out.writeObject(profileName);
            out.flush();

            while(true) {
                String notify = (String) in.readObject();
                out.writeObject("SYN");
                out.flush();
                if(notify.equals("No New Message")) {
                    break;
                }

                Message m = (Message) in.readObject();
                out.writeObject("SYNACK");
                out.flush();
                if(m.getMessageType()) {

                    String topicName = (String) in.readObject();
                    String message = m.getTextValue();
                    msgs.add(m);
                }
                else {
                    String fileText = (String) in.readObject();
                    String topicName = (String) in.readObject();
                    int chunks = m.getChunks();
                    String sender = fileText.split("\\|")[1];
                    ArrayList<Message> chunkMessages = new ArrayList<Message>();
                    chunkMessages.add(m);
                    for(int i = 1; i < chunks; i++) {
                        notify = (String) in.readObject();
                        out.writeObject("SYN");
                        out.flush();
                        Message m_tmp = (Message) in.readObject();
                        out.writeObject("SYNACK");
                        out.flush();
                        fileText = (String) in.readObject();
                        topicName = (String) in.readObject();
                        chunkMessages.add(m_tmp);
                    }
                    notify = (String) in.readObject();
                    out.writeObject("SYN");
                    out.flush();
                    Message m_tmp = (Message) in.readObject();
                    out.writeObject("SYNACK");
                    out.flush();
                    topicName = (String) in.readObject();
                    String fileExtension = m_tmp.getFileText();
                    MultimediaFile file = new MultimediaFile();

                    for(Message chunk : chunkMessages) {
                        file.setChunk(chunk.getFileValue().getMultimediaFile());
                    }
                    Message completeFile = new Message();
                    completeFile.setMessageValue(file,chunks);
                    completeFile.setFileText(topicName+"|"+sender+"|"+fileExtension);
                    msgs.add(completeFile);
                }
            }
            in.close();
            listener.close();
            return msgs;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
