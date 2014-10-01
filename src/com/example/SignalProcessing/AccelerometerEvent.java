package com.example.SignalProcessing;

import com.example.Constants.Globals;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class AccelerometerEvent implements SensorEventListener {
	//static boolean ok  = false;
	static long timestamp = 0;
	
	public AccelerometerEvent(){}
	
	@Override
	public void onAccuracyChanged(Sensor s, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent rawVal) {
		
		Log.v("yonutix", "Sensor updated");
		
		if (rawVal.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
			return;
		
		timestamp = rawVal.timestamp;
		
		if(Globals.refTime == -1){
			Globals.refTime = timestamp;
		}
		
		SignalProcessingEngine.getInstance().update(rawVal);	
	}
	
	public static long getTime(){
		return timestamp;
	}
	
	public static void resetTime(){
		Globals.refTime = timestamp;
	}

}
