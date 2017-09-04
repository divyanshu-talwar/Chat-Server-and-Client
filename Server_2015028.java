import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

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
	    	inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
	        String line = null;
	        while (true) {
	          line = inputStream.readLine();
	          if(line == null){
	          	  line = "Client " + clientId.toString() + " quit the service!";
	          	  System.out.println(line);
	          	  break;
	          }
	          else{
	            System.out.println("Recieved from Client " + clientId.toString() + " : " + line);
	            String[] words = line.split(" ");
	            int size = words.length;
	            line = "";
	            for(int i = size -1; i >= 0; i--){
	              line += words[i] + " ";
	            }
	            outputStream.println("Recieved from server: " + line);
	          }
	        }
	        outputStream.close();
	        inputStream.close();
	        clientSocket.close();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		System.out.println(e);
    	}
	}
}
public class Server_2015028 {
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
			clientId += 1;
			System.out.println("Client number " + clientId.toString() + " connected!");
			ClientHandler c = new ClientHandler(clientSocket, clientId);
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