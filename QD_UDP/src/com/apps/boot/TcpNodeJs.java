package com.apps.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpNodeJs {
	public void test(){
		Socket socket;
		try {
			socket = new Socket("127.0.0.1", 8809);
			BufferedReader sin = new BufferedReader(
			        new InputStreamReader(System.in));
			    PrintWriter os = new PrintWriter(socket.getOutputStream());
			    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    String readline;
			    readline = sin.readLine();
			    while (!readline.equals("bye")) {
			      os.println(readline);
			      os.flush();
			      System.out.println("Client:" + readline);
			      System.out.println("Server:" + is.readLine());
			      readline = sin.readLine();
			    }
			    os.close();
			    is.close();
			    socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		TcpNodeJs js = new TcpNodeJs();
		js.test();
	}
}
