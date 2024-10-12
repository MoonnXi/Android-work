package com.imagesharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imagesharing.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommentListAdapter extends BaseAdapter {

    private final List<JSONObject> records;
    private final Context context;

    private Long commentId;
    private Long shareId;

    public CommentListAdapter(List<JSONObject> records, Context context) {
        this.records = records;
        this.context = context;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_comment, viewGroup, false);
            holder = new ViewHolder(view, context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.bind(records, i);

        return view;
    }

    public class ViewHolder {
        TextView tvUsername;
        TextView tvContent;
        TextView tvTime;

        Context context;

        ViewHolder(View view, Context context) {
            tvUsername = view.findViewById(R.id.tv_username);
            tvContent = view.findViewById(R.id.tv_content);
            tvTime = view.findViewById(R.id.tv_time);

            this.context = context;
        }

        void bind(List<JSONObject> records, int position) {
            JSONObject record = records.get(position);

            try {
                tvUsername.setText(record.getString("userName"));
                tvContent.setText(record.getString("content"));
                tvTime.setText(record.getString("createTime"));

                commentId = record.getLong("id");
                shareId = record.getLong("shareId");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
