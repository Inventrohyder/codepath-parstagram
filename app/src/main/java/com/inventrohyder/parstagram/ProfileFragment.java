package com.inventrohyder.parstagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private AppCompatActivity mActivity;
    private TextView mTvBio;
    private ParseUser mCurrentUser;
    private ImageView mIvProfile;
    private ProgressBar mProgressBarProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) Objects.requireNonNull(getActivity());

        mCurrentUser = ParseUser.getCurrentUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.profile_topAppBar);

        mActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = Objects.requireNonNull(mActivity.getSupportActionBar());
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());

        mTvBio = view.findViewById(R.id.tvBio);
        mIvProfile = view.findViewById(R.id.ivProfile);
        mProgressBarProfile = view.findViewById(R.id.progressProfilePicture);
        mProgressBarProfile.setVisibility(View.VISIBLE);

        mCurrentUser.fetchInBackground((object, e) -> {
            if (e == null) {
                updateUserData();
            } else {
                // Error
                Log.e(TAG, "done: ", e);
            }
        });

        showDefaultProfile();
        updateUserData();
    }

    private void updateUserData() {
        mTvBio.setText(mCurrentUser.getString("bio"));

        ParseFile profilePicture = mCurrentUser.getParseFile("profilePicture");
        if (profilePicture != null) {
            Glide.with(this)
                    .load(profilePicture.getUrl())
                    .circleCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mProgressBarProfile.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mProgressBarProfile.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .fallback(R.drawable.default_profile)
                    .into(mIvProfile);
        } else {
            // Set up default profile
            showDefaultProfile();
            mProgressBarProfile.setVisibility(View.GONE);
        }
    }

    private void showDefaultProfile() {
        Glide.with(this)
                .load(R.drawable.default_profile)
                .circleCrop()
                .into(mIvProfile);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_log_out) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        ParseUser.logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "onViewCreated: Error Logging out", e);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            mActivity.finish();
        });
    }
}