package com.example.SignalProcessing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.GeometricEngine.Vector3;

public class MagnetometerListener implements SensorEventListener{

	static Vector3 values;
	
	public MagnetometerListener() {
		super();
		values = new Vector3();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor != SignalProcessingEngine.getInstance().magneticSensor)
			return;

		float[] rotationMatrix = new float[9];
	    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
	    int worldAxisX = SensorManager.AXIS_X;
	    int worldAxisZ = SensorManager.AXIS_Z;
	    float[] adjustedRotationMatrix = new float[9];
	    SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
	    float[] orientation = new float[3];
	    SensorManager.getOrientation(adjustedRotationMatrix, orientation);
	    values.x = orientation[1];
	    values.y = orientation[2];
	    values.z = orientation[0];
	}

}
