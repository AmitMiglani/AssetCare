package com.toyota.assetcare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.adapter.AssetAdapter;
import com.toyota.assetcare.response.AssigneeDetails;
import com.toyota.assetcare.inputrequest.CommentIR;
import com.toyota.assetcare.inputrequest.Description;
import com.toyota.assetcare.inputrequest.Summary;
import com.toyota.assetcare.inputrequest.Update;
import com.toyota.assetcare.inputrequest.UpdateIssueIR;
import com.toyota.assetcare.response.Asset;
import com.toyota.assetcare.response.CommentResponse;
import com.toyota.assetcare.service.AssetCareIntentService;
import com.toyota.assetcare.utils.CommonConstants;
import com.toyota.assetcare.utils.ExpandableListView;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham_Aggarwal03 on 8/2/2018.
 */

public class AssetDetailsActivity extends AppCompatActivity {

    private ServiceBus bus;
    //private ImageView assetImage;
    private TextView issueId;
    private TextView issueTitle;
    private TextView issueStatus;
    private TextView issueDescription;
    private TextView projectName;
    private TextView issueType;
   // private TextView assignee;
    private TextView reporter;
    private TextView commentTextView;
    private ExpandableListView commentsList;
    private Button updateButton;
    private TextView addACommentTextView;
    private EditText addACommentEditText;
    //issue id
    private String id;
    private ProgressDialog progressDialog;
    private TextView projectLabel;
    private TextView issueLabel;
    private TextView assigneeLabel;
    private TextView reporterLabel;
    private ImageView sendmessageImage;
    private FrameLayout addACommentFL;
    private EditText issueTitleEditText;
    private EditText issueDescriptionEditText;
    private Spinner spinner;
    private int spinnerSelectionCheck = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assets_detail_activity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage(getString(R.string.loading_spinner));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        bus = ServiceBus.getInstance();
        issueId = (TextView) findViewById(R.id.issue_id_textview);
        issueTitle = (TextView) findViewById(R.id.issue_title_textview);
        issueStatus = (TextView) findViewById(R.id.issue_status_textview);
        issueDescription = (TextView) findViewById(R.id.issue_description_textview);
        projectName = (TextView) findViewById(R.id.project_name_textview);
        issueType = (TextView) findViewById(R.id.issue_type_textview);
        //assignee = (TextView) findViewById(R.id.issue_assignee_textview);
        reporter = (TextView) findViewById(R.id.issue_reporter_textview);
        commentTextView = (TextView) findViewById(R.id.comment_text);
        commentsList = (ExpandableListView) findViewById(R.id.comments_list);
        updateButton = (Button) findViewById(R.id.update_button);
        addACommentTextView = (TextView) findViewById(R.id.add_a_comment_textview);
        addACommentEditText= (EditText) findViewById(R.id.add_a_comment_edittext);
        sendmessageImage = (ImageView) findViewById(R.id.sendmessageImage);
        addACommentFL = (FrameLayout) findViewById(R.id.add_a_comment_framelayout);
        issueTitleEditText = (EditText) findViewById(R.id.issue_title_edittext);
        issueDescriptionEditText = (EditText) findViewById(R.id.issue_description_edittext);
        spinner = (Spinner) findViewById(R.id.issue_assignee_spinner);

        issueTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueTitle.setVisibility(View.INVISIBLE);
                issueTitleEditText.setText(issueTitle.getText());
                issueTitleEditText.setVisibility(View.VISIBLE);
                openKeyboard(issueTitleEditText);
            }
        });
        issueDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueDescription.setVisibility(View.INVISIBLE);
                issueDescriptionEditText.setText(issueDescription.getText());
                issueDescriptionEditText.setVisibility(View.VISIBLE);
                openKeyboard(issueDescriptionEditText);
            }
        });

        sendmessageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addACommentEditText.getText().toString().trim().equals(""))
                    return;
                progressDialog.show();
                Intent i = new Intent(AssetDetailsActivity.this, AssetCareIntentService.class);
                i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                        AssetCareIntentService.REQUEST_ADD_A_COMMENT_TO_ISSUE);
                CommentIR comment = new CommentIR();
                comment.body = addACommentEditText.getText().toString().trim();
                i.putExtra(AssetCareIntentService.EXTRA_ISSUEID,
                        id);
                i.putExtra(AssetCareIntentService.EXTRA_PARAM,
                        comment);
                startService(i);
            }
        });


        Intent i = getIntent();
        id = i.getStringExtra("assetId");
        //get Asset data as per this id
        getIssueData(id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(id.toUpperCase());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hideLabels();
    }

    public void setAssigneeSpinner(ArrayList<AssigneeDetails> list, String displayName) {
        if(list.size() < 1)
            return;
        final List<String> displayNameList = new ArrayList<String>();
        final List<String> nameList = new ArrayList<String>();
        for (AssigneeDetails assignee: list) {
            displayNameList.add(assignee.displayName);
            nameList.add(assignee.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, displayNameList);

        spinner.setAdapter(adapter);
        for (int i = 0; i < list.size(); i++ ) {
                if(displayName.equals(list.get(i).displayName))
                    spinner.setSelection(i);
            }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long iden)
            {
                if(++spinnerSelectionCheck == 1)
                    return;
                //update call
                Intent i = new Intent(AssetDetailsActivity.this, AssetCareIntentService.class);
                i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                        AssetCareIntentService.REQUEST_UPDATE_ASSIGNEE);
                AssigneeDetails details = new AssigneeDetails();
                if(nameList.get(pos).equals("Unassigned"))
                    details.name = null;
                else
                    details.name = nameList.get(pos);
                i.putExtra(AssetCareIntentService.EXTRA_PARAM,
                        details);
                i.putExtra(AssetCareIntentService.EXTRA_ISSUEID,
                        id);
                startService(i);
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }
    private void openKeyboard(EditText edittext) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                edittext.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideLabels() {
        projectLabel = (TextView) findViewById(R.id.project_label);
        issueLabel = (TextView) findViewById(R.id.issue_label);
        assigneeLabel = (TextView) findViewById(R.id.assignee_label);
        reporterLabel = (TextView) findViewById(R.id.reporter_label);
        projectLabel.setVisibility(View.INVISIBLE);
        issueLabel.setVisibility(View.INVISIBLE);
        assigneeLabel.setVisibility(View.INVISIBLE);
        reporterLabel.setVisibility(View.INVISIBLE);
    }

    private void displayLabels() {
        projectLabel.setVisibility(View.VISIBLE);
        issueLabel.setVisibility(View.VISIBLE);
        assigneeLabel.setVisibility(View.VISIBLE);
        reporterLabel.setVisibility(View.VISIBLE);
    }

    private void getIssueData(String id) {
        progressDialog.show();
        Intent i = new Intent(AssetDetailsActivity.this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_GET_ISSUE_DETAILS);
        i.putExtra(AssetCareIntentService.EXTRA_ISSUEID,
                id);
        startService(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    @Subscribe
    public void assetAvailable(Asset asset) {
        progressDialog.dismiss();
        issueId.setText(asset.getIssueId());
        issueTitle.setText(asset.getFields().summary);
        issueDescriptionEditText.setText(asset.getFields().description);
        issueStatus.setText(asset.getFields().status.name);
        issueDescription.setText(asset.getFields().description);
        issueDescriptionEditText.setText(asset.getFields().description);
        projectName.setText(asset.getFields().project.name);
        issueType.setText(asset.getFields().issuetype.name);
        reporter.setText(asset.getFields().reporter.name);
        updateButton.setVisibility(View.VISIBLE);

        if(asset.getFields().comment.commentsList.size() > 0) {
            commentTextView.setVisibility(View.VISIBLE);
            addACommentTextView.setVisibility(View.VISIBLE);
            commentsList.setVisibility(View.VISIBLE);
            commentsList.setAdapter(new AssetAdapter(this, asset.getFields().comment.commentsList));
            commentsList.setExpanded(true);
        }
        displayLabels();

        //Set list of assignees based on the project id
        Gson mGson = new Gson();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        HashMap<String, ArrayList<AssigneeDetails>> map = mGson.fromJson(prefs.getString(CommonConstants.ASSIGNEE_HASHMAP, null), new TypeToken<HashMap<String, ArrayList<AssigneeDetails>>>() {}.getType());
        if(map != null) {
            if(map.containsKey(asset.getFields().project.id)) {
                ArrayList<AssigneeDetails> list = (ArrayList<AssigneeDetails>)map.get(asset.getFields().project.id);
                if(asset.getFields().assignee == null) {
                    setAssigneeSpinner(list, "Unassigned");
                } else {
                    setAssigneeSpinner(list, asset.getFields().assignee.displayName);
                }
            }
        }

    }

    public void onUpdateClick(View view) {
        if(issueTitleEditText.getText().toString().equals("") && issueDescriptionEditText.getText().toString().equals(""))
            return;
        progressDialog.show();
        Intent i = new Intent(AssetDetailsActivity.this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_UPDATE_ISSUE);

        Summary summary = new Summary();
        summary.set = issueTitleEditText.getText().toString();
        Description description = new Description();
        description.set = issueDescriptionEditText.getText().toString();
        ArrayList<Summary> summaryList = new ArrayList<Summary>();
        ArrayList<Description> descriptionList = new ArrayList<Description>();
        summaryList.add(summary);
        descriptionList.add(description);
        Update update = new Update();
        update.summary = summaryList;
        update.description = descriptionList;
        UpdateIssueIR issueIR = new UpdateIssueIR();
        issueIR.update = update;

        i.putExtra(AssetCareIntentService.EXTRA_ISSUEID,
                id);
        i.putExtra(AssetCareIntentService.EXTRA_PARAM,
                issueIR);
        startService(i);
    }

    @Subscribe
    public void commentAdded(CommentResponse comment) {
        progressDialog.dismiss();
        Toast.makeText(AssetDetailsActivity.this, "Added comment", Toast.LENGTH_LONG).show();
        addACommentEditText.setText("");
       // addACommentEditText.setVisibility(View.INVISIBLE);
        addACommentFL.setVisibility(View.INVISIBLE);
        addACommentTextView.setVisibility(View.VISIBLE);
        getIssueData(id);
    }

    @Subscribe
    public void updateAssetDetails(String assetUpdateString) {
        progressDialog.dismiss();
        Toast.makeText(AssetDetailsActivity.this, assetUpdateString, Toast.LENGTH_LONG).show();
        issueDescriptionEditText.setVisibility(View.INVISIBLE);
        issueTitleEditText.setVisibility(View.INVISIBLE);
        issueDescription.setVisibility(View.VISIBLE);
        issueTitle.setVisibility(View.VISIBLE);
        issueTitle.setText(issueTitleEditText.getText());
        issueDescription.setText(issueDescriptionEditText.getText());
    }

    public void onAddACommentTextView(View view) {
        addACommentFL.setVisibility(View.VISIBLE);
        //addACommentEditText.setVisibility(View.VISIBLE);
        addACommentTextView.setVisibility(View.GONE);
       openKeyboard(addACommentEditText);
    }

}
