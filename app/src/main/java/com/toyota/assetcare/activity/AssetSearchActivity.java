package com.toyota.assetcare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.response.Asset;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;

/**
 * Created by Shubham_Aggarwal03 on 7/31/2018.
 */

public class AssetSearchActivity extends AppCompatActivity {

    private String TAG = AssetSearchActivity.this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_search);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void onScanQRCodeClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
        /*Intent i = new Intent(AssetSearchActivity.this, AssetDetailsActivity.class);
        i.putExtra("assetId", "Toyot-1");
        startActivity(i);*/
    }

    public void onManualSearchButtonClick(View v) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Toast.makeText(AssetSearchActivity.this, R.string.barcode_failure, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AssetSearchActivity.this, "QR code ID: " + result.getContents(), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(AssetSearchActivity.this, AssetDetailsActivity.class);
                    i.putExtra("assetId", result.getContents());
                    startActivity(i);
                }
            } else {
                Toast.makeText(AssetSearchActivity.this, R.string.barcode_error, Toast.LENGTH_LONG).show();
            }
    }

    public void backButtonClick(View view) {
        finish();
    }
}
