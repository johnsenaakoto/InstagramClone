package com.example.instagramclone;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Post class stores attributes of posts and allows us to retrieve and send posts to the backend
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";

    // Getter and setter functions for post description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    // Getter and setter functions for image file
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    // Getter and setter functions for username and user imfo
    public ParseUser getUser() { return getParseUser(KEY_USER); }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
