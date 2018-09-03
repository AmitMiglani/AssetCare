package com.toyota.assetcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.toyota.assetcare.R;
import com.toyota.assetcare.response.Asset;
import com.toyota.assetcare.response.CommentResponse;

import java.util.List;

/**
 * Created by Shubham_Aggarwal03 on 8/9/2018.
 */

public class AssetAdapter extends BaseAdapter {

    private List<CommentResponse.Comment> commentsList;
    private final Context ctx;

    public AssetAdapter(Context ctx, List<CommentResponse.Comment> commentsList) {
        this.commentsList = commentsList;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(this.ctx);
        if(view == null) {
            view = inflater.inflate(R.layout.activity_listview, viewGroup, false);
        }

        TextView name = view.findViewById(R.id.name);
        name.setText(commentsList.get(i).author.displayName);

        TextView comment = view.findViewById(R.id.comment);
        comment.setText(commentsList.get(i).body);
        return view;
    }

}
