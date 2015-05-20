package stopmo;

/*
 * the real work of the socket !
 * 
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class WorkerRunnable implements Runnable {	
	protected Socket clientSocket = null;
	protected String serverText   = null;

	private CamSocketServerListener controller;
	
	public WorkerRunnable(Socket clientSocket, String serverText,CamSocketServerListener controller) {
		this.clientSocket = clientSocket;
		this.serverText   = serverText;
		
		controller.addCamera(this);
	}

	@Override
	public void run() {
		try {
			InputStream input  = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();
			long time = System.currentTimeMillis();
			output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
					this.serverText + " - " +
					time +
					"").getBytes());
			output.close();
			input.close();
			System.out.println("Request processed: " + time);
		} catch (IOException e) {
			//report exception somewhere.
			e.printStackTrace();
		}
	}
}
