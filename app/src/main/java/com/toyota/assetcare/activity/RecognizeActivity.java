//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Vision-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.toyota.assetcare.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.toyota.assetcare.R;
import com.toyota.assetcare.response.ProjectDetails;
import com.toyota.assetcare.service.AssetCareIntentService;
import com.toyota.assetcare.textrecognition.ImageHelper;
import com.toyota.assetcare.textrecognition.model.Line;
import com.toyota.assetcare.textrecognition.model.OCR;
import com.toyota.assetcare.textrecognition.model.Region;
import com.toyota.assetcare.textrecognition.model.Word;
import com.toyota.assetcare.utils.CommonConstants;
import com.toyota.assetcare.utils.ServiceBus;

import java.util.ArrayList;
import java.util.List;

public class RecognizeActivity extends ActionBarActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private Button mButtonSelectImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private EditText mEditText;


    private ServiceBus bus;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        mButtonSelectImage = (Button)findViewById(R.id.buttonSelectImage);
        mEditText = (EditText)findViewById(R.id.editTextResult);
        bus = ServiceBus.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading meta data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        mEditText.setText("");

        Intent intent;
        intent = new Intent(RecognizeActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AnalyzeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if(resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());
                        doRecognize(data);
                    }
                }
                break;
            default:
                break;
        }
    }


    public void doRecognize(Intent data) {
        mButtonSelectImage.setEnabled(false);
        mEditText.setText("Analyzing...");

        Intent i = new Intent(RecognizeActivity.this, AssetCareIntentService.class);
        i.putExtra(AssetCareIntentService.EXTRA_REQUEST,
                AssetCareIntentService.REQUEST_TEXT_RECOGNITION);
        i.putExtra(AssetCareIntentService.EXTRA_PARAM,
                data);
        startService(i);
    }

    @Subscribe
    public void onTextRecognitionResponse(OCR ocr) {
        Gson gson = new Gson();
        String str = gson.toJson(ocr);
        String result = "";
        ArrayList<String> keywords= new ArrayList<>();
        keywords.add("IMEI:");
        keywords.add("eUICCID:");
        keywords.add("Serial");
        keywords.add("Model");

        for (int r = 0;  r < ocr.regions.size(); r++) {
            for (int l = 0; l < ocr.regions.get(r).lines.size(); l++) {
                boolean wordFound = false;
                for (int w = 0; w < ocr.regions.get(r).lines.get(l).words.size(); w++) {
                    //check if word is present in the keywords list
                    String word = ocr.regions.get(r).lines.get(l).words.get(w).text;
                    if(word.contains("IMEI:")|| word.contains("IMEl:")) {
                        result = result + getIMEIFromList(ocr.regions.get(r).lines.get(l).words).substring(0,21) + "\n";
                    } else if (word.contains("eUlCClD:")) {
                        result = result + getIMEIFromList(ocr.regions.get(r).lines.get(l).words) + "\n";
                    } else if (word.contains("Model") || word.equalsIgnoreCase("Serial")) {
                        try {
                            result = result + getIMEIFromList(ocr.regions.get(r).lines.get(l).words) + getIMEIFromList(ocr.regions.get(r+1).lines.get(l).words) + "\n";
                        } catch ( Exception e) {
                            result = result + getIMEIFromList(ocr.regions.get(r).lines.get(l).words) + getIMEIFromList(ocr.regions.get(r+1).lines.get(l-1).words) + "\n";
                        }
                    }
                }
            }
        }



        mEditText.setText(result);
        navigateToCreateIssue(result);

    }

    public void navigateToCreateIssue(String result) {
        String str = prefs.getString(CommonConstants.PROJECTS_LIST_SP, null);
        if(str == null) {
            progressDialog.show();
            return;
        }

        Intent i = new Intent(this, AssetRegistrationActivity.class);
        i.putExtra(CommonConstants.TICKET_DESCRIPTION, result);
        startActivity(i);
        finish();
    }

    @Subscribe
    public void getProjectList(final ArrayList<ProjectDetails> projects) {
        progressDialog.dismiss();
        Gson mGson = new Gson();
        prefs.edit().putString(CommonConstants.PROJECTS_LIST_SP,mGson.toJson(projects)).commit();

    }


    private String getModelOrSerialNoFromList(List<Word> words) {

        return "";
    }

    private String getIMEIFromList(List<Word> words) {
        String str = "";
        for (Word word: words) {
            str = str +" "+ word.text;
        }
        return  str;
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
}
