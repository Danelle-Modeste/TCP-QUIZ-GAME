										/* 	Danelle Modeste
										813117885
										COMP3150 ASSIGNMENT 1 */

//Assumptions made (based on what was mentioned by lecturer in class) 
//Client only has a maximum of two chances to answer a question			
													
import java.io.*;
import java.net.*;

class GameServerTCP {

	public static void main(String[] args) throws Exception {
		//String variables that store client and server responses as well as track client score card
		String clientAns,serverResp,track,trackAll="";
		//variables keep track of client score and questions answered
		int score=0,count=0,contd=0,ans=0,wrong=0;
		
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
			ServerSocket welcomeSocket = new ServerSocket(6789);//listening socket of server
			System.out.println("Server running....\n");
			
			while(true) {

				Socket connectionSocket = welcomeSocket.accept();//Makes connection with socket
				//Creates buffered reader to read responses from client
				BufferedReader inFromClient =new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				//Creates dataoutput stream to send server responses to client
				DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					
				clientAns = inFromClient.readLine();//Retrieves client response and stores as string
				
				//if value of response is "start" starts the game
				if(clientAns.trim().equalsIgnoreCase("start")){
					serverResp="Type EXIT to quit at any time. Enter ANY key to continue..." + '\n';
					outToClient.writeBytes(serverResp);//Transfers server response to client
					
					clientAns = inFromClient.readLine();
					
					//if value of client response is "exit" ends the game
					if(clientAns.trim().equalsIgnoreCase("exit")){
						contd=1;
					}		
					
					//While contd ==0 i.e user did not respond with "exit" AND
					//All the questions havent been asked. Continue playing game
					while(contd==0 && count<questions.length){
						
						serverResp=questions[count].getQuest() + '\n';//Transfers the question as the servers response to the client
						outToClient.writeBytes(serverResp);
						clientAns = inFromClient.readLine(); //Read client solution
						
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
						
						serverResp=queue[count].getQuest() + '\n';
						outToClient.writeBytes(serverResp);
						
						clientAns = inFromClient.readLine();
						
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
				serverResp="Your Score is " + score +". You Answered "+ ans + " questions correctly." + '\n';
				outToClient.writeBytes(serverResp);
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