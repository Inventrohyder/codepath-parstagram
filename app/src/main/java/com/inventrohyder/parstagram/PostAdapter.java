package com.inventrohyder.parstagram;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Post> mPosts;

    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Post> posts) {
        mPosts.addAll(posts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvUsername;
        private final ImageView mIvImage;
        private final TextView mTvDescription;
        private final ImageView mIvProfile;
        private final TextView mTvCreated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvUsername = itemView.findViewById(R.id.tvUsername);
            mIvImage = itemView.findViewById(R.id.ivPostImage);
            mTvDescription = itemView.findViewById(R.id.tvDescription);
            mIvProfile = itemView.findViewById(R.id.ivProfile);
            mTvCreated = itemView.findViewById(R.id.tvCreated);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            mTvDescription.setText(post.getDescription());
            ParseUser user = post.getUser();
            mTvUsername.setText(user.getUsername());
            mTvCreated.setText(
                    DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
            );
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(mIvImage);
            }

            ParseFile profilePicture = user.getParseFile("profilePicture");
            if (profilePicture != null) {
                Glide.with(mContext)
                        .load(profilePicture.getUrl())
                        .circleCrop()
                        .fallback(R.drawable.default_profile)
                        .into(mIvProfile);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.default_profile)
                        .circleCrop()
                        .into(mIvProfile);
            }
        }
    }
}
