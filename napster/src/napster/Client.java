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
import java.util.*;			//Provides access to all the classes which are preset in the java.util package.

/*********************************************************************************************/
public class Client 
{
	public static void main(String args[])
	{
		int id;
		int c = 1;
		String f_name;
		int portofserver;
		int portasserver;
		Scanner scan = new Scanner(System.in);
		String serverName = args[0];
		int port=4001;
		Data d = new Data();
		Peer p = new Peer();
		int peer;
		String sharedDir;
		try {
			System.out.println("*********************************************************************************************");
			System.out.println("Connecting to " + serverName + " on port# " + port);
			System.out.println("*********************************************************************************************");
			Socket client = new Socket(serverName, port);		//Creating the socket for connecting to server -> new socket created on client side
																//Gets confirmation from the other side and gets the new port#
			System.out.println("*********************************************************************************************");
			System.out.println("Connected to " + client.getRemoteSocketAddress());
			System.out.println("*********************************************************************************************");
			OutputStream out = client.getOutputStream();
			ObjectOutputStream oos;
			InputStream in = client.getInputStream();
			ObjectInputStream ois;
			
			System.out.println("*********************************************************************************************");
			System.out.println("Enter the path of the Peer directory to be registerd in the Central Indexing Server: ");						//Shared Directory of the client containing its files
			System.out.println("*********************************************************************************************");
			sharedDir=scan.nextLine();
			System.out.println("*********************************************************************************************");
			System.out.println("Enter a port no. where client acts as server: ");		//Port Number at which this client will work as a server
			System.out.println("*********************************************************************************************");
			portasserver=scan.nextInt();
			ClientServer cs=new ClientServer(portasserver,sharedDir);		//Start a thread to make any client work as a server
			cs.start();
							//System.out.println("enter 1 to register or 2 search for a file");
			int ch = 1;
			if (ch == 1) {
				
				File newfind;
				File directoryObj = new File(sharedDir);
				String[] filesList = directoryObj.list();
				for (int j = 0; j < filesList.length; j++) {						//Loop to registering all files 
					newfind = new File(filesList[j]);								//in the directory to the server
					oos = new ObjectOutputStream(out);
					d.filename = newfind.getName();
					d.peer_id=Integer.parseInt(args[1]);
					d.choice=1;
					oos.writeObject(d);
					System.out.println("Registering Files |" + newfind.getName() + "| from |" + sharedDir + "| to the Central Indexing Server");
				}
			}
			System.out.println("*********************************************************************************************");
			System.out.println("Enter 1 to search for a file or Enter 2 to sequential search.");
			System.out.println("*********************************************************************************************");
			c=scan.nextInt();
			if (c == 1) {
				try {
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the file to be searched in .txt format: ");
					System.out.println("*********************************************************************************************");
					scan.nextLine();
					d.filename = scan.nextLine();
					d.choice = 2;
					oos = new ObjectOutputStream(out);		//Send the filename to be searched to the server			
					oos.writeObject(d);
					oos.flush();
					System.out.println();
					System.out.println("Peers having the file " + d.filename + " is/are: ");
					ois = new ObjectInputStream(in);
					p = (Peer) ois.readObject();
					for (int i = 0; i < p.files.length; i++)
						if(p.files[i]!=0)
						System.out.println(p.files[i]);
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the Peer number from where the file is downloaded: " + d.filename);
					System.out.println("*********************************************************************************************");
					peer=scan.nextInt();
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the the respective port no. of the peer that acts as a server: ");
					System.out.println("*********************************************************************************************");
					portofserver=scan.nextInt();
					//ClientasServer(peer,portofserver,d.filename,sharedDir); 												//Downloading the file from Peer						
					
					try{											 //Connecting to the peer Server having the file to be downloaded	
						Socket clientasserversocket=new Socket("localhost",portofserver);
						ObjectOutputStream ooos=new ObjectOutputStream(clientasserversocket.getOutputStream());
						ooos.flush();
						ObjectInputStream oois=new ObjectInputStream(clientasserversocket.getInputStream());
						ooos.writeObject(d.filename);
						int readbytes=(int)oois.readObject();
						System.out.println("*********************************************************************************************");
						System.out.println("Bytes transferred: " + readbytes);
						System.out.println("*********************************************************************************************");
						byte[] b=new byte[readbytes];
						oois.readFully(b);
						OutputStream fileos=new FileOutputStream(sharedDir+"//"+d.filename);
						BufferedOutputStream bos=new BufferedOutputStream(fileos);
						bos.write(b, 0,(int) readbytes);
						System.out.println("*********************************************************************************************");
				        System.out.println("The file name |" + d.filename + "| has be downloaded to the directory |" + sharedDir + "|");
						System.out.println("*********************************************************************************************");
				        bos.flush();
				        clientasserversocket.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					System.out.println("The file name " + d.filename + " has been downloaded from peer id# " + peer + " at port no. " + portofserver);
					System.out.println("*********************************************************************************************");
					client.close();
					}catch(IOException io)
					{
						io.printStackTrace();
					}
				}
				else if(c == 2){
					final long startTime = System.currentTimeMillis();

/*********************************************************************************************/					
					try {
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the file to be searched in .txt format: ");
					System.out.println("*********************************************************************************************");
					scan.nextLine();
					d.filename = scan.nextLine();
					d.choice = 2;
					oos = new ObjectOutputStream(out);		//Send the filename to be searched to the server			
					oos.writeObject(d);
					oos.flush();
					System.out.println();
					System.out.println("Peers having the file " + d.filename + " is/are: ");
					ois = new ObjectInputStream(in);
					p = (Peer) ois.readObject();
					for (int i = 0; i < p.files.length; i++)
						if(p.files[i]!=0)
						System.out.println(p.files[i]);
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the Peer number from where the file is downloaded: " + d.filename);
					System.out.println("*********************************************************************************************");
					peer=scan.nextInt();
					System.out.println("*********************************************************************************************");
					System.out.println("Enter the the respective port no. of the peer that acts as a server: ");
					System.out.println("*********************************************************************************************");
					portofserver=scan.nextInt();
					//ClientasServer(peer,portofserver,d.filename,sharedDir); 												//Downloading the file from Peer						
					
					try{											 //Connecting to the peer Server having the file to be downloaded	
						
						Socket clientasserversocket=new Socket("localhost",portofserver);
						ObjectOutputStream ooos=new ObjectOutputStream(clientasserversocket.getOutputStream());
						ooos.flush();
						ObjectInputStream oois=new ObjectInputStream(clientasserversocket.getInputStream());
						ooos.writeObject(d.filename);
						int readbytes=(int)oois.readObject();
						
						System.out.println("*********************************************************************************************");
						System.out.println("Bytes transferred: " + readbytes);
						System.out.println("*********************************************************************************************");
						byte[] b=new byte[readbytes];
						oois.readFully(b);
						OutputStream fileos=new FileOutputStream(sharedDir+"//"+d.filename);
						BufferedOutputStream bos=new BufferedOutputStream(fileos);
						for(int i=0;i<1000;i++){
						bos.write(b, 0,(int) readbytes);
						System.out.println("*********************************************************************************************");
				        System.out.println("The file name |" + d.filename + "| has be downloaded to the directory |" + sharedDir + "|");
						System.out.println("*********************************************************************************************");
				        bos.flush();
				        
						}
					clientasserversocket.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					System.out.println("The file name " + d.filename + " has been downloaded from peer id# " + peer + " at port no. " + portofserver);
					System.out.println("*********************************************************************************************");
					client.close();
					}catch(IOException io)
					{
						io.printStackTrace();
					}
/*********************************************************************************************/					
					final long endTime = System.currentTimeMillis();
					System.out.println("The total time taken to process 1000 sequential requests is " +(endTime - startTime)+ " milliseconds");
				}
				else
				{	
					System.out.println("Terminating the client");
					System.exit(1);
				}
				} catch (ClassNotFoundException cnf) {
					cnf.printStackTrace();
				} catch (IOException ie) {
					ie.printStackTrace();
				}
			}
}
/*********************************************************************************************/
	
