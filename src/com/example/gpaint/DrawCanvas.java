package com.example.gpaint;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.Constants.DumpLog;
import com.example.GeometricEngine.Function;
import com.example.GeometricEngine.GeometricEngine;
import com.example.GeometricEngine.Vector2;
import com.example.GeometricEngine.Vector3;
import com.example.SignalProcessing.SignalProcessingEngine;

public class DrawCanvas extends View {

	public static Paint paint;
	public static Canvas cacheCanvas;

	public static final int ON = 1;
	public static final int OFF = 0;
	public static int touchStatus;

	// DEBUG
	public static final int VERTICAL_LINES = 4;
	public static final int HORIZONTAL_LINES = 4;

	public static final float GEN_BOARD_SIZE = 300;

	public static Function<Vector3> normalToScreen;

	CourveStyle currentCourve = null;

	public static final int NONE = 0;
	public static final int MOVE = 1;
	public static final int PINCH = 2;
	public static final int ROTATE = 3;
	public static int edit_mode = NONE;

	public static int selectedCourve = -1;

	public static float initialPinch = 0;

	public DrawCanvas(Context context) {
		super(context);
		touchStatus = OFF;
		paint = new Paint();
		init();
	}

	public DrawCanvas(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		paint = new Paint();
		touchStatus = OFF;
		init();
	}

	public void init() {
		normalToScreen = new Function<Vector3>() {

			@Override
			public Vector3 function(Vector3 v) {
				Vector3 res = new Vector3(v);

				res.x = (res.x * DrawCanvas.GEN_BOARD_SIZE);

				res.y = ((1 - res.y) * DrawCanvas.GEN_BOARD_SIZE);
				return res;
			}

			@Override
			public Vector3 motionFunction(Vector3 v, Vector2 translation,
					Vector2 scale, Vector2 rot) {
				Vector3 res = new Vector3(v);

				res.x = (res.x * DrawCanvas.GEN_BOARD_SIZE);

				res.y = ((1 - res.y) * DrawCanvas.GEN_BOARD_SIZE);

				res.x = res.x * scale.x + translation.x;
				res.y = res.y * scale.y + translation.y;

				return res;
			}

		};

	}

	public void drawDebugGrid(Canvas grid) {
		paint.setColor(Color.GREEN);

		float verticalStep = ((float) grid.getWidth()) / (VERTICAL_LINES + 1);
		float horizontalSteps = ((float) grid.getHeight())
				/ (HORIZONTAL_LINES + 1);

		for (int i = 0; i < VERTICAL_LINES; ++i) {
			grid.drawLine((i + 1) * verticalStep, 0, (i + 1) * verticalStep,
					grid.getHeight(), paint);
		}

		for (int i = 0; i < HORIZONTAL_LINES; ++i) {
			grid.drawLine(0, (i + 1) * horizontalSteps, grid.getWidth(),
					(i + 1) * horizontalSteps, paint);
		}

		paint.setColor(Color.RED);
		Vector2 upLeftCorner = new Vector2();
		upLeftCorner.x = grid.getWidth() / 2 - GEN_BOARD_SIZE / 2;
		upLeftCorner.y = grid.getHeight() / 2 - GEN_BOARD_SIZE / 2;

		Vector2 downRightCorner = new Vector2();
		downRightCorner.x = grid.getWidth() / 2 + GEN_BOARD_SIZE / 2;
		downRightCorner.y = grid.getHeight() / 2 + GEN_BOARD_SIZE / 2;
		Log.v("yonutix_draw",
				" x" + grid.getWidth() / 2 + " " + grid.getHeight() / 2);

		grid.drawLine(upLeftCorner.x, upLeftCorner.y, upLeftCorner.x
				+ GEN_BOARD_SIZE, upLeftCorner.y, paint);
		grid.drawLine(downRightCorner.x - GEN_BOARD_SIZE, downRightCorner.y,
				downRightCorner.x, downRightCorner.y, paint);

		grid.drawLine(upLeftCorner.x, upLeftCorner.y, upLeftCorner.x,
				upLeftCorner.y + GEN_BOARD_SIZE, paint);
		grid.drawLine(downRightCorner.x, downRightCorner.y - GEN_BOARD_SIZE,
				downRightCorner.x, downRightCorner.y, paint);

	}

	public static void drawLine(double startX, double startY, double stopX,
			double stopY, Paint p, Canvas grid) {
		grid.drawLine((float) startX, (float) startY, (float) stopX,
				(float) stopY, p);
	}

	/*
	 * public void drawCourve(Canvas grid, CourveStyle cs) { cs.drawCourve(grid,
	 * paint, Color); }
	 */
	Vector3 touchPoint = new Vector3();
	public static Vector3 closePoint1 = new Vector3();
	public static Vector3 closePoint2 = new Vector3();
	public static Vector3 closePoint3 = new Vector3();

	public void onDraw(Canvas grid) {
		cacheCanvas = grid;

		grid.drawColor(Color.BLACK);
		drawDebugGrid(grid);

		if (currentCourve != null) {
			// drawCourve(grid, currentCourve);
		}

		paint.setColor(Color.WHITE);

		grid.drawCircle((float) touchPoint.x, (float) touchPoint.y, 3, paint);
		grid.drawCircle((float) closePoint1.x, (float) closePoint1.y, 3, paint);
		grid.drawCircle((float) closePoint2.x, (float) closePoint2.y, 3, paint);
		grid.drawCircle((float) closePoint3.x, (float) closePoint3.y, 3, paint);

		for (int i = 0; i < MainActivity.currentPaintInstance.courves.size(); ++i) {
			if (i == selectedCourve)
				MainActivity.currentPaintInstance.courves.get(i).drawCourve(
						grid, paint, Color.WHITE);
			else
				MainActivity.currentPaintInstance.courves.get(i).drawCourve(
						grid, paint, Color.RED);
		}
	}

