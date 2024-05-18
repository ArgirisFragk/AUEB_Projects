package Event_Delivery_System;
import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = -1L;
    private String text;
    private MultimediaFile multimediaFile;
    private int numOfChunks;
    private Boolean isText;

    public Message() {

    }

    public void setMessageValue(String text) {
        this.text = text;
        this.isText = true;
    }

    public void setMessageValue(MultimediaFile multimediaFile,int numOfChunks) {
        this.multimediaFile = multimediaFile;
        this.isText = false;
        this.numOfChunks = numOfChunks;
    }

    public Boolean getMessageType() {
        return this.isText;
    }

    public String getTextValue() {
        return this.text;
    }

    public MultimediaFile getFileValue() {
        return this.multimediaFile;
    }

    public void setFileText(String text) {this.text = text;}

    public String getFileText() {return this.text;}

    public int getChunks() {
        return this.numOfChunks;
    }

}
