package entity_classes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Messages")
public class Message 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "messageId", updatable = false, nullable = false)
	private int messageId;

	@Column(name = "fromClient")
	private String fromClient;
	
	@Column(name = "toClient")
	private String toClient;
	
	@Column(name = "msg")
	private String msg;

	public Message() {
		super();
		this.fromClient = "";
		this.toClient = "";
		this.msg = "";
	}

	public Message(int messageId, String fromClient, String toClient, String msg) {
		super();
		this.messageId = messageId;
		this.fromClient = fromClient;
		this.toClient = toClient;
		this.msg = msg;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getFromClient() {
		return fromClient;
	}

	public void setFromClient(String fromClient) {
		this.fromClient = fromClient;
	}

	public String getToClient() {
		return toClient;
	}

	public void setToClient(String toClient) {
		this.toClient = toClient;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
