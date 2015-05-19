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
		} 
		if (msg.startsWith("PSize")) {
			getPreviewSize(msg);
		}
		if (msg.startsWith("Shot")) {
			System.out.println("new shot");
			state = 2;
		}
		if (msg.startsWith("Balance")) {
			parseBalance(msg);
		} 
		if (msg.startsWith("Preview")) {
			System.out.println("new preview");
			state = 1;
		} 
		System.out.println("parseMessage: " + msg);
	}

	/*
	 * camera send preview as w,h
	 */
	private void getPreviewSize(String msg) {
		String blist = msg.split(":",2)[1];
		String[] slist = blist.split("\\s*,\\s*");
		width = Integer.parseInt(slist[0]);
		height = Integer.parseInt(slist[1]);
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

		if (state == 2) {
			System.out.println("parseMessage: in shot" );
			try {
				InputStream in = new ByteArrayInputStream(message.array());
				BufferedImage img = ImageIO.read(in);

				if (listener != null)
					listener.onShot(img);

			} catch (IOException e) {			
				e.printStackTrace();
			}
		} else if (state == 1) {
			System.out.println("parseMessage: in preview ("+width+","+height+")" );

			//YuvImage img = new YuvImage(message, ImageFormat.NV21, PreviewSizeWidth, PreviewSizeHeight, null);
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
