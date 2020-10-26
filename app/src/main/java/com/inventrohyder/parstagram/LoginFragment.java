package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parse.ParseUser;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private EditText mEtUsername;
    private EditText mEtPassword;
    private ProgressBar mProgressBar;

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.loading);

        mEtUsername = view.findViewById(R.id.username);
        mEtPassword = view.findViewById(R.id.password);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(
                view1 -> loginUser(
                        mEtUsername.getText().toString().trim(),
                        mEtPassword.getText().toString().trim()
                )
        );

        Button btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view1 -> ((LoginActivity) Objects.requireNonNull(getActivity())).replaceFragments(new RegisterFragment()));
    }

    public void loginUser(String username, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "registerUser: ");
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with login", e);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                return;
            }

            // Navigate to MainActivity if the user signed in properly
            ((LoginActivity) Objects.requireNonNull(getActivity())).goToMainActivity();
            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
        });
    }

}