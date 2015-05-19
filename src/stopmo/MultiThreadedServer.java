package stopmo;

/*
 * socket server for Camera Remote
 * create a new worker on connection
 * this should replace the websocket one !
 * 
 * should be instantiate like this :
 * MultiThreadedServer server = new MultiThreadedServer(9000);
 * new Thread(server).start();
 *
 *  try {
 *    Thread.sleep(20 * 1000);
 *  } catch (InterruptedException e) {
 *       e.printStackTrace();
 *  }
 *  System.out.println("Stopping Server");
 *  server.stop();
 */

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class MultiThreadedServer implements Runnable {

	protected int          serverPort   = 5000;
	protected ServerSocket serverSocket = null;
	protected boolean      isStopped    = false;
	protected Thread       runningThread= null;

	public MultiThreadedServer(int port){
		serverPort = port;
	}

	@Override
	public void run(){
		synchronized(this){
			runningThread = Thread.currentThread();
		}
		openServerSocket();
		while(! isStopped()){
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				if(isStopped()) {
					System.out.println("Server Stopped.") ;
					return;
				}
				throw new RuntimeException(
						"Error accepting client connection", e);
			}
			new Thread(
					new WorkerRunnable(
							clientSocket, "Multithreaded Server")
					).start();
		}
		System.out.println("Server Stopped.") ;
	}


	private synchronized boolean isStopped() {
		return isStopped;
	}

	public synchronized void stop(){
		isStopped = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	private void openServerSocket() {
		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port ["+serverPort+"]", e);
		}
	}

}
