package com.example.gpaint;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GPaintInstance {
	ArrayList<CourveStyle> courves;
	
	public GPaintInstance() {
		super();
		this.courves = new ArrayList<CourveStyle>();
	}
	
	public void add(CourveStyle cs){
		courves.add(cs);
	}
}
