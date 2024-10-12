package com.imagesharing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.imagesharing.bean.Record;
import com.imagesharing.response.ApiResponse;
import com.imagesharing.util.HeadersUtil;
import com.imagesharing.view.CollectionActivity;
import com.imagesharing.view.DraftsActivity;
import com.imagesharing.view.FocusActivity;
import com.imagesharing.view.LikesActivity;
import com.imagesharing.view.MyDynamicsActivity;
import com.imagesharing.R;
import com.imagesharing.view.SettingsActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {

    private LinearLayout releaseLayout;
    private LinearLayout aboutLayout;
    private LinearLayout downloadsLayout;
    private LinearLayout collectionsLayout;
    private LinearLayout historyLayout;
    private LinearLayout settingsLayout;

    private ImageView profileImage;

    private final Long userId;
    private final String username;
    private final String avatar;

    public MeFragment(Long userId, String username, String avatar) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_me, container, false);

        // 初始化组件
        releaseLayout = view.findViewById(R.id.release);
        aboutLayout = view.findViewById(R.id.about_layout);
        downloadsLayout = view.findViewById(R.id.downloads_layout);
        collectionsLayout = view.findViewById(R.id.collections_layout);
        historyLayout = view.findViewById(R.id.history_layout);
        settingsLayout = view.findViewById(R.id.settings_layout);

        profileImage = view.findViewById(R.id.profile_image);
        setProfileImage();

        TextView myUsername = view.findViewById(R.id.my_username);
        myUsername.setText(username);

        // 设置点击事件
        setupReleaseClicks();
        setupAboutClicks();
        setupDownloadsClicks();
        setupCollectionsClicks();
        setupHistoryClicks();
        setupSettingsClicks();

        get();
        fetchFollowedActivities();

        return view;
    }

    // 初始化用户头像
    private void setProfileImage() {
        Glide.with(this)
                .load(avatar)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.girlpng)
                .into(profileImage);
    }

    // 获取我的动态列表
    private void get() {
        new Thread(() -> {
            // 构建URL
            String url = "https://api-store.openguet.cn/api/member/photo/share/myself?userId=" + userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
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

    // 我的动态列表回调
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            // 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String body = response.body().string();

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);

            // 确保code为200且有数据
            if (apiResponse != null && apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                List<Record> records = apiResponse.getData().getRecords();
                getActivity().runOnUiThread(() -> {
                    TextView releaseTextView = getActivity().findViewById(R.id.release_num);
                    releaseTextView.setText(String.valueOf(records.size()));
                });
            }
        }
    };

    // 获取我的关注列表
    private void fetchFollowedActivities() {
        new Thread(() -> {
            // 构建URL
            String url = "https://api-store.openguet.cn/api/member/photo/focus?userId=" + userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(callback2);
        }).start();
    }

    // 我的关注列表回调
    private final Callback callback2 = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            // 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String body = response.body().string();

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);

            // 确保code为200且有数据
            if (apiResponse != null && apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                List<Record> records = apiResponse.getData().getRecords();
                getActivity().runOnUiThread(() -> {
                    TextView focusTextView = getActivity().findViewById(R.id.focus_num);
                    focusTextView.setText(String.valueOf(records.size()));
                });
            }
        }
    };

    // 处理点击跳转到我的动态页面
    private void setupReleaseClicks() {
        releaseLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyDynamicsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 处理点击跳转到我的关注页面
    private void setupAboutClicks() {
        aboutLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FocusActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 处理点击跳转到我的草稿页面
    private void setupDownloadsClicks() {
        downloadsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DraftsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 处理点击跳转到我的收藏页面
    private void setupCollectionsClicks() {
        collectionsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CollectionActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 处理点击跳转到我的点赞页面
    private void setupHistoryClicks() {
        historyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LikesActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    // 处理点击跳转到设置页面
    private void setupSettingsClicks() {
        settingsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

}
