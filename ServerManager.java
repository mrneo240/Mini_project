/** ***************************
 *XX XX - XXX
 * CIST 2372-60273
 * Mini Project: Chat-Program, Client+Server chat program
 * This project implements a full chat server and client that can be used to send messages 
 * back and forth using sockets, all wrapped up in a nice gui
 * ServerManager.java - Provides the Main Server that will handle new connections from clients
 * Copyright (C) 2018XX XX
 **************************** */

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerManager extends Thread {
	
	private ServerGui gui;
	private ServerSocket socketServ;
	private ArrayList<ServerImplementationThread> clientThreads = new ArrayList<ServerImplementationThread>();
	private ChannelManager channelManager;
	public ChannelManager getChannelManager(){
		return channelManager;
	}
	
	private boolean run = true;
		
    public ServerManager(ServerGui serverGui) {
		gui = serverGui;
		channelManager = new ChannelManager();
	}

	public Channel moveUserToChannel(String name, ServerImplementationThread client){
		Channel channel = channelManager.joinChannel(name, client);
		return channel;
	}
	public Channel moveUserToChannel(Channel channel, ServerImplementationThread client){
		channelManager.joinChannel(channel, client);
		return channel;
	}
	
	//shorthand for Below
	public void sendMessageToAllClients(String text){
		sendMessageToAllClients(text, true);
	}
	
	//sends a message to all connected clients
	public void sendMessageToAllClients(String text, boolean fromServer){
		for(ServerImplementationThread client : clientThreads){
			client.sendMessageToClient(text, fromServer);
		}
	}
	//shorthand for Below
	public void sendMessageToAllClientsInChannel(String text, Channel channel){
		sendMessageToAllClientsInChannel(text, true, channel);
	}
	
	//sends a message to all connected clients
	public void sendMessageToAllClientsInChannel(String text, boolean fromServer, Channel channel){
		for(ServerImplementationThread client : channel.getClients()){
			client.sendMessageToClient(text, fromServer);
		}
	}
	
	//Loop through and stop all clientThreads
	public void killAll(){
		for(ServerImplementationThread client : clientThreads){
			client.Stop();
		}
		Stop();
	}

	//remove a specific clientThread, used when disconnecting for any reason
	public void removeThread(ServerImplementationThread thread){
		clientThreads.remove(thread);
	}

	//Used by the commandHandler to get a list of all users
	public String cmd_Users(){
		String temp ="";
		for(ServerImplementationThread client : clientThreads){
			temp += client.getUserName()+", ";
		}
		return temp;
	}
	
	//Signals this thread will stop and closed
	public void Stop() {
		run = false; 
		try{socketServ.close();} catch(IOException e){}
	}
 
	//All of our Main Server logic is here
    public void run() {

		//Server Vars
		Socket socket;
		int portNum = 7777;
		
		//Try to start the server here
		try {
			//Open a new server on the port specified
			socketServ = new ServerSocket(portNum);
			gui.addMessage(String.format("Server started on port %d",portNum));
			
			//Loop Forever while handling multiple clients and quitting when needed
			while(run){
				//Let the client connect
				socket = socketServ.accept();
				//Create a new thread to handle all input between user and server
				ServerImplementationThread thread = new ServerImplementationThread(socket, gui);
				clientThreads.add(thread);
				thread.start();
			}
		}
		catch (Exception e) {
			gui.addMessage("Error in socketSetup()");
		}
	}
}