package org.semi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.semi.R;
import org.semi.Comment;

import java.util.Collection;
import java.util.List;

public class StoreDetailCommentAdapter extends RecyclerView.Adapter<StoreDetailCommentAdapter.ViewHolder> {
    private Context mRootContext;
    private List<Comment> mData;


    // data is passed into the constructor

    public StoreDetailCommentAdapter(Context mRootContext, List<Comment> data) {
        this.mRootContext = mRootContext;
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public StoreDetailCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mRootContext);
        View view = mInflater.inflate(R.layout.store_detail_list_comment_item, parent, false);
        return new StoreDetailCommentAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull StoreDetailCommentAdapter.ViewHolder holder, int position) {
        holder.user_avatar.setImageResource(mData.get(position).getUserAvatar());
        holder.user_comment_name.setText(mData.get(position).getUserName());
        holder.user_comment.setText(mData.get(position).getUserComment());
        holder.user_comment_time.setText(mData.get(position).getUserCommentTime());
        holder.user_rating.setRating(Float.valueOf(mData.get(position).getRating()));
        holder.user_rating_number.setText(String.valueOf(mData.get(position).getRating()) + "/5");
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Collection<Comment> data) {
        int lenght = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(lenght, data.size());
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_comment_name, user_comment, user_comment_time, user_rating_number;
        ImageView user_avatar;
        RatingBar user_rating;

        public ViewHolder(View itemView) {
            super(itemView);
            user_comment_name = itemView.findViewById(R.id.store_detail_user_name);
            user_comment = itemView.findViewById(R.id.store_detail_user_comment);
            user_comment_time = itemView.findViewById(R.id.store_detail_user_comment_time);
            user_avatar = itemView.findViewById(R.id.store_detail_user_avatar);
            user_rating = itemView.findViewById(R.id.store_detail_user_comment_rating);
            user_rating_number = itemView.findViewById(R.id.store_detail_user_comment_rating_number);

        }
    }
}
