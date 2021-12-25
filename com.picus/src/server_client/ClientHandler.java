package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import db_operation.Operations;
import entity_classes.Message;

public class ClientHandler implements Runnable {
	
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientNickname;
	
	// When a new client entered
	public ClientHandler(Socket socket)
	{
		try 
		{
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientNickname = bufferedReader.readLine();
			
			clientHandlers.add(this);
			
			this.broadcastMessage("Server: entered " + clientNickname);
			
		} catch (Exception e) 
		{
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
		
	}
	
	// When a client broadcast a message for another client
	@Override
	public void run() 
	{
		String messageFromClient;
		
		while(socket.isConnected())
		{
			try 
			{
				messageFromClient = bufferedReader.readLine();
				
				broadcastMessage(messageFromClient);
				
			} catch (Exception e) 
			{
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	
	// Server can broadcast nicknames of entered and left clients for all clients,
	// and
	// Each client can broadcast message for any other client according to the nicknames.
	public void broadcastMessage(String messageToSend)
	{
		String from = "";
		String toClient = "";	
		
		// messageToSend string format-> "from: to message_part"
		
		String[] strgs = messageToSend.split(" ");
		from = strgs[0];
		from = from.replace(from.substring(from.length()-1), ""); // for ":" character
		toClient = strgs[1];
		
		for(ClientHandler clientHandler : clientHandlers)
		{
			try 
			{
				if(from.equals("Server"))
				{
					// Server broadcasts nicknames of connected clients any changing to all clients
					
					String connectedClients = "connectedClients ";
					for(ClientHandler clientHandlerConnected : clientHandlers)
					{
						connectedClients += clientHandlerConnected.clientNickname + " ";
					}
					
					clientHandler.bufferedWriter.write(connectedClients);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
				else if(from.equals("quit_client"))
				{
					closeEverything(socket, bufferedReader, bufferedWriter);
				}
				else // Any client
				{
					// 1-1 (client-to-client) message
					if(clientHandler.clientNickname.equals(toClient))
					{
						clientHandler.bufferedWriter.write(messageToSend);
						clientHandler.bufferedWriter.newLine();
						clientHandler.bufferedWriter.flush();
						
						this.insertMsgToDatabase(messageToSend);
					}
				}
			} catch (Exception e) 
			{
				closeEverything(socket, bufferedReader, bufferedWriter);
				e.printStackTrace();
			}
		}
	}
	
	// All messages are stored Postgresql database
	private void insertMsgToDatabase(String messageToInsert)
	{
		Message message = new Message();
		
		String[] strgs = messageToInsert.split(" ");
		message.setFromClient(strgs[0].replace(strgs[0].substring(strgs[0].length()-1), ""));
		message.setToClient(strgs[1]);
		
		String msg = "";
		for(int i = 2; i < strgs.length; i++)
		{
			msg += strgs[i] + " ";
		}
		message.setMsg(msg);
		
		Operations.insertMessage(message);
	}
	
	public void removeClientHandler()
	{
		clientHandlers.remove(this);
		broadcastMessage("Server: left " + clientNickname);
	}
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
	{
		removeClientHandler();
		
		try 
		{
			if(bufferedReader != null)
			{
				bufferedReader.close();
			}
			if(bufferedWriter != null)
			{
				bufferedWriter.close();
			}
			if(socket != null)
			{
				socket.close();
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}












