package server_client;


import java.net.*;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Client 
{
	private Socket socket;
	private BufferedReader bufferReader;
	private BufferedWriter bufferWriter;
	private String nickname;
	
	private JFrame clientFrame;
	private DefaultListModel<String> lstConnectedClientsModel;
	private DefaultListModel<String> lstMessagesModel;
	
	private JTextField txtEnterMessage;
	
	public Client(Socket socket, String nickname)
	{
		try 
		{
			this.socket = socket;
			this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.nickname = nickname;
			
			this.createClientUI();
			
		} catch (Exception e) 
		{
			closeEverything(socket, bufferReader, bufferWriter);
			e.printStackTrace();
		}
	}
	
	public void sendFirstMessage()
	{
		this.sendMessage(nickname);
	}
	
	private void sendMessage(String msg)
	{
		try 
		{
			bufferWriter.write(msg);
			bufferWriter.newLine();
			bufferWriter.flush();
			
		} catch (Exception e) 
		{
			closeEverything(socket, bufferReader, bufferWriter);
		}
	}
	
	// When a message is received
	public void listenForMessage()
	{
		new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				String msgFromChat;
				
				while(!socket.isClosed())
				{
					try 
					{
						msgFromChat = bufferReader.readLine();
						
						String from = "";
						
						// messageToSend string format-> "from: to message_part"
						
						String[] strgs = msgFromChat.split(" ");
						from = strgs[0];
						from = from.replace(from.substring(from.length()-1), ""); // for ":" character
						
						if(from.equals("connectedClient"))
						{
							// This is for current connected clients
							String[] modifiedArray = Arrays.copyOfRange(strgs, 1, strgs.length);
							updateConnectedClients(modifiedArray);
						}
						else
						{
							// This is a 1-1 message
							// The received message is added to the lstMessagesModel
							
							String msg = "";
							for(int i = 2; i < strgs.length; i++)
							{
								msg += strgs[i] + " ";
							}
							lstMessagesModel.add(lstMessagesModel.getSize(), from + " : " + msg);
						}
					} catch (Exception e) 
					{
						closeEverything(socket, bufferReader, bufferWriter);
					}
				}
			}
		}).start();
	}
	
	private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
	{
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
	
	private void createClientUI()
	{
		this.clientFrame = new JFrame("Client");
		
		JLabel lblApp = new JLabel("Client @" + nickname);
		lblApp.setBounds(100, 25, 250, 30);
		
		JButton btnQuery = new JButton("Query");
		btnQuery.setBounds(540, 25, 100, 30);
		btnQuery.setForeground(Color.RED);
		btnQuery.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Each client can query for past messages with its nickname
				// Each client can show only messages with related itself
				ClientQuery clientQuery = new ClientQuery(nickname);
			}
		});
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setBounds(650, 25, 100, 30);
		btnQuit.setForeground(Color.RED);
		btnQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage("quit_client");
				closeEverything(socket, bufferReader, bufferWriter);
				clientFrame.dispose();
			}
		});
		
		JLabel lblConnectedClients = new JLabel("Connected Clients");
		lblConnectedClients.setBounds(20, 75, 250, 30);
		
		this.lstConnectedClientsModel = new DefaultListModel<>();
		JList lstConnectedClients = new JList<>(this.lstConnectedClientsModel);
		lstConnectedClients.setBounds(20, 120, 200, 225);
		
		JLabel lblMessages = new JLabel("Messages");
		lblMessages.setBounds(250, 75, 250, 30);
		
		this.lstMessagesModel = new DefaultListModel<>();
		JList lstMessages = new JList<>(this.lstMessagesModel);
		lstMessages.setBounds(250, 120, 500, 225);
		
		this.txtEnterMessage = new JTextField();
		txtEnterMessage.setBounds(250, 375, 375, 30);
		
		JButton btnSendMsg = new JButton("Send");
		btnSendMsg.setBounds(650, 375, 100, 30);
		btnSendMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String msg = txtEnterMessage.getText();
				
				String[] strgs = msg.split(" ");
				String toClient = strgs[0];
				
				lstMessagesModel.add(lstMessagesModel.getSize(), "You -> " + toClient + " : " + (msg.replace(toClient + " ", "")));
				
				txtEnterMessage.setText("");
				
				sendMessage(nickname + ": " + msg);
			}
		});

		this.clientFrame.add(lblApp);
		this.clientFrame.add(btnQuery);
		this.clientFrame.add(btnQuit);
		this.clientFrame.add(lblConnectedClients);
		this.clientFrame.add(lstConnectedClients);
		this.clientFrame.add(lblMessages);
		this.clientFrame.add(lstMessages);
		this.clientFrame.add(this.txtEnterMessage);
		this.clientFrame.add(btnSendMsg);
		
		this.clientFrame.setSize(800, 500);  
		this.clientFrame.setLocationRelativeTo(null);

		this.clientFrame.setLayout(null);  
		this.clientFrame.setVisible(true);
	}
	
	private void updateConnectedClients(String[] newConnectedClients)
	{
		this.lstConnectedClientsModel.clear();
		for(String connectedClient : newConnectedClients)
		{
			if(connectedClient.equals(this.nickname))
				connectedClient += " (you)";
			
			this.lstConnectedClientsModel.add(0, connectedClient);
		}
	}
}




