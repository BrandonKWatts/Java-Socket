/*
 * Password is "password"
 */

import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serversocket;
    Socket client;
    int bytesRead;
    Connect c = new Connect();
    BufferedReader input;
    PrintWriter output;
    private DatagramSocket socket = null;
    private FileEvent fileEvent = null;

    public boolean start() throws IOException{
        System.out.println("Connection Starting on port:" + c.getPort());
        //make connection to client on port specified
        
        serversocket = new ServerSocket(c.getPort());

        //accept connection from client
        client = serversocket.accept();

        System.out.println("Waiting for connection from client");

        try {
            if(logInfo()){
            	return true;
            }
            
        } 
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean logInfo() throws Exception{
        //open buffered reader for reading data from client
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        boolean login = false;
        String password = input.readLine();
        
        //open printwriter for writing data to client
        output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
        System.out.println("SERVER SIDE " + password);
    
        if(password.equals(c.getPassword())){
        	login = true;
       
        }
        else{
        	output.println("Login Failed, Abort!!!");
        	login = false;
        }

        
        
        output.flush();
        output.close();
        return login;
    }
    
    public static void main(String[] args){
        Server server = new Server();
        try {
            if(server.start()){
            	server.createAndListenSocket();
            }
        } 
        catch (IOException e) {
        	e.printStackTrace();
        }
    } 
    public void createAndListenSocket() {
    	try {
    		socket = new DatagramSocket(9876);
    		byte[] incomingData = new byte[1024 * 1000 * 50];
    		while (true) {
    			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
    			socket.receive(incomingPacket);
    			byte[] data = incomingPacket.getData();
    			ByteArrayInputStream in = new ByteArrayInputStream(data);
    			ObjectInputStream is = new ObjectInputStream(in);
    			fileEvent = (FileEvent) is.readObject();
    			if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
    				System.out.println("Some issue happened while packing the data @ client side");
    				System.exit(0);
    			}
    			createAndWriteFile(); // writing the file to hard disk
    			InetAddress IPAddress = incomingPacket.getAddress();
    			int port = incomingPacket.getPort();
    			String reply = "Thank you for the message";
    			byte[] replyBytea = reply.getBytes();
    			DatagramPacket replyPacket =
    					new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
    			socket.send(replyPacket);
    			Thread.sleep(3000);
    			System.exit(0);

    		}

    	} 
    	catch (SocketException e) {
    		e.printStackTrace();
    	} 
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	} 
    	catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

    public void createAndWriteFile() {
    	String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
    	if (!new File(fileEvent.getDestinationDirectory()).exists()) {
    		new File(fileEvent.getDestinationDirectory()).mkdirs();
    	}
    	File dstFile = new File(outputFile);
    	FileOutputStream fileOutputStream = null;
    	try {
    		fileOutputStream = new FileOutputStream(dstFile);
    		fileOutputStream.write(fileEvent.getFileData());
    		fileOutputStream.flush();
    		fileOutputStream.close();
    		

    	} 
    	catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}

    }

    	
}