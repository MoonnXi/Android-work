package com.imagesharing.view;

import android.app.Activity;
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
import com.imagesharing.adapter.ChildCommentAdapter;
import com.imagesharing.adapter.FirstCommentAdapter;
import com.imagesharing.adapter.SecondCommentAdapter;
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

public class SecondCommentActivity extends AppCompatActivity {

    private ListView secondCommentList;
    private EditText etContent;
    private Button btnComment;
    private TextView contentNum;

    // 一级评论id
    private Long parentCommentId = 6653L;
    // 一级评论的用户id
    private Long parentCommentUserId = 1826656600132292608L;
    // 被回复的评论id
    private Long replyCommentId = 6653L;
    // 被回复的评论的用户id
    private Long replyCommentUserId = 1826656600132292608L;
    // 图文分享的主键id
    private Long shareId = 8009L;
    // 评论人userId
    private Long userId = 1826656600132292608L;
    // 评论人userId
    private String userName = "heguizhang";

    private Long commentId = 6653L;

    private int shareListCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_second_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        secondCommentList = findViewById(R.id.second_comment_list);
        etContent = findViewById(R.id.et_content);
        btnComment = findViewById(R.id.btn_comment);
        contentNum = findViewById(R.id.content_num);

        initSecondCommentList();
        btnCommentClick();

    }

    // 回复按钮点击事件
    private void btnCommentClick() {
        btnComment.setOnClickListener(v -> {
            if (etContent.getText().toString().isEmpty()) {
                Toast.makeText(this, "你还没有填写任何内容", Toast.LENGTH_SHORT).show();
            } else {
                addSecondComment();
                etContent.setText("");
                initSecondCommentList();
            }
        });
    }

    /**
     * 获取二级评论列表
     */
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

            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");

                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    JSONArray recordsArray = data.getJSONArray("records");

                    List<JSONObject> records = new ArrayList<>();

                    for (int i = 0; i < recordsArray.length(); i++) {
                        JSONObject record = recordsArray.getJSONObject(i);
                        records.add(record);
                    }

                    // 更新firstCommentList
                    updateSecondCommentList(records);

                }

                Log.d("SecondCommentActivity", "二级评论列表请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("SecondCommentActivity", "Error response: " + e.getMessage());
        }
    };

    private void updateSecondCommentList(List<JSONObject> records) {
        runOnUiThread(() -> {
            contentNum.setText(String.valueOf(records.size()));
            SecondCommentAdapter secondCommentAdapter = new SecondCommentAdapter(records, this);
            secondCommentList.setAdapter(secondCommentAdapter);
        });
    }

    /**
     * 添加二级评论
     */
    private void addSecondComment() {
        new Thread(() -> {
            String url = "http:///10.70.142.223:8080/comment/second";

            RequestQueue queue = Volley.newRequestQueue(this);

            JSONObject params = new JSONObject();
            try {
                params.put("content", etContent.getText().toString());
                params.put("parentCommentId", parentCommentId);
                params.put("parentCommentUserId", parentCommentUserId);
                params.put("replyCommentId", replyCommentId);
                params.put("replyCommentUserId", replyCommentUserId);
                params.put("shareId", shareId);
                params.put("userId", userId);
                params.put("userName", userName);

            } catch (Exception e) {
                Log.d("SecondCommentActivity", e.toString());
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {
                        Log.d("SecondCommentActivity", response.toString());
                        try {
                            shareListCode = response.getInt("code");
                            Log.d("shareListCode", "添加二级评论或回复请求 " + shareListCode);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }, error -> Log.d("SecondCommentActivity", error.toString())
            );

            queue.add(jsonObjectRequest);

        }).start();
    }


}