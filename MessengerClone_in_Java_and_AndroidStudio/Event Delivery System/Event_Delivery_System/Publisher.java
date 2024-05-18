package Event_Delivery_System;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Publisher  extends Thread implements Serializable {
    private static final long serialVersionUID = -1L;
	private final ProfileName profileName;
	private Socket socket;
    private ObjectOutputStream out;
    private int int_chunks;
	
	public Publisher(ProfileName profileName) {    
        this.profileName = profileName;
    }

    /*
     This method converts the file to chunks of 512KB so that it can be sent to the Broker
    */
    private ArrayList<MultimediaFile> convertFile(byte[] fileBytes) {
        
        MultimediaFile multimediaFile = new MultimediaFile();

        multimediaFile.setChunk(fileBytes);

        byte[] file = multimediaFile.getMultimediaFile();
        ArrayList<MultimediaFile> chunks = new ArrayList<>(); 

        int leftovers = file.length % 524288; //chunk size 512KB
        int numberOfChunks = Math.floorDiv(file.length, 524288); //number of chunks we will need
        int_chunks  = numberOfChunks + 1;
        int i = 0;
        while(i < file.length) {
            if(numberOfChunks == 0) { //the file may have a last chunk which size is not 512KB
                byte[] lastChunk = new byte[leftovers];
                for(int j = 0; j < leftovers; j++) {
                    lastChunk[j] = file[i];
                    i++;
                }
                MultimediaFile tmp = new MultimediaFile();
                tmp.setChunk(lastChunk);
                chunks.add(tmp);
                break;
            }
            else {
                byte[] chunk = new byte[524288];//array which containg the bytes of the chunk
                for(int j = 0; j < 524288; j++) {
                    chunk[j] = file[i]; 
                    i++;
                }
                numberOfChunks--;
                MultimediaFile temp = new MultimediaFile();
                temp.setChunk(chunk);
                chunks.add(temp);
                }
        }
        return chunks;
    }

    //This method is used to modify the text the user gave us to the appropriate form
	private String sendText(String message) {
        Scanner scanner = new Scanner(System.in);
        String myname =this.profileName.getActiveTopic().getName() + "|" + this.profileName.getName()+"|";
        String teliko_mhnuma=myname+message;                                                                        
        return teliko_mhnuma;
        
    }

    //This method is used to select the topic the user wants to write
    private void selectActiveTopic() {
        System.out.println("CHOOSE WHICH TOPIC TO WRITE : ");
        for(int i = 0; i < profileName.getRegisteredConversasion().size(); i++) {
            System.out.println(i + ": " + profileName.getRegisteredConversasion().get(i).getName());
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while(true) {
            choice = scanner.nextInt();

            if(choice >= 0 && choice < profileName.getRegisteredConversasion().size()) {
                break;
            }
            System.out.println("Wrong Input! Please try again.");
        }

        this.profileName.setActiveTopic(profileName.getRegisteredConversasion().get(choice));
    }

    /*
     This method is used to send the Messages the user wants to the Broker 
     The Messages can either contain text of file chunks depending on what the user chose
    */
    private void Send(ObjectOutputStream out,int x,String message,byte[] fileBytes,String fileExtension) {
        int typeOfMessage = x;
        if(typeOfMessage == 1) { //type 1 is text
            Message m = new Message();
            m.setMessageValue(sendText(message));
            try {
                out.writeObject("Ready to send message");
                out.flush();
                out.writeObject(m);
                out.flush();
                out.writeObject(profileName.getActiveTopic().getName());
                out.flush();
                out.writeObject(profileName);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(typeOfMessage == 2) //type 2 is file
        {
            ArrayList<MultimediaFile> file = convertFile(fileBytes);
            for(int i = 0; i < file.size() + 1; i++) {
                Message m = new Message();
                if(i == file.size()) {
                    m.setMessageValue(fileExtension);
                }
                else {
                    m.setMessageValue(file.get(i), int_chunks);//we send each chunk seperately
                    m.setFileText(profileName.getActiveTopic().getName() + "|" + profileName.getName());
                }
                try {
                    out.writeObject("Ready to send message");
                    out.flush();
                    out.writeObject(m);
                    out.flush();
                    out.writeObject(profileName.getActiveTopic().getName());
                    out.flush();
                    out.writeObject(profileName);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        else if(typeOfMessage == 3)//type 3 is for exiting the topic
        {
            try {
                out.writeObject("exit");
                out.flush();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     This method is used when we register to another topic and we want to get the history of the topic we registed
    */
    private void syncHistory(ObjectOutputStream out) {
        Message m = new Message();
        m.setMessageValue("SYNC");
        try {
            out.writeObject(m);
            out.flush();
            out.writeObject(profileName.getActiveTopic().getName());
            out.flush();
            out.writeObject(profileName);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyActiveTopic(ObjectOutputStream out) {
        
        try {
            out.writeObject("Active Topic");
            out.flush();
            out.writeObject(profileName.getName());
            out.flush();
            out.writeObject(profileName.getActiveTopic().getName());
            out.flush();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
    }


    public void initConv(Topic activeTopic) {
        try {
            socket = new Socket(activeTopic.getIP(),activeTopic.getPort());


            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            wr.flush();
            wr.write("0"); //auto stelnetai ston broker gia na kserei ti na emfanisei
            wr.newLine();
            wr.flush();

            //This is used for synchronization between the opening of object streams
            String openStream = rd.readLine();
            if(openStream.equals("open outputstream")) {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                wr.flush();
            }

            notifyActiveTopic(out);

            syncHistory(out);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        Send(out,1,message,null,null);
    }

    public void sendMessage(byte[] fileBytes,String fileExtension) {
        Send(out,2,"",fileBytes,fileExtension);
    }

    public void sendMessage(boolean exit) {
        Send(out,3,null,null,null);
    }

}
