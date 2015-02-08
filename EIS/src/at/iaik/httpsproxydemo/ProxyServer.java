package at.iaik.httpsproxydemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {

	public static void main(String[] args) {
		ProxyServer server = new ProxyServer();
		server.runServer(8080);
	}

	public void runServer(int socket) {
		try {
			// creating server socket
			ServerSocket servsocket = new ServerSocket(socket);
			System.out.println("ServerSocket created");
			while (true) {
				// accepting client request
				Socket client = servsocket.accept();
				System.out.println("Client connected, creating HandlerThread");
			
				// creating handler thread
				ProxyClientThread thread = new ProxyClientThread(client);
				thread.start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
