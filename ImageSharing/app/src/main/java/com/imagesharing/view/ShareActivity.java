package com.imagesharing.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.imagesharing.R;
import com.imagesharing.adapter.NewImageAdapter;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareActivity extends AppCompatActivity {

    private String id;
    private String pUserId;
    private String imageCode;
    private String title;
    private String content;

    private Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> { //为根视图适应系统窗口边距
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //  Intent 中获取传递的参数
        id = getIntent().getStringExtra("id");
        pUserId = getIntent().getStringExtra("pUserId");
        imageCode = getIntent().getStringExtra("imageCode");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");

        // 获取 imageUrlList获取图片 URL 列表
        @SuppressWarnings("unchecked")
        List<String> imageUrlList = (List<String>) getIntent().getSerializableExtra("imageUrlList");

        EditText etTitle = findViewById(R.id.et_title);
        EditText etContent = findViewById(R.id.et_content);

        GridView gvImage = findViewById(R.id.gv_image);

        btnSend = findViewById(R.id.btn_send);

        NewImageAdapter adapter = new NewImageAdapter(this, imageUrlList);
        gvImage.setAdapter(adapter); //用于显示图片列表

        etTitle.setText(title);
        etContent.setText(content);

        sendShare();

    }

    private void sendShare() { //实现点击分享按钮后的操作，发送数据给服务器
        btnSend.setOnClickListener(v -> {
            String url = "https://api-store.openguet.cn/api/member/photo/share/change";

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
                params.put("content", content);
                params.put("id", id);
                params.put("imageCode", imageCode);
                params.put("pUserId", pUserId);
                params.put("title", title);

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

            client.newCall(request).enqueue(callbackSendShare);
        });
    }

    private final Callback callbackSendShare = new Callback() {  //处理响应回调
        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            Log.e("ShareActivity callbackSendShare", response.body().string());
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("ShareActivity callbackSendShare", e.getMessage());
        }
    };


}