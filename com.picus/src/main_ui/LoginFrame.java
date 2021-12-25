package main_ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import server_client.Client;
import server_client.Server;

public class LoginFrame 
{
	private JFrame loginFrame;
	
	private JButton btnGenerateServer;
	private JLabel serverInfo;
	
	// to control of server's condition
	private boolean serverRunning = false;
	
	private ServerSocket serverSocket;
	private Server server;
	
	public LoginFrame()
	{
		this.createLoginFrame();
	}
	
	private void createLoginFrame()
	{
		loginFrame = new JFrame("Messaging Application");
		
		JLabel lblApp = new JLabel("Welcome to TCP/IP Messaging App");
		lblApp.setBounds(75, 25, 250, 30);  
		
		this.btnGenerateServer = new JButton("Generate a Server");
		this.btnGenerateServer.setBounds(30, 75, 165, 30);
		this.btnGenerateServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// First click can run server
				// Second click can stop server
				
				if(serverRunning == false)
					generateServerUI();
				else
				{
					server.closeServerSocket();
					serverRunning = false;
					serverInfo.setText("");
					btnGenerateServer.setText("Generate a Server");
				}
			}
		});
		
		JButton btnGenerateClient = new JButton("Generate a Client");
		btnGenerateClient.setBounds(205, 75, 165, 30);
		btnGenerateClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// There is no limit for generating client
				generateClientUI();
			}
		});
		
		this.serverInfo = new JLabel("");
		this.serverInfo.setBounds(30, 120, 250, 30);
		
	    loginFrame.add(lblApp);
	    loginFrame.add(this.btnGenerateServer);
	    loginFrame.add(btnGenerateClient);
	    loginFrame.add(this.serverInfo);
		
	    loginFrame.setSize(400,200);  
	    loginFrame.setLocationRelativeTo(null);
	    
	    loginFrame.setLayout(null);  
	    loginFrame.setVisible(true);
	}
	
	private void generateServerUI()
	{
		JFrame frmGenerateServer = new JFrame("Specify Params for Server");
		
		JLabel lblPortNumber = new JLabel("Enter Port Number");
		lblPortNumber.setBounds(30, 40, 250, 30); 
		
		JTextField txtPortNumber = new JTextField(4);
		txtPortNumber.setBounds(200, 40, 100, 30); 
		
		JButton btnOkey = new JButton("Okey");
		btnOkey.setBounds(200, 80, 100, 30);
		btnOkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int portNumber = Integer.valueOf(txtPortNumber.getText());
				
				serverInfo.setText("Server is Running on " + String.valueOf(portNumber) + " port");
				btnGenerateServer.setText("Stop Server");
				
				// Server accepts the connected clients 
				// therefore, it run continuously in a thread
				Thread thread = new Thread(){
				    public void run(){
				      try {
							serverSocket = new ServerSocket(portNumber);
							server = new Server(serverSocket);
							server.startServer();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				    }
				 };
				 thread.start();
				 
				 serverRunning = true;

				frmGenerateServer.dispose();
			}
		});
		
		frmGenerateServer.add(lblPortNumber);
		frmGenerateServer.add(txtPortNumber);
		frmGenerateServer.add(btnOkey);
		
		frmGenerateServer.setSize(350,200);  
		frmGenerateServer.setLocationRelativeTo(null);
	    
		frmGenerateServer.setLayout(null);  
		frmGenerateServer.setVisible(true);
	}
	
	private void generateClientUI()
	{
		JFrame frmGenerateClient = new JFrame("Specify Params for Client");
		
		JLabel lblNickName= new JLabel("Enter Nickname");
		lblNickName.setBounds(30, 40, 250, 30); 
		
		JTextField txtNickName = new JTextField(16);
		txtNickName.setBounds(200, 40, 100, 30); 
		
		JLabel lblIpAddress= new JLabel("Enter IP Address");
		lblIpAddress.setBounds(30, 80, 250, 30); 
		
		JTextField txtIpAddress = new JTextField(16);
		txtIpAddress.setBounds(200, 80, 100, 30);
		
		JLabel lblPortNumber = new JLabel("Enter Port Number");
		lblPortNumber.setBounds(30, 120, 250, 30); 
		
		JTextField txtPortNumber = new JTextField(4);
		txtPortNumber.setBounds(200, 120, 100, 30); 
		
		JButton btnOkey = new JButton("Okey");
		btnOkey.setBounds(200, 160, 100, 30);
		btnOkey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 
				Socket socket = null;
				try 
				{
					socket = new Socket(txtIpAddress.getText(), 
							Integer.valueOf(txtPortNumber.getText()));
					
					Client client = new Client(socket, txtNickName.getText());
					client.listenForMessage();
					client.sendFirstMessage();
					
				} catch (NumberFormatException | IOException e1) {
					e1.printStackTrace();
				}
				
				frmGenerateClient.dispose();
			}
		});
		
		frmGenerateClient.add(lblNickName);
		frmGenerateClient.add(txtNickName);
		frmGenerateClient.add(lblIpAddress);
		frmGenerateClient.add(txtIpAddress);
		frmGenerateClient.add(lblPortNumber);
		frmGenerateClient.add(txtPortNumber);
		frmGenerateClient.add(btnOkey);
		
		frmGenerateClient.setSize(350,300);  
		frmGenerateClient.setLocationRelativeTo(null);
	    
		frmGenerateClient.setLayout(null);  
		frmGenerateClient.setVisible(true);
	}
}
