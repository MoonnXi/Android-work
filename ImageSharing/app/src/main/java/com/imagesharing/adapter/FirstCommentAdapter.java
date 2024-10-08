package com.imagesharing.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.imagesharing.R;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class FirstCommentAdapter extends BaseAdapter {

    private final List<JSONObject> records;
    private final Context context;

    private ViewHolder viewHolder;
    private Long commentId;
    private Long shareId;

    private String num;

    public FirstCommentAdapter(List<JSONObject> records, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_first_comment, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JSONObject record = records.get(position);

        try {
            viewHolder.tvUsername.setText(record.getString("userName"));
            viewHolder.tvTime.setText(record.getString("createTime"));
            viewHolder.tvContent.setText(record.getString("content"));

            commentId = record.getLong("id");
            shareId = record.getLong("shareId");

            if (!viewHolder.isInitialized) {
                initSecondCommentList();
                viewHolder.isInitialized = true;
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return convertView;
    }

    private void initSecondCommentList() {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/comment/second?commentId=" + commentId + "&shareId=" + shareId;

            OkHttpClient client = new OkHttpClient();

            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .headers(headers)
                    .get()
                    .build();

            client.newCall(request).enqueue(callbackSecondCommentList);
        }).start();
    }

    private final Callback callbackSecondCommentList = new Callback() {

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            ListView secondCommentList = viewHolder.secondCommentList;

            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");

                Log.d("FirstCommentAdapter", "二级评论列表数据：" + jsonResponse);

                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    JSONArray recordsArray = data.getJSONArray("records");

                    num = String.valueOf(recordsArray.length());

                    List<JSONObject> records = new ArrayList<>();

                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject record = recordsArray.getJSONObject(i);
                        records.add(record);
                    }

                    ((Activity) context).runOnUiThread(() -> {
                        viewHolder.replyNum.setText(num);

                        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.rlSecondComment.getLayoutParams();
                        layoutParams.height = 380;
                        viewHolder.rlSecondComment.setLayoutParams(layoutParams);

                        ChildCommentAdapter childCommentAdapter = new ChildCommentAdapter(records, context);
                        secondCommentList.setAdapter(childCommentAdapter);
                    });

                }

                Log.d("FirstCommentAdapter", "二级评论列表请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("FirstCommentAdapter", "Error response: " + e.getMessage());
        }
    };

    public static class ViewHolder {
        TextView tvUsername;
        TextView tvTime;
        TextView tvContent;
        ListView secondCommentList;
        RelativeLayout rlSecondComment;
        TextView replyNum;
        boolean isInitialized = false;

        ViewHolder(View itemView) {
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            secondCommentList = itemView.findViewById(R.id.second_comment_list);
            rlSecondComment = itemView.findViewById(R.id.rl_second_comment);
            replyNum = itemView.findViewById(R.id.reply_num);
        }
    }
}