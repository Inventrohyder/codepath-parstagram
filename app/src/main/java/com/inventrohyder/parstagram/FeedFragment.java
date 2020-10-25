package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.parse.ParseQuery;

public class FeedFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with getting posts", e);
                return;
            }

            for (Post post : posts) {
                Log.i(TAG,
                        "Post desc: " + post.getDescription() + ", username: " + post.getUser().getUsername()
                );
            }
        });
    }
}