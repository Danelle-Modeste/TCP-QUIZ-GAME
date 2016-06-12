										/* 	Danelle Modeste
										813117885
										COMP3150 ASSIGNMENT 2 */

//Assumptions made (based on what was mentioned by lecturer in class) 
//Client only has a maximum of two chances to answer a question			

													
import java.io.*;
import java.net.*;
import java.util.*;

class GameServerUDP {

	public static void main(String[] args) throws Exception {
		
		
		BlockedIP blockList= new BlockedIP();// declare blocklist variable
		Question[] questions= new Question[10];//Question array- list of questions
		Question[] queue= new Question[10]; //Secondary queue to add incorrectly answered questions
		
		//	Create question with question,answer and status 
		questions[0]= new Question( "True or False: All apples are red?","false", 0);
		questions[1]= new Question( "True or False: The Earth is flat " , "false" , 0);
		questions[2]= new Question( "True or False: Clouds are made of water " , "true" , 0);
		questions[3]= new Question( "True or False: All Networks are connection-less " , "false" , 0);
		questions[4]= new Question( "What is: 10 + 16 + 23 ?" , "49" , 0);
		questions[5]= new Question( "What is: 5 + 71 ?" , "76" , 0);
		questions[6]= new Question( "What is: ((12 / 3 + 2) - 5 + 1) + 17 ?" , "19" , 0);;
		questions[7]= new Question( "What is: 1 - 26 + (20 * 6 - 5) / 3 ?" , "0" , 0);
		questions[8]= new Question( "What flat shape has three sides?" , "triangle" , 0);
		questions[9]= new Question( "What is a young goat called? " , "kid" , 0);
		
		try{ 			
			DatagramSocket serverSocket = new DatagramSocket(6789);//listening socket of server
			System.out.println("Server running....");
					
			while(true) {
				
				InetAddress IPAddress; //Ip address of client
				byte[] recData = new byte[512]; //byte array to store client response
				byte[] sendData  = new byte[512]; //byte array to store server response
				DatagramPacket recPacket,sendPacket;
				
				//String variables that store client and server responses as well as track client score card
				String clientAns,serverResp,track,trackAll="";
				
				//variables keep track of client score and questions answered and port number
				int port,score=0,count=0,contd=0,ans=0,wrong=0;
	
				recPacket = new DatagramPacket(recData, recData.length); //Creates a datagram packet  for receiving client data
				serverSocket.receive(recPacket);// receives client data
				
/*In order to block an IP address the address to be blocked must be added to the block list using blockList.add(UserInetAdress)
Upon doing this if the user with the given IP address tries to connect they would receive the message
 that they are blocked and there game will be terminated*/

 //Use to test block list functionality by adding incoming user ip to blocked list
//*************	blockList.addToList(recPacket.getAddress());
				
				
				//Checks to see if the incoming user ip is blocked, if so sends response and terminates communication with user
				if(blockList.isBlocked(recPacket.getAddress())){
					serverResp="Sorry, your IP address is blocked";
					sendData=serverResp.getBytes(); //stores server response as bytes in byte array
					IPAddress = recPacket.getAddress(); //gets ip address of client
					port = recPacket.getPort(); //gets port number of client
					sendPacket= new DatagramPacket(sendData,sendData.length,IPAddress,port); //creates datagram packet with server response
					serverSocket.send(sendPacket); //sends datagram packet with server response to client
					continue;
				}
				
				clientAns = new String(recPacket.getData()).trim();//Retrieves client response and stores as string
				
				//if value of response is "start" starts the game
				if(clientAns.trim().equalsIgnoreCase("start")){
					
					serverResp="Type EXIT to quit at any time. Enter ANY key to continue...";
					
					sendData=serverResp.getBytes(); //stores server response as bytes in byte array
					IPAddress = recPacket.getAddress(); //gets ip address of client
					port = recPacket.getPort(); //gets port number of client
					
					sendPacket= new DatagramPacket(sendData,sendData.length,IPAddress,port); //creates datagram packet with server response
					serverSocket.send(sendPacket); //sends datagram packet with server response to client
	
					recData = new byte[512];
					recPacket = new DatagramPacket(recData, recData.length);
					serverSocket.receive(recPacket);
					clientAns = new String(recPacket.getData()).trim();
					System.out.println(clientAns);	
					//if value of client response is "exit" ends the game
					if(clientAns.trim().equalsIgnoreCase("exit")){
						contd=1;
					}		
					
					//While contd ==0 i.e user did not respond with "exit" AND
					//All the questions have not been asked. Continue playing game
					while(contd==0 && count<questions.length){
						
						serverResp=questions[count].getQuest();//Transfers the question as the servers response to the client
						sendData=serverResp.getBytes();
						IPAddress = recPacket.getAddress();
						port = recPacket.getPort();
						
						sendPacket= new DatagramPacket(sendData,sendData.length,IPAddress,port);
						serverSocket.send(sendPacket);
						
						recData = new byte[512];
						recPacket = new DatagramPacket(recData, recData.length);						
						serverSocket.receive(recPacket);
						clientAns = new String(recPacket.getData()).trim(); //Read client solution

						//If client response is exit end the game
						if(clientAns.trim().equalsIgnoreCase("exit")){
							contd=1;
						}
						//If client's response is correct increase their total score
						else if(clientAns.trim().equalsIgnoreCase(questions[count].getAns())){
							score+=10;
							ans+=1;
							questions[count].setStatus(1);
							
							//Outputs the user's score for the question and keeps track of it 
							track="Question Answered Correctly on first try +10pts";
							System.out.println(track);
							trackAll+=track;
						}
						//If client's response is incorrect, add question to queue 
						else{
							queue[wrong]= new Question(questions[count].getQuest(),questions[count].getAns(),0);
							wrong++;
							
							//Outputs the user's score for the question and keeps track of it
							track="Question Answered Incorrectly on first try +0pts" ;
							System.out.println(track);
							trackAll+=track;
						}
						count++;//Increments to next question
					}
					count=0;				
					
					//While contd ==0 i.e user did not respond with "exit" AND
					//All the questions in the queue haven't been asked, continue game.
					while(contd==0 && count<wrong){
						
						serverResp=queue[count].getQuest();
						sendData=serverResp.getBytes();
						IPAddress = recPacket.getAddress();
						port = recPacket.getPort();
						
						sendPacket= new DatagramPacket(sendData,sendData.length,IPAddress,port);
						serverSocket.send(sendPacket);
		
						recData = new byte[512];
						recPacket = new DatagramPacket(recData, recData.length);
						serverSocket.receive(recPacket);
						clientAns = new String(recPacket.getData()).trim();
						
						if(clientAns.trim().equalsIgnoreCase("exit")){
							contd=1;
						}
						else if(clientAns.trim().equalsIgnoreCase(queue[count].getAns())){
							score+=5;
							ans+=1;
							queue[count].setStatus(1);
							
							track="Question Answered Correctly on second try +5pts" ;
							System.out.println(track);
							trackAll+=track;
						}
						else{
							track="Question Answered Incorrectly on second try +0pts " ;
							System.out.println(track);
							trackAll+=track;
						}
						count++;	
					}
				}
				//END OF GAME AS QUESTIONS HAVE BEEN ANSWERED OR EXIT WAS SELECTED
				//Transfer the client's score and number of questions answered in a response to client
				serverResp="Your Score is " + score +". You Answered "+ ans + " questions correctly.";
				sendData=serverResp.getBytes();
				IPAddress = recPacket.getAddress();
				port = recPacket.getPort();
				
				sendPacket= new DatagramPacket(sendData,sendData.length,IPAddress,port);
				serverSocket.send(sendPacket);				
				
				System.out.println("\nClient total score is: " +score);
			}
		}
		catch(Exception e){
            e.printStackTrace();
		}
	}
    
}

//This class defines the structure of a question object
class Question{
	private String question; //question value
	private String answer; //single solution the question
	private int status; // status of if the client answered the question correctly or not
	//Instantiates a question object
	Question(String q,String a,int x){
		question=q;
		answer=a;
		status=x;
	}
	//Accessor methods for attributes of the question object
	public String getQuest(){
		return question;
	}
	public String getAns(){
		return answer;
	}
	public int getStatus(){
		return status;
	}
	public void setStatus(int y){
		status=y;
	}
}

//Block list class
class BlockedIP{
	public ArrayList<InetAddress> blockedIP;
	BlockedIP(){
		blockedIP= new ArrayList<InetAddress>();
	}
	public boolean isBlocked(InetAddress ip){//Checks to see if an InetAddress is blocked
		return(blockedIP.contains(ip));
	}
	public void addToList(InetAddress ip){ //adds an InetAddress to the blocked list
		blockedIP.add(ip);
	}
	public void removeFromList(InetAddress ip){// remove an InetAddress from the blocked list
		blockedIP.remove(ip);
	}
	public InetAddress[] getAllBlocked(){//Returns all addresses in blocked list
		return ((InetAddress[]) blockedIP.toArray());
	}
}