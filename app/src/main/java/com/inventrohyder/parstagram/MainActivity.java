package com.inventrohyder.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    private EditText mEtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtDescription = findViewById(R.id.etDescription);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(view -> {
            String description = mEtDescription.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser currentUser = ParseUser.getCurrentUser();
            savePost(description, currentUser);
        });

//        queryPosts();
    }

    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
//        post.setImage();
        post.setUser(currentUser);
        post.saveInBackground((e -> {
            if (e != null) {
                Log.e(TAG, "savePost: Error while saving", e);
                Toast.makeText(getApplicationContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "savePost: Post save was successful!!");
            mEtDescription.setText("");
        }));
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
        });
    }
}