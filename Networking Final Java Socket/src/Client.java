/*
 * Password is "password"
 */



import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Client {
    Connect connect = new Connect();
    Socket socket;
    BufferedReader read;
    PrintWriter output;
    private DatagramSocket socket1 = null;
    private FileEvent event = null;
    private String sourceFilePath = "src/theFile.txt";
    private String destinationPath = "src/download/";
    private String hostName = "localHost";

    public void startClient() throws UnknownHostException, IOException{
        //Create socket connection
    	socket = new Socket(connect.gethostName(), connect.getPort());

        //create printwriter for sending login to server
    	output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        //prompt for password
        String password = JOptionPane.showInputDialog(null, "Enter The Designated Password");

        //send password to server
        output.println(password);
        output.flush();

        //create Buffered reader for reading response from server
        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //read response from server
        String response = read.readLine();
        System.out.println("This is the response: " + response);
        JOptionPane.showMessageDialog(null, response);   
        		
        //display response
        socket1 = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(hostName);
        byte[] incomingData = new byte[1024];
        event = getFileEvent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(event);
        byte[] data = outputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);
        socket1.send(sendPacket);
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        socket1.receive(incomingPacket);
        System.out.println("OK!!!: theFile.txt is successfully saved into src/download/");
        String response1= " Shutting Down";
		System.out.println("Response from server:" + response1);
              
        
    	try {
			Thread.sleep(2000);
		} 
    	catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.exit(0);	
    }

    public void fileInfo(){

    }


    	
    	
   public FileEvent getFileEvent() {
    	FileEvent fileEvent = new FileEvent();
    	String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
    	String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
    	fileEvent.setDestinationDirectory(destinationPath);
    	fileEvent.setFilename(fileName);
    	fileEvent.setSourceDirectory(sourceFilePath);
    	File file = new File(sourceFilePath);
    	if (file.isFile()) {
    		try {
    			DataInputStream diStream = new DataInputStream(new FileInputStream(file));
    			long len = (int) file.length();
    			byte[] fileBytes = new byte[(int) len];
    			int read = 0;
    			int numRead = 0;
    			while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
    				read = read + numRead;
    			}
    			fileEvent.setFileSize(len);
    			fileEvent.setFileData(fileBytes);
    			fileEvent.setStatus("Success");
    		} catch (Exception e) {
    			e.printStackTrace();
    			fileEvent.setStatus("Error");
    		}
    	} 
    	else {
    			System.out.println("path specified is not pointing to a file");
    			fileEvent.setStatus("Error");
    	}
    	return fileEvent;
    }
   
    public static void main(String args[]){
        Client client = new Client();
        try {
            client.startClient();
        } 
        catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();    
        }
        
    }
}