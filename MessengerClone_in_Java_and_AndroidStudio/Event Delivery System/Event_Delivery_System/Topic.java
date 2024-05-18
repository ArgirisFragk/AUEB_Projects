package Event_Delivery_System;
import java.io.Serializable;
import java.util.ArrayList;

public class Topic implements Serializable {
    private static final long serialVersionUID = -1L;
    private String name;
    private String IP;
    private int port;
    private ArrayList<ProfileName> profiles;

    public Topic(String name, String IP, int port) {
        this.name = name;
        this.IP = IP;
        this.port = port;
        this.profiles = new ArrayList<ProfileName>();
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public ArrayList<ProfileName> getRegisteredProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
