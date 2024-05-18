package Event_Delivery_System;
import java.util.HashMap;
import java.io.Serializable;
import java.util.ArrayList;

public class ProfileName implements Serializable{
	private static final long serialVersionUID = -1L;
	private String profileName;//Name of the profile
	private ArrayList<Topic> subscribedConversations;//The Topics the user is registered in
	private Topic activeTopic;//The topic the user is writing at the moment
	private boolean registerToNewTopic = false;//Determines if the user wants to register to another topic or not(used to synchronize the utilities of Publisher and Consumer)
	
	public ProfileName(String profileName) {
		this.profileName = profileName;
		subscribedConversations = new ArrayList<>();
	}

	public void setRegisterToNewTopic(boolean b) {
		this.registerToNewTopic = b;
	}

	public boolean getRegisterToNewTopic() {
		return this.registerToNewTopic;
	}
	
	public Topic getActiveTopic() {
		return activeTopic;
	}

	public void setActiveTopic(Topic activeTopic) {
		this.activeTopic = activeTopic;
	}

	public String getName() {
		return profileName;
	}

	public void registerToConversasion(Topic t) {
		synchronized(subscribedConversations) {
		subscribedConversations.add(t);
		}
	}

	public ArrayList<Topic> getRegisteredConversasion() {
		synchronized(subscribedConversations) {
			return subscribedConversations;
		}
	}

	public void setRegisteredConversasion(ArrayList<Topic> otherRegisteredConversation) {
		synchronized(subscribedConversations) {
			this.subscribedConversations = otherRegisteredConversation;
		}
	}
}
