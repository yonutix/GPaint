package com.example.GeometricEngine;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

import com.example.gpaint.CourveStyle;
import com.example.gpaint.DrawCanvas;

public class GeometricEngine {
	
	

	private static GeometricEngine singleton = new GeometricEngine();

	private GeometricEngine() {
	}

	public static GeometricEngine getInstance() {
		return singleton;
	}

	public static float getLineDist(Vector3 x1, Vector3 x2, Vector3 x0) {

		Vector3 axis = new Vector3();
		axis.x = x2.x - x1.x;
		axis.y = x2.y - x1.y;
		axis.z = x2.z - x1.z;

		axis = axis.normalize();

		Vector3 dir = new Vector3();

		dir.x = x0.x - x1.x;
		dir.y = x0.y - x1.y;
		dir.z = x0.z - x1.z;

		Vector3 proj = new Vector3();
		proj.x = Vector3.min(x1.x, x2.x) + dir.x * axis.x;
		proj.y = Vector3.min(x1.y, x2.y) + dir.y * axis.y;
		proj.z = Vector3.min(x1.z, x2.z) + dir.z * axis.z;

		if (proj.isInside(x1, x2, Vector3.OX | Vector3.OY))
			return proj.getDist(x0);

		float d = x1.getDist(x0);

		if (d > x2.getDist(x0)) {
			d = x2.getDist(x0);
		}

		return (float) d;
	}

	public static float getCourveDist(CourveStyle courve, Vector3 p,
			Function<Vector3> f) {

		float minDist = Float.MAX_VALUE;

		for (int i = 0; i < courve.gesture.acceleration.size() - 1; ++i) {
			Vector3 current = courve.gesture.acceleration.get(i);
			Vector3 next = courve.gesture.acceleration.get(i + 1);

			float d = getLineDist(f.motionFunction(current, courve.trans,
					courve.scale, courve.scale), f.motionFunction(next,
					courve.trans, courve.scale, courve.scale), p);

			if (d < minDist) {
				minDist = d;
				DrawCanvas.closePoint1.x = f.motionFunction(current,
						courve.trans, courve.scale, courve.scale).x;
				DrawCanvas.closePoint1.y = f.motionFunction(current,
						courve.trans, courve.scale, courve.scale).y;
				
				DrawCanvas.closePoint3.x = f.motionFunction(next,
						courve.trans, courve.scale, courve.scale).x;
				DrawCanvas.closePoint3.y = f.motionFunction(next,
						courve.trans, courve.scale, courve.scale).y;
			}
		}

		return minDist;
	}

	public static double findSegment(Vector<Vector3> points, double dist) {
		double currentDist = 0;

		for (int i = 0; i < points.size() - 1; ++i) {
			double deltaD = points.elementAt(i)
					.getDist(points.elementAt(i + 1));
			if (deltaD == 0) {
				continue;
			}
			if (dist >= currentDist && dist <= currentDist + deltaD) {
				return i + (dist - currentDist) / deltaD;
			}
			currentDist += deltaD;
		}
		return -1;
	}

	public static double findSegment(ArrayList<Vector3> points, double dist) {
		double currentDist = 0;

		for (int i = 0; i < points.size() - 1; ++i) {
			double deltaD = points.get(i).getDist(points.get(i + 1));
			if (deltaD == 0) {
				continue;
			}
			if (dist >= currentDist && dist <= currentDist + deltaD) {
				return i + (dist - currentDist) / deltaD;
			}
			currentDist += deltaD;
		}
		return -1;
	}

	public static Vector<Vector3> linearInterpolation(Vector<Vector3> points,
			int k) {
		if (k < 2) {
			return null;
		}

		Vector<Vector3> result = new Vector<Vector3>();

		double totalDist = 0;

		for (int i = 0; i < points.size() - 1; ++i) {
			totalDist += points.elementAt(i).getDist(points.elementAt(i + 1));
		}

		long deltaT = points.elementAt(points.size() - 1).timestamp
				- points.elementAt(0).timestamp;
		if (deltaT < 0)
			deltaT = -deltaT;
		deltaT /= (k - 1);
		for (int i = 0; i < k - 1; ++i) {
			double dist = (double) i / ((double) (k - 1)) * totalDist;

			double ind = findSegment(points, dist);
			Vector3 p0 = points.elementAt((int) Math.floor(ind));
			Vector3 p1 = points.elementAt((int) Math.floor(ind) + 1);

			Vector3 newPoint = p0.add((p1.diff(p0)).multiply(ind
					- Math.floor(ind)));
			newPoint.timestamp = points.elementAt(0).timestamp + deltaT * i;

			result.add(new Vector3(newPoint));

		}

		result.add(new Vector3(points.elementAt(points.size() - 1)));
		result.elementAt(result.size() - 1).timestamp = points.elementAt(points
				.size() - 1).timestamp;
		return result;
	}

