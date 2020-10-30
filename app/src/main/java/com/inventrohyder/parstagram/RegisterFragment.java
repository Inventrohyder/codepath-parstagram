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

public class RegisterFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private ProgressBar mProgressBar;

    public RegisterFragment() {
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.loading);

        mEtUsername = view.findViewById(R.id.username);
        mEtPassword = view.findViewById(R.id.password);
        mEtConfirmPassword = view.findViewById(R.id.confirm_password);

        Button btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view1 -> {
            String password = mEtPassword.getText().toString().trim();
            String confirmPassword = mEtConfirmPassword.getText().toString().trim();

            if (password.equals(confirmPassword)) {
                registerUser(
                        mEtUsername.getText().toString().trim(),
                        password
                );
            } else {
                Log.i(TAG, "onViewCreated: Passwords mismatch");
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                mEtPassword.requestFocus();
            }
        });

        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view1 -> ((LoginActivity) Objects.requireNonNull(getActivity())).replaceFragments(new LoginFragment()));
    }

    public void registerUser(String username, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "registerUser: ");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with sign up", e);
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