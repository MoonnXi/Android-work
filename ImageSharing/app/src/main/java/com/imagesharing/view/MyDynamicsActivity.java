package com.imagesharing.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.imagesharing.R;
import com.imagesharing.adapter.MyDynamicsAdapter;
import com.imagesharing.bean.Record;
import com.imagesharing.response.ApiResponse;
import com.imagesharing.util.HeadersUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyDynamicsActivity extends AppCompatActivity {

    private ListView myDynamicsListView;
    private MyDynamicsAdapter myDynamicsAdapter;

    private TextView DynamicsNum;
    private TextView tvInfo;

    private static final String TAG = "MyDynamicsActivity";
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamics);

        userId = getIntent().getLongExtra("userId", -1);
        Log.d(TAG, "userId: " + userId);

        DynamicsNum = findViewById(R.id.dynamics_num);
        tvInfo = findViewById(R.id.tv_info);

        // 初始化ListView
        myDynamicsListView = findViewById(R.id.myDynamicsListView);
        myDynamicsAdapter = new MyDynamicsAdapter(this, new ArrayList<>());
        myDynamicsListView.setAdapter(myDynamicsAdapter);

        // 设置 item 之间的间距
        myDynamicsListView.setDivider(getResources().getDrawable(R.drawable.divider));
        myDynamicsListView.setDividerHeight(25); // 设置间距高度

        get();
    }

    private void get() {
        new Thread(() -> {
            // 构建URL
            String url = "https://api-store.openguet.cn/api/member/photo/share/myself?userId=" + userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
                    .add("Content-Type", "application/json")
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
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String body = response.body().string();
            Log.d(TAG, "Response Body: " + body);

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);

            // 确保code为200且有数据
            if (apiResponse != null && apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                List<Record> records = apiResponse.getData().getRecords();
                runOnUiThread(() -> {
                    // 更新UI
                    updateUI(records);
                });
            } else {
                runOnUiThread(() -> tvInfo.setText("暂无任何动态"));
            }
        }
    };

    // 更新UI的方法
    private void updateUI(List<Record> items) {
        String num = "(" + items.size() + ")";
        DynamicsNum.setText(num);
        myDynamicsAdapter.setData(items);
        myDynamicsAdapter.notifyDataSetChanged();
    }

}