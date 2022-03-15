package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Post;
import com.example.instagramclone.PostAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    public static final String TAG = "PostFragment";
    protected RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;
    protected PostAdapter adapter;
    protected List<Post> allPosts;


    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        //RecyclerView divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvPosts = view.findViewById(R.id.rvPosts);

        // Steps to use the recycler view:
        //  create layout for one row in the list

        //  create the data source
        allPosts = new ArrayList<>();

        //  create the adapter
        adapter = new PostAdapter(getContext(), allPosts);

        //  set the adapter on the reycler view
        rvPosts.setAdapter(adapter);
        // rvPosts.addItemDecoration(itemDecoration);
        // queryPost();

        //  set the layout manager on the recyler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPost();

        // Refresh screen when you swipe up
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                allPosts.clear();
                adapter.notifyDataSetChanged();
                queryPost();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    // queryPost allows attributes to be retrieved from Parse backend
    protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);     // Set limit to post query
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        // query post object to Parse backend on different thread
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // checks if the query was successful. When save is successful, ParseException is null
                if (e != null){
                    Log.e(TAG, "Issue with retrieving posts", e);
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