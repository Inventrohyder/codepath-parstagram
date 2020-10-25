package com.inventrohyder.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parse.ParseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(view1 -> ParseUser.logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "onViewCreated: Error Logging out", e);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(getContext(), "User logged out!", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).finish();
        }));
    }
}