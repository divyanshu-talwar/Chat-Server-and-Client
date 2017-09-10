import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class ClientHandler implements Runnable{
	public Socket clientSocket = null;
	public Integer clientId = 0;


	ClientHandler(Socket socket, Integer id){
		this.clientSocket = socket;
		this.clientId = id;
	}

	public void run(){
		BufferedReader inputStream = null;
    	PrintWriter outputStream = null;
    	try{
	        while (true){
		      inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		      outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
		      String line = null;
	          line = inputStream.readLine();
	          Server_2015028 server = new Server_2015028();
	          if(line == null){
	          	  line = "Client " + clientId.toString() + " quit the service!";
	          	  System.out.println(line);
	          	  server.disconnected(clientId);
	          	  break;
	          }
	          else{
	            System.out.println("Recieved from Client " + clientId.toString() + " : " + line);
	            String[] words = line.split(" ");
	            if(words.length > 1 && ( words[0].toLowerCase().equals("all") || words[0].toLowerCase().equals("client") || words[0].toLowerCase().equals("server"))){
	            	String phrase = line.split(":")[1];
	            	String message = "Recieved from Client " + clientId.toString() + " : " + phrase;
	            	if(words[0].toLowerCase().equals("client")){
	            		String[] sendTo = words[1].split(",");
	            		int size = sendTo.length;
	            		sendTo[size -1] = sendTo[size -1].replace(":","");
	            		Integer[] recepients = new Integer[size];
	            		for( int i = 0; i< size; i++){
	            			recepients[i] = Integer.parseInt(sendTo[i].trim());
	            		}
	            		server.messageClientList(clientId, recepients, message);
	            	}
	            	else if(words[0].toLowerCase().equals("all")){
	            		server.messageAll(clientId, message);
	            	}
	            	else{
	            		if(phrase.toLowerCase().equals(" list all")){
	            			server.listAll(clientId);
	            		}
	            		else{	
	            			outputStream.println("Recieved from Server(echo) : " + line);
	            		}
	            	}
	            }
	            else{
	            	outputStream.println("Recieved from Server : " + line);
	            }
	          }
	        }
	        outputStream.close();
	        inputStream.close();
	        clientSocket.close();
    	}
    	catch(IOException e){
    	}
	}
}
public class Server_2015028 {
	private static ArrayList<Socket> connectedUsers = new ArrayList<Socket>();
    private static HashMap<String, Integer> unameId = new HashMap<String, Integer>();

    public static void disconnected(Integer id){
    	connectedUsers.set(id -1, null);
    }
    public static void listAll(Integer sid){
    	Socket clientSocket = connectedUsers.get(sid -1);
    	try{
	    	PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
	    	Integer size = unameId.size();
	    	outputStream.println("ID 		Username");
	    	for(Map.Entry<String, Integer> entry : unameId.entrySet()){
	    		String username = entry.getKey();
	    		Integer id = entry.getValue();
	    		if(connectedUsers.get(id -1) == null){
	    			continue;
	    		}
	    		else{
	    			outputStream.println(id.toString() + " 		" + username);
	    		}
	    	}
	    	outputStream.println("------- end -------\r");
    	}
    	catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
    }
    public static void messageSpecificClient(Integer sid, Integer did, String message){
    	Socket sourceSocket = connectedUsers.get(sid -1);
    	try{
	    	PrintWriter sourceOutputStream = new PrintWriter(sourceSocket.getOutputStream(), true);
	    	Integer maxClients = connectedUsers.size();
	    	if((did - 1) >= maxClients || connectedUsers.get(did - 1) == null){
	    		sourceOutputStream.println("Client : " + did.toString() + " does not exist or is not available!!");
	    	}
	    	else{	    		
		    	Socket clientSocket = connectedUsers.get(did -1);
		    	PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
		    	outputStream.println(message);
	    	}
    	}
    	catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
    }
    public static void messageClientList(Integer sid, Integer[] recepients, String message){
    	Integer size = recepients.length;
    	for(int i = 0; i< size; i++){
    		messageSpecificClient(sid, recepients[i], message);
    	}
    }
    public static void messageAll(Integer sid, String message){
    	Integer size = unameId.size();
    	ArrayList<Integer> sendTo = new ArrayList<Integer>();
    	for(Map.Entry<String, Integer> entry : unameId.entrySet()){
    		String username = entry.getKey();
    		Integer id = entry.getValue();
    		if(connectedUsers.get(id -1) == null || id == sid){
    			continue;
    		}
    		else{
    			sendTo.add(id);
    		}
    	}
    	Integer[] recepients = new Integer[sendTo.size()];
    	recepients = sendTo.toArray(recepients);
    	messageClientList(sid, recepients, message);
    }
    
  public static void main(String args[]) {
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    Integer PortNumber = 2048;
    Integer clientId = 0;

    try{
      serverSocket = new ServerSocket(PortNumber);
    }
    catch (IOException e) {
      e.printStackTrace();
      System.out.println(e);
    }

    System.out.println("Server running at port: " + PortNumber.toString() + ". Press ctrl + C to stop the service");
    while(true){    	
		try {
			clientSocket = serverSocket.accept();
	    	BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    	String username = inputStream.readLine();
	    	ClientHandler c = null;
	    	if(unameId.containsKey(username)){
	    		Integer id = unameId.get(username);
	    		connectedUsers.set(id - 1, clientSocket);
				System.out.println("Client number " + id.toString() + " connected!");
				c = new ClientHandler(clientSocket, id);
	    	}
	    	else{
				clientId += 1;
				unameId.put(username, clientId);
				connectedUsers.add(clientSocket);
				System.out.println("Client number " + clientId.toString() + " connected!");
				c = new ClientHandler(clientSocket, clientId);
	    	}
			Thread t = new Thread(c);
			t.start();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
    }
  }
}