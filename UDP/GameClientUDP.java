																/* 	Danelle Modeste
																	813117885
																	COMP3150 ASSIGNMENT 2 */

//Assumptions made (based on what was mentioned by lecturer in class)
// Client only has a maximum of two chances to answer a question			

import java.io.*;
import java.net.*;

class GameClientUDP{

    public static void main(String[] args) throws Exception{
		try{
			//String variables that store client and server responses
			String clientResp,serverResp;
			int port=6789;
			InetAddress IPAddress = InetAddress.getLocalHost();; //Ip address of client
			byte[] recData = new byte[512]; //byte array to store client response
			byte[] sendData  = new byte[512]; //byte array to store server response
			DatagramPacket recPacket,sendPacket;
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); //Gets input from user
			
			DatagramSocket clientSocket = new DatagramSocket();//Create Client socket 

			System.out.println("\nPlease Enter 'START' to start playing or any other key to exit");
			System.out.println("\nRemember 10pts for each question correct on first try but only 5pts on the second try\n\n");
			
			clientResp = inFromUser.readLine();//Stores user input as client response
			sendData=clientResp.getBytes();
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);//Transfers client response to server

			recPacket = new DatagramPacket(recData, recData.length); //Creates a datagram packet  for receiving server data
			clientSocket.receive(recPacket);// receives server data	
			serverResp = new String(recPacket.getData()).trim();//Retrieves server response and stores as string		
			
			System.out.println('\n');
			System.out.println(serverResp);//Outputs server response to screen for user viewing
			
			if(!serverResp.contains("Sorry, your IP address is blocked")){
				//while game is not finished read server responses and respond
				while(!serverResp.contains("Your Score is")){
					
					clientResp =inFromUser.readLine();
					sendData=clientResp.getBytes();
					IPAddress=recPacket.getAddress();
					port=recPacket.getPort();
					
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					clientSocket.send(sendPacket);

					recData = new byte[512];		
					recPacket = new DatagramPacket(recData, recData.length);	
					clientSocket.receive(recPacket);// receives server data	
					serverResp = new String(recPacket.getData()).trim();//Retrieves server response and stores as string	
					
					System.out.println('\n');
					System.out.println(serverResp);
				}
			}
			clientSocket.close();//Closes connection to server
		}
		catch(Exception e){
            e.printStackTrace();
        }
    }
}