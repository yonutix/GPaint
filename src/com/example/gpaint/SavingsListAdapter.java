package com.example.gpaint;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SavingsListAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] appNames;

	public SavingsListAdapter(Activity context, String[] web) {
		super(context, R.layout.list_item, web);
		this.context = context;
		this.appNames = web;

	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_item, null, true);

		TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
		txtTitle.setText(appNames[position]);
		return rowView;
	}
}
