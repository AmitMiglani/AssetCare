package com.toyota.assetcare.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.toyota.assetcare.R;

/**
 * Created by Shubham_Aggarwal03 on 8/8/2018.
 */

public class HowToGetTokenActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_get_token_activity);
        TextView instructionTextView = (TextView) findViewById(R.id.instructions_textview);
        String blueString = getResources().getString(R.string.blue_string);
        instructionTextView.setText(Html.fromHtml(blueString));
        instructionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HowToGetTokenActivity.this, WebViewActivity.class);
                startActivity(i);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void backButtonClick(View v) {
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
