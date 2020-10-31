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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    public static final int QUERY_LIMIT = 20;
    private final String TAG = getClass().getSimpleName();
    private RecyclerView mRvPosts;
    private PostAdapter mPostAdapter;
    private List<Post> mPostList;
    private SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener mScrollListener;

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

        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mRvPosts = view.findViewById(R.id.rvPosts);

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeContainer.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh: ");
            queryPosts();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                queryMorePosts();
            }
        };
        // Add scroll listener to RecyclerView
        mRvPosts.addOnScrollListener(mScrollListener);

        mPostList = new ArrayList<>();
        mPostAdapter = new PostAdapter(getContext(), mPostList);

        // Steps to use the recycler view:
        // 0. create the layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        mRvPosts.setAdapter(mPostAdapter);
        // 4. set the layout manager on the recycler view
        mRvPosts.setLayoutManager(layoutManager);
        queryPosts();
    }

    private void queryMorePosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereLessThan(Post.KEY_CREATED_AT, mPostList.get(mPostList.size() - 1).getCreatedAt());
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with getting more posts", e);
                mSwipeContainer.setRefreshing(false);
                return;
            }

            for (Post post : posts) {
                Log.i(TAG,
                        "Post desc: " + post.getDescription() + ", username: " + post.getUser().getUsername()
                );
            }

            mPostAdapter.addAll(posts);
            mSwipeContainer.setRefreshing(false);
        });

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with getting posts", e);
                mSwipeContainer.setRefreshing(false);
                return;
            }

            for (Post post : posts) {
                Log.i(TAG,
                        "Post desc: " + post.getDescription() + ", username: " + post.getUser().getUsername()
                );
            }
            mPostAdapter.clear();
            mPostAdapter.addAll(posts);
            mSwipeContainer.setRefreshing(false);
        });
    }
}