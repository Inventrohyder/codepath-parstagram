package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String FRAG_POST = "FRAG_POST";
    public static final String FRAG_PROFILE = "FRAG_PROFILE";
    public static final String FRAG_FEED = "FRAG_FEED";
    public static final int NOT_CHOSEN = -1;
    public static final String CHOSEN_FRAGMENT_KEY = "CHOSEN_FRAGMENT";
    private final String TAG = getClass().getSimpleName();
    private int mItemId = NOT_CHOSEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            Fragment fragment;
            mItemId = item.getItemId();
            String tag;
            if (mItemId == R.id.action_feed) {
                tag = FRAG_FEED;
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new FeedFragment();
                }
            } else if (mItemId == R.id.action_post) {
                tag = FRAG_POST;
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new PostFragment();
                }
            } else {
                tag = FRAG_PROFILE;
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new ProfileFragment();
                }
            }

            fragmentTransaction
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit();

            return true;
        });

        // Check if there is an existing bottom navigation click (e.g from recreating the activity)
        if (savedInstanceState != null) {
            mItemId = savedInstanceState.getInt(CHOSEN_FRAGMENT_KEY);
        }

        if (mItemId == NOT_CHOSEN) {
            // The default bottom navigation is the feed
            bottomNavigationView.setSelectedItemId(R.id.action_feed);
        } else {
            // Navigate through bottom nav to the previously selected fragment before destroying
            bottomNavigationView.setSelectedItemId(mItemId);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        // Save the chosen bottom nav action that lead to the fragment being destroyed
        outState.putInt(CHOSEN_FRAGMENT_KEY, mItemId);
    }
}