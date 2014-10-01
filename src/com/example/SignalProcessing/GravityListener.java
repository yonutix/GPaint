package com.example.SignalProcessing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.GeometricEngine.Vector3;

public class GravityListener implements SensorEventListener{
	static Vector3 values;
	static Vector3 angles;
	public static final double TRESHOLD = 1.0;
	
	public GravityListener() {
		super();
		values = new Vector3();
		angles = new Vector3();
	}
	

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		values.x = event.values[0];
		values.y = event.values[1];
		values.z = event.values[2];
		
		
		angles.x = getOxAngle();
		angles.y = getOyAngle();
		angles.z = getOzAngle();
	}
	
	public static double getOzAngle(){
		Vector3 projection = new Vector3(values.x, values.y, 0.0);
		if(projection.length() < TRESHOLD)
			return 0;
		Vector3 oy = new Vector3(0, 1, 0);
		double res = Math.toDegrees(Vector3.angle(oy, projection));
		if(values.x > 0){
			res = -res;
		}
	//	Log.v("" + res, "yonutixoz");
		return  res;
	}
	
	public static double getOxAngle(){
		Vector3 projection = new Vector3(0, values.y, values.z);
		if(projection.length() < TRESHOLD)
			return 0;
		
		Vector3 oy = new Vector3(0, 1, 0);
		
		double res =  Math.toDegrees(Vector3.angle(oy, projection));
		if(values.z > 0){
			res = -res;
		}
		
	//	Log.v("" + res, "yonutixox");
		
		
		return res;
	}
	
	public static double getOyAngle(){
		Vector3 projection = new Vector3(values.x, 0, values.z);
		
		if(projection.length() < TRESHOLD)
			return 0;
		Vector3 oy = new Vector3(0, 0, 1);
		
		double res =  Math.toDegrees(Vector3.angle(oy, projection));
		if(values.x > 0){
			res = -res;
		}
		//Log.v("" + res, "yonutixoy");
		return res;
	}

}
