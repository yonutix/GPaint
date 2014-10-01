package com.example.gpaint;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

import com.example.GeometricEngine.Vector3;
import com.example.GestureLayer.GestureListener;
import com.example.GestureLayer.RawGesture;

public class ServiceGestureListener implements GestureListener {
	MainActivity context;
	
	RawGesture currentGesture;
	
	public static Queue<Vector3> rawData;

	public ServiceGestureListener(MainActivity context) {
		super();
		this.context = context;
		currentGesture = null;
		rawData = new LinkedList<Vector3>();
	}

	@Override
	public void onGesture(RawGesture g) {

	}

	@Override
	public void onDelimiter() {

	}

	@Override
	public void onRecording() {
		
	}

	@Override
	public void onDataAquisition(Vector3 data) {
		if(DrawCanvas.touchStatus == DrawCanvas.ON){
			NewPaint.board.invalidate();
			rawData.add(data);
		}
	}
	
	

}
