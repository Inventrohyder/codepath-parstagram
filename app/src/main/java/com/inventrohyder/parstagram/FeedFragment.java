package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedFragment extends Fragment {

    public static final int QUERY_LIMIT = 20;
    public static final String KEY_POSTS = "POSTS";
    private final String TAG = getClass().getSimpleName();
    private PostAdapter mPostAdapter;
    private List<Post> mPostList;
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar mProgressBar;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("posts", (ArrayList<Post>) mPostList);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            mPostList = new ArrayList<>();
        } else {
            mPostList = savedInstanceState.getParcelableArrayList(KEY_POSTS);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.topAppBar);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);

        mProgressBar = view.findViewById(R.id.progressBar);

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

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                queryOlderPosts();
            }
        };
        // Add scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        mPostAdapter = new PostAdapter(getContext(), mPostList);

        // Steps to use the recycler view:
        // 0. create the layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvPosts.setAdapter(mPostAdapter);
        // 4. set the layout manager on the recycler view
        rvPosts.setLayoutManager(layoutManager);
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
        query.include(Post.KEY_LIKED_BY);
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

                updateIsPostLiked(post);

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
        query.include(Post.KEY_LIKED_BY);
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

                updateIsPostLiked(post);
            }

            if (posts.size() > 0) {
                mPostList.addAll(0, posts);
                mPostAdapter.notifyDataSetChanged();
            }

            mProgressBar.setVisibility(View.GONE);
            mSwipeContainer.setRefreshing(false);
        });
    }

    private void updateIsPostLiked(Post post) {
        post.getRelation(Post.KEY_LIKED_BY)
                .getQuery()
                .whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId())
                .getFirstInBackground((object, e) -> {
                    if (e == null) {
                        // User exists in relation
                        post.setIsLiked(true, false);
                        mPostAdapter.notifyItemChanged(mPostList.indexOf(post));
                    } else {
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            // User doesn't exist in relation
                            post.setIsLiked(false, false);
                            mPostAdapter.notifyItemChanged(mPostList.indexOf(post));
                        } else {
                            // Another error
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.feed_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}