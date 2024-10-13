package com.imagesharing.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesharing.R;
import com.imagesharing.view.SecondCommentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private final List<JSONObject> records;
    private final Context context;

    private final Long userId;
    private final String userName;

    private Long id;
    private Long pUserId;
    private Long shareId;


    public CommentListAdapter(List<JSONObject> records, Context context, Long userId, String userName) {
        this.records = records;
        this.context = context;
        this.userId = userId;
        this.userName = userName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject record = records.get(position);

        try {
            holder.tvUsername.setText(record.getString("userName"));
            holder.tvContent.setText(record.getString("content"));
            holder.tvTime.setText(record.getString("createTime"));

            id = record.getLong("id");
            pUserId = record.getLong("pUserId");
            shareId = record.getLong("shareId");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        holder.setReplyClick();
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvContent;
        TextView tvTime;
        ImageView tvReply;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvReply = itemView.findViewById(R.id.tv_reply);
        }

        // 跳转到二级评论
        public void setReplyClick() {
            if (tvReply != null) {
                tvReply.setOnClickListener(v -> {
                    Intent intent = new Intent(context, SecondCommentActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("pUserId", pUserId);
                    intent.putExtra("shareId", shareId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userName", userName);
                    context.startActivity(intent);
                });
            }
        }
    }
}
