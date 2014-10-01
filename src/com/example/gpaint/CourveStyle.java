package com.example.gpaint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.example.Constants.Globals;
import com.example.GeometricEngine.Vector2;
import com.example.GeometricEngine.Vector3;
import com.example.GestureLayer.RawGesture;
import com.example.SignalProcessing.SignalProcessingEngine;

public class CourveStyle {
	public RawGesture gesture;

	public Vector2 scale;
	public Vector2 rot;
	public Vector2 trans;
	
	public static Random r = new Random();
	
	int color;

	public CourveStyle() {
		scale = new Vector2(1.0f, 1.0f);
		rot = new Vector2();
		trans = new Vector2();
		color = getRandColor();
	}
	
	
	public static int getRandColor(){
		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(Color.BLUE);
		colors.add(Color.RED);
		colors.add(Color.MAGENTA);
		colors.add(Color.YELLOW);
		colors.add(Color.CYAN);
		colors.add(Color.GREEN);
		
		int x = r.nextInt(colors.size());
		return colors.get(x);
	}
	
	public CourveStyle(Queue<Vector3> accData) {
		ArrayList<Vector3> checkpoints = new ArrayList<Vector3>();
		scale = new Vector2(1.0f, 1.0f);
		rot = new Vector2();
		trans = new Vector2();
		
		ArrayList<Vector3> speedSupport = new ArrayList<Vector3>();
		ArrayList<Vector3> distSupport = new ArrayList<Vector3>();
		
		for(Vector3 v: accData){
			speedSupport.add(v);
		}

		accData.clear();
		
		Queue<Vector3> tmpQ = new LinkedList<Vector3>();
		
		tmpQ.add(speedSupport.get(0));
		
		for(int i = 1; i < speedSupport.size(); ++i){
			
			tmpQ.add(speedSupport.get(i));
			Vector3 speed = SignalProcessingEngine.integration(tmpQ);
			if(distSupport.size() > 0)
				speed = speed.add(distSupport.get(distSupport.size()-1));
			distSupport.add(speed);
		}
		
		tmpQ.clear();
		tmpQ.add(distSupport.get(0));

		for(int i = 1; i < distSupport.size(); ++i){
			tmpQ.add(distSupport.get(i));
			Vector3 dist = SignalProcessingEngine.integration(tmpQ);
			if(checkpoints.size() > 0)
				dist = dist.add(checkpoints.get(checkpoints.size()-1));
			checkpoints.add(dist);
		}
		
		gesture = new RawGesture(checkpoints);
		gesture.normalize();
		color = getRandColor();
	}
	
	
	public CourveStyle(ArrayList<Vector3> speedSupport) {
		ArrayList<Vector3> checkpoints = new ArrayList<Vector3>();
		scale = new Vector2(1.0f, 1.0f);
		rot = new Vector2();
		trans = new Vector2();
		
		ArrayList<Vector3> distSupport = new ArrayList<Vector3>();
		
		
		Queue<Vector3> tmpQ = new LinkedList<Vector3>();
		
		tmpQ.add(speedSupport.get(0));
		
		for(int i = 1; i < speedSupport.size(); ++i){
			
			tmpQ.add(speedSupport.get(i));
			Vector3 speed = SignalProcessingEngine.integration(tmpQ);
			if(distSupport.size() > 0)
				speed = speed.add(distSupport.get(distSupport.size()-1));
			distSupport.add(speed);
		}
		
		speedSupport.clear();
		
		tmpQ.clear();
		tmpQ.add(distSupport.get(0));

		for(int i = 1; i < distSupport.size(); ++i){
			tmpQ.add(distSupport.get(i));
			Vector3 dist = SignalProcessingEngine.integration(tmpQ);
			if(checkpoints.size() > 0)
				dist = dist.add(checkpoints.get(checkpoints.size()-1));
			checkpoints.add(dist);
		}
		
		gesture = new RawGesture(checkpoints);
		gesture.normalize();
		color = getRandColor();
		
	}

	public void drawCourve(Canvas grid, Paint paint, int color) {
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);
		
		
		
		paint.setShader(new LinearGradient(0, 0, 0, grid.getHeight(), this.color, Color.WHITE, Shader.TileMode.MIRROR));
		//paint.set
		for (int i = 0; i < gesture.acceleration.size() - 1; ++i) {
			Vector3 p1 = new Vector3(gesture.acceleration.get(i));
			Vector3 p2 =new Vector3(gesture.acceleration.get(i + 1));
			
			Vector3 screenP1 = DrawCanvas.normalToScreen.motionFunction(p1, trans, scale, null);
			Vector3 screenP2 = DrawCanvas.normalToScreen.motionFunction(p2, trans, scale, null);
			
			DrawCanvas.drawLine(screenP1.x, screenP1.y, screenP2.x, screenP2.y, paint, grid);
			
		}
		paint.reset();
	}

}
