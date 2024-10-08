package com.imagesharing.view;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.imagesharing.R;
import com.imagesharing.adapter.FirstCommentAdapter;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstCommentActivity extends AppCompatActivity {

    private ListView firstCommentList;
    private EditText etContent;
    private Button btnComment;
    private TextView contentNum;

    private Long shareId = 8009L;
    private Long userId = 1826656600132292608L;
    private String userName = "heguizhang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_first_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstCommentList = findViewById(R.id.first_comment_list);
        etContent = findViewById(R.id.et_content);
        btnComment = findViewById(R.id.btn_comment);
        contentNum = findViewById(R.id.content_num);

        initFirstCommentList();

        btnCommentClick();

    }

    private void btnCommentClick() {
        btnComment.setOnClickListener(v -> {
            if (etContent.getText().toString().isEmpty()) {
                Toast.makeText(this, "你还没有写任何内容", Toast.LENGTH_SHORT).show();
            } else {
                addFirstComment();
                etContent.setText("");
                initFirstCommentList();
            }
        });
    }

    // 初始化一级评论列表
    private void initFirstCommentList() {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/comment/first?shareId=" + shareId;

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

            client.newCall(request).enqueue(callbackFirstCommentList);

        }).start();
    }

    // 获取一级评论列表回调
    private final Callback callbackFirstCommentList = new Callback() {

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

                    // 显示评论数量
                    String contentNumStr = "(" + recordsArray.length() + ")";

                    runOnUiThread(() -> contentNum.setText(contentNumStr));

                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject record = recordsArray.getJSONObject(i);
                        records.add(record);
                    }

                    Log.d("FirstCommentActivity", "一级评论列表请求" + records);

                    // 更新firstCommentList
                    updateFirstCommentList(records);

                }

                Log.d("FirstCommentActivity", "一级评论列表请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("FirstCommentActivity", "Error response: " + e.getMessage());
        }
    };

    private void updateFirstCommentList(List<JSONObject> records) {
        runOnUiThread(() -> {
            FirstCommentAdapter firstCommentAdapter = new FirstCommentAdapter(records, this);
            firstCommentList.setAdapter(firstCommentAdapter);
        });
    }


    // 添加一级评论
    private void addFirstComment() {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/comment/first";

            OkHttpClient client = new OkHttpClient();

            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
                    .add("Content-Type", "application/json")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 构建请求参数
            JSONObject params = new JSONObject();
            try {
                params.put("content", etContent.getText().toString());
                params.put("shareId", shareId);
                params.put("userId", userId);
                params.put("userName", userName);

            } catch (Exception e) {
                Log.d("FirstCommentActivity", e.toString());
            }

            String json = params.toString();
            RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));


            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(callbackAddFirstComment);
        }).start();
    }

    // 添加一级评论回调
    private final Callback callbackAddFirstComment = new Callback() {

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            Log.d("FirstCommentActivity", response.toString());
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.d("FirstCommentActivity", e.toString());
        }
    };

}
