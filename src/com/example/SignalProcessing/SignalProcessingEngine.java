package com.example.SignalProcessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.Constants.Globals;
import com.example.GeometricEngine.Vector3;
import com.example.GestureLayer.GestureListener;
import com.example.GestureLayer.RawGesture;
import com.example.gpaint.MainActivity;

public class SignalProcessingEngine {

	private static SignalProcessingEngine singleton = null;

	public MainActivity context = null;

	// public long refTime = -1;

	// private EngineListener pauseListener = null;
	private GestureListener gListener = null;

	public static final int OX = 1;
	public static final int OY = 2;
	public static final int OZ = 4;

	public static final int ACC_DELIMITER = 1;
	public static final int TOUCH_DELIMITER = 2;
	public static final int NO_DELIMITER = 0;

	public static int delimiterOptions = 0;

	public static boolean touchOnDelimiter = false;
	public static boolean touchOffDelimiter = false;

	Vector3 lastMeasuredVal;

	public static final float TRESHOLD = 0.3f;

	Queue<Vector3> medianBuffer;
	public static final int MEDIAN_BUFF_SIZE = 8;

	Queue<Vector3> speedSupportBuffer;
	public static final int SPEED_SUPPORT_BUFFER = 2;

	Queue<Vector3> distanceSupportBuffer;
	public static final int DISTANCE_SUPPORT_BUFFER = 2;

	Queue<Vector3> delimitersBuffer;
	public static final int DELIMITER_BUFFER_SIZE = 16;

	public static final float DELIMITER_ENTROPY = 0.9f;

	Vector<Vector3> gestureBuffer;

	Vector3 currentAccVal;
	Vector3 currentSpeedVal;
	Vector3 currentDistanceVal;

	boolean delimiterStatus;

	boolean screenTouch = false;

	boolean pause = false;

	SensorManager sensorManager;
	Sensor sensor;
	Sensor gravitySensor;
	Sensor magneticSensor;

	static AccelerometerEvent listener;

	private SignalProcessingEngine() {
		lastMeasuredVal = new Vector3();
		medianBuffer = new LinkedList<Vector3>();
		speedSupportBuffer = new LinkedList<Vector3>();
		distanceSupportBuffer = new LinkedList<Vector3>();

		for (int i = 0; i < MEDIAN_BUFF_SIZE; ++i) {
			medianBuffer.add(new Vector3());
		}

		for (int i = 0; i < SPEED_SUPPORT_BUFFER; ++i) {
			speedSupportBuffer.add(new Vector3());
		}

		for (int i = 0; i < DISTANCE_SUPPORT_BUFFER; ++i) {
			distanceSupportBuffer.add(new Vector3());
		}

		delimitersBuffer = new LinkedList<Vector3>();

		gestureBuffer = new Vector<Vector3>();
		currentSpeedVal = new Vector3();
		currentDistanceVal = new Vector3();
		currentAccVal = new Vector3();
		delimiterStatus = true;

		speedSupportBuffer.add(new Vector3());

		delimiterOptions |= NO_DELIMITER;

	}

