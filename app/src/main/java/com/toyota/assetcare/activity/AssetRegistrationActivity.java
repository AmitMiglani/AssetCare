package com.toyota.assetcare.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.inputrequest.IssueIR;
import com.toyota.assetcare.response.CreateIssueResponse;
import com.toyota.assetcare.response.Fields;
import com.toyota.assetcare.response.IssueType;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.service.AssetCareIntentService;
import com.toyota.assetcare.utils.CommonConstants;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;
import java.util.List;

public class AssetRegistrationActivity extends AppCompatActivity {

    private TextView summary;
    private TextView description;
    private TextView issueType;
    private TextView projectName;
    private ProgressDialog progressDialog;
    private ServiceBus bus;
    private Spinner spinner;
    private Spinner spinnerProject;
    private String selectedProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setContentView(R.layout.activity_asset_registration);
        summary = (TextView) findViewById(R.id.summary);
        description = (TextView) findViewById(R.id.description);
        issueType = (TextView) findViewById(R.id.issue_type_textview);
        projectName = (TextView) findViewById(R.id.project_name_textview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bus = ServiceBus.getInstance();
        if(getIntent().hasExtra(CommonConstants.TICKET_DESCRIPTION))
            description.setText(getIntent().getStringExtra(CommonConstants.TICKET_DESCRIPTION));
        spinner = (Spinner) findViewById(R.id.issue_type_spinner);
        spinnerProject = (Spinner) findViewById(R.id.project_name_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, getResources().getStringArray(R.array.issue_type_array));

        spinner.setAdapter(adapter);

       /* Intent i = new Intent(this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_GET_PROJECTS);
        startService(i);
        progressDialog.show();*/
       Gson mGson = new Gson();
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       ArrayList<ProjectDetails> list = mGson.fromJson(prefs.getString(CommonConstants.PROJECTS_LIST_SP, null), new TypeToken<List<ProjectDetails>>() {}.getType());
       if(list != null)
        getProjectList(list);

    }

    public void getProjectList(final ArrayList<ProjectDetails> projects) {
        progressDialog.dismiss();
        if(projects.size() < 1)
            return;
        final List<String> spinnerArray = new ArrayList<String>();
        for (ProjectDetails project: projects)
            spinnerArray.add(project.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerArray);

        spinnerProject.setAdapter(adapter);
        selectedProject = projects.get(0).id;
        spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                selectedProject = projects.get(pos).id;
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onCreateClick(View view) {
        if(selectedProject.equals("") || selectedProject.equals(null)) {
            Toast.makeText(this, "Project id is necessary", Toast.LENGTH_SHORT).show();
            return;
        }

        if(summary.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter summary", Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return;
        }

        Fields field = new Fields();
        IssueType issueType = new IssueType();
        if(spinner.getSelectedItem().toString().equals("Epic")) {
            issueType.id = "10000";
            field.customfield = "1";
        } else if(spinner.getSelectedItem().toString().equals("Story")) {
            issueType.id = "10001";
        } else if (spinner.getSelectedItem().toString().equals("Bug")) {
            issueType.id = "10004";
        } else {
            issueType.id = "10002";
        }
        ProjectDetails project = new ProjectDetails();
        //project.id = "10000";
        project.id = selectedProject;

        field.summary = summary.getText().toString().trim();
        field.description = description.getText().toString().trim();
        field.issuetype = issueType;
        field.project = project;

        IssueIR issue = new IssueIR();
        issue.fields = field;

        progressDialog.show();
        Intent i = new Intent(this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_CREATE_ISSUE);
        i.putExtra(AssetCareIntentService.EXTRA_PARAM,
                issue);
        startService(i);
    }

    @Subscribe
    public void onIssueCreated(CreateIssueResponse response) {
        progressDialog.dismiss();
        Toast.makeText(this, "Issue Created: "+ response.key, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, AssetDetailsActivity.class);
        i.putExtra("assetId", response.key);
        startActivity(i);
        finish();
    }
}
