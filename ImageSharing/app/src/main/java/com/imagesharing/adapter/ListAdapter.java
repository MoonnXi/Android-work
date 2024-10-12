package com.imagesharing.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imagesharing.R;
import com.imagesharing.view.ShareDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ListAdapter extends BaseAdapter {

    private final List<JSONObject> records;
    private final Context context;
    private final Long userId;
    private final String userName;

    private Long shareId;
    private String avatar;
    private String username;

    public ListAdapter(List<JSONObject> records, Context context, Long userId, String userName) {
        this.records = records;
        this.context = context;
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder(view, context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.bind(records, i);

        setCardClick(view, i);

        return view;
    }

    public static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvLikeNumbers;
        TextView tvCollectNumbers;

        ImageView ivLike;
        ImageView ivCollect;

        Context context;

        ViewHolder(View view, Context context) {
            ivImage = view.findViewById(R.id.iv_image);
            tvTitle = view.findViewById(R.id.tv_title);
            tvLikeNumbers = view.findViewById(R.id.tv_likeNumbers);
            tvCollectNumbers = view.findViewById(R.id.tv_collectNumbers);

            ivLike = view.findViewById(R.id.iv_like);
            ivCollect = view.findViewById(R.id.iv_collect);

            this.context = context;
        }

        void bind(List<JSONObject> records, int position) {
            JSONObject record = records.get(position);
            try {
                String title = record.getString("title");
                if (title.length() > 6) {
                    String newTitle = title.substring(0, 6) + "...";
                    tvTitle.setText(newTitle);
                } else {
                    tvTitle.setText(title);
                }
                tvLikeNumbers.setText(record.getString("likeNum"));
                tvCollectNumbers.setText(record.getString("collectNum"));

                if (record.isNull("imageUrl")) {
                    JSONArray imageUrlList = record.getJSONArray("imageUrlList");
                    loadImages(imageUrlList);
                } else {
                    ivImage.setImageResource(R.drawable.default_image2);
                }

            } catch (JSONException e) {
                Log.e("ListAdapter", Objects.requireNonNull(e.getMessage()));
            }
        }

        private void loadImages(JSONArray imageUrlList) throws JSONException {
            int count = Math.min(imageUrlList.length(), 3);
            for (int i = 0; i < count; i++) {
                String imageUrl = imageUrlList.getString(i);
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_loading))
                        .into(ivImage);
            }
        }

    }

    public void setCardClick(View view, int i) {
        view.setOnClickListener(v -> {
            try {
                JSONObject record = records.get(i);

                shareId = record.getLong("id");
                avatar = record.getString("avatar");
                username = record.getString("username");
                Log.d("ListAdapter", "shareId: " + shareId);

                Intent intent = new Intent(context, ShareDetailActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("shareId", shareId);
                intent.putExtra("avatar", avatar);
                intent.putExtra("username", username);
                intent.putExtra("userName", userName);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

}
