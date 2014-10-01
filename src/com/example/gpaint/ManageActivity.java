package com.example.gpaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

class OnAppListListener implements OnItemClickListener {

	Activity context;

	public OnAppListListener(Activity context) {
		this.context = context;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long arg3) {
		CharSequence colors[] = new CharSequence[] {"Edit", "Delete", "Rename"};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Options");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        
		    }
		});
		builder.show();
	}

}

public class ManageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);
		String[] appNames = new String[10];

		appNames[0] = "Untitled1";
		appNames[1] = "Untitled2";
		appNames[2] = "Untitled3";
		appNames[3] = "Untitled4";
		appNames[4] = "Untitled5";
		appNames[5] = "Untitled6";
		appNames[6] = "Untitled7";
		appNames[7] = "Untitled8";
		appNames[8] = "Untitled9";
		appNames[9] = "Untitled0";

		ListView l = (ListView) findViewById(R.id.listview);

		l.setAdapter(new SavingsListAdapter(this, appNames));
		l.setOnItemClickListener(new OnAppListListener(this));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage, menu);
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
}
