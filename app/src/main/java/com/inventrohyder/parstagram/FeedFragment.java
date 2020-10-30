package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private RecyclerView mRvPosts;
    private PostAdapter mPostAdapter;
    private List<Post> mPostList;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvPosts = view.findViewById(R.id.rvPosts);

        mPostList = new ArrayList<>();
        mPostAdapter = new PostAdapter(getContext(), mPostList);

        // Steps to use the recycler view:
        // 0. create the layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        mRvPosts.setAdapter(mPostAdapter);
        // 4. set the layout manager on the recycler view
        mRvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
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
            mPostList.addAll(posts);
            mPostAdapter.notifyDataSetChanged();
        });
    }
}