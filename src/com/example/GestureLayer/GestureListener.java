package com.example.GestureLayer;

import com.example.GeometricEngine.Vector3;

public interface GestureListener {
	public void onGesture(RawGesture g);
	public void onDelimiter();
	public void onRecording();
	public void onDataAquisition(Vector3 data);
}
