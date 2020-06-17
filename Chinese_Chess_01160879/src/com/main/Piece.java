package com.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piece {
	private BufferedImage img_buf;
	private int x;
	private int y;
	private int og_x;
	private int og_y;
	
	private int flag;
	private Image img;
	
	public Piece(int p_x, int p_y, int p_flag,String p_file) {
		this.x = p_x;
		this.y = p_y;
		this.og_x = p_x;
		this.og_y = p_y;
		this.flag = p_flag;
		
		try {
			this.img_buf = ImageIO.read(new File(p_file));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		//System.out.println(p_file);
		this.img = img_buf.getScaledInstance(80, 80, img_buf.SCALE_SMOOTH);
	}
	
	public void set_xy(int new_x,int new_y) {
		this.x = new_x;
		this.y = new_y;
	}
	public void set_og_xy(int new_x,int new_y) {
		this.og_x = new_x;
		this.og_y = new_y;
	}
	
	public Image get_img() {
		return this.img;
	}
	
	public int get_x() {
		return this.x;
	}
	public int get_og_x() {
		return this.og_x;
	}
	
	public int get_y() {
		return this.y;
	}
	public int get_og_y() {
		return this.og_y;
	}
	
	public int get_flag() {
		return this.flag;
	}
	
}
