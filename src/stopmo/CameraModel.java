package stopmo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;

public class CameraModel {
	private WebSocket conn;
	private String model;
	private List<String> WhiteBalanceList;
	
	// should be a set ?
	private CameraModelListener listener;
	
	public CameraModel(WebSocket conn) {
		super();
		this.conn = conn;
	}
	
	public void parseMessage(String msg) {		
		if (msg.startsWith("hello")) {
			model = msg.split(":",2)[1];
		} else
		if (msg.startsWith("Shot")) {
			System.out.println("new shot");
		} else
		if (msg.startsWith("Balance")) {
			parseBalance(msg);
		} else
			System.out.println("parseMessage: " + msg);
	}
	
	/*
	 * camera send the Balance mode list
	 * store it
	 */
	private void parseBalance(String msg) {
		String blist = msg.split(":",2)[1];
		WhiteBalanceList = Arrays.asList(blist.split("\\s*,\\s*"));		
	}

	public List<String> getWhiteBalanceList() {
		return WhiteBalanceList;
	}
	
	public void parseMessage( ByteBuffer message ) {
		System.out.println("parseMessage: bytes" );
		
		try {
			InputStream in = new ByteArrayInputStream(message.array());
			BufferedImage img = ImageIO.read(in);
					
			if (listener != null)
				listener.onShot(img);
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
		
	public void takeShot() {
		System.out.println("take shot");
		conn.send("takeShot");
	}
	
	public String getModel() {
		return model;
	}
	
	public void attach(CameraModelListener listener) {
		this.listener = listener;
	}
	
	public void detach(CameraModelListener listener) {
		this.listener = null;
	}
}
