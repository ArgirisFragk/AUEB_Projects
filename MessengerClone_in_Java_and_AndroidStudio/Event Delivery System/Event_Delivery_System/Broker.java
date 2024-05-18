package Event_Delivery_System;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.math.BigInteger;


public class Broker{

    private static int NumberOfBrokers;//indicates the number of brokers (read from file)
    private int BROKERID; //indicates the BrokerID (read from file)
    private static int PORT;//indicates the Port of the Broker (read from file)
    private static String IP;//indicates the IP of the Broker (read from file)
    private static ArrayList<String> BrokerIPs;//indicates the other Brokers IPs (read from file)
    private static ArrayList<Integer> BrokerPorts;//indicates the other Brokers Ports (read from file)
	private static ArrayList<Topic> topics = new ArrayList<>();//Stores all the topics in the system (Brokers share their topics with each other -- ss BrokerHandler)
    private static ArrayList<Topic> ownedTopics = new ArrayList<>();//Stores the topics owned by the Broker
    private static HashMap<String,ProfileName> registeredUsers = new HashMap<>();//Stores the registeredUsers in this Broker(via a Topic)
    private static HashMap<String,HashMap<String,Integer>> UsersTopicHistory = new HashMap<>();
    private static HashMap<String,String> UsersActiveTopics = new HashMap<>();//Keeps the Active Topic of each registered User
    private static HashMap<String,ArrayList<Message[]>> topicsHistory = new HashMap<>();//Stores the history of Messages sent to the ownedTopics
    private ServerSocket serverSocket;


	public Broker(int id) {
        BrokerIPs = new ArrayList<>();
        BrokerPorts = new ArrayList<>();
        initBrokers(id);
        initTopics();
        for(Topic t : topics) {
            System.out.println(t.getName());
            topicsHistory.put(t.getName(),new ArrayList<Message[]>());
        }
	}

    public static HashMap<String,ProfileName>
     getRegisteredUsers() {
        return registeredUsers;
    }

    public static int getPort() {
        return PORT;
    }

    public static String getIP() {
        return IP;
    }

    public static int getNumberOfBrokers() {
        return NumberOfBrokers;
    }

    public static ArrayList<Topic> getOwnedTopics() {
        return ownedTopics;
    }

    public static ArrayList<Topic> getTopics() {
        return topics;
    }

    public static HashMap<String,ArrayList<Message[]>> getTopicsHistory() {
        return topicsHistory;
    }

    public static HashMap<String,String> getUserActiveTopic() {
        return UsersActiveTopics;
    }

    public static HashMap<String,HashMap<String,Integer>> getUsersTopicHistory() {
        return UsersTopicHistory;
    }


    /*
      This method is used for initializing the brokers when they open
      It gets the id of the broker(0,1,2...NumberOfBrokers which is given when the program starts from the user as input)
      and then it reads it's IP and Port from the file based on this id
    */
    private void initBrokers(int brokerID) {
        try {

            File init = new File("InitBrokers\\initBrokers.txt");
            BufferedReader reader = new BufferedReader(new FileReader(init));

            String line = reader.readLine();
            while(!line.equals("%")) {
                
                String[] s = line.split(",");

                if(s.length == 1) {
                    NumberOfBrokers = Integer.parseInt(s[0]);
                    line = reader.readLine();
                    continue;
                }
                else {
                    if(Integer.parseInt(s[0]) > NumberOfBrokers)
                        return;
                }

                

                if(Integer.parseInt(s[0]) == brokerID) {
                    BROKERID = Integer.parseInt(s[0]) - 1;
                    IP = s[1];
                    PORT = Integer.parseInt(s[2]);
                }
                else {
                    BrokerIPs.add(s[1]);
                    BrokerPorts.add(Integer.parseInt(s[2]));
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getBrokerIPs() {
        return BrokerIPs;
    }

    public static ArrayList<Integer> getBrokerPorts() {
        return BrokerPorts;
    }

    /*
      This method gets a string as an input(Topic name) and returns through a hash function(SHA-1) returns an integer 
      which coresponds to the broker id which will be responsible for the topic
    */
    public static int hashString(String input)
    {
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-1");
  
           
            byte[] messageDigest = md.digest(input.getBytes());
  
           
            BigInteger no = new BigInteger(1, messageDigest);
  
           
            return Math.abs(no.intValue() % NumberOfBrokers);
        }
  
        catch (NoSuchAlgorithmException  e) {
            throw new RuntimeException(e);
        }
    }

    /*
      This method initializes the topics written in the InitBrokers file.It reads the Topic name 
      and then runs the hash function.Finally it checks if the returned ID is equal to it's BrokerID
      to get the topic(set it's IP and Port - Chech Topic Class)
    */
    private void initTopics() {
        try {
            File init = new File("InitBrokers\\initBrokers.txt");
            BufferedReader reader = new BufferedReader(new FileReader(init));

            String line = reader.readLine();
            while(!line.equals("%")) {
                line = reader.readLine();
            }

            line = reader.readLine();
            while(line != null) {
                int selectedBroker = hashString(line);
                if(selectedBroker == BROKERID) {
                    Topic t = new Topic(line, IP, PORT);
                    topics.add(t);
                    ownedTopics.add(t);
                }
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
      This method is used in the BrokerHandler to update the internal arraylist which holds the broker's
      topics with the topic's stored in the other Broker's so that every Broker knows all the Topics 
      available
    */
    public static void updateTopics(ArrayList<Topic> otherTopics) {
        synchronized(Broker.topics) {

            for(int i = 0; i < otherTopics.size(); i++) {
                boolean exists = false;
                for(int j = 0; j < Broker.topics.size(); j++) {
                    if(otherTopics.get(i).getName().equals(Broker.topics.get(j).getName())) {
                        exists = true;
                    }
                }
                if(!exists) {
                    Broker.topics.add(otherTopics.get(i));
                }
            }
        }
    }
	
    /*
      This method is where the broker opens the server port and accepts connections
      It opens the BrokerHandler thread which used for updating the other Brokers for the topics it's responsible for 
      and also learn about the other Broker's topics.
      Secondly it accepts connections from Publishers and Consumers and then opens a thread(ConsumerHandler or PublisherHandler) to 
      execute the apropriate operations
    */
	public void startServer() {
        try {
            ServerSocket serverSoc= new ServerSocket(PORT);
            this.serverSocket=serverSoc;
            System.out.println("Server started");
            
            BrokerHandler brokerHandler = new BrokerHandler();
            Thread t = new Thread(brokerHandler);
            t.start();
            
            while (!serverSocket.isClosed()) {
              
                Socket socket = serverSocket.accept();
                
                BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
               
                String flag = read.readLine();//Sent by the client to determine if he is a Consumer or a Publisher

                if(flag.equals("1")) {
                    ConsumerHandler c = new ConsumerHandler(socket, topics);
                    Thread th = new Thread(c);
                    th.start();
                }
                else {
                    PublisherHandler p = new PublisherHandler(socket);
                    Thread th = new Thread(p);
                    th.start();
                }
                
            }
            
        } catch (IOException e) {
            System.out.println("main prob");
            closeServerSocket();
        }
    }


    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
      This method is used to store each Message(either text or file) which is sent to a Topic the Broker
      is responsible for.
    */
    public static void updateTopicHistory(String topicName,Message[] message) {
        synchronized(topicsHistory) {
            for(Topic t : topics) {
                if(t.getName().equals(topicName)) {
                    topicsHistory.get(t.getName()).add(message);
                }
            }
        }
    }

	public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        
        Broker server1 = new Broker(id);
        server1.startServer();
    }
	
}
