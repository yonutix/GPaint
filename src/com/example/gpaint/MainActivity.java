package com.example.gpaint;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.SignalProcessing.SignalProcessingEngine;

public class MainActivity extends ActionBarActivity {
	
	static ArrayList<GPaintInstance> allPaints = new ArrayList<GPaintInstance>();
	
	static GPaintInstance currentPaintInstance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		SignalProcessingEngine.getInstance().setGestureListener(
				new ServiceGestureListener(this));
		SignalProcessingEngine.getInstance().init(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	public void onNewPaint(View v){
		Intent intent = new Intent(this, NewPaint.class);
		startActivity(intent);
		
		currentPaintInstance = new GPaintInstance();
		
	}
	
	public void onEditRemovePaint(View v){
		Intent intent = new Intent(this, ManageActivity.class);
		startActivity(intent);
	}
	
	public void onExitApp(View v){
		finish();
	}
}
