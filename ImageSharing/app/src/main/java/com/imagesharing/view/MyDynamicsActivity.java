package com.imagesharing.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.imagesharing.R;
import com.imagesharing.adapter.MyDynamicsAdapter;
import com.imagesharing.bean.Record;
import com.imagesharing.fragment.MeFragment;
import com.imagesharing.response.ApiResponse;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyDynamicsActivity extends AppCompatActivity {

    private ListView myDynamicsListView;
    private MyDynamicsAdapter myDynamicsAdapter;
    public static final int REQUEST_ME_FRAGMENT = 1;
    private TextView DynamicsNum;
    private TextView tvInfo;
    private TextView userName;
    private static final String TAG = "MyDynamicsActivity";
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dynamics);

        userId = getIntent().getLongExtra("userId", -1);
        Log.d(TAG, "userId88888: " + userId);

        DynamicsNum = findViewById(R.id.dynamics_num);
        tvInfo = findViewById(R.id.tv_info);

        // 初始化ListView
        myDynamicsListView = findViewById(R.id.myDynamicsListView);
        myDynamicsAdapter = new MyDynamicsAdapter(this, new ArrayList<>());


        myDynamicsAdapter.setOnItemClickListener((shareId, position) -> {
            System.out.println("SHAREId00000000000: " + shareId);
            System.out.println("SHAREId00000000000: " + position);
            delete(shareId, position);
        });

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

    private void delete(Long shareId, int position) {
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/photo/share/delete?shareId=" + shareId + "&userId=" + userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "0c43d325bd4c4077a2ef71afd51a2ac9")
                    .add("appSecret", "1796311d4cb76a76e4c309e0ca1c6ff5a13c0")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(deleteCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    // 删除回调
    private final Callback deleteCallback = new Callback() {
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
            Log.d(TAG, "Delete Response Body: " + body);

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);

            if (apiResponse != null && apiResponse.getCode() == 200) {

                runOnUiThread(() -> {
                    //apiResponse.getData().getId().toString().hashCode();
                    myDynamicsAdapter.notifyDataSetChanged();
                    Toast.makeText(MyDynamicsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MyDynamicsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                });
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ME_FRAGMENT && resultCode == RESULT_OK) {
            // MyDynamicsActivity 结束后重新请求 MeFragment 的数据
        }
    }

    // 更新UI的方法
    private void updateUI(List<Record> items) {
        String num = "(" + items.size() + ")";
        DynamicsNum.setText(num);
        myDynamicsAdapter.setData(items);
        myDynamicsAdapter.notifyDataSetChanged();
    }
}
