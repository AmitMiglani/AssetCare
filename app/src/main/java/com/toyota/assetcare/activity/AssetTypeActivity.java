package com.toyota.assetcare.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.response.AssigneeDetails;
import com.toyota.assetcare.response.AssigneeWrapper;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.service.AssetCareIntentService;
import com.toyota.assetcare.utils.CommonConstants;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;
import java.util.HashMap;

public class AssetTypeActivity extends AppCompatActivity {

    private ServiceBus bus;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;
    private ArrayList<ProjectDetails> projectsList;
    private int ctr = 0;
    HashMap<String, ArrayList<AssigneeDetails>> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_type);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bus = ServiceBus.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(prefs.getString(CommonConstants.EMAIL_SP, "toyotasurvey123@gmail.com"));
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
        Intent i = new Intent(this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_GET_PROJECTS);
        startService(i);
   /*     getAssigneesList("10000");
        getAssigneesList("10001");*/
    }


    @Subscribe
    public void setProjectList(ArrayList<ProjectDetails> projects) {
        Gson mGson = new Gson();
        prefs.edit().putString(CommonConstants.PROJECTS_LIST_SP,mGson.toJson(projects)).commit();
        projectsList = projects;
        if(projects.size() > 1)
            getAssigneesList(projectsList.get(ctr).id);
        else
            progressDialog.dismiss();
    }

    private void getAssigneesList(String projectId) {
        Intent i = new Intent(this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_GET_ASSIGNEE_LIST);
        i.putExtra(AssetCareIntentService.EXTRA_PROJECTID,
                projectId);
        startService(i);
    }

    @Subscribe
    public void setAssigneeList(AssigneeWrapper wrapper) {
        AssigneeWrapper tempWrapper = new AssigneeWrapper();
        tempWrapper.list = new ArrayList<AssigneeDetails>();

        AssigneeDetails assignee = new AssigneeDetails();
        assignee.name = "Unassigned";
        assignee.displayName = "Unassigned";
        tempWrapper.list.add(assignee);

        for (AssigneeDetails details: wrapper.list)
            tempWrapper.list.add(details);

        //Add to a hashmap with project id as key
        map.put(projectsList.get(ctr).id, tempWrapper.list);
        Gson mGson = new Gson();
        prefs.edit().putString(CommonConstants.ASSIGNEE_HASHMAP,mGson.toJson(map)).commit();

        if(ctr < projectsList.size() -1) {
                 getAssigneesList(projectsList.get(++ctr).id);
        } else {
            progressDialog.dismiss();
        }
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

    public void onNewAssetButtonClick(View view) {
        Intent i = new Intent(this, AssetCreationActivity.class);
        startActivity(i);
    }

    public void onExistingAssetClick(View view) {
        Intent i = new Intent(this, AssetSearchActivity.class);
        startActivity(i);
    }

    public void backButtonClick(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset: {
                // do your sign-out stuff
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().clear().commit();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            }

            case R.id.action_project_url: {
                final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_base_url);
                final EditText editText = (EditText) dialog.findViewById(R.id.base_url_edittext);
                Button yesButton = (Button) dialog.findViewById(R.id.btn_yes);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AssetTypeActivity.this);
                        prefs.edit().putString(CommonConstants.BASE_URL_SP,editText.getText().toString()).commit();
                        dialog.dismiss();
                    }
                });

                Button noButton = (Button) dialog.findViewById(R.id.btn_no);
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
            case R.id.sync_app_data: {
                Intent i = new Intent(this, AssetCareIntentService.class);
                i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                        AssetCareIntentService.REQUEST_GET_PROJECTS);
                startService(i);
                progressDialog.show();
                break;
            }
        }
        return false;
    }
}
