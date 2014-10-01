package com.example.GestureLayer;

import java.util.ArrayList;

public class GestureGroup {
	RawGesture median;
	ArrayList<Double> deviations;
	
	public GestureGroup() {
		super();
		median = null;
		deviations = new ArrayList<Double>();
		// TODO Auto-generated constructor stub
	}
	public GestureGroup(GestureGroup gp){
		median = new RawGesture(gp.median);
		for(int i = 0; i < gp.deviations.size(); ++i){
			deviations.add(gp.deviations.get(i));
		}
	}
	
	
	public double compare(RawGesture g){
		return -1;
	}	
	
	
}
