package com.imagesharing.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.imagesharing.R;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "sp_login";
    private static final String KEY_USERNAME = "login_username";
    private static final String KEY_PASSWORD = "login_password";
    private static final String KEY_REMEMBER = "login_remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText etUsername = findViewById(R.id.et_username);
        TextInputEditText etPassword = findViewById(R.id.et_password);

        Button btnRegister = findViewById(R.id.bt_register);

        btnRegister.setOnClickListener(v -> {
            // 获取用户名和密码
            String username = Objects.requireNonNull(etUsername.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();

            if (username.isEmpty()) { // 判断用户名是否为空
                // lambda表达式的线程
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show());

            } else if (password.isEmpty()) { // 判断密码是否为空

                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show());

            } else {
                // 正常输入后,进行注册请求,并返回登录信息
                Register(password, username);

            }
        });

        Button btnCancel = findViewById(R.id.bt_cancel);

        btnCancel.setOnClickListener(v -> { // 取消注册，返回登录界面
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    /**
     * 注册请求
     * @param password 密码
     * @param username 用户名
     */
    private void Register(String password, String username) {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/user/register";

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
                params.put("password", password);
                params.put("username", username);

            } catch (Exception e) {
                Log.e("ShareActivity", Objects.requireNonNull(e.getMessage()));
            }

            String json = params.toString();
            RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(callbackRegister);

        }).start();
    }

    private final Callback callbackRegister = new Callback() {
        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");
                int code = jsonResponse.getInt("code");

                // 另起一个线程在界面显示信息
                runOnUiThread(() -> {
                    if (code == 200) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", responseBody);
                        // 清除记住密码
                        clearRemember();
                        // 注册成功,返回登录界面
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", responseBody);
                    }
                });


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("LoginActivity", "callbackRegister: " + e.getMessage());
        }
    };

    private void clearRemember() { // 清除记住密码
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_REMEMBER);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

}