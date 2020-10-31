package com.inventrohyder.parstagram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String FRAG_POST = "FRAG_POST";
    public static final String FRAG_PROFILE = "FRAG_PROFILE";
    public static final String FRAG_FEED = "FRAG_FEED";
    private final String TAG = getClass().getSimpleName();

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
            int itemId = item.getItemId();
            String tag;
            if (itemId == R.id.action_feed) {
                tag = FRAG_FEED;
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new FeedFragment();
                }
            } else if (itemId == R.id.action_post) {
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

        bottomNavigationView.setSelectedItemId(R.id.action_feed);
    }

}