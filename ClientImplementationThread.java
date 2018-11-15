/** ***************************
 *XX XX - XXX
 * CIST 2372-60273
 * Mini Project: Chat-Program, Client+Server chat program
 * This project implements a full chat server and client that can be used to send messages 
 * back and forth using sockets, all wrapped up in a nice gui
 * ClientImplementationThread.java - Provides the Main Server logic for each client that is connected
 * Copyright (C) 2018XX XX
 **************************** */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientImplementationThread extends Thread {

	private ClientGui gui;
	private Socket socket;
	private PrintStream printStream;
	private BufferedReader reader;

	private String userName;
	public void setUserName(String text){
		userName = text;
	}

	private boolean run = true;

    public ClientImplementationThread(ClientGui clientGui) {
        gui = clientGui;
    }
	
	//recieve a message from the server
	public void receiveMessage(String text){
		//since we broadcast ALL messages to EVERY client, ignore if we sent it
		if(!text.startsWith(userName)){
			//update the GUI
			gui.addMessage(text);
		}
	}
	
	public void Stop() {
		run = false; 
		try{socket.close();} catch(IOException e){}catch(NullPointerException e){}
	}

	//send a message to the server
	public void sendMessage(String text){
		if(printStream != null){
			printStream.println(text);
		}
	}
 
    public void run() {
        try {
			//Connect to the server on port 7777
			socket = new Socket("localhost", 7777);
			//Setup a stream to the output of our server so we can send data
			printStream = new PrintStream(socket.getOutputStream());
	
			//Create a buffered reader so we can easily read and parse the client input
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//Loop forever and keep asking for new input to send to the server
			while(run){				
				String string = reader.readLine();
				if(string.contains("/quit")){break;}
				receiveMessage(string);
			}
			//Close our connection
			socket.close();
		}
		catch (Exception e) {
			System.out.println("Error in socketSetup()");
		}
    }
}