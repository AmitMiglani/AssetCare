package com.toyota.assetcare.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Ekjyot_Kaur on 2/5/2016.
 */
public class EditTextEmoticons  extends EditText {
    public EditTextEmoticons(Context context) {
        super(context);
        init();
    }

    public EditTextEmoticons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextEmoticons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Gotham_Light.otf");
        setTypeface(tf);
    }


}
