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

public class Channel {

    private String name;
    public String getName(){
        return name;
    }

    private ArrayList<ServerImplementationThread> joinedClients = new ArrayList<ServerImplementationThread>();
    public ArrayList<ServerImplementationThread> getClients(){
        return joinedClients;
    }
    
    public Channel(){
       new Channel("NULL");
    }

    public Channel(String _name){
        name = _name;
    }

    public int getUserCount(){
        return joinedClients.size();
    }

    public boolean addUser(ServerImplementationThread client){
        joinedClients.add(client);
        return true;
    }

    public void removeUser(ServerImplementationThread client){
        joinedClients.remove(client);
    }

    //Used by the commandHandler to get a list of all users
	public String getUsersToString(){
		String temp ="";
		for(ServerImplementationThread client : joinedClients){
			temp += client.getUserName()+", ";
		}
		return temp;
	}
}