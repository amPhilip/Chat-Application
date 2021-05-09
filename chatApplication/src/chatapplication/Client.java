/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatapplication;

import java.net.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author favored
 */
public class Client 
{
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private clientApplication clientApp;
    
    private String server, chatname;
    private int port;
    
    Client(String server, int port, String chatname) 
    {
        this(server, port, chatname, null);
    }
    
    Client(String server, int port, String chatname, clientApplication clientApp) 
    {
        this.server = server;
        this.port = port;
        this.chatname = chatname;
        this.clientApp = clientApp;
    }
    
     public boolean start() 
     {
        try 
        {
         socket = new Socket(server, port);
        } 
        catch(Exception ec) 
        {
         display("Error connectiong to server:" + ec);
         return false;
        }  
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        try
        {
         sInput  = new ObjectInputStream(socket.getInputStream());
         sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) 
        {
         display("Exception creating new Input/output Streams: " + eIO);
         return false;
        }
        new ListenFromServer().start();
        try
        {
         sOutput.writeObject(chatname);
        }
        catch (IOException eIO) 
        {
         display("Exception doing login : " + eIO);
         disconnect();
         return false;
        }
        return true;
 }
     
      private void display(String msg) 
        {
          clientApp.append(msg + "\n");
        }
      
      void sendMessage(ChatMessage msg) 
        {
         try 
         {
          sOutput.writeObject(msg);
         }
         catch(IOException e) 
         {
          display("Error occured while writing to server: " + e);
         }
        }
      
      private void disconnect() 
      {
        try 
        { 
         if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try 
        {
         if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try
        {
            if(socket != null) socket.close();
        }
        catch(Exception e) {}
        if(clientApp != null)
        clientApp.connectionFailed();
    }
      
   class ListenFromServer extends Thread 
    {
     public void run() 
     {
      while(true) 
      {
       try 
       {
        String msg = (String) sInput.readObject();
        clientApp.append(msg);
       }
       catch(IOException e) 
       {
        display("Server has closed the connection: " + e);
        if(clientApp != null) 
        {
           clientApp.connectionFailed();
            break; 
        }         
       }
       // can't happen with a String object but need the catch anyhow
       catch(ClassNotFoundException e2) 
       {
       }
      }
     }
    }
   
   
}
