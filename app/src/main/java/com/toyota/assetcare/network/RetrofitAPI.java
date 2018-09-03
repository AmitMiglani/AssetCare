package com.toyota.assetcare.network;

import com.toyota.assetcare.inputrequest.CommentIR;
import com.toyota.assetcare.inputrequest.IssueIR;
import com.toyota.assetcare.inputrequest.UpdateIssueIR;
import com.toyota.assetcare.response.Asset;
import com.toyota.assetcare.response.AssigneeDetails;
import com.toyota.assetcare.response.CreateIssueResponse;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.textrecognition.model.OCR;

import org.apache.http.entity.ByteArrayEntity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Shubham_Aggarwal03 on 8/8/2018.
 */

public interface RetrofitAPI {

    @GET("rest/api/2/issue/{assetId}/")
    Call<Asset> getAssetDetails(@Path("assetId") String assetId);

    @POST("rest/api/2/issue/{issueIdOrKey}/comment/")
    Call<Void> addAComment(@Path("issueIdOrKey") String key, @Body CommentIR String);

    @GET("rest/api/2/project/")
    Call<ArrayList<ProjectDetails>> getProjects();

    @POST("rest/api/2/issue/")
    Call<CreateIssueResponse> createIssue(@Body IssueIR str);

    @PUT("rest/api/2/issue/{issueIdOrKey}")
    Call<Void> updateIssue(@Path("issueIdOrKey") String key, @Body UpdateIssueIR updateIssueIR);

    @GET("rest/api/2/user/assignable/search")
    Call<ArrayList<AssigneeDetails>> getAssigneeList(@Query("project") String projectID);

    @PUT("rest/api/2/issue/{issueID}/assignee")
    Call<Void> updateAssignee(@Path("issueID") String issueID, @Body AssigneeDetails assignee);

    //Recognizing text using microsoft endpoint
 /*   @Multipart
    @POST("/ocr")
    Call<OCR> recognizetext(@Part MultipartBody.Part filePart);
*/
//Recognizing text using microsoft endpoint
  /*  @POST("ocr")
    Call<OCR> recognizeText(@Body ByteArrayEntity str);*/

    @POST("ocr")
    Call<OCR> recognizeText(@Body RequestBody str);

}
