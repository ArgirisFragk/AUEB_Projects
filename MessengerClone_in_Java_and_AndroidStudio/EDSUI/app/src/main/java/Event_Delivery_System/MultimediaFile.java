package Event_Delivery_System;
import java.io.*;


public class MultimediaFile implements Serializable {
	private static final long serialVersionUID = -1L;
	private String multimediaFileName;
	private String profileName;
	private String dateCreated;
	private String length;
	private String framerate;
	private String frameWidth;
	private String frameHeight;
	private byte[] multimediaFileChunk;
	private String fileName;

	
	public MultimediaFile() {
		multimediaFileChunk = new byte[0];
	}
	
	//This method reads the file and return the number of chunks that are needed for the file
	public int readFile(File file) {
		System.out.println(file.getName());
		try {
			FileInputStream input = new FileInputStream(file);
			multimediaFileChunk = new byte[(int) file.length()]; 
			input.read(multimediaFileChunk);
			input.close();

			int leftovers = multimediaFileChunk.length % 524288;
        	int numberOfChunks = Math.floorDiv(multimediaFileChunk.length, 524288);
			if(leftovers == 0) {
				return numberOfChunks; 
			}
			return numberOfChunks + 1; 

		} catch (IOException e) {
			return 0;
		}
	}

	//This method is for creating a MultimediaFile containing the chunk of a File
	public void setChunk(byte[] chunk) {
		if(multimediaFileChunk.length == 0) {
			multimediaFileChunk = chunk;
		}
		else {
			byte[] combined = new byte[multimediaFileChunk.length + chunk.length];
			System.arraycopy(multimediaFileChunk, 0, combined, 0, multimediaFileChunk.length);
			System.arraycopy(chunk,0,combined,multimediaFileChunk.length,chunk.length);
			multimediaFileChunk = combined;
		}
	}

	//Returns the stored bytes in the MultimediaFile
	public byte[] getMultimediaFile() {
		return multimediaFileChunk;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}
}
