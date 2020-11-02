package com.inventrohyder.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES_COUNT = "likesCount";

    private final String TAG = getClass().getSimpleName();

    // By default, a post is not liked by the user
    private boolean isLiked = false;

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getLikesCount() {
        return getInt(KEY_LIKES_COUNT);
    }

    public void setIsLiked(boolean liked, boolean isUpdate) {
        if (isUpdate) {
            ParseRelation<ParseObject> relation = this.getRelation(Post.KEY_LIKED_BY);
            if (liked) {
                relation.add(ParseUser.getCurrentUser());
                this.increment(KEY_LIKES_COUNT);
            } else {
                relation.remove(ParseUser.getCurrentUser());
                this.increment(KEY_LIKES_COUNT, -1);
            }
            this.saveEventually();
        }
        isLiked = liked;
    }

    public boolean getIsLiked() {
        return isLiked;
    }
}
