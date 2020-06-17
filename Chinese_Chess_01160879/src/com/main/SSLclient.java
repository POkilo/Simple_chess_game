package com.main;

import java.io.*;
import java.net.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLclient {
	
	private OutputStream to_server;
	private SSLSocket socket;

	private String trustpath = "C:\\Program Files\\Java\\jre1.8.0_181\\lib\\security\\test";
	private String trustname = "sslclienttrust";
	private String trustpwd = "changeit";
	private String host = "192.168.204.1";
	private InputStream in_stream;
	//private String reply;
	
	private int port;
	//constructor : setup ssl socket
	public SSLclient(int po)  {
		port = po;
	}
	
	public String send(String message) throws UnknownHostException, IOException {
		
		boolean debug = false;
		
		String trustFilename = trustpath+"\\"+trustname;
		
		System.setProperty("javax.net.ssl.trustStore",trustFilename);
		System.setProperty("javax.net.ssl.trustStorePassword",trustpwd);
			
		if(debug)
			System.setProperty("javax.net.debug", "all");
		
		SSLSocketFactory socket_f = (SSLSocketFactory) SSLSocketFactory.getDefault();
		socket = (SSLSocket)socket_f.createSocket(host, port);
		//o stream
		//to_server = socket.getOutputStream(); //og
		PrintWriter to_server = new PrintWriter(socket.getOutputStream(), true);
		
		//i stream
		in_stream = socket.getInputStream();
		BufferedReader bufferedreader = new BufferedReader( new InputStreamReader(in_stream));		
		
		//to_server.write(message.getBytes()); //og
		to_server.println(message);
		to_server.flush();
		
		//every send get a reply back
		String reply = null;
		reply = bufferedreader.readLine();
		System.out.println("message get at client\""+reply+"\"");
        System.out.flush();      
		
		in_stream.close();
		to_server.close();
		socket.close();
		
		return reply;
	}
	
	
	
}


