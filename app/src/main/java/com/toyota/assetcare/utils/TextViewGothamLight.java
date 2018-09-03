package com.toyota.assetcare.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/*
 * Set custom fonts to textview using this class
 */
public class TextViewGothamLight extends TextView{

	public TextViewGothamLight(Context context) {
		super(context);
		init();
	}
	public TextViewGothamLight(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextViewGothamLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Gotham_Light.otf");
		setTypeface(tf);
	}
}