	public void onGesture() {

		if (NewPaint.mode == NewPaint.CREATE_MODE) {
			
			ArrayList<Vector3> tmpC = new ArrayList<Vector3>();
			for(Vector3 v: ServiceGestureListener.rawData){
				tmpC.add(v);
			}
			
			SignalProcessingEngine.mergeSimilarPoints(tmpC);
			
			if (tmpC.size() < 2) {
				return;
			}
			
			currentCourve = new CourveStyle(tmpC);

			currentCourve.gesture.acceleration = GeometricEngine
					.remapDiscreteCourve(currentCourve.gesture.acceleration);

			currentCourve.trans.x = cacheCanvas.getWidth() / 2
					- DrawCanvas.GEN_BOARD_SIZE / 2;
			currentCourve.trans.y = cacheCanvas.getHeight() / 2
					- DrawCanvas.GEN_BOARD_SIZE / 2;

			MainActivity.currentPaintInstance.add(currentCourve);
			
			currentCourve = null;
			ServiceGestureListener.rawData.clear();
			
			
		}

	}

	public float getPinchDist(MotionEvent event) {
		Vector2 finger1 = new Vector2();
		Vector2 finger2 = new Vector2();
		finger1.x = event.getX(0);
		finger1.y = event.getY(0);

		finger2.x = event.getX(1);
		finger2.y = event.getY(1);

		return (float) finger1.getDist(finger2);
	}

	public void touchEventActionMoveHandler(MotionEvent event) {
		// event.findPointerIndex(pointerId)

		if (NewPaint.mode == NewPaint.EDIT_MODE) {

			if (event.getPointerCount() == 1) {

				if (edit_mode == PINCH) {
					edit_mode = NONE;
					Log.v("yonutix_draw", "Pinch ending");
				}

				Vector3 finger = new Vector3();

				finger.x = event.getX();
				finger.y = event.getY();

				if (selectedCourve > -1) {
					MainActivity.currentPaintInstance.courves
							.get(selectedCourve).trans.x = (float) (finger.x - GEN_BOARD_SIZE / 2);
					MainActivity.currentPaintInstance.courves
							.get(selectedCourve).trans.y = (float) (finger.y - GEN_BOARD_SIZE / 2);
				}

			}

			if (event.getPointerCount() == 2) {
				if (edit_mode != PINCH) {
					Log.v("yonutix_draw", "Pinch starting");
					initialPinch = getPinchDist(event);
				}
				edit_mode = PINCH;

				Log.v("yonutix_draw Scale: ", "" + initialPinch
						/ getPinchDist(event));

				if (selectedCourve != -1) {
					MainActivity.currentPaintInstance.courves
							.get(selectedCourve).scale.x = initialPinch
							/ getPinchDist(event);

					MainActivity.currentPaintInstance.courves
							.get(selectedCourve).scale.y = initialPinch
							/ getPinchDist(event);
				}

			}

			this.invalidate();
		}

	}

	public void touchEventActionDownHandler(MotionEvent event) {
		if (NewPaint.mode == NewPaint.CREATE_MODE)
			touchStatus = ON;

		if (NewPaint.mode == NewPaint.EDIT_MODE && event.getPointerCount() == 1) {
			float minDist = Float.MAX_VALUE;
			int minInd = -1;

			Vector3 finger = new Vector3();

			finger.x = event.getX();
			finger.y = event.getY();

			touchPoint = new Vector3(finger);
			Log.v("yonutix_draw",
					"" + finger.x + " " + finger.y + " "
							+ cacheCanvas.getWidth() / 2 + " "
							+ cacheCanvas.getHeight() / 2 + " " + event.getX()
							+ " " + event.getY());

			for (int i = 0; i < MainActivity.currentPaintInstance.courves
					.size(); ++i) {
				CourveStyle it = MainActivity.currentPaintInstance.courves
						.get(i);
				float d = GeometricEngine.getCourveDist(it, finger, normalToScreen);
				Log.v("yonutix_courve",
						"dist: "
								+ d
								+ " "
								+ MainActivity.currentPaintInstance.courves
										.get(i).gesture.acceleration.size());
				if (d < minDist) {
					minDist = d;
					minInd = i;
				}
			}

			if (minDist > 50) {
				minInd = -1;
			}

			Log.v("yonutix_courve", "dist: " + minInd + " " + minDist
					+ "\n========================");
			selectedCourve = minInd;
		}

		this.invalidate();
	}

	public void touchEventActionUpHandler(MotionEvent event) {
		if (NewPaint.mode == NewPaint.CREATE_MODE) {
			touchStatus = OFF;
			onGesture();
			selectedCourve = -1;
			this.invalidate();
		}

		if (edit_mode == PINCH) {
			edit_mode = NONE;
			Log.v("yonutix_draw", "Pinch ending");
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			touchEventActionDownHandler(event);

			break;

		case MotionEvent.ACTION_MOVE:
			touchEventActionMoveHandler(event);
			break;

		case MotionEvent.ACTION_UP:
			touchEventActionUpHandler(event);

			break;
		}

		// tell the system that we handled the event and no further processing
		// is required
		return true;
	}

}