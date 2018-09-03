package com.toyota.assetcare.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toyota.assetcare.network.RetrofitAPI;
import com.toyota.assetcare.network.RetrofitHelper;
import com.toyota.assetcare.inputrequest.CommentIR;
import com.toyota.assetcare.inputrequest.IssueIR;
import com.toyota.assetcare.inputrequest.UpdateIssueIR;
import com.toyota.assetcare.network.RetrofitHelperTextRecognition;
import com.toyota.assetcare.response.Asset;
import com.toyota.assetcare.response.AssetResponse;
import com.toyota.assetcare.response.AssigneeDetails;
import com.toyota.assetcare.response.AssigneeWrapper;
import com.toyota.assetcare.response.CommentResponse;
import com.toyota.assetcare.response.CreateIssueResponse;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.textrecognition.ImageHelper;
import com.toyota.assetcare.textrecognition.model.OCR;
import com.toyota.assetcare.utils.CommonUtils;
import com.toyota.assetcare.utils.ServiceBus;

import org.apache.http.entity.ByteArrayEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Shubham_Aggarwal03 on 8/2/2018.
 */

public class AssetCareIntentService extends IntentService{

    public static final String EXTRA_ISSUEID = "extra_asset";
    public static final String EXTRA_PROJECTID = "extra_project";
    public static final String EXTRA_PARAM = "extra_param_serializable_object";
    private boolean loadDataFromAssetsFolder = false;
    public static final String EXTRA_REQUEST = "extra_request";
    public static final int REQUEST_GET_ISSUE_DETAILS = 1;
    public static final int REQUEST_ASSET_LIST_BY_ASSET_TYPE_ID = 2;
    public static final int REQUEST_ADD_A_COMMENT_TO_ISSUE = 3;
    public static final int REQUEST_GET_PROJECTS = 4;
    public static final int REQUEST_CREATE_ISSUE = 5;
    public static final int REQUEST_UPDATE_ISSUE = 6;
    public static final int REQUEST_GET_ASSIGNEE_LIST = 7;
    public static final int REQUEST_UPDATE_ASSIGNEE = 8;
    public static final int REQUEST_TEXT_RECOGNITION = 9;
    private ServiceBus bus;
    private Retrofit retrofit;
    private RetrofitAPI retrofitAPI;

    public AssetCareIntentService() {
        super("AssetCareIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = ServiceBus.getInstance();
        retrofit = RetrofitHelper.getInstance(this);
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getIntExtra(EXTRA_REQUEST, -1)) {
            case REQUEST_GET_ISSUE_DETAILS:
                String assetId = intent.getStringExtra(AssetCareIntentService.EXTRA_ISSUEID);
                getAssetDetailsRest(assetId);
                break;
            case REQUEST_ADD_A_COMMENT_TO_ISSUE:
                String issueId = intent.getStringExtra(AssetCareIntentService.EXTRA_ISSUEID);
                CommentIR comment = (CommentIR) intent.getSerializableExtra(AssetCareIntentService.EXTRA_PARAM);
                addAComment(issueId, comment);
                break;
            case REQUEST_GET_PROJECTS:
                getProjects();
                break;
            case REQUEST_CREATE_ISSUE:
                IssueIR issue = (IssueIR)intent.getSerializableExtra(AssetCareIntentService.EXTRA_PARAM);
                createIssue(issue);
                break;
            case REQUEST_UPDATE_ISSUE:
                UpdateIssueIR updateIssue = (UpdateIssueIR)intent.getSerializableExtra(AssetCareIntentService.EXTRA_PARAM);
                assetId = intent.getStringExtra(AssetCareIntentService.EXTRA_ISSUEID);
                updateIssue(assetId, updateIssue);
                break;
            case REQUEST_GET_ASSIGNEE_LIST:
                String projectId = intent.getStringExtra(AssetCareIntentService.EXTRA_PROJECTID);
                getAssigneeList(projectId);
                break;
            case REQUEST_UPDATE_ASSIGNEE:
                String issueID = intent.getStringExtra(AssetCareIntentService.EXTRA_ISSUEID);
                AssigneeDetails assignee = (AssigneeDetails) intent.getSerializableExtra(AssetCareIntentService.EXTRA_PARAM);
                updateAssignee(issueID, assignee);
                break;
            case REQUEST_TEXT_RECOGNITION:
                //Map<String, Object> requestBody = (Map<String, Object>) intent.getSerializableExtra(AssetCareIntentService.EXTRA_PARAM);
                Intent data = (Intent) intent.getParcelableExtra(AssetCareIntentService.EXTRA_PARAM);
                recognizeText(data.getData());
                break;
        }
    }


