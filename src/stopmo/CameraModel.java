package stopmo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class CameraModel implements Runnable {
	protected Socket clientSocket = null;
	private DataInputStream dIn;
	private DataOutputStream dOs;

	//private WebSocket conn;
	private String model;
	private List<String> WhiteBalanceList;
	private int state;

	// should be a set ?
	private CameraModelListener listener;
	private int width;
	private int height;


	public CameraModel(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
		state = 0;
		try {
			dIn = new DataInputStream(clientSocket.getInputStream());
			dOs = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		try {
			//TODO: 

			//BufferedReader reader = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );


			//InputStream input  = clientSocket.getInputStream();
			//OutputStream output = clientSocket.getOutputStream();
			//TOD ?
			while (true) {
				/*
				String line = this.socketIn.readLine();
				long time = System.currentTimeMillis();
				output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
					this.serverText + " - " +
					time +
					"").getBytes());
				output.close();
				input.close();
				System.out.println("Request processed: " + time);
				 */
				//char ch = (char) reader.read();

				char type =  dIn.readChar();
				int length = dIn.readInt();                    // read length of incoming message
				//System.out.println("will read type "+ type + "of len : "+length);
				if(length>0) {
					byte[] message = new byte[length];
					dIn.readFully(message, 0, message.length); // read the message
					// do something with it !
					//System.out.println("read " + length + "bytes");
					parseMessage(type,length,message);
				} else {			
					// ???
					System.out.println("nothing read ?");
				}
			} // while
		}  catch (SocketException e) {
			System.out.println("socket : "+ e.getMessage());
		}catch (IOException e) {
			//report exception somewhere.
			e.printStackTrace();
		}
	}

	private void parseMessage(char type, int length, byte[] message) {		
		switch (type) {
		case 'H' : // hello
			model = new String(message);
			break;
		case 'S' : // shot
			System.out.println("new shot (jpeg size " + length + ")");
			state = 2;
			break;
		case 'P' : // preview
			System.out.println("new preview");
			state = 1;
			break;
		case 'B' : // balance
			parseBalance(new String(message));
			break;
		case 'T' : // preview size
			getPreviewSize(new String(message));
			break;
		case 'Q' : // quit !
			break;
		default:
			System.out.println("parseMessage of type :" + type + " with len " + length);
		}	 		
	}

	/*
	 * camera send preview as w,h
	 */
	private void getPreviewSize(String msg) {
		//String blist = msg.split(":",2)[1];
		String[] slist = msg.split("\\s*,\\s*");
		width = Integer.parseInt(slist[0]);
		height = Integer.parseInt(slist[1]);
	}

	/*
	 * camera send the Balance mode list
	 * store it
	 */
	private void parseBalance(String msg) {
		//String blist = msg.split(":",2)[1];
		WhiteBalanceList = Arrays.asList(msg.split("\\s*,\\s*"));		
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
		try {
			// should be TLV !!
			//os.write("takeShot".getBytes());
			//dOs.writeChar(0x0053); // "S"
			dOs.writeChar('S'); 
			dOs.writeLong(0L);		

		} catch (IOException e) {		
			e.printStackTrace();
		}
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
