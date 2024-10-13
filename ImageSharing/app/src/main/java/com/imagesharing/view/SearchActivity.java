package com.imagesharing.view;

import static com.imagesharing.util.HeadersUtil.APP_ID;
import static com.imagesharing.util.HeadersUtil.APP_SECRET;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.imagesharing.R;
import com.imagesharing.bean.Data;
import com.imagesharing.response.ApiResponse;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {


    private TextView userNameTextView;
    private TextView introductionTextView;
    private SearchView serchView2;
    private LinearLayout uc;
    private ImageButton followButton;
    private ImageView avatarImageView;
    private static final String TAG = "SearchActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        userNameTextView = findViewById(R.id.user_name);
        introductionTextView = findViewById(R.id.introduction);
        uc = findViewById(R.id.user_card);
        followButton = findViewById(R.id.focusButton);
        serchView2 = findViewById(R.id.search_view2);
        avatarImageView = findViewById(R.id.user_avatar);

        String query = getIntent().getStringExtra("keyword");
        if (query.isEmpty()) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }else {
            searchRecords(query);
        }

        serchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户点击搜索按钮或者按下键盘上的搜索键时触发
                String keyword = query.trim();
                System.out.println("搜索关键词：" + keyword);
                if (!keyword.isEmpty()) {
                    searchRecords(keyword);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当查询文本发生变化时触发
                // 可以在这里做实时搜索建议等功能
                return false; // 返回false表示不做任何操作
            }
        });

    }

    private void searchRecords(String keyword) {
        new Thread(() -> {
            // 构建URL
            String url = "https://api-store.openguet.cn/api/member/photo/user/getUserByName?username=" + keyword;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", APP_ID)
                    .add("appSecret", APP_SECRET)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(callback);
        }).start();
    }

    // 回调
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            // 请求失败处理
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(SearchActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                return;
            }
            String body = response.body().string();
            Log.d(TAG, "Response Body: " + body);

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);

            // 确保code为200且有数据
            if (apiResponse != null && apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                Data data = apiResponse.getData();
                runOnUiThread(() -> {
                    // 更新UI
                    updateUI(data);
                    uc.setVisibility(View.VISIBLE);
                });
            } else {
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "没有找到数据", Toast.LENGTH_SHORT).show());
            }
        }
    };

    // 更新UI的方法
    private void updateUI(Data item) {
        userNameTextView.setText("用户名：" + item.getUsername());
        introductionTextView.setText("个人介绍：" + item.getIntroduce());
        Glide.with(avatarImageView).load(item.getAvatar()).apply(RequestOptions.circleCropTransform()).into(avatarImageView);
        //关注和取消关注按钮
        followButton.setOnClickListener(v -> {
            if (item.isHasFocus()) {
                // 已经关注，点击后取消关注
                item.setHasFocus(false);
                followButton.setImageResource(R.drawable.ic_gz4);
                Toast.makeText(SearchActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
            } else {
                // 未关注，点击后关注
                item.setHasFocus(true);
                followButton.setImageResource(R.drawable.ic_ygz2);
                Toast.makeText(SearchActivity.this, "已关注", Toast.LENGTH_SHORT).show();
            }
        });
    }
}