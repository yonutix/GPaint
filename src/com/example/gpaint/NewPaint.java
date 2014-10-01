package com.example.gpaint;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class NewPaint extends ActionBarActivity {

	public static final int EDIT_MODE = 1;
	public static final int CREATE_MODE = 2;
	
	static DrawCanvas board;
	
	public static int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_paint);
		mode = CREATE_MODE;
		board = (DrawCanvas)findViewById(R.id.canvas);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_paint, menu);
		return true;
	}
	
	public void onSaveCourve(View v){
		//MainActivity.currentPaintInstance.add(board.currentCourve);
		//board.currentCourve = null;
	}
	
	public void onCreateMode(View v){
		mode = CREATE_MODE;
		
	}
	
	public void onRemoveSelected(View v){
		if(DrawCanvas.selectedCourve != -1){
			MainActivity.currentPaintInstance.courves.remove(DrawCanvas.selectedCourve);
			DrawCanvas.selectedCourve = -1;
			board.invalidate();
		}
	}
	
	public void onEditMode(View v){
		if(board.currentCourve != null){
			MainActivity.currentPaintInstance.add(board.currentCourve);
			board.currentCourve = null;
		}
		mode = EDIT_MODE;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
