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
            queryNewerPosts();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                queryOlderPosts();
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
        queryNewerPosts();
    }

    private void queryOlderPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if (mPostList.size() > 0) {
            Post oldestPost = mPostList.get(mPostList.size() - 1);
            query.whereLessThanOrEqualTo(Post.KEY_CREATED_AT, oldestPost.getCreatedAt());
            query.whereNotEqualTo(Post.KEY_USER, oldestPost.getUser().getObjectId());
        }

        query.include(Post.KEY_USER);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

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

            if (posts.size() > 0) {
                mPostList.addAll(posts);
                mPostAdapter.notifyDataSetChanged();
            }

            mSwipeContainer.setRefreshing(false);
        });

    }

    private void queryNewerPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if (mPostList.size() > 0) {
            Post newestPost = mPostList.get(0);
            query.whereGreaterThanOrEqualTo(Post.KEY_CREATED_AT, newestPost.getCreatedAt());
            query.whereNotEqualTo(Post.KEY_USER, newestPost.getUser().getObjectId());
        }
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

            if (posts.size() > 0) {
                mPostList.addAll(0, posts);
                mPostAdapter.notifyDataSetChanged();
            }
            mSwipeContainer.setRefreshing(false);
        });
    }
}