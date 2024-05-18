package Event_Delivery_System;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class BrokerHandler implements Runnable {
    
    private int topicSize;//Used to check if there's been a new Topic 
    private ServerSocket serverSocket;//To accept the connections of the other brokers
    private Socket[] socket = new Socket[Broker.getNumberOfBrokers() - 1];//To connect to the other brokers
    private boolean[] check_brokers = new boolean[Broker.getNumberOfBrokers() - 1];//to check if a connection to a broker has been made
    private ObjectInputStream[] in = new ObjectInputStream[Broker.getNumberOfBrokers() - 1];
    private ObjectOutputStream[] out = new ObjectOutputStream[Broker.getNumberOfBrokers() - 1];

    public BrokerHandler() {
        try {
            serverSocket = new ServerSocket(Broker.getPort() + 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        topicSize = 0;
    }

    /*
      This method tries to connect to the other brokers so that each can share its topics to the others
      and won't stop until the connections to the other brokers are made.
      It runs on a seperate thread.
    */
    private void tryToConnect() {
        new Thread(new Runnable() {
            public void run() {
            
                while(true){
                    boolean allOpen = true;

                    for(boolean b : check_brokers) {
                        if(!b) {
                            allOpen = false;
                            break;
                        }

                    }

                    if(allOpen)
                        break;

                    for(int i = 0; i < socket.length; i++) {
                        if (check_brokers[i]==false){
                            try {
                                socket[i] = new Socket(Broker.getBrokerIPs().get(i),Broker.getBrokerPorts().get(i)+10);
                                out[i] = new ObjectOutputStream(socket[i].getOutputStream());
                                out[i].flush();
                                check_brokers[i]=true;
                            } catch (IOException e) {
                                System.out.print("");
                            }
                        }
                    }
                }
            }
                
        }).start();
    }

    /*
      This method is used to accept the connections of the other brokers trying to connect with 
      the method tryToConnect() and when it accepts a connection it starts the listenForMessages() method
      for that connection
    */
    private void acceptConnections() {
        for(int i = 0; i < socket.length; i++) {
            try {
                Socket connection = serverSocket.accept();
                listenForMessages(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /*
      This method is used for receiving the other broker's topics and then call the updateTopics method
      so that the broker can store these topics we received.This method runs also on a seperate thread.
    */
    public void listenForMessages(Socket socket) {
        new Thread(new Runnable() {
            public void run() {
                while(socket.isConnected()) {
                    try {
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        String flag = (String)input.readObject();
                        ArrayList<Topic> otherTopics = (ArrayList<Topic>) input.readObject();
                        Broker.updateTopics(otherTopics);
                        
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
                
        }).start();
    }

    /*
      This method is used for sending the broker topics to the other Brokers
      It also runs on a seperate thread
    */
    private void informBrokers() {

        new Thread(new Runnable() {
            public void run() {

                while(topicSize == Broker.getOwnedTopics().size()) {
                    System.out.print("");
                }//Checking if the has been a change in the Brokers topics

                topicSize = Broker.getOwnedTopics().size();//Updating the changes

                for(int i =0; i < socket.length; i++) {
                    try {
                        out[i].writeObject("Sending topics");
                        out[i].flush();
                        synchronized(Broker.getTopics()) {
                            out[i].writeObject(Broker.getOwnedTopics());
                        }
                        out[i].flush();
                    } catch (IOException e) {
                        
                        e.printStackTrace();
                    }
                }
        
            }
                
        }).start();
    }

    public void run(){
        tryToConnect();
        acceptConnections();
        while(true) {
            boolean allOpen = true;

            for(boolean b : check_brokers) {
                if(!b) {
                    allOpen = false;
                    break;
                }
            }

            if(allOpen)
                break;
        }
        informBrokers();
    }
}
