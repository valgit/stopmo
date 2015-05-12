package stopmo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class CamSocketServer extends WebSocketServer {
	private Set<WebSocket> conns;
	private Map<WebSocket, CameraModel> cameras;
	private CamSocketServerListener controller;
	
	public CamSocketServer(InetSocketAddress address, int port,CamSocketServerListener listener) {
		//super(address, decoders);
		super(new InetSocketAddress(port));
		
		System.out.println("websocket created");
	    conns = new HashSet<>();
	    cameras = new HashMap<>();
	    controller = listener;
	}

	 /** 
	   * Method handler when a connection has been closed.
	   */
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
		CameraModel camera = cameras.get(conn);
		conns.remove(conn);
		cameras.remove(conn);
		controller.delCamera(camera);
		
		
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		 System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
		 ex.printStackTrace();
	}

	/** 
	   * Method handler when a message has been received from the client.
	   */
	@Override
	public void onMessage(WebSocket conn, String message) {
		//System.out.println("Received: " + message);
		CameraModel camera = cameras.get(conn);
		camera.parseMessage(message);		
	}

	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
		//System.out.println("Received: bytes" );
		CameraModel camera = cameras.get(conn);
		camera.parseMessage(message);
	}
	
	/** 
	   * Method handler when a new connection has been opened. 
	   */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	    conns.add(conn);
	    // register the camera
	    CameraModel camera = new CameraModel(conn);
	    cameras.put(conn, camera);
	    controller.addCamera(camera);
	    		
	}
	
}
