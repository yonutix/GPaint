package com.example.GeometricEngine;

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Vector2() {
		super();
	}
	
	public Vector2(Vector2 b) {
		super();
		x = b.x;
		y = b.y;
	}
	
	public double getDist(Vector2 a){
		return Math.sqrt(x*a.x + y*a.y);
	}
	
	
	
}
