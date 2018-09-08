/*
 * Copyright © 2018 by Abhishek Kumar
 *
   All rights reserved. No part of this code may be reproduced, distributed, or transmitted 
   in any form or by any means, without the prior written permission of the programmer.  
 *
 * 
   CS 5352 Advanced Operating Systems and Design
   Project Title: Napster Style Peer to Peer (P2P) file sharing in JAVA
 * 
 * */
//package napster;
import java.net.*;			//Provides the classes for implementing networking applications.
import java.io.*;			//Provides for system input and output through data streams, serialization and the file system.
import java.util.ArrayList; //Provides resizable-array and implements the List interface.

/*********************************************************************************************/
public class Server {										//Thread class to handle multiple clients 
	public static void main(String args[]) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		int port=4001;    // Random port number in the range of 1024 and 65535
		ArrayList<FileProperties> filess=new ArrayList<FileProperties>(); //A class defined by the programmer to register files. 
																		  //FileProperties class has a Peer id (int peer_id) and a file name (String Filename).

		try {
			serverSocket = new ServerSocket(port); //Port# passed - Client knows whom to connect with.
												   // Returns a socket in order to communicate to the client who has made the request.
		} catch (IOException io) {
			io.printStackTrace();
		}
		
		while (true) {
			try {
				System.out.println("*********************************************************************************************");
				System.out.println("Central Indexing Server is waiting for Client");
				System.out.println("*********************************************************************************************");
				socket = serverSocket.accept(); // Blocking call - making the program go in listening mode until some client has connected.
			} catch (IOException e) {
				System.out.println("IO?error: " + e.getMessage());
			}
			new EchoThread(socket,filess).start(); // Causes this thread to begin execution; the Java Virtual Machine calls the run method of EchoThread.
		}
		
	}
}
/*********************************************************************************************/

/*********************************************************************************************/
class EchoThread extends Thread
{
	protected Socket server;

	int count = 0;
	int value = 1;
	ArrayList<FileProperties> filess=new ArrayList<FileProperties>();       //ArrayList  maps to FileProperties class to store registered files of clients 

	int[] files_per_peer = new int[20];
	Data d = new Data();
	Peer p = new Peer();

	public EchoThread(Socket server,ArrayList<FileProperties> filess) 
	{
		this.server = server;  //reference variable referring to the current object
		this.filess=filess;	   //reference variable referring to the current object
		
	}
	public void run() //Overriding the super class run() in order to define the thread.
	{
		try {
			OutputStream os = server.getOutputStream(); //Application provides information byte by byte to OutputStream which is stored in the destination (socket). 
			InputStream is = server.getInputStream(); //Reads from source (socket) and provides byte by byte to the application.  
			ObjectOutputStream oos; //oos is a reference variable of the class ObjectOutputStream
			ObjectInputStream ois; //ois is a reference variable of the class ObjectInputStream

	
				System.out.println("Connected to client on port no." + server.getRemoteSocketAddress());
				System.out.println();
				
				while (value == 1)
				{
					ois = new ObjectInputStream(is);
					d = (Data) ois.readObject();	//Type casting object of type object to object of type Data				
					value = d.choice;
					if (value == 2)
						break;
					if(register(d.peer_id, d.filename)==1)		//register() to register files of the peer on Central Indexing Server.
						System.out.println("Registering File |" + d.filename + "| of peer ID " + d.peer_id + " to the Central Indexing Server");
				}

				if (value == 2) 
				{
					System.out.println("*********************************************************************************************");
					System.out.println("^Search Invoked^");									
					System.out.println("*********************************************************************************************");
					search(d.filename);		//search() returning list of peer_ids
					oos = new ObjectOutputStream(os);
					oos.writeObject(p);
					oos.flush(); //Method flushes the stream i.e., it flushes all the buffers in a chain of Writers and OutputStreams.
					oos.close(); //Method closes this scanner. 
				}

		
			} catch (ClassNotFoundException c) {
				System.out.println("Data class not found");
				c.printStackTrace();
				
			} catch (IOException ie) {
				ie.printStackTrace();
				
			}

		try 
		{
			server.close();  //Method closes this scanner.
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int register(int peer_id, String filename)  //Defining the register() method. 
	{										
		FileProperties f=new FileProperties();
		f.peer_id=peer_id;
		f.Filename=filename;
		filess.add(f);
		return 1;
	}

	public void search(String filename)		//Defining the search() method. 
	{												
		FileProperties fi;
		int k=0;
		for(int i=0;i<filess.size();i++)
		{
			fi=filess.get(i);
			if(fi.Filename.equals(filename))
				p.files[k++] = fi.peer_id;
		}
	}
}
/*********************************************************************************************/
