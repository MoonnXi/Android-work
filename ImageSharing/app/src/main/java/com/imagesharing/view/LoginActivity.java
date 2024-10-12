package com.imagesharing.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "sp_login";
    private static final String KEY_USERNAME = "login_username";
    private static final String KEY_PASSWORD = "login_password";
    private static final String KEY_REMEMBER = "login_remember";

    private Long id;
    private String username;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.bt_login);
        cbRemember = findViewById(R.id.cb_remember);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loginClick();

        reload();

        TextView tvRegister = findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(v -> { // 处理注册标签点击事件
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    // 登录按钮点击事件
    private void loginClick() {
        btnLogin.setOnClickListener(v -> { // 处理登录按钮点击事件
            // 获取用户名和密码
            String username = Objects.requireNonNull(etUsername.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();

            if (username.isEmpty()) { // 判断用户名是否为空
                // lambda表达式的线程
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show());

            } else if (password.isEmpty()) { // 判断密码是否为空

                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show());

            } else { // 正常输入后,进行登录请求,并返回登录信息
                login(password, username);
            }

        });
    }

    // 重新加载上次登录信息
    private void reload() {
        boolean isRemember = sharedPreferences.getBoolean(KEY_REMEMBER, false);

        if (isRemember) { // 如果上次点击了记住密码，则加载用户名和密码
            String username = sharedPreferences.getString(KEY_USERNAME, "");
            String password = sharedPreferences.getString(KEY_PASSWORD, "");
            etUsername.setText(username);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }
    }

    /**
     * 登录请求
     * @param password 密码
     * @param username 用户名
     */
    private void login(String password, String username) {
        new Thread(() -> {

            String url = "https://api-store.openguet.cn/api/member/photo/user/login?password=" + password + "&username=" + username;

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
                params.put("username", username);
                params.put("password", password);

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

            client.newCall(request).enqueue(callbackLogin);

            if (cbRemember.isChecked()) { // 如果勾选了记住密码，则保存用户名和密码
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_USERNAME, username);
                editor.putString(KEY_PASSWORD, password);
                editor.putBoolean(KEY_REMEMBER, cbRemember.isChecked());
                editor.apply();
            }

        }).start();
    }

    // 登录请求回调
    private final Callback callbackLogin = new Callback() {

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");

                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    id = data.getLong("id");
                    username = data.getString("username");
                    avatar = data.getString("avatar");

                    // 另起一个线程在界面显示信息
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    });

                    activityJump();
                }

                Log.d("LoginActivity ", msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.d("LoginActivity", "Error: " + e.getMessage());
        }
    };

    private void activityJump() {
        // 启动 NavigationActivity 并传递 userId
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("userId", id);
        intent.putExtra("username", username);
        intent.putExtra("avatar", avatar);
        startActivity(intent);
        finish();
    }

}