package server_client;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db_operation.Operations;
import entity_classes.Message;

public class ClientQuery {

	private String nickname;
	
	private JFrame clientQueryFrame;
	
	private DefaultTableModel mdlQueryResults;

	private JCheckBox chcLastXMessage;
	private JCheckBox chcContainSomeText;
	private JCheckBox chcSendByMe;
	private JCheckBox chcToMe;
	
	private int lastXMessage = 0;
	private String someText = "";
	
	public ClientQuery(String nickname)
	{
		this.nickname = nickname;
		
		this.createClientUI();
		this.showClientFrame();
	}
	
	private void createClientUI()
	{
		this.clientQueryFrame = new JFrame("Client Query");
		
		JLabel lblApp = new JLabel("Client @" + nickname);
		lblApp.setBounds(100, 25, 250, 30);
	    
	    this.mdlQueryResults = new DefaultTableModel(); 
		JTable tblQueryResults = new JTable(this.mdlQueryResults);
		
		JTextField txtLastXMessage = new JTextField();
		txtLastXMessage.setBounds(300, 60, 50, 30);
		
		this.chcLastXMessage = new JCheckBox("Get Last X Messages");  
		this.chcLastXMessage.setBounds(100, 50, 180, 50);
		this.chcLastXMessage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				lastXMessage = Integer.valueOf(txtLastXMessage.getText());
				loadDataMdlQueryResults();
			}
		});
        
        JTextField txtSomeText = new JTextField();
		txtSomeText.setBounds(425, 100, 75, 30);
        
        this.chcContainSomeText = new JCheckBox("Get Messages that Contain Some Text");  
        this.chcContainSomeText.setBounds(100, 90, 300, 50);  
        this.chcContainSomeText.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				someText = txtSomeText.getText();
				loadDataMdlQueryResults();
			}
		});
        
		this.chcSendByMe = new JCheckBox("Get Messages Send By Me");  
		this.chcSendByMe.setBounds(100, 130, 250, 50);  
		this.chcSendByMe.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				loadDataMdlQueryResults();
			}
		});
        
		this.chcToMe = new JCheckBox("Get Messages To Me");  
		this.chcToMe.setBounds(350, 130, 200, 50);  
		this.chcToMe.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				loadDataMdlQueryResults();
			}
		});

		
		JScrollPane spQueryResults = new JScrollPane(tblQueryResults);
		spQueryResults.setBounds(100, 200, 500, 325);

        this.clientQueryFrame.add(lblApp);
        this.clientQueryFrame.add(spQueryResults);
        this.clientQueryFrame.add(txtLastXMessage);
        this.clientQueryFrame.add(chcLastXMessage);
        this.clientQueryFrame.add(txtSomeText);
        this.clientQueryFrame.add(chcContainSomeText);
        this.clientQueryFrame.add(chcSendByMe);
        this.clientQueryFrame.add(chcToMe);
        
        this.clientQueryFrame.setSize(700, 600);  
		this.clientQueryFrame.setLocationRelativeTo(null);
		
        this.loadColumnMdlQueryResults();
	}
	
	private void showClientFrame()
	{
		this.clientQueryFrame.setLayout(null);  
		this.clientQueryFrame.setVisible(true);
	}
	
	private void loadColumnMdlQueryResults()
	{
		this.mdlQueryResults.addColumn("From Client");
		this.mdlQueryResults.addColumn("To Client");
		this.mdlQueryResults.addColumn("Message");
		
		// model.addRow(new Object[]{"v1", "v2"});
	}
	
	private void loadDataMdlQueryResults()
	{
		this.clearDataMdlQueryResults();
		
		List<Message> lstResults = null;
		lstResults = this.getDataFromDatabase();
		
		if(lstResults != null)
		{
			for(int i = 0; i < lstResults.size(); i++)
			{
				this.mdlQueryResults.addRow(new Object[] { 
						lstResults.get(i).getFromClient(),
						lstResults.get(i).getToClient(),
						lstResults.get(i).getMsg()
					}
				);
			}
		}
	}
	
	private List<Message> getDataFromDatabase()
	{
		ArrayList<Message> lstResults = new ArrayList<>();
		ArrayList<Integer> msgIDs = new ArrayList<>();
		
		List<Message> lstLastXMessage = null;
		List<Message> lstSomeText = null;
		List<Message> lstSendByMe = null;
		List<Message> lstToMe = null;
		
		if(this.chcLastXMessage.isSelected())
		{
			lstLastXMessage = Operations.getLastXMessage(this.nickname, this.lastXMessage);
		}
		
		if(this.chcContainSomeText.isSelected())
		{
			lstSomeText = Operations.getMessagesContainText(this.nickname, this.someText);
		}
		
		if(this.chcSendByMe.isSelected())
		{
			lstSendByMe = Operations.getMessagesAccordingDirection(this.nickname, "fromClient");
		}
		
		if(this.chcToMe.isSelected())
		{
			lstToMe = Operations.getMessagesAccordingDirection(this.nickname, "toClient");
		}

		Message tempMsg = new Message();
		if(lstLastXMessage != null)
		{
			for(int i = 0; i < lstLastXMessage.size(); i++)
			{
				tempMsg = lstLastXMessage.get(i);
				if(!(msgIDs.contains(tempMsg.getMessageId())))
				{
					msgIDs.add(tempMsg.getMessageId());
					lstResults.add(tempMsg);
				}
			}
		}
		
		if(lstSomeText != null)
		{
			for(int i = 0; i < lstSomeText.size(); i++)
			{
				tempMsg = lstSomeText.get(i);
				if(!(msgIDs.contains(tempMsg.getMessageId())))
				{
					msgIDs.add(tempMsg.getMessageId());
					lstResults.add(tempMsg);
				}
			}
		}
			
		if(lstSendByMe != null)
		{
			for(int i = 0; i < lstSendByMe.size(); i++)
			{
				tempMsg = lstSendByMe.get(i);
				if(!(msgIDs.contains(tempMsg.getMessageId())))
				{
					msgIDs.add(tempMsg.getMessageId());
					lstResults.add(tempMsg);
				}
			}
		}
		
		if(lstToMe != null)
		{
			for(int i = 0; i < lstToMe.size(); i++)
			{
				tempMsg = lstToMe.get(i);
				if(!(msgIDs.contains(tempMsg.getMessageId())))
				{
					msgIDs.add(tempMsg.getMessageId());
					lstResults.add(tempMsg);
				}
			}
		}
		
		return lstResults;
	}
	
	private void clearDataMdlQueryResults()
	{
		while (this.mdlQueryResults.getRowCount() > 0) {
			this.mdlQueryResults.removeRow(0);
		}
	}
}
