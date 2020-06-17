package com.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import javax.swing.text.DocumentFilter;

import org.omg.CORBA.portable.OutputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;



public class Window extends JComponent  
implements MouseMotionListener, ActionListener, Runnable{
	
	private static final long serialVersionUID = 1810247406099463112L;
	

	chessboard background;
	button login_button;
	button reg_button;
	button login_cancel_button;
	button reg_cancel_button;
	button menu_login_button;
	button menu_reg_button;
	JFrame login_frame;
	JFrame menu_frame;
	JFrame reg_frame;
	JFrame frame;
	String label;
	
	String random = null;
	String opponent;
	String color="1";
	String turn="-1";
	//ssl
	SSLclient socket;
	int port=2222;
	String host_re;
	
	//the cor x and y of chess
 	static double chess_obj_x [] = {1}; 
	static double chess_obj_y [] = {1};
	
	int usr_count=0;
	int pwd_count=0;
	public Window(int width, int height, String title){
		//get a frame as window
		frame = new JFrame(title);
		
		frame.setPreferredSize(new Dimension(width,height));
		frame.setMaximumSize(new Dimension(width,height));
		frame.setMinimumSize(new Dimension(width,height));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		//the window will be on top left if there is no this line
		frame.setLocationRelativeTo(null);
		

		//==========================================
		reg_frame = new JFrame("reg");
		reg_frame.setPreferredSize(new Dimension(300,150));
		reg_frame.setMaximumSize(new Dimension(300,150));
		reg_frame.setMinimumSize(new Dimension(300,150));
		reg_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		reg_frame.setResizable(false);
		reg_frame.setLocationRelativeTo(null);
		
		//panel container set up for login menu
		JPanel reg_panel = new JPanel();
		reg_panel.setLayout(new SpringLayout());
		
		JLabel reg_usr = new JLabel("User Name(6~10 letter):");
		JLabel reg_pwd = new JLabel("Password(6~10 letter):");
				
		JLabel reg_constraint = new JLabel("legal characters:");
		JLabel reg_constraint2 = new JLabel("a-z A-Z 0-9  _  ~  ?  # ");
				
		JTextField r_usr = new JTextField(20);
		JTextField r_pwd = new JTextField(20);

		r_usr.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					String usr_get = r_usr.getText();
					int keyChar = e.getKeyChar();
					if(((keyChar >= KeyEvent.VK_A && keyChar <= KeyEvent.VK_Z) ||
							(keyChar >= 97 && keyChar <= 122) ||
							(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) ||
							keyChar == KeyEvent.VK_BACK_SPACE ||
							keyChar == 126 ||keyChar == 95||keyChar == 63||keyChar == 35) &&
							usr_get.length() < 10) {
							}
					else{
						e.consume();
					}
				}
			}
		);
		r_pwd.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				String pwd_get = r_pwd.getText();
				int keyChar = e.getKeyChar();
				if(((keyChar >= KeyEvent.VK_A && keyChar <= KeyEvent.VK_Z) ||
						(keyChar >= 97 && keyChar <= 122) ||
						(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)||
						keyChar == KeyEvent.VK_BACK_SPACE ||
						keyChar == 126 ||keyChar == 95||keyChar == 63||keyChar == 35) &&
						pwd_get.length() < 10) {
						}
				else{
					e.consume();
				}
						
			}
		}
		);
				
		//JPasswordField pwd = new JPasswordField(20);
		reg_usr.setLabelFor(r_usr);
		reg_pwd.setLabelFor(r_pwd);		
		
		reg_button = new button("register",frame.getX(),frame.getY(),50,50);
		
		reg_button.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					String usr_get = r_usr.getText();
					String pwd_get = r_pwd.getText();
					if((usr_get.length() > 10 || usr_get.length() < 6) || 
						pwd_get.length() > 10 || pwd_get.length() < 6) {
						System.out.println("the username or password is not legal");
						return;
					}
					//================================
					try {
						//every send get a reply back
						host_re = socket.send("\"66\" "+usr_get+" "+pwd_get);
						String temp[] = host_re.split(" ");
						if(Objects.equals(temp[0] ,"reg")) {
							//reg success
							reg_frame.setVisible(false);
							menu_frame.setVisible(true);
							JOptionPane.showMessageDialog(menu_frame, "success");
						}else if(Objects.equals(temp[0] ,"notreg")) {
							//same name and password
							JOptionPane.showMessageDialog(reg_frame, "fail-the user name or password has been used");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//================================
				}
		});
		reg_cancel_button = new button("exit",frame.getX(),frame.getY(),50,50);
		reg_cancel_button.addActionListener( this );
		
		//add component to container
		reg_panel.add(reg_usr);
		reg_panel.add(r_usr);
		reg_panel.add(reg_pwd);
		reg_panel.add(r_pwd);
		
		reg_panel.add(reg_constraint);
		reg_panel.add(reg_constraint2);
		
		
		reg_panel.add(reg_cancel_button);
		reg_panel.add(reg_button);
		
        SpringUtilities.makeCompactGrid(reg_panel,
                4, 2, //rows, cols
                6, 6, //initX, initY
                6, 6); //xPad, yPad
		reg_frame.getContentPane().add(reg_panel);
		
		
		menu_frame = new JFrame("menu");
		
		menu_frame.setPreferredSize(new Dimension(300,150));
		menu_frame.setMaximumSize(new Dimension(300,150));
		menu_frame.setMinimumSize(new Dimension(300,150));
		menu_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu_frame.setResizable(false);
		menu_frame.setLocationRelativeTo(null);
		menu_login_button = new button("login",menu_frame.getX(),menu_frame.getY(),50,50);
		menu_reg_button = new button("register",menu_frame.getX(),menu_frame.getY(),50,50);
		//register 
		menu_reg_button.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				menu_frame.setVisible(false);
				reg_frame.setVisible(true);
			}
		});	
		//log in
		menu_login_button.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				menu_frame.setVisible(false);
				login_frame.setVisible(true);
				
			}
		});	
		JPanel menu_panel = new JPanel();
		menu_panel.setLayout(new SpringLayout());
		menu_panel.add(menu_login_button);
		menu_panel.add(menu_reg_button);
		SpringUtilities.makeCompactGrid(menu_panel,
                2, 1, //rows, cols
                6, 6, //initX, initY
                6, 6); //xPad, yPad
		menu_frame.getContentPane().add(menu_panel);
		menu_frame.setVisible(true);
		//===========================================