    private void addAComment(String issueId, CommentIR comment) {
        Call<Void> call = retrofitAPI.addAComment(issueId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    CommentResponse comment = new CommentResponse();
                    bus.post(comment);
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void getAssetDetails(String assetId) {
        if(loadDataFromAssetsFolder) {
            String jsonString = CommonUtils.loadJSONFromAssetFolder(AssetCareIntentService.this);
            AssetResponse response = parseJSON(jsonString);
            for (Asset asset: response.assetList) {
                if(assetId == asset.getIssueId()) {
                    //send asset to asset details activity
                    bus.post(asset);
                }
            }
        } else {
            //load data from Rest API
            getAssetDetailsRest(assetId);
        }
    }

    private void getAssetDetails(int assetTypeId) {
        ArrayList<Asset> assetsList = new ArrayList<Asset>();
        if(loadDataFromAssetsFolder) {
            String jsonString = CommonUtils.loadJSONFromAssetFolder(AssetCareIntentService.this);
            AssetResponse response = parseJSON(jsonString);
            for (Asset asset: response.assetList) {
              /*  if(assetTypeId == asset.getAssetTypeId()) {
                    assetsList.add(asset);
                }*/
            }
        } else {
            //load data from Rest API
        }
        bus.post(assetsList);
    }

    public static AssetResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        AssetResponse assetResponse = gson.fromJson(response, AssetResponse.class);
        return assetResponse;
    }

    public void getAssetDetailsRest(String assetId) {
        Call<Asset> call = retrofitAPI.getAssetDetails(assetId);
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if(response.isSuccessful()) {
                    bus.post(response.body());
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {

            }
        });
    }

    public void getProjects() {
        Call<ArrayList<ProjectDetails>> call = retrofitAPI.getProjects();
        call.enqueue(new Callback<ArrayList<ProjectDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<ProjectDetails>> call, Response<ArrayList<ProjectDetails>> response) {
                if(response.isSuccessful()) {
                    bus.post(response.body());
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ProjectDetails>> call, Throwable t) {

            }
        });
    }

    public void createIssue(IssueIR issue) {
        Call<CreateIssueResponse> call = retrofitAPI.createIssue(issue);
        call.enqueue(new Callback<CreateIssueResponse>() {
            @Override
            public void onResponse(Call<CreateIssueResponse> call, Response<CreateIssueResponse> response) {
                if(response.isSuccessful()) {
                    bus.post(response.body());
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CreateIssueResponse> call, Throwable t) {

            }
        });
    }

    private void updateIssue(String issueId, UpdateIssueIR updateIssue) {
        Call<Void> call = retrofitAPI.updateIssue(issueId,updateIssue);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    bus.post("Issue updated successfully");
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void getAssigneeList(String projectId) {
        Call<ArrayList<AssigneeDetails>> call = retrofitAPI.getAssigneeList(projectId);
        call.enqueue(new Callback<ArrayList<AssigneeDetails>>() {
            @Override
            public void onResponse(Call<ArrayList<AssigneeDetails>> call, Response<ArrayList<AssigneeDetails>> response) {
                if(response.isSuccessful()) {
                    AssigneeWrapper wrapper = new AssigneeWrapper();
                    wrapper.list = response.body();
                    bus.post(wrapper);
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AssigneeDetails>> call, Throwable t) {

            }
        });
    }

    private void updateAssignee(String issueID, AssigneeDetails assignee) {
        Call<Void> call = retrofitAPI.updateAssignee(issueID, assignee);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                   // bus.post("Updated Assignee Details");
                    Toast.makeText(AssetCareIntentService.this, "Updated Assignee Details", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AssetCareIntentService.this, "Failure while requesting data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void recognizeText(Uri uri) {

        Bitmap mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                uri, getContentResolver());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());


        Retrofit retrofitMS = RetrofitHelperTextRecognition.getInstance(this);
        RetrofitAPI retrofitAPIMS = retrofitMS.create(RetrofitAPI.class);

        Call<OCR> call = null;
        try {
            byte[] buf;
            buf = new byte[inputStream.available()];
            while (inputStream.read(buf) != -1);
            RequestBody requestBody = RequestBody
                    .create(MediaType.parse("application/octet-stream"), buf);
            call = retrofitAPIMS.recognizeText(requestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<OCR>() {
            @Override
            public void onResponse(Call<OCR> call, Response<OCR> response) {
                bus.post(response.body());
            }

            @Override
            public void onFailure(Call<OCR> call, Throwable t) {

            }
        });
    }

}
