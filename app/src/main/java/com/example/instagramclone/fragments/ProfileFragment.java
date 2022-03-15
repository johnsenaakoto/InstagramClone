package com.example.instagramclone.fragments;

import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.instagramclone.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostFragment{

    @Override
    protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);     // Set limit to post query
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        // query post object to Parse backend on different thread
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // checks if the query was successful. When save is successful, ParseException is null
                if (e != null){
                    Log.e(TAG, "Issue with retrieving profile posts", e);
                    //return;
                }
                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