	public static ArrayList<Vector3> linearInterpolation(
			ArrayList<Vector3> points, int k) {
		if (k < 2) {
			return null;
		}

		ArrayList<Vector3> result = new ArrayList<Vector3>();

		double totalDist = 0;

		for (int i = 0; i < points.size() - 1; ++i) {
			totalDist += points.get(i).getDist(points.get(i + 1));
		}

		long deltaT = points.get(points.size() - 1).timestamp
				- points.get(0).timestamp;
		if (deltaT < 0)
			deltaT = -deltaT;
		deltaT /= (k - 1);
		for (int i = 0; i < k - 1; ++i) {
			double dist = (double) i / ((double) (k - 1)) * totalDist;

			double ind = findSegment(points, dist);
			Vector3 p0 = points.get((int) Math.floor(ind));
			Vector3 p1 = points.get((int) Math.floor(ind) + 1);

			Vector3 newPoint = p0.add((p1.diff(p0)).multiply(ind
					- Math.floor(ind)));
			newPoint.timestamp = points.get(0).timestamp + deltaT * i;

			result.add(new Vector3(newPoint));

		}

		result.add(new Vector3(points.get(points.size() - 1)));
		result.get(result.size() - 1).timestamp = points.get(points.size() - 1).timestamp;
		return result;
	}

	public static Vector<Vector3> remapDiscreteCourve(Vector<Vector3> points) {
		if (points.size() < 2) {
			return null;
		}

		return linearInterpolation(points, 32);
	}

	public static ArrayList<Vector3> remapDiscreteCourve(
			ArrayList<Vector3> points) {
		if (points.size() < 2) {
			return null;
		}

		return linearInterpolation(points, 16);
	}

	public static double maxThree(double a, double b, double c) {
		double max = (a > b) ? a : b;
		max = (max > c) ? max : c;
		return max;
	}

	public static double minThree(double a, double b, double c) {
		double min = (a < b) ? a : b;
		min = (min < c) ? min : c;
		return min;
	}

	public static Vector3 DinamicTimeWrapping(Vector<Vector3> c_1,
			Vector<Vector3> c_2) {
		int n = c_1.size();
		int m = c_2.size();

		double[][] xMat = new double[n][m];
		double[][] yMat = new double[n][m];
		double[][] zMat = new double[n][m];

		for (int i = 0; i < n; ++i) {
			xMat[i][0] = Double.MAX_VALUE;
			yMat[i][0] = Double.MAX_VALUE;
			zMat[i][0] = Double.MAX_VALUE;
		}

		for (int i = 0; i < m; ++i) {
			xMat[0][i] = Double.MAX_VALUE;
			yMat[0][i] = Double.MAX_VALUE;
			zMat[0][i] = Double.MAX_VALUE;
		}
		xMat[0][0] = 0;
		yMat[0][0] = 0;
		zMat[0][0] = 0;

		for (int i = 1; i < n; ++i) {
			for (int j = 1; j < m; ++j) {
				double costX = Math
						.abs(c_1.elementAt(i).x - c_2.elementAt(j).x);
				double costY = Math
						.abs(c_1.elementAt(i).y - c_2.elementAt(j).y);
				double costZ = Math
						.abs(c_1.elementAt(i).z - c_2.elementAt(j).z);

				xMat[i][j] = costX
						+ minThree(xMat[i - 1][j], xMat[i][j - 1],
								xMat[i - 1][j - 1]);

				yMat[i][j] = costY
						+ minThree(yMat[i - 1][j], yMat[i][j - 1],
								yMat[i - 1][j - 1]);

				zMat[i][j] = costZ
						+ minThree(zMat[i - 1][j], zMat[i][j - 1],
								zMat[i - 1][j - 1]);

			}
		}

		Vector3 res = new Vector3();
		res.x = xMat[n - 1][m - 1];
		res.y = yMat[n - 1][m - 1];
		res.z = zMat[n - 1][m - 1];

		return res;
	}

}
