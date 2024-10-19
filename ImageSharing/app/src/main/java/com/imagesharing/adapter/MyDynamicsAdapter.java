package com.imagesharing.adapter;

import static com.imagesharing.view.CollectionActivity.userId;
import static java.nio.file.Files.delete;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imagesharing.R;
import com.imagesharing.bean.Record;
import com.imagesharing.view.ShareDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyDynamicsAdapter extends ArrayAdapter<Record> {

    private List<Record> items;
    private OnItemClickListener onItemClickListener;

    public MyDynamicsAdapter(Context context, List<Record> items) {
        super(context, R.layout.item_my_dynamics, items);
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_my_dynamics, parent, false);
        }

        Record item = items.get(position);
        ImageView avatarImageView = convertView.findViewById(R.id.avatarImageView);
        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView likeTextView = convertView.findViewById(R.id.likeTextView);
        TextView collectTextView = convertView.findViewById(R.id.collectTextView);
        ImageView delectImageView = convertView.findViewById(R.id.delectImageView);

        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getContent());
        usernameTextView.setText(item.getUsername());
        Glide.with(avatarImageView)
                .load(item.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.girlpng)
                .into(avatarImageView);

        // 获取 likeNum 和 collectNum
        Object likeNumObj = item.getLikeNum();
        Object collectNumObj = item.getCollectNum();

        int likeNumInt;
        int collectNumInt;

        if (likeNumObj instanceof Integer) {
            likeNumInt = (Integer) likeNumObj;
        } else if (likeNumObj instanceof Double) {
            likeNumInt = ((Double) likeNumObj).intValue();
        } else {
            likeNumInt = 0; // 或者抛出异常
        }

        if (collectNumObj instanceof Integer) {
            collectNumInt = (Integer) collectNumObj;
        } else if (collectNumObj instanceof Double) {
            collectNumInt = ((Double) collectNumObj).intValue();
        } else {
            collectNumInt = 0; // 或者抛出异常
        }

        likeTextView.setText(String.valueOf(likeNumInt));
        collectTextView.setText(String.valueOf(collectNumInt));

        // 检查 imageUrlList 是否为空
        List<String> imageUrlList = item.getImageUrlList();
        if (imageUrlList != null && !imageUrlList.isEmpty()) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    250, // 宽度为 100dp
                    250  // 高度为 100dp
            );
            imageView.setLayoutParams(params);
            // 加载图片
            Picasso.get().load(imageUrlList.get(0)).into(imageView);
        } else {
            // 使用默认图片或不显示图片
            imageView.setImageResource(R.drawable.default_image2);
        }

        // 设置点击事件
        delectImageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item.getId(), position);
                removeAtPosition(position);
            }
        });

        // 添加点击事件
        convertView.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), ShareDetailActivity.class);
            intent.putExtra("userId",userId);
            intent.putExtra("shareId", item.getId());
            intent.putExtra("avatar", (String) item.getAvatar());
            intent.putExtra("username", item.getUsername());
            intent.putExtra("userName", item.getPUserId());
            getContext().startActivity(intent);

        });

        return convertView;
    }

    public void setData(List<Record> newData) {
        this.items.clear();
        this.items.addAll(newData);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAtPosition(int position) {
        if (position >= 0 && position < getCount()) {
            items.remove(position);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Long shareId, int position);
    }
}
