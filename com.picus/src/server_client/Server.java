package server_client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Server 
{
	// ServerSocket provides implementation of the server side
	// of a client-server socket connection
	private ServerSocket serverSocket;
	
	public Server(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void startServer()
	{
		try 
		{
			while(!serverSocket.isClosed())
			{
				// When a client generated (new socket)
				Socket socket = serverSocket.accept();
				System.out.println("A new client has connected.");
				
				// The generated client is added to static Client Handler
				ClientHandler clientHandler = new ClientHandler(socket);
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
		} catch (Exception e) 
		{
			closeServerSocket();
			//e.printStackTrace();
		}	
	}
	
	public void closeServerSocket()
	{
		try 
		{
			if(serverSocket != null)
			{
				serverSocket.close();
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
