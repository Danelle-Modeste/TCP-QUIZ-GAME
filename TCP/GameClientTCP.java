																/* 	Danelle Modeste
																	813117885
																	COMP3150 ASSIGNMENT 1 */

//Assumptions made (based on what was mentioned by lecturer in class)
// Client only has a maximum of two chances to answer a question			

import java.io.*;
import java.net.*;

class GameClientTCP{

    public static void main(String[] args) throws Exception{
		try{
			//String variables that store client and server responses
			String clientResp,serverResp;
			
			//Buffered reader for reading user input
			BufferedReader inFromUser =	new BufferedReader(new InputStreamReader(System.in));
			InetAddress IPAddress = InetAddress.getLocalHost();//IP ADDRESS OF HOST
			Socket clientSocket = new Socket(IPAddress,6789);//Create Client socket connection
			
			//Creates dataoutput stream to send client responses to server
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			//Creates buffered reader to read responses from server
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			System.out.println("Please Enter 'START' to start playing or any other key to exit");
			System.out.println("\nRemember 10pts for each question correct on first try but only 5pts on the second try\n");
			clientResp = inFromUser.readLine();//Stores user input as client response
			outToServer.writeBytes(clientResp +'\n');//Transfers client response to server

			serverResp = inFromServer.readLine();//Reads server response and stores as string
			System.out.println('\n');
			System.out.println(serverResp);//Outputs server response to screen for user viewing
			
			//while game is not finished read server responses and respond
			while(!serverResp.contains("Your Score is")){
				clientResp = inFromUser.readLine() + '\n';
				outToServer.writeBytes(clientResp);
				
				serverResp = inFromServer.readLine();
				System.out.println('\n');
				System.out.println(serverResp);
			}
			clientSocket.close();//Closes connection to server
		}
		catch(Exception e){
            e.printStackTrace();
        }
    }
}