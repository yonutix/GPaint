package com.example.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.os.Environment;
import android.util.Log;

import com.example.GeometricEngine.Vector3;
import com.example.GestureLayer.RawGesture;

public class DumpLog {
	private static DumpLog singleton = new DumpLog();

	static File raf;
	static FileOutputStream fOut;
	static OutputStreamWriter myOutWriter;

	private DumpLog() {

	}

	public static void startSesssion() {
		
		try {
			raf = new File( "/storage/sdcard0/" + "sensor_out.log");
			
			

			raf.createNewFile();
			fOut = new FileOutputStream( "/storage/sdcard0/"
					+ "sensor_out.log");
			myOutWriter = new OutputStreamWriter(fOut);
		} catch (Exception e) {
			Log.v("yonutix", "Exception occured" + e.getMessage());
		}
	}

	public static void startSesssion(String filename) {
		try {
			raf = new File("/storage/sdcard0/" + filename);
			
			Log.v("DEBUG_P", "/storage/sdcard0/" + filename);

			raf.createNewFile();
			fOut = new FileOutputStream( "/storage/sdcard0/"
					+ filename);
			myOutWriter = new OutputStreamWriter(fOut);
		} catch (Exception e) {
			Log.v("yonutix", "Exception occured" + e.getMessage());
		}
	}

	public static void endSession() {
		try {
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			Log.v("yonutix", "Exception occured" + e.getMessage());
		}
	}

	public static DumpLog getInstance() {
		return singleton;
	}

	public static void _log(Vector3 v) {
		try {
			myOutWriter.write(v.timestamp + " " + v.x + " " + v.y + " " + v.z
					+ "\n");
			// Log.v("yonutix", "Something to write");
		} catch (Exception e) {
			Log.v("yonutix", "Exception occured" + e.getMessage());
		}
	}

	public static void log_hr(Vector3 v) {
		try {
			myOutWriter.write(v.toString());
		} catch (Exception e) {
			Log.v("yonutix", "Exception occured" + e.getMessage());
		}
	}

	public static void _log(RawGesture g) {
		startSesssion();

		for (int i = 0; i < g.acceleration.size(); ++i) {
			try {
				myOutWriter.write("" + g.normalizedTime.elementAt(i)
						+ " " + g.acceleration.get(i).x + " "
						+ g.acceleration.get(i).y + " "
						+ g.acceleration.get(i).z + "\n");
				// Log.v("yonutix", "Something to write");
			} catch (Exception e) {
				Log.v("yonutix", "Exception occured" + e.getMessage());
			}
		}

		endSession();
	}
	
	public static void _log(RawGesture g, String filename) {
		startSesssion(filename);

		for (int i = 0; i < g.acceleration.size(); ++i) {
			try {
				myOutWriter.write("" + g.normalizedTime.elementAt(i)
						+ " " + g.acceleration.get(i).x + " "
						+ g.acceleration.get(i).y + " "
						+ g.acceleration.get(i).z + "\n");
				// Log.v("yonutix", "Something to write");
			} catch (Exception e) {
				Log.v("yonutix", "Exception occured" + e.getMessage());
			}
		}

		endSession();
	}

}
