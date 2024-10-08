package com.imagesharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imagesharing.R;
import com.imagesharing.bean.Record;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyDynamicsAdapter extends ArrayAdapter<Record> {
    private List<Record> items;

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
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getContent());
        timeTextView.setText(item.getCreateTime());

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
            // 例如，使用默认图片
            imageView.setImageResource(R.drawable.default_image2);
        }


        return convertView;
    }

    public void setData(List<Record> newData) {
        this.items.clear();
        this.items.addAll(newData);
        notifyDataSetChanged();
    }
}
