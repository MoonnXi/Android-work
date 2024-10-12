package com.imagesharing.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

public class DetailImageAdapter extends BaseAdapter {

    private Context context;
    private JSONArray imageUrlList;

    public DetailImageAdapter(Context context, JSONArray imageUrlList) {
        this.context = context;
        this.imageUrlList = imageUrlList;
    }

    @Override
    public int getCount() {
        return imageUrlList.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return imageUrlList.getString(i);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
        } else {
            imageView = (ImageView) view;
        }

        // 使用 Glide 加载图片
        try {
            Glide.with(context)
                    .load(imageUrlList.get(i))
                    .into(imageView);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return imageView;
    }
}