	public void init(MainActivity context) {
		this.context = context;
		sensorManager = (SensorManager) context.getSystemService(context
				.getApplicationContext().SENSOR_SERVICE);
		sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		magneticSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		listener = new AccelerometerEvent();

		if (sensor != null) {
			sensorManager.registerListener(listener, sensor,
					SensorManager.SENSOR_DELAY_FASTEST);

			sensorManager.registerListener(new GravityListener(),
					gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);

			sensorManager.registerListener(new MagnetometerListener(),
					magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	public void reset() {
		lastMeasuredVal = new Vector3();
		medianBuffer = new LinkedList<Vector3>();
		speedSupportBuffer = new LinkedList<Vector3>();
		distanceSupportBuffer = new LinkedList<Vector3>();

		for (int i = 0; i < MEDIAN_BUFF_SIZE; ++i) {
			medianBuffer.add(new Vector3());
		}

		for (int i = 0; i < SPEED_SUPPORT_BUFFER; ++i) {
			speedSupportBuffer.add(new Vector3());
		}

		for (int i = 0; i < DISTANCE_SUPPORT_BUFFER; ++i) {
			distanceSupportBuffer.add(new Vector3());
		}

		delimitersBuffer = new LinkedList<Vector3>();

		gestureBuffer = new Vector<Vector3>();
		currentSpeedVal = new Vector3();
		currentDistanceVal = new Vector3();
		currentAccVal = new Vector3();
		delimiterStatus = true;

		speedSupportBuffer.add(new Vector3());

		listener.resetTime();

		// delimiterOptions |= TOUCH_DELIMITER;
		delimiterOptions |= NO_DELIMITER;
	}

	public static SignalProcessingEngine getInstance() {
		if (singleton == null) {
			singleton = new SignalProcessingEngine();
		}
		return singleton;
	}

	public void setGestureListener(GestureListener g) {
		this.gListener = g;
	}

	/*
	 * public void setPauseListener(EngineListener el){ //pauseListener = el; }
	 */
	public void pause() {
		/*
		 * 
		 * if(pauseListener != null){ pauseListener.onPause(); }
		 */
	}

	/*
	 * public void resume(){ if(pauseListener != null){
	 * pauseListener.onResume(); } }
	 */
	public static Vector3 arithmeticMean(Queue<Vector3> buffer) {
		Vector3 median = new Vector3();

		Iterator<Vector3> it = buffer.iterator();

		for (int i = 0; i < buffer.size(); ++i) {
			Vector3 iv = it.next();
			median.x += iv.x;
			median.y += iv.y;
			median.z += iv.z;
			median.timestamp = iv.timestamp;

		}

		median.x = median.x / (float) buffer.size();
		median.y = median.y / (float) buffer.size();
		median.z = median.z / (float) buffer.size();

		return median;
	}

	public static Vector3 weightedMean(Queue<Vector3> buffer) {
		Vector3 median = new Vector3();
		int p = (int) Math.pow(2, buffer.size());
		double i = 0;
		for (Vector3 it : buffer) {

			median.x += it.x * (1.0 / (double) p);
			median.y += it.y * (1.0 / (double) p);
			median.z += it.z * (1.0 / (double) p);
			median.timestamp = it.timestamp;

			p /= 2;

			i += (1.0 / (double) p);
		}
		median.x = median.x / (float) i;
		median.y = median.y / (float) i;
		median.z = median.z / (float) i;

		return median;

	}

	public static Vector3 integration(Queue<Vector3> buffer) {
		Vector3 sum = new Vector3();

		Iterator<Vector3> it = buffer.iterator();

		Vector3 previous = it.next();

		for (int i = 1; i < buffer.size(); ++i) {
			Vector3 current = it.next();

			Vector3 triangle = current.add(previous);
			triangle = triangle.multiply(0.5f);
			triangle = triangle
					.multiply((float) ((double) (current.timestamp - previous.timestamp) / 1000000000.0));

			sum = sum.add(triangle);
			previous = current;
		}

		for (int i = 0; i < buffer.size() - 1; ++i) {
			buffer.poll();
		}
		sum.timestamp = getLast(buffer).timestamp;

		return sum;
	}
	/*
	public static Vector3 integration(ArrayList<Vector3> buffer) {
		Vector3 sum = new Vector3();

		Iterator<Vector3> it = buffer.iterator();

		Vector3 previous = it.next();

		for (int i = 1; i < buffer.size(); ++i) {
			Vector3 current = it.next();

			Vector3 triangle = current.add(previous);
			triangle = triangle.multiply(0.5f);
			triangle = triangle
					.multiply((float) ((double) (current.timestamp - previous.timestamp) / 1000000000.0));

			sum = sum.add(triangle);
			previous = current;
		}

		sum.timestamp = buffer.get(buffer.size()-1).timestamp;

		return sum;
	}
*/
	public boolean isAccDelimiter(Vector<Vector3> buffer) {
		Vector<Vector3> val = new Vector<Vector3>();

		for (Vector3 v : buffer) {
			if (val.indexOf(v) < 0) {
				val.add(new Vector3(v));
			}
		}

		if ((float) (buffer.size() - val.size() + 1) / (float) buffer.size() > DELIMITER_ENTROPY) {
			return true;
		}

		return false;
	}

	public boolean isAccDelimiter(Queue<Vector3> buffer) {
		Vector<Vector3> val = new Vector<Vector3>();

		for (Vector3 v : buffer) {
			if (val.indexOf(v) < 0) {
				val.add(new Vector3(v));
			}
		}

		if ((float) (buffer.size() - val.size() + 1) / (float) buffer.size() > DELIMITER_ENTROPY) {
			return true;
		}

		return false;
	}

	public static Vector3 getLast(Queue<Vector3> q) {
		Iterator<Vector3> it = q.iterator();
		Vector3 res = null;
		if (q.size() == 0)
			return null;

		while (it.hasNext()) {
			res = it.next();
		}
		return res;
	}
	
	
	

	public static void mergeSimilarPoints(ArrayList<Vector3> gesture) {
		for (int i = 0; i < gesture.size() - 1; ++i) {
			if (gesture.get(i).getDist(gesture.get(i + 1)) < Math
					.pow(10, -2)) {
				gesture.remove(i);
				i--;
			}
		}
	}
	
	public static void mergeSimilarPoints(Vector<Vector3> gesture) {
		for (int i = 0; i < gesture.size() - 1; ++i) {
			if (gesture.elementAt(i).getDist(gesture.elementAt(i + 1)) < Math
					.pow(10, -9)) {
				gesture.remove(i);
				i--;
			}
		}
	}
	
	

	public boolean delimiterOff() {
		boolean res = false;
		if ((delimiterOptions & ACC_DELIMITER) > 0) {
			res = res || (!isAccDelimiter(delimitersBuffer));
		}

		if ((delimiterOptions & TOUCH_DELIMITER) > 0) {
			res = res || screenTouch;
		}

		return res;
	}

	public boolean delimiterOn() {
		boolean res = true;
		if ((delimiterOptions & ACC_DELIMITER) > 0) {
			res = res && isAccDelimiter(delimitersBuffer);
		}

		if ((delimiterOptions & TOUCH_DELIMITER) > 0) {
			res = res && !screenTouch;
		}

		return res;
	}

	public void checkForDelimiter(Vector3 currentRawValue) {
		if (delimitersBuffer.size() > DELIMITER_BUFFER_SIZE) {
			delimitersBuffer.poll();
		}

		delimitersBuffer.add(new Vector3(currentRawValue));

		if (delimitersBuffer.size() > DELIMITER_BUFFER_SIZE) {
			boolean delimiter = delimiterOn();
			if (!delimiter && delimiterStatus) {

				for (Vector3 v : delimitersBuffer) {
					gestureBuffer.add(new Vector3(v));
				}
				gestureBuffer.remove(gestureBuffer.size() - 1);

			}

			if (delimiter && !delimiterStatus) {

				gestureBuffer.remove(gestureBuffer.size() - 1);

				mergeSimilarPoints(gestureBuffer);

				gListener.onGesture(new RawGesture(gestureBuffer));

				speedSupportBuffer.clear();
				gestureBuffer.clear();
				reset();

			}

			delimiterStatus = delimiter;
		}

	}

	public void update(SensorEvent rawVal) {
		long timestamp = rawVal.timestamp;
		

		Vector3 currentRawValue = new Vector3(rawVal);

		if (currentRawValue.timestamp == lastMeasuredVal.timestamp)
			return;

		currentRawValue = SignalProcessingEngine.thresholding(currentRawValue,
				lastMeasuredVal);
		currentRawValue.timestamp = timestamp - Globals.refTime;
		
		if (delimiterOptions != NO_DELIMITER) {
			checkForDelimiter(currentRawValue);
			if (delimiterStatus) {
				currentRawValue = new Vector3();
				currentRawValue.timestamp = timestamp - Globals.refTime;
				gListener.onDelimiter();
			} else {
				gListener.onRecording();
			}
		}
		if (delimiterOptions != NO_DELIMITER) {
			if (delimiterStatus) {
				Vector3 base = new Vector3();
				base.timestamp = currentRawValue.timestamp;

				medianBuffer.add(base);
			} else {
				Vector3 base = new Vector3(rawVal);
				base.timestamp = currentRawValue.timestamp;
				medianBuffer.add(base);
			}
		}
		else{
			Vector3 base = new Vector3(rawVal);
			base.timestamp = currentRawValue.timestamp;
			medianBuffer.add(base);
		}

		if (medianBuffer.size() > MEDIAN_BUFF_SIZE) {
			medianBuffer.poll();

			currentAccVal = weightedMean(medianBuffer);

			Vector3 sRaw = new Vector3(currentAccVal);

			if (medianBuffer.size() > 0) {
				sRaw = SignalProcessingEngine.thresholding(currentAccVal,
						getLast(speedSupportBuffer));
			}

			sRaw.timestamp = currentAccVal.timestamp;

			currentAccVal.x = sRaw.x;
			currentAccVal.y = sRaw.y;
			currentAccVal.z = sRaw.z;

		}

		// Vector3 newObj = new Vector3(currentAccVal);
		if(delimiterOptions == NO_DELIMITER){
			gListener.onDataAquisition(currentAccVal);
		}
		


		lastMeasuredVal = new Vector3(currentRawValue);
	}

	public Vector3 getAcceleration() {
		return currentAccVal;
	}

	public static int tresholdPass(Vector3 a, Vector3 b) {
		int axis = 0;
		if (Math.abs(a.x - b.x) > TRESHOLD) {
			axis |= OX;
		}
		if (Math.abs(a.y - b.y) > TRESHOLD) {
			axis |= OY;
		}
		if (Math.abs(a.z - b.z) > TRESHOLD) {
			axis |= OZ;
		}
		return axis;
	}

	public static Vector3 thresholding(Vector3 newValue, Vector3 oldValue) {
		int axis = tresholdPass(newValue, oldValue);

		Vector3 result = new Vector3(oldValue);

		if ((axis & OX) > 0) {
			result.x = newValue.x;
		}

		if ((axis & OY) > 0) {
			result.y = newValue.y;
		}

		if ((axis & OZ) > 0) {
			result.z = newValue.z;
		}

		axis = tresholdPass(newValue, new Vector3());
		if ((axis & OX) == 0) {
			result.x = 0;
		}

		if ((axis & OY) == 0) {
			result.y = 0;
		}

		if ((axis & OZ) == 0) {
			result.z = 0;
		}

		return result;
	}
}
