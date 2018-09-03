package com.toyota.assetcare.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.utils.CommonConstants;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;

public class AssetCreationActivity extends AppCompatActivity {

    private String TAG = AssetCreationActivity.this.getClass().getName();
    private ServiceBus bus;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_creation);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading meta data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        bus = ServiceBus.getInstance();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onScanQRCodeClick(View v) {
        Intent i = new Intent(this, RecognizeActivity.class);
        startActivity(i);
    }

    public void onManualCreationButtonClick(View v) {
        String str = prefs.getString(CommonConstants.PROJECTS_LIST_SP, null);
        if(str == null) {
            progressDialog.show();
            return;
        }

        Intent i = new Intent(this, AssetRegistrationActivity.class);
        startActivity(i);
    }

    @Subscribe
    public void getProjectList(final ArrayList<ProjectDetails> projects) {
        progressDialog.dismiss();
        Gson mGson = new Gson();
        prefs.edit().putString(CommonConstants.PROJECTS_LIST_SP,mGson.toJson(projects)).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(AssetCreationActivity.this, R.string.barcode_failure, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AssetCreationActivity.this, "QR code ID: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(AssetCreationActivity.this, R.string.barcode_error, Toast.LENGTH_LONG).show();
        }
    }

    public void backButtonClick(View view) {
        finish();
    }
}