//		//the background, log_in button, 
//		background = new chessboard(".\\\\pics\\\\chess_board.jpg",frame.getWidth(),frame.getHeight());
//		
//		//background.setLayout(new BorderLayout());
//		frame.add(background);
//		//frame.setVisible(true);
		
		//frame container set up for login menu
		login_frame = new JFrame("login");
		login_frame.setPreferredSize(new Dimension(400,180));
		login_frame.setMaximumSize(new Dimension(400,180));
		login_frame.setMinimumSize(new Dimension(400,180));
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.setResizable(false);
		login_frame.setLocationRelativeTo(null);

		//panel container set up for login menu
		JPanel login_panel = new JPanel();
		login_panel.setLayout(new SpringLayout());
		
		//text field set up for login menu
		JLabel lbl_usr = new JLabel("User Name(6~10 letter):");
		JLabel lbl_pwd = new JLabel("Password(6~10 letter):");
		
		JLabel lbl_constraint = new JLabel("legal characters:");
		JLabel lbl_constraint2 = new JLabel("a-z A-Z 0-9  _  ~  ?  # ");
		
		//JTextField usr = new filterJTextField(20);
		//JTextField pwd = new filterJTextField(20);
		JTextField usr = new JTextField(20);
		JTextField pwd = new JTextField(20);

		//filter the legal username and password
		usr.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					String usr_get = usr.getText();
					int keyChar = e.getKeyChar();
					if(((keyChar >= KeyEvent.VK_A && keyChar <= KeyEvent.VK_Z) ||
							(keyChar >= 97 && keyChar <= 122) ||
							(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) ||
							keyChar == KeyEvent.VK_BACK_SPACE ||
							keyChar == 126 ||keyChar == 95||keyChar == 63||keyChar == 35) &&
							usr_get.length() < 10) {
					}
					else{
						e.consume();
					}
				}
			}
		);
		pwd.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				String pwd_get = pwd.getText();
				int keyChar = e.getKeyChar();
				if(((keyChar >= KeyEvent.VK_A && keyChar <= KeyEvent.VK_Z) ||
						(keyChar >= 97 && keyChar <= 122) ||
						(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)||
						keyChar == KeyEvent.VK_BACK_SPACE ||
						keyChar == 126 ||keyChar == 95||keyChar == 63||keyChar == 35) &&
						pwd_get.length() < 10) {
				}
				else{
					e.consume();
				}
				
			}
		}
	);
		
		//JPasswordField pwd = new JPasswordField(20);
		lbl_usr.setLabelFor(usr);
		lbl_pwd.setLabelFor(pwd);
		
		//login button set up for login menu 		
		try {
			set_ssl();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		login_button = new button("login",frame.getX(),frame.getY(),50,50);
		
		login_button.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					String usr_get = usr.getText();
					String pwd_get = pwd.getText();
					if((usr_get.length() > 10 || usr_get.length() < 6) || 
						pwd_get.length() > 10 || pwd_get.length() < 6) {
						System.out.println("the username or password is not legal");
						return;
					}
					//==================================================
					try {
						//every send get a reply back
						host_re = socket.send("\"01\""+usr_get+" "+pwd_get);
						String temp[] = host_re.split(" ");
						label = host_re.substring(0,4);
						if(Objects.equals(label ,"\"10\"")) {
							System.out.println("not cool, this user is already online\n");
							return;
						}else if(Objects.equals(label ,"\"11\"")) {
							//get random id and constantly asking for match
							JOptionPane.showMessageDialog(menu_frame, "Waiting for match...");
							System.out.println("waiting for match\n");
							random = temp[1];
							String no_yet = "\"02\"";
							//reuse temp
							temp[0] = no_yet;
							//ask for match every 3 sec
							while(Objects.equals(temp[0],"\"02\"")) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								no_yet = socket.send("\"02\""+" "+random);
								System.out.println("random = " + random);
								temp = no_yet.split(" ");
								if(Objects.equals(temp[0],"\"13\"")) {
									opponent = temp[1];
									color = temp[2];
									turn = temp[3];
									break;
								}
							}
							System.out.println("got the match");
							System.out.println(opponent+" "+random+" "+color+" "+turn+"\n");
							login_frame.setVisible(false);
							//the background, log_in button, 
							background = new chessboard(".\\\\pics\\\\chess_board.jpg",frame.getWidth(),frame.getHeight());
							
							//background.setLayout(new BorderLayout());
							frame.add(background);
							frame.setVisible(true);
							
							return;
							
						}else if(Objects.equals(label ,"\"12\"")) {
							//get the opponent_name  random_id  color  turn
							opponent = temp[1];
							random = temp[2];
							color = temp[3];
							turn = temp[4];
							System.out.println(opponent+" "+random+" "+color+" "+turn+"\n");
							login_frame.setVisible(false);
							//the background, log_in button, 
							background = new chessboard(".\\\\pics\\\\chess_board.jpg",frame.getWidth(),frame.getHeight());
							
							//background.setLayout(new BorderLayout());
							frame.add(background);
							frame.setVisible(true);
							return;
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//==================================================
				}
			}
		);
		login_cancel_button = new button("exit",frame.getX(),frame.getY(),50,50);
		login_cancel_button.addActionListener( this );
		
		//add component to container
		login_panel.add(lbl_usr);
		login_panel.add(usr);
		login_panel.add(lbl_pwd);
		login_panel.add(pwd);
		
		login_panel.add(lbl_constraint);
		login_panel.add(lbl_constraint2);
		
		
		login_panel.add(login_cancel_button);
		login_panel.add(login_button);
		
        SpringUtilities.makeCompactGrid(login_panel,
                4, 2, //rows, cols
                6, 6, //initX, initY
                6, 6); //xPad, yPad
		login_frame.getContentPane().add(login_panel);
		//login_frame.setVisible(true);
		

		
	}
	//========================================================
	//====================self define method==================
	//if the user name and password is not correct , return -1
	public void set_ssl() throws IOException {
		socket = new SSLclient(port);
	}
	
	//where you run the game
	@Override
	public void run() {
		
	}
	//====================self define method==================
	//========================================================
	//while clicking login and login cancel button
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == login_cancel_button ) {
			//System.out.println("exit program");
			//System.exit(0);
			login_frame.setVisible(false);
			menu_frame.setVisible(true);
			
		} else if(e.getSource() == reg_cancel_button) {
			reg_frame.setVisible(false);
			menu_frame.setVisible(true);
			
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//System.out.println(e.getX()+" "+e.getY());
	}
	//========================================================
	//========================================================
	//class to get board image, and set chess pieces 
	private class chessboard extends JPanel
	implements MouseMotionListener, ActionListener, Runnable,MouseListener{

		private static final long serialVersionUID = 441597803268331672L;
		
		JFrame info_frame;
		
		private int board_w;
		private int board_h;
		private BufferedImage img = null;
		private Image dimg;
		
		//16 chess piece each color
		private BufferedImage red;
		private BufferedImage black;

		private Piece[] red_chess = new Piece[16];
		private Piece[] black_chess = new Piece[16];
				
		//the position array of the chess pieces
		//set the beginning position of chess pieces
		//x=1~9 y=1~10 
//		private int red_chess_position[] = {5*10,4*10,6*10,3*10,7*10,2*10,8*10,1*10,9*10,
//				2*8,8*8,1*7,3*7,5*7,7*7,9*7};
//		private int black_chess_position[] = {5*1,4*1,6*1,3*1,7*1,2*1,8*1,1*1,9*1,
//				2*3,8*3,1*3,3*5,5*5,7*5,9*5};
		
		private int red_chess_x[] = {35+4*108,35+3*108,35+5*108,35+2*108,35+6*108,35+1*108,35+7*108,35+0*108,35+8*108
				,35+1*108,35+7*108,35+0*108,35+2*108,35+4*108,35+6*108,35+8*108};
		private int red_chess_y[] = {15+9*97,15+9*97,15+9*97,15+9*97,15+9*97,15+9*97,15+9*97,15+9*97,15+9*97,
				15+7*97,15+7*97,15+6*97,15+6*97,15+6*97,15+6*97,15+6*97};
		private int black_chess_x[] = {35+4*108,35+3*108,35+5*108,35+2*108,35+6*108,35+1*108,35+7*108,35+0*108,35+8*108
				,35+1*108,35+7*108,35+0*108,35+2*108,35+4*108,35+6*108,35+8*108};
		private int black_chess_y[] = {15+0*97,15+0*97,15+0*97,15+0*97,15+0*97,15+0*97,15+0*97,15+0*97,15+0*97,
				15+2*97,15+2*97,15+3*97,15+3*97,15+3*97,15+3*97,15+3*97};
		
		//x(left to right) 9    y(up to down) 10
		private int[][] legal_move_x = new int[9][10];
		private int[][] legal_move_y = new int[9][10];
		
		private int x[] = {0,1,2,3,4,5,6,7,8};
		private int y[] = {0,1,2,3,4,5,6,7,8,9};
		private int flag = -1;
		public chessboard(String file,int Width,int Height) {
			board_w = Width;
			board_h = Height;
			try {
			    img = ImageIO.read(new File(file));
			} catch (IOException e) {
			    e.printStackTrace();
			}
			dimg = img.getScaledInstance(board_w-100, board_h-100,
					img.SCALE_SMOOTH);
			
			for(int i=0;i<9;i++) {
				for(int j=0;j<10;j++) {
					legal_move_x[i][j] = 35+i*108;
					legal_move_y[i][j] = 15+j*97;
				}
			}	
			for(int i=0;i<16;i++) {
				red_chess[i] = new Piece(red_chess_x[i],red_chess_y[i],i,".\\\\pics\\\\red_"+i+".jpg");
				black_chess[i] = new Piece(black_chess_x[i],black_chess_y[i],/*16+*/i,".\\\\pics\\\\black_"+i+".jpg");
			}			
			
			setBackground(Color.WHITE);  
			addMouseMotionListener( this );
			addMouseListener( this );
		
			//=================info panel=================
			//frame container set up for user info
			info_frame = new JFrame("info");
			info_frame.setPreferredSize(new Dimension(300,150));
			info_frame.setMaximumSize(new Dimension(300,150));
			info_frame.setMinimumSize(new Dimension(300,150));
			info_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			info_frame.setResizable(false);
			info_frame.setLocationRelativeTo(null);
					
			//panel container set up for login menu
			JPanel info_panel = new JPanel();
			info_panel.setLayout(new SpringLayout());
					
			//text field set up for login menu
			JLabel opponent_name = new JLabel("   Your opponent name : "+opponent);
			JLabel piece_color = new JLabel("   Your piece color : "+(Objects.equals(color,"0")?"red":"black"));
					
			//add component to container
			info_panel.add(opponent_name);
			info_panel.add(piece_color);
			
			JButton surrender_button = new JButton("surrender");
			surrender_button.setText("surrender");
			surrender_button.setPreferredSize(new Dimension(50,50));
			
			surrender_button.addActionListener( new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(Objects.equals(turn,"1")) {
						try {
							host_re = socket.send("surrender "+random);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println("exit program");
						System.exit(0);
					}
				}
			});
					
			
			info_panel.add(surrender_button);
			
			SpringUtilities.makeCompactGrid(info_panel,
	                3, 1, //rows, cols
	                6, 6, //initX, initY
	                6, 6); //xPad, yPad
			info_frame.getContentPane().add(info_panel);
			info_frame.setAlwaysOnTop(true);
			info_frame.setVisible(true);
			//=========================================================
			
			if(Objects.equals(turn,"0"))
				const_ask();
			
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(dimg,50,25, this);
			
			//draw chess here, get the position of chess every time taking turns
			for(int i=0;i<16;i++) {
				g.drawImage(red_chess[i].get_img(),red_chess[i].get_x(),red_chess[i].get_y(), this);
				g.drawImage(black_chess[i].get_img(),black_chess[i].get_x(),black_chess[i].get_y(), this);
			}
			
		}

		@Override
		public void run() {

			//========================
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.out.println("click");
		}
		
		public void mousePressed(MouseEvent e) {
			if(Objects.equals(turn,"1")) {
					
				for(int i=0;i<16;i++) {
					if(Objects.equals(color,"0") && e.getX()-red_chess[i].get_x()<=40 && e.getX()-red_chess[i].get_x()>=0
							&&
							e.getY()-red_chess[i].get_y()<=40 && e.getY()-red_chess[i].get_y()>=0){
						flag = red_chess[i].get_flag();
						break;
					}
					if(Objects.equals(color,"1") && e.getX()-black_chess[i].get_x()<=40 && e.getX()-black_chess[i].get_x()>=0 
							&&
							e.getY()-black_chess[i].get_y()<=40 && e.getY()-black_chess[i].get_y()>=0){
						flag = black_chess[i].get_flag();
						break;
					}
				}
				return;
			}
		}
		public void mouseReleased(MouseEvent e) {
		    // TODO: add your code here
			if(Objects.equals(turn,"1")) {
					
				for(int i=0;i<9;i++) {
					for(int j=0;j<10;j++) {
						if(Objects.equals(color,"0")) { 
							if(e.getX()-legal_move_x[i][j]<=60 && e.getX()-legal_move_x[i][j]>=0 &&
								e.getY()-legal_move_y[i][j]<=60 && e.getY()-legal_move_y[i][j]>=0) {
								
								if(move_change(red_chess[flag],legal_move_x[i][j],legal_move_y[i][j])<0) {
									red_chess[flag].set_xy(red_chess[flag].get_og_x(), red_chess[flag].get_og_y());
									repaint();
									break;
								}
								
								red_chess[flag].set_xy(legal_move_x[i][j], legal_move_y[i][j]); 
								red_chess[flag].set_og_xy(legal_move_x[i][j], legal_move_y[i][j]);
								repaint();
								send_move(flag,legal_move_x[i][j],legal_move_y[i][j]);
								
							}
							else {
								red_chess[flag].set_xy(red_chess[flag].get_og_x(), red_chess[flag].get_og_y());
								repaint();
							}
						}
						
						else if(Objects.equals(color,"1")) {
							if((e.getX()-legal_move_x[i][j]<=60) && (e.getX()-legal_move_x[i][j]>=0) &&
								(e.getY()-legal_move_y[i][j]<=60) && (e.getY()-legal_move_y[i][j]>=0)) {
								
								if(move_change(black_chess[flag],legal_move_x[i][j],legal_move_y[i][j])<0) {
									black_chess[flag].set_xy(black_chess[flag].get_og_x(), black_chess[flag].get_og_y());
									repaint();
									break;
								}
								black_chess[flag].set_xy(legal_move_x[i][j], legal_move_y[i][j]); 
								black_chess[flag].set_og_xy(legal_move_x[i][j], legal_move_y[i][j]);
																	
								repaint();
								send_move(flag,legal_move_x[i][j],legal_move_y[i][j]);
								
							}
							else {
								black_chess[flag].set_xy(black_chess[flag].get_og_x(), black_chess[flag].get_og_y());
								repaint();
							}
					
						}
						
					}
				}	
				flag = -1;
				return;
			}
		 }
		
		void send_move(int f, int x,int y) {
			try {
				
				host_re = socket.send("\"03\" "+random+" "+f+" "+x+" "+y);
				turn = "0";//give out the turn
				const_ask();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//String temp[] = host_re.split(" ");
		}
		
		 public void mouseClicked(MouseEvent e) {	 
		 }

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(Objects.equals(turn,"1")) {
				if(Objects.equals(color,"0")) {
					red_chess[flag].set_xy(e.getX(),e.getY());
				}
				else if(Objects.equals(color,"1")){
					black_chess[flag].set_xy(e.getX(),e.getY());
				}	
				repaint();
				return;
			}
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		private void const_ask() {
			
//			if(Objects.equals(turn,"1")||Objects.equals(turn,"-1")) {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//			}else 
				if(Objects.equals(turn,"0")) {
				host_re = "\"04\"";//
				String temp[] = host_re.split(" ");
				while(Objects.equals(temp[0],"\"04\"")) {
					
					try {
						Thread.sleep(2000);
						//send        label/randomid/flag/new x/new y
						//get         "03"   and wait for opponent to finish
						host_re = socket.send("\"04\" "+random);
						temp = host_re.split(" ");
						if(Objects.equals(temp[0],"newstep") && temp.length>=3) {
							//change opposite color
							if(Objects.equals(color,"0")) {
								black_chess[Integer.parseInt(temp[1])].set_og_xy(black_chess[Integer.parseInt(temp[1])].get_x(),black_chess[Integer.parseInt(temp[1])].get_y());
								black_chess[Integer.parseInt(temp[1])].set_xy(Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
						
								//move_change(Integer.parseInt(temp[1]));
								
								turn="1";
								break;
							}else if(Objects.equals(color,"1")) {
								red_chess[Integer.parseInt(temp[1])].set_og_xy(red_chess[Integer.parseInt(temp[1])].get_x(),red_chess[Integer.parseInt(temp[1])].get_y());
								red_chess[Integer.parseInt(temp[1])].set_xy(Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
								
								//move_change(Integer.parseInt(temp[1]));
								
								turn="1";
								break;
							}
							
						}else if(Objects.equals(temp[0],"win")) {
							JOptionPane.showMessageDialog(info_frame, "You win!!!");
							frame.setVisible(false);
							info_frame.setVisible(false);
							menu_frame.setVisible(true);
						}else if(Objects.equals(temp[0],"timeout")) {
							JOptionPane.showMessageDialog(info_frame, "opponent timeout, the game is shutting down");
							frame.setVisible(false);
							info_frame.setVisible(false);
							menu_frame.setVisible(true);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
		private int move_change(Piece chess,int newx,int newy) {
			int l=-1;
			switch(chess.get_flag()) {
				case 0:
					//general
					if((newx==chess.get_og_x()&& (newy==chess.get_og_y()+97 || newy==chess.get_og_y()-97)) ||
							(newy==chess.get_og_y()&& (newx==chess.get_og_x()+108 || newx==chess.get_og_x()-108)) &&
							newx<=legal_move_x[5][0] && newx>=legal_move_x[3][0] &&
							newy<=legal_move_y[2][0] && newy>=legal_move_y[0][0]) {
						l=0;
						for(int i=0;i<16;i++) {
							//if()
						}
					}
				break;
				
				case 1:
					//advisor
					
				break;
				
				case 2:
					//elephant
				break;
				
				case 3:
					//horse
				break;
				
				case 4:
					//chariot
				break;
				
				case 5:
					//cannon
				break;
				
				case 6:
					//soldier
				break;
				
			}
			return 0;
		}
	}
	//===============================================================
	//===============================================================
	private class filterJTextField extends DocumentFilter{
		private int limit;
		
		
	}
	private class button extends JButton{
		public button(String name,int x,int y,int w,int h) {
			this.setText(name);
			this.setPreferredSize(new Dimension(w,h));
		}
	}
	
}


