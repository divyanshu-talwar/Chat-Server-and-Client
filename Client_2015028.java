import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Client_2015028 implements Runnable{
  private static Socket clientSocket = null;
  private static BufferedReader inputStream = null;
  private static PrintWriter outputStream = null;
  private static boolean exit = false;

  public void run(){
    try{
        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
        if (clientSocket != null && outputStream != null && inputStream != null) {   
            String inputLine;         
            while ((inputLine = inputStream.readLine()) != null ) {
                System.out.println(inputLine);
                if (inputLine.toLowerCase().contains("quit") || exit) {
                  break;
                }
            }
        }
    }
    catch (IOException e) {
    }

  }
  public void stop(){
    exit = true;
  }

  public static void main(String[] args) {

    InputStreamReader stdin = new InputStreamReader(System.in);    
    BufferedReader stdinReader = new BufferedReader(stdin);

    String hostName = "localhost";
    Integer portNumber = 2048;
    String username = null;
    System.out.println("Enter the Host Name : ");
    try{
	    hostName = stdinReader.readLine();
	    System.out.println("Enter the Port Number : ");
	    portNumber = Integer.parseInt(stdinReader.readLine());
      System.out.println("Enter the Username : ");
      username = stdinReader.readLine();
    }
    catch(Exception e){
    	e.printStackTrace();
    	System.out.println(e);
    }

    try {
      clientSocket = new Socket(hostName, portNumber);
      inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
      System.err.println(e);
    }
    catch (IOException e) {
      e.printStackTrace();
      System.err.println(e);
    }

    if (clientSocket != null && outputStream != null && inputStream != null) {
      try {
        System.out.println("Client running... \nType 'Quit' to quit.");
        outputStream.println(username);
        String inputLine;
        Client_2015028 c = new Client_2015028();
        Thread t = new Thread(c);
        t.start();
        while(true){
            inputLine = stdinReader.readLine();            
            outputStream.println(inputLine);
            if (inputLine.toLowerCase().contains("quit")) {
                  break;
            }          
        }
        c.stop();
        outputStream.close();
        inputStream.close();
        clientSocket.close();
      }
      catch (UnknownHostException e) {
        e.printStackTrace();
        System.out.println(e);
      }
      catch (IOException e) {
        e.printStackTrace();
        System.out.println(e);
      }
    }
  }
}