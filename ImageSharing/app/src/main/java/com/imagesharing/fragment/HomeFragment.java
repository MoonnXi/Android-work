package com.imagesharing.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imagesharing.R;
import com.imagesharing.adapter.ListAdapter;
import com.imagesharing.util.HeadersUtil;
import com.imagesharing.view.SearchActivity;

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

public class HomeFragment extends Fragment {

    private Long userId;
    private String username;
    private SearchView searchView;
    private ListAdapter adapter;
    private TextView text;
    private GridView shareList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {

    }
   
    public HomeFragment(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        shareList = view.findViewById(R.id.share_list);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        text = view.findViewById(R.id.tv_search);

        searchView = view.findViewById(R.id.search_view1);
        searchView.setOnSearchClickListener(v -> {
            text.setVisibility(View.INVISIBLE);
        });
        searchView.setOnCloseListener(() -> {
            text.setVisibility(View.VISIBLE);
            return false;
        });
        // 设置查询文本变化监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户点击搜索按钮或者按下键盘上的搜索键时触发

                String keyword = query.trim();
                System.out.println("搜索关键词：" + keyword);
                if (!keyword.isEmpty()) {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
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

        // 初始化首页列表数据
        initShareList();

        // 处理下拉刷新页面
        reLoadShareList();

        return view;
    }

    // 下拉刷新页面数据
    private void reLoadShareList() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 开始刷新时显示提示
            swipeRefreshLayout.setRefreshing(true);
            new Thread(() -> {
                // 模拟网络请求时间
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 在主线程更新UI
                getActivity().runOnUiThread(() -> {
                    // 重新加载数据
                    initShareList();
                    // 刷新结束，关闭刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
    }

    // 初始化首页列表数据
    public void initShareList() {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/share" + "?userId="  + userId;

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

            client.newCall(request).enqueue(callback);

        }).start();
    }

    // 初始化首页列表回调
    private final Callback callback = new Callback() {

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");

                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    JSONArray recordsArray = data.getJSONArray("records");
                    // 遍历记录并添加到列表
                    List<JSONObject> records = new ArrayList<>();

                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject record = recordsArray.getJSONObject(i);
                        records.add(record);
                    }

                    // 更新ListView
                    updateShareList(records);
                }
                Log.d("HomeFragment", "分享列表请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("HomeFragment", "Error parsing JSON response: " + e.getMessage());
        }
    };

    // 构建首页列表适配器
    private void updateShareList(List<JSONObject> records) {
        getActivity().runOnUiThread(() -> {
            adapter = new ListAdapter(records, requireContext(), userId, username);
            shareList.setAdapter(adapter);
        });
    }

}