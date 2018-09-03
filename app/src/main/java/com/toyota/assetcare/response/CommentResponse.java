package com.toyota.assetcare.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CommentResponse implements Serializable {
    @SerializedName("comments")
    public List<Comment> commentsList;

    public class Comment {
        public String body;

        public Author author;

        public class Author {
            public String displayName;
        }
    }

}