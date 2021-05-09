/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatapplication;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 *
 * @author favored
 */
public class Server 
{
 private static int uniqueId;
 private ArrayList<ClientThread> al;
 private serverApplication serverApp;
 private SimpleDateFormat date;
 private int port;
 private boolean keepGoing; 
 
 public Server(int port, serverApplication serverApp) 
 {
  this.serverApp = serverApp;
  this.port = port;
  date = new SimpleDateFormat("HH:mm:ss");
  al = new ArrayList<ClientThread>();
 }
 
  public void start() 
  {
  keepGoing = true;
  try 
  {
   ServerSocket serverSocket = new ServerSocket(port);
   while(keepGoing) 
   {
    display("Server waiting for clients on port " + port + ".");    
    Socket socket = serverSocket.accept();
    if(!keepGoing)
     break;
    ClientThread t = new ClientThread(socket);
    al.add(t);
    t.start();
   }
   try 
   {
    serverSocket.close();
    for(int i = 0; i < al.size(); ++i) 
    {
     ClientThread tc = al.get(i);
     try 
     {
     tc.sInput.close();
     tc.sOutput.close();
     tc.socket.close();
     }
     catch(IOException ioE) 
     {
     }
    }
   }
   catch(Exception e) 
   {
    display("Exception closing the server and clients: " + e);
   }
  }
  // something went bad
  catch (IOException e) 
  {
   String msg = date.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
   display(msg);
  }
 }
  
   protected void stop() 
   {
        keepGoing = false;
        try 
        {
         new Socket("localhost", port);
        }
        catch(Exception e) 
        {
        }
    }
   
   private void display(String msg) 
   {
    String time = date.format(new Date()) + " " + msg;
    if(serverApp == null)
    {
        System.out.println(time);
    }   
    else
    {
        serverApp.appendEvent(time + "\n");
    }
   }
   
   private synchronized void broadcast(String message) 
   {
    String time = date.format(new Date());
    String messageLf = time + " " + message + "\n";
    if(serverApp == null)
    {
        System.out.print(messageLf);
    }     
    else
    {
        serverApp.appendRoom(messageLf);
    }
    for(int i = al.size(); --i >= 0;) 
    {
     ClientThread ct = al.get(i);
     if(!ct.writeMsg(messageLf)) 
     {
      al.remove(i);
      display("Disconnected Client " + ct.chatname + " removed from list.");
     }
    }
   }
   
   synchronized void remove(int id) 
   {
    for(int i = 0; i < al.size(); ++i) 
    {
     ClientThread ct = al.get(i);
     if(ct.id == id) 
     {
      al.remove(i);
      return;
     }
    }
   }
   
    class ClientThread extends Thread 
    { 
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String chatname;
        ChatMessage cm;
        String date;
        
         ClientThread(Socket socket) 
         {
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
             sOutput = new ObjectOutputStream(socket.getOutputStream());
             sInput  = new ObjectInputStream(socket.getInputStream());
             chatname = (String) sInput.readObject();
             display(chatname + " just connected.");
            }
            catch (IOException e) 
            {
             display("Exception creating new Input/output Streams: " + e);
             return;
            }
            catch (ClassNotFoundException e) 
            {
            }
               date = new Date().toString() + "\n";
       }
    
    public void run()
    {
        boolean keepGoing = true;
        while(keepGoing)
            {
                try 
                {
                 cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) 
                {
                 display(chatname + " Exception reading Streams: " + e);
                 break;    
                }
                catch(ClassNotFoundException e2) 
                {
                 break;
                }
            String message = cm.getMessage();
            switch(cm.getType()) 
            {
                case ChatMessage.MESSAGE:
                broadcast(chatname + ": " + message);
                break;
                case ChatMessage.LOGOUT:
                 display(chatname + " disconnected with a LOGOUT message.");
                 keepGoing = false;
                 break;
                case ChatMessage.WHOISIN:
                 writeMsg("List of the users connected at " + new Date() + "\n");
            for(int i = 0; i < al.size(); ++i) 
            {
             ClientThread ct = al.get(i);
             writeMsg((i+1) + ") " + ct.chatname + " since " + ct.date);
            }
            break;
           }
        }
        remove(id);
        close();
    }
    
    private void close() 
    {
        try 
        {
         if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try 
        {
         if(sInput != null) sInput.close();
        }
        catch(Exception e) {};
        try {
         if(socket != null) socket.close();
        }
        catch (Exception e) {}
    }
    
    private boolean writeMsg(String msg) 
    {
     if(!socket.isConnected()) 
     {
      close();
      return false;
     }
     try 
     {
      sOutput.writeObject(msg);
     }
     catch(IOException e) 
     {
      display("Error sending message to " + chatname);
      display(e.toString());
     }
     return true;
    }
}
}
