package com.example.GestureLayer;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

import com.example.Constants.DumpLog;
import com.example.GeometricEngine.Vector3;

public class RawGesture {
	static final double size = 0.06;
	public ArrayList<Vector3> acceleration;
	public Vector<Double> normalizedTime;

	public RawGesture(RawGesture g) {
		normalizedTime = new Vector<Double>();
		acceleration = new ArrayList<Vector3>();
		for (Vector3 v : g.acceleration) {
			acceleration.add(new Vector3(v));
		}
		normalizeTime();
	}

	public RawGesture(Vector<Vector3> gesture) {
		normalizedTime = new Vector<Double>();
		this.acceleration = new ArrayList<Vector3>();
		for (Vector3 g : gesture) {
			this.acceleration.add(new Vector3(g));
		}
		normalizeTime();
	}

	public RawGesture(ArrayList<Vector3> gesture) {
		normalizedTime = new Vector<Double>();
		this.acceleration = new ArrayList<Vector3>();
		for (Vector3 g : gesture) {
			this.acceleration.add(new Vector3(g));
		}
		normalizeTime();
	}

	public void removeBias() {
		Vector3 med = new Vector3();
		for (int i = 0; i < acceleration.size(); ++i) {
			med = med.add(acceleration.get(i));
		}
		med.x = med.x / acceleration.size();
		med.y = med.y / acceleration.size();
		med.z = med.z / acceleration.size();

		for (int i = 0; i < acceleration.size(); ++i) {
			acceleration.get(i).x -= med.x;
			acceleration.get(i).y -= med.y;
			acceleration.get(i).z -= med.z;
		}

	}

	public void normalize() {

		Vector3 mins = new Vector3();
		mins.x = Double.MAX_VALUE;
		mins.y = Double.MAX_VALUE;
		mins.z = Double.MAX_VALUE;

		Vector3 maxs = new Vector3();
		maxs.x = Double.MIN_VALUE;
		maxs.y = Double.MIN_VALUE;
		maxs.z = Double.MIN_VALUE;

		for (Vector3 v : acceleration) {
			maxs.x = (maxs.x < v.x) ? v.x : maxs.x;
			maxs.y = (maxs.y < v.y) ? v.y : maxs.y;
			maxs.z = (maxs.z < v.z) ? v.z : maxs.z;

			mins.x = (mins.x > v.x) ? v.x : mins.x;
			mins.y = (mins.y > v.y) ? v.y : mins.y;
			mins.z = (mins.z > v.z) ? v.z : mins.z;
		}

		double deltaMax = Double.MIN_VALUE;
		if (maxs.x - mins.x > deltaMax)
			deltaMax = maxs.x - mins.x;
		if (maxs.y - mins.y > deltaMax)
			deltaMax = maxs.y - mins.y;
		if (maxs.z - mins.z > deltaMax)
			deltaMax = maxs.z - mins.z;

		double scale = 1 / deltaMax;
		Vector3 scaleV = new Vector3();
		scaleV.x = scale;
		scaleV.y = scale;
		scaleV.z = scale;

		for (int i = 0; i < acceleration.size(); ++i) {
			Vector3 v = acceleration.get(i);
			v.x = v.x - mins.x;
			v.y = v.y - mins.y;
			v.z = v.z - mins.z;

			v.x = v.x * scale;
			v.y = v.y * scale;
			v.z = v.z * scale;

			acceleration.set(i, new Vector3(v));
		}

	}

	public void normalizeTime() {
		long maxT = -1;
		long minT = Long.MAX_VALUE;

		for (Vector3 v : acceleration) {
			if (v.timestamp < minT) {
				minT = v.timestamp;
			}
			if (v.timestamp > maxT) {
				maxT = v.timestamp;
			}
		}

		long deltaT = maxT - minT;

		for (Vector3 v : acceleration) {
			v.timestamp -= minT;
			normalizedTime.add(Double.valueOf(((double) v.timestamp)
					/ ((double) deltaT)));
		}
	}

	public void dump() {
		DumpLog.startSesssion();
		for (Vector3 v : acceleration) {
			DumpLog._log(v);
		}
		DumpLog.endSession();
	}

	public void dump(String filename) {
		DumpLog.startSesssion(filename);
		for (Vector3 v : acceleration) {
			DumpLog._log(v);
		}
		DumpLog.endSession();
	}

}
