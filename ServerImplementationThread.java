/** ***************************
 *XX XX - XXX
 * CIST 2372-60273
 * Mini Project: Chat-Program, Client+Server chat program
 * This project implements a full chat server and client that can be used to send messages 
 * back and forth using sockets, all wrapped up in a nice gui
 * ServerImplementationThread.java - Provides the Main Server logic for each client that is connected
 * Copyright (C) 2018XX XX
 **************************** */

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.*;

public class ServerImplementationThread extends Thread {

	private Socket socket;
	private ServerGui gui;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Channel currentChannel;
	
	private String userName = "";
	public String getUserName(){
		return userName;
	}

	private boolean run = true;


    public ServerImplementationThread(Socket sock, ServerGui serverGui) {
        socket = sock;
		gui = serverGui;
    }
	
	//shorthand for below
	public void sendMessageToClient(String text){
		sendMessageToClient(text,true);
	}
	
	//Receive a message from the socket and display to user
	public void sendMessageToClient(String text, boolean fromServer){
		if(fromServer){
			writer.println("Server: " + text);
		} else {
			writer.println(text);
		}
	}

	public void changeChannel(Channel channel){
		currentChannel = gui.getServer().moveUserToChannel(channel, this);
		gui.addMessage(String.format("Client[%s] Joined Channel[%s]",userName, channel.getName()));
		writer.println(String.format("Joined #%s! ",channel.getName()));
	}
	
	//Signals this thread will stop and closed
	public void Stop() {
		run = false; 
		try{socket.close();} catch(IOException e){}
	}

	//Extra fun, commands can be executed by clients and could do things
	public void handleCommand(String input){
		//strip slash and then split by whitespace
		input = input.substring(1,input.length());
		String[] commandInput = input.split(" ");
		if(commandInput.length > 0){
			gui.addMessage(String.format("Client[%s] Issued Command: %s",userName, commandInput[0]));
			switch(commandInput[0]){
				case "date":
					writer.println(java.time.LocalDate.now());
					break;
				case "time":
					writer.println(java.time.LocalTime.now());
					break;	
				case "users":
					writer.println("Connected Users: "+gui.getServer().cmd_Users());
					break;
				case "help":
					writer.println("Available Commands:\ndate, time, users, join, help");
					break;
				case "join":
					changeChannel(gui.getServer().getChannelManager().findChannel(commandInput[1]));
				case "quit":
					break;
				default:
					writer.println("Invalid Command!");
			}
		}
	}
 
    public void run() {
        try {
			//Create a buffered reader so we can easily read and parse the client input
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

			//Handle all input from this User
            String string;
            do {
				//Read the string passed to us
				string = reader.readLine();
				//Basic Null Check
				if(string != null){
					//If username is not set, this must be a new connection
					if(userName.length() == 0){
						//Set username on this thread
						userName = string;
						//Welcome to user, on their client, the server, and all other clients
						gui.addMessage(String.format("Client[%s] Connected!",userName));
						writer.println(String.format("Welcome %s! ",userName));
						gui.getServer().sendMessageToAllClients(String.format("%s has Connected to the Server! ",userName));
						changeChannel(gui.getServer().getChannelManager().getDefault());
					} else {
						//This is just a regular message, handle appropriately
						if(string.length() > 0){
							if(string.startsWith("/")){
								handleCommand(string);
								continue;
							}
							//Show the message on the server console, and also broadcast to all clients
							gui.addMessage(String.format("%s: %s",userName,string));
							gui.getServer().sendMessageToAllClientsInChannel(String.format("%s: %s",userName,string), false, currentChannel);
						}
					}
				}
			//Loop forever or until this thread is supposed to stop or client issues "/quit"
			} while ((string != null ? !string.contains("/quit") : false) && run);
			
			//let the server know, and also all clients the user is gone.
			gui.addMessage(String.format("Client[%s] Disconnected!", userName));
			gui.getServer().sendMessageToAllClients(String.format("%s has Disconnected! ",userName));

			//cleanup and close
			gui.getServer().removeThread(this);
            socket.close();
        } catch (IOException ex) {
            gui.addMessage(String.format("Server exception: " + ex.getMessage()));
			gui.addMessage(String.format("Client[%s] Disconnected!", userName));
        }
    }
}