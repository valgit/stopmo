package stopmo;

import java.awt.Dimension;
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
	private int state;

	// should be a set ?
	private CameraModelListener listener;
	private int width;
	private int height;

	public CameraModel(WebSocket conn) {
		super();
		this.conn = conn;
		state = 0;
	}

	public void parseMessage(String msg) {		
		if (msg.startsWith("hello")) {
			model = msg.split(":",2)[1];
		} else
			if (msg.startsWith("PSize")) {
				setPreviewSize(msg);
			} else
				if (msg.startsWith("Shot")) {
					//System.out.println("new shot");
					state = 2;
				} else
					if (msg.startsWith("Balance")) {
						parseBalance(msg);
					} else
						if (msg.startsWith("Preview")) {
							//System.out.println("new preview");
							state = 1;
						} else
							System.out.println("parseMessage: " + msg);
	}

	/*
	 * camera send preview as w,h
	 */
	private void setPreviewSize(String msg) {
		String blist = msg.split(":",2)[1];
		String[] slist = blist.split("\\s*,\\s*");
		width = Integer.parseInt(slist[0]);
		height = Integer.parseInt(slist[1]);
		if (listener != null) {
			Dimension psize = new Dimension(width,height);
			listener.onPreviewSize(psize);
		}
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
		if (state == 2) {
			System.out.println("parseMessage: in shot" );
			/*
			try {
				
				InputStream in = new ByteArrayInputStream(message.array());
				BufferedImage img = ImageIO.read(in);

				if (listener != null)
					listener.onShot(img);
				
				
				
			} catch (IOException e) {			
				e.printStackTrace();
			} */
			if (listener != null)
				listener.onShot(message.array());
			
		} else if (state == 1) {
			//System.out.println("parseMessage: in preview ("+width+","+height+")" );
			// test encoding in phone?			
			try {
				InputStream in = new ByteArrayInputStream(message.array());
				BufferedImage img = ImageIO.read(in);
				
				/* 
				int width = img.getWidth();
				int height = img.getHeight();
				System.out.println("onPreview: (jpg) size: "+width+" h: "+height);
				*/
				if (listener != null)
					listener.onPreview(img);
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
						
		} else {
			System.out.println("parseMessage: unhandled bytes, state : " + state );
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
