package at.iaik.httpsproxydemo;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ProxyServerThread extends Thread {
	private Socket _server;
	private Socket _client;

	public ProxyServerThread(Socket server, Socket client) {
		_server = server;
		_client = client;

	}

	public void run() {
		BufferedOutputStream clientOut = null;
		try {
			clientOut = new BufferedOutputStream(_client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		try {
			while (_server.isClosed() == false && _client.isClosed() == false) {
				// reading data from server (if available)
				byte[] serverdata = Util.readAll(_server.getInputStream());
				
				// if stream is invalid, exiting and terminating
				if (serverdata == null) {
					_client.shutdownOutput();
					_client.shutdownInput();
					_server.shutdownInput();
					_server.shutdownOutput();
					break;
				}

				/*System.out.println();
				System.out.println("server");
				System.out.println("content-type: " + serverdata[0]);
				System.out.println("version: " + serverdata[1] + "." + serverdata[2]);
				System.out.println();*/
				
				//0x14 	20 	ChangeCipherSpec
				//0x15 	21 	Alert
				//0x16 	22 	Handshake
				//0x17 	23 	Application
				//0x18 	24 	Heartbeat
				/*if (serverdata[0] == 22 || serverdata[0] == 20 || serverdata[0] == 23) {
					serverdata[2] = 0x01;
				}*/
				/*System.out.println();
				System.out.println("server2");
				System.out.println("content-type: " + serverdata[0]);
				System.out.println("version: " + serverdata[1] + "."
						+ serverdata[2]);
				System.out.println();*/
				
				// writing tls data to client
				clientOut.write(serverdata);
				clientOut.flush();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("in catch, so really quit");
		}
	}
	
	synchronized void shutdown()
	{
		if (_server.isClosed() == false)
		{
			try {
				_server.shutdownInput();
				_server.shutdownOutput();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}