package com.example.Constants;

import java.util.Random;


public class Globals {
	
	static Globals singleton = new Globals();
	
	
	public static long refTime;
	
	public static Random r = new Random();
	
	private Globals(){
		refTime = -1;
	}
	
	public static Globals getInstance(){
        return singleton;
    }
	
	public static void setRefTime(long newRefTime){
		refTime = newRefTime;
	}
}
