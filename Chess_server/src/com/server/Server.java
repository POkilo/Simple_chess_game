package com.server;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class Server {
	
	//the user no that is waiting for a match
	private static int waiting = -1;
	
	//the user will keep asking if the other user is finishing his/her 
	//turn or not in the thread
	public static void main(String[] args) throws Exception {
		
		ssl_server s1 = new ssl_server("host1");
		//ssl_server s2 = new ssl_server("host2");
		System.out.println("end");
	}
	
	
	
	//=========================================================================
	
	//private class ssl_server start=====================================================
	private static class ssl_server extends Thread{ 
		private String return_to_client;

		private User_info user[] = {
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1),
				new User_info("\"","\"",-1,"0",-1,-1,-1)
		};
				
		public ssl_server(String host_name) throws IOException, InterruptedException {
			String pathToStores = "C:\\Program Files\\Java\\jre1.8.0_181\\lib\\security\\test"; // The Directory
			String keyStoreFile = "sslserverkeys"; // The FileName
			String passwd = "changeit"; // The Password
			int theServerPort = 2222;
			boolean debug = false;
			
			String trustFilename = pathToStores + "\\" + keyStoreFile;
			
			System.setProperty("javax.net.ssl.keyStore", trustFilename);
			System.setProperty("javax.net.ssl.keyStorePassword", passwd);
			if (debug)
				System.setProperty("javax.net.debug", "all");
			
			SSLServerSocketFactory sslssf = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket) sslssf
					.createServerSocket(theServerPort);
		
			while(true) {
				
				System.out.println(host_name);
				SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
				InputStream sslIS = sslSocket.getInputStream();
				
				//For Writing Back to the Client
				//OutputStream to_client = sslSocket.getOutputStream();	//og	
				PrintWriter to_client = new PrintWriter(sslSocket.getOutputStream(), true);
				
				//Read from the Client
		        BufferedReader bufferedreader = new BufferedReader( new InputStreamReader(sslIS));
		        
		        String string = null;
		        
		       // while ((string = bufferedreader.readLine()) != null) {
		        string = bufferedreader.readLine() ;
		        System.out.println("message get at server\""+string+"\"");
		        System.out.flush();
		            
		        return_to_client = check_purpose(string); 
		        to_client.println(return_to_client);
			    to_client.flush();        
		        		        
		        
		        to_client.close();
		        sslIS.close();
				sslSocket.close();
		        
				System.out.println("handshake over \n");
	
			}
		}

		private String check_purpose(String m) {
			
			//get tag "xx"
			String label = m.substring(0,4);
			String te[] = m.split(" "); 
			//when user log in
			//return random id + 
			//"1" if user got a match/"0" ask user to wait for a match
			if(Objects.equals(label ,"\"01\"")) {
				//usr[0] is name     usr[1] is password
				String usr[] = m.substring(4).split(" "); 
				//====================search for userinfo==============
				for(int i=0;i<user.length;i++) {
					if(Objects.equals(user[i].get_name(),usr[0]) && 
							Objects.equals(user[i].get_password(),usr[1])) {
						
						if(user[i].get_online()==0){
							return "\"10\"not cool, this user is already online";
						}else if(user[i].get_online()==-1){
							System.out.println("the user "+user[i].get_name()+" is online now");
							//set a random number to the user
							user[i].set_random_id(Integer.toString(new Random().nextInt(100000)));  
							System.out.println("set the random id "+user[i].get_random_id());
							//set online to 0
							user[i].set_online(0);
							
							int match = set_match(i);
							//just log in and haven't got the match
							//check for matches  0 waiting match/1 got match
							if(match == 0) {
								return "\"11\" "+user[i].get_random_id();	
							}else if(match == 1){
								//just log in and got the match
								//return opponent name and turn(whether you got turn or not)
								return "\"12\" "+user[user[i].get_opponent()].get_name()+
										" "+user[i].get_random_id()+
										" "+Integer.toString(user[i].get_color())+
										" "+Integer.toString(user[i].get_turn());
							}
						}
						
					}
					
				}
				//=======================================================
			}
			//ask for match
			//message[1] = random id of the user
			else if(Objects.equals(label ,"\"02\"")) {
			
				String message[] = m.split(" "); 
				for(int i=0;i<user.length;i++) {
					if(message.length >= 2 && 
							Objects.equals(message[1],user[i].get_random_id()) &&
							user[i].get_opponent() != -1) {
						//found the opponent
						return "\"13\" "+user[user[i].get_opponent()].get_name()+
								" "+Integer.toString(user[i].get_color())+
								" "+Integer.toString(user[i].get_turn());
					}
					
				}
				return "\"02\"";
				
			}	
			//move change during the match, the user will send message when change done
			//the rule is reflect on the client side
			// label/randomid/flag/new x/new y
			else if(Objects.equals(label ,"\"03\"")) {
				String message[] = m.split(" ");
				for(int i=0;i<user.length;i++) {
					if(message.length >= 3 && 
							Objects.equals(message[1],user[i].get_random_id())) {
								user[user[i].opponent].set_new_position(message[2], message[3], message[4]);
								user[user[i].opponent].set_turn(1);
								user[i].set_turn(0);
								return "wait for opponent";
							}
				}
			}
			//the user who is'nt taking the turn will keep asking 
			//whether the opponent is finish or not  
			else if(Objects.equals(label ,"\"04\"")) {
				String message[] = m.split(" ");
				for(int i=0;i<user.length;i++) {
					if(Objects.equals(message[1],user[i].get_random_id())) {
						if(user[i].get_win()==1) {
							return "win";
						}
						else if(Objects.equals(user[i].turn,1)) {
							return "newstep "+user[i].get_flag()+" "+user[i].get_x()+" "+user[i].get_y();
						}else if(user[i].get_timeout()==30) {
							return "timeout";
						}	
						user[i].timeout_inc();
					}
				}
			}
			else if(Objects.equals(te[0] ,"surrender")) {
				for(int i=0;i<user.length;i++) {
					if(te.length>=1 && Objects.equals(te[1],user[i].get_random_id()) ) {
						user[user[i].opponent].set_win(1);
					}	
				}
			}
			else if(Objects.equals(te[0] ,"\"66\"")) {
				for(int i=0;i<user.length;i++) {
					if(Objects.equals(user[i].get_name(),"\"")  
							&& Objects.equals(user[i].get_password(),"\"")){
						user[i].set_name(te[1]);
						user[i].set_pwd(te[2]);
						return "reg";
					}else if(Objects.equals(user[i].get_name(),te[1])  
							&& Objects.equals(user[i].get_password(),te[2])){
						return "noreg";
					}
				}
			}
			
			return m;//echo     do not ddo this     remember to remove this 
		}
		
		//if got a match/give the id of the opponent
		//return 0 if no match
		//return 1 if got a match
		public int set_match(int no) {
			
			if(waiting == -1) {
				//put user into waiting
				waiting = no;
				return 0;
			}else {
				//pair the users
				//user[no].opponent = user[waiting].random_id;
				//user[waiting].opponent = user[no].random_id;
				user[no].set_opponent(waiting);
				user[waiting].set_opponent(no);
				
				//randomly decide who got the first hand/turn
				//no need to decide red/black randomly
				Random rand = new Random();
				
				user[no].set_color(0); user[waiting].set_color(1);
				
				if(rand.nextInt(2) == 1) {
					user[no].set_turn(1); user[waiting].set_turn(0);
				}	
				else {
					user[no].set_turn(0); user[waiting].set_turn(1);	
				}
						
				//set non waiting
				waiting = -1;
				return 1;
			}
			
		}
		
		//private class ssl_server end=====================================================
	
	}
	
	private static class User_info{
		//the statement of the user
		private String name;
		private String password;
		private int win=0;//1 = win
		//-1 not login/0 login
		private int online;
		//-1 not login/0 wait for the other user/random num,randomly generated id for user 
		private String random_id;
		//-1 no other user/the array no of the oponent(won't send to any user)
		private int opponent;
		//-1 no other user/0 red/1 black
		private int color;
		//-1 no other user/0 others turn/1 user's turn
		private int turn;
		private int timeout=0;
		//
		private String flag;
		private String new_x;
		private String new_y;
		
		public User_info(String usr,String pwd,int on,String id, int opo, int col, int tu) {
			this.name = usr;
			this.password = pwd;
			this.online = on;
			this.random_id = id;
			this.opponent = opo;
			this.color = col;
			this.turn = tu;
		}
		private String get_name() {
			return this.name;
		}
		private void set_name(String n) {
			this.name=n;
		}
		private String get_password() {
			return this.password;
		}
		private void set_pwd(String p) {
			this.password=p;
		}
		
		private void set_online(int on) {
			this.online = on;
			return;
		}
		private int get_online() {
			return this.online;
		}
		
		
		private void set_random_id(String id) {
			//give user a random id, setup connection base on this id
			this.random_id = id;
			return;
		}
		private String get_random_id() {
			return this.random_id;
		}
		
		private void set_opponent(int op) {
			this.opponent = op;
			return;
		}
		private int get_opponent() {
			return this.opponent;
		}
		
		private void set_color(int co) {
			//red = 0     black = 1 
			this.color = co;
			return;
		}
		private int get_color() {
			return this.color;
		}
		
		private void set_turn(int tu) {
			this.turn = tu;
			return;
		}private int get_turn() {
			return this.turn;
		}
		
		private void set_new_position(String f,String x,String y) {
			this.flag=f;
			this.new_x=x;
			this.new_y=y;
			return;
		}
		private String get_flag() {
			return this.flag;
		}
		private String get_x() {
			return this.new_x;
		}
		private String get_y() {
			return this.new_y;
		}
		
		private void set_win(int w) {
			this.win = w;
		}
		private int get_win() {
			return this.win;
		}
		
		private void timeout_inc() {
			this.timeout++;
		}
		private void timeout_zero() {
			this.timeout=0;
		}
		private int get_timeout() {
			return this.timeout;
		}
	}
	
	//when 2 gamers been matched up by server
	private static class room {
		private User_info player1;
		private User_info player2;
		
		public room() {
			
		}
	}
}
