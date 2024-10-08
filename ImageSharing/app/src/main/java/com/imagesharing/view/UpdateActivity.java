package com.imagesharing.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imagesharing.R;
import com.imagesharing.response.ApiService;
import com.imagesharing.util.HeadersUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    private static final String TAG = "UpdateActivity";

    private final ApiService apiService = new ApiService();
    private final Gson gson = new Gson();
    private EditText etUsername;
    private EditText etAvatar;
    private EditText etIntroduce;
    private EditText etSex;
    private Long userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // 初始化 EditText 和 Button
        etUsername = findViewById(R.id.et_username);
        etAvatar = findViewById(R.id.et_avatar);
        etIntroduce = findViewById(R.id.et_introduce);
        etSex = findViewById(R.id.et_sex);
        Button btnSave = findViewById(R.id.btn_save);

        // 设置点击监听器
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private void post() {
        new Thread(() -> {
            // 获取用户输入的内容
            String username = etUsername.getText().toString().trim();
            String avatar = etAvatar.getText().toString().trim();
            String introduce = etIntroduce.getText().toString().trim();
            String sex = etSex.getText().toString().trim();

            // 检查是否有必填字段为空
            if (username.isEmpty() || avatar.isEmpty() || introduce.isEmpty() || sex.isEmpty()) {
                // 如果有为空的字段，显示提示信息
                runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "参数不能为空", Toast.LENGTH_SHORT).show());
                return;
            }

            // url路径
            String url = "https://api-store.openguet.cn/api/member/photo/user/update";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", HeadersUtil.APP_ID)
                    .add("appSecret", HeadersUtil.APP_SECRET)
                    .add("Content-Type", "application/json")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

//            userId = getIntent().getLongExtra("userId", -1);

            userId = getIntent().getLongExtra("userId", -1);
            // 请求体
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", avatar);
            bodyMap.put("id", userId);
            bodyMap.put("introduce", introduce);
            bodyMap.put("sex", sex);
            bodyMap.put("username", username);
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            // 请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();

            OkHttpClient client = new OkHttpClient();
            // 发起请求，传入 callback 进行回调
            client.newCall(request).enqueue(callback);
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            // 请求失败处理
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            // 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>() {}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(() -> Toast.makeText(UpdateActivity.this, "修改成功", Toast.LENGTH_SHORT).show());
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody<T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody() {}

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
