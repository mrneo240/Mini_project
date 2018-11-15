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

public class ChannelManager {

    private ArrayList<Channel> channelList;


    public ChannelManager(){
        channelList  = new ArrayList<Channel>();
        Channel main = new Channel("Main");
        Channel special = new Channel("Special");
        channelList.add(main);
        channelList.add(special);
    }

    public Channel getDefault(){
        return channelList.get(0);
    }

    public Channel findChannel(String name){
        Channel temp = null;
        for(Channel channel : channelList){
            if(channel.getName().equalsIgnoreCase(name)){
                temp = channel;
            }
        }
        return temp;
    }

    public Channel joinChannel(String name, ServerImplementationThread client){
        return joinChannel(findChannel(name), client);
    }

    public Channel joinChannel(Channel channel, ServerImplementationThread client){
        for(Channel temp : channelList){
            temp.removeUser(client);
        }
        channel.addUser(client);
        return channel;
    }
    
}