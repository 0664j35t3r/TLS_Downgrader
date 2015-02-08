package at.iaik.httpsproxydemo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ProxyClientThread extends Thread {
	private Socket _client = null;
	private Socket _server = null;

	public ProxyClientThread(Socket client) {
		_client = client;
	}

	public void run() {
		try {
			while (_client.isClosed() == false) {
				// reading available data from client

				// just a quick hack to check whether the CONNECT HTTP request
				// is present (required for using an HTTPS proxy)
				// in the real world parsing would be implemented in a more
				// robust and efficient way...
				byte[] clientdata = Util.readAll(_client.getInputStream());
				
				// if stream is invalid, exiting and terminating
				if (clientdata == null) {
					break;
				}

				String request = new String(clientdata);
				// if data is present, proceed
				if (clientdata.length > 0) {
				    //System.out.println("request: " + request);
					if (request.startsWith("CONNECT")) {
						// find host name which is also present in the HTTP
						// CONNECT request
						String host = Util.findHost(clientdata);
						System.out.println("host: " + host);
						// creating server socket connection
						_server = new Socket(host, 443);

						// sending ok response to client
						// creating output stream to client
						DataOutputStream outbound = new DataOutputStream(
								_client.getOutputStream());
						// Send the response header
						outbound.writeBytes("HTTP/1.0 200 OK\r\n");
						outbound.writeBytes("Content-type: text/html\r\n");
						// outbound.writeBytes("Content-Length: " + 128 +
						// "\r\n");
						outbound.writeBytes("\r\n");
						outbound.flush();

						// creating ProxyServerThread managing response of
						// server and starting it
						System.out.println("creating server thread");
						ProxyServerThread serverthread = new ProxyServerThread(
								_server, _client);

						serverthread.start();

						continue;
					} else {
						System.out.println();
						System.out.println("client");
						System.out.println("content-type: " + clientdata[0]);
						System.out.println("version: " + clientdata[1] + "." + clientdata[2]);
						System.out.println("length: " + clientdata[3] + "." + clientdata[4]);
						System.out.println();

						
						//0x14 	20 	ChangeCipherSpec
						//0x15 	21 	Alert
						//0x16 	22 	Handshake
						//0x17 	23 	Application
						//0x18 	24 	Heartbeat
						if ((clientdata[0] == 0x16) && 
							(clientdata[1] == 0x03) && (clientdata[2] == 0x01) && 
							(clientdata[5] == 0x01)) {
		
							System.out.println("ClientHello");	
							_server.getOutputStream().write(clientdata);
							_server.getOutputStream().flush();
						}
						else
						{
							_server.getOutputStream().write(clientdata);
							_server.getOutputStream().flush();
						}
						
						
						/*System.out.println();
						System.out.println("client2");
						System.out.println("content-type: " + clientdata[0]);
						System.out.println("version: " + clientdata[1] + "."
								+ clientdata[2]);
						System.out.println();*/
						
						// writing tls data to server
						
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Closing Socket!");
			try {
				_client.close();
			} catch (IOException e1) {

			}
		}
	}

}
