package com.toyota.assetcare.utils;

import android.content.Context;

import com.toyota.assetcare.response.AssetResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Shubham_Aggarwal03 on 8/2/2018.
 */

public class CommonUtils {

    public static String loadJSONFromAssetFolder(Context ctx) {
        String jsonString = null;
        try {
            InputStream is = ctx.getAssets().open("asset_json.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }
}
