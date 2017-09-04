import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Client_2015028 {
  public static void main(String[] args) {

    Socket clientSocket = null;
    BufferedReader inputStream = null;
    PrintWriter outputStream = null;

    InputStreamReader stdin = new InputStreamReader(System.in);    
    BufferedReader stdinReader = new BufferedReader(stdin);

    String hostName = "localhost";
    Integer portNumber = 2048;
    System.out.println("Enter the Host Name : ");
    try{
	    hostName = stdinReader.readLine();
	    System.out.println("Enter the Port Number : ");
	    portNumber = Integer.parseInt(stdinReader.readLine());
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
        String inputLine;
        outputStream.println(stdinReader.readLine());
        while ((inputLine = inputStream.readLine()) != null) {
          System.out.println(inputLine);
          if (inputLine.toLowerCase().contains("quit")) {
            break;
          }
          outputStream.println(stdinReader.readLine());
        }
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