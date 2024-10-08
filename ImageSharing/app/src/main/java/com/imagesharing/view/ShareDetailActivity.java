package com.imagesharing.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imagesharing.R;
import com.imagesharing.adapter.DetailImageAdapter;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ShareDetailActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView ivUsername;
    private TextView tvTitle;
    private TextView tvContent;
    private GridView gvImage;

    private Long userId;
    private Long shareId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("userId", 0);
        shareId = getIntent().getLongExtra("shareId", 0);

        ivAvatar = findViewById(R.id.iv_avatar);
        ivUsername = findViewById(R.id.tv_username);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        gvImage = findViewById(R.id.gv_image);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        initShareDetail();

    }

    // 初始化分享详情
    private void initShareDetail() {
        new Thread(() -> {
            String url = "https://api-store.openguet.cn/api/member/photo/share/detail?shareId=" + shareId + "&userId=" + userId;

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

            client.newCall(request).enqueue(callBackInitShareDetail);
        }).start();
    }

    // 分享详情回调
    private final Callback callBackInitShareDetail = new Callback() {
        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseBody = Objects.requireNonNull(response.body()).string();

            try {
                JSONObject jsonResponse = new JSONObject(responseBody);

                String msg = jsonResponse.getString("msg");

                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    updateShareDetail(data);

                }
                Log.d("initShareDetail", "分享详情请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("initShareDetail", "callBackInitShareDetail" + e.getMessage());
        }
    };

    // 初始化分享详情UI界面
    private void updateShareDetail(JSONObject data) {
        runOnUiThread(() -> {
            try {
                // 加载头像
                String avatar = data.getString("avatar");
                Log.d("updateShareDetail", "头像地址：" + avatar);
                Glide.with(this)
                        .load(avatar)
                        .apply(new RequestOptions().placeholder(R.drawable.girlpng))
                        .into(ivAvatar);

                ivUsername.setText(data.getString("username"));
                tvTitle.setText(data.getString("title"));
                tvContent.setText(data.getString("content"));

                // 加载图片内容
                JSONArray imageUrlList = data.getJSONArray("imageUrlList");
                loadImages(imageUrlList);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 加载图片内容
    private void loadImages(JSONArray imageUrlList) throws JSONException {
        gvImage.setAdapter(new DetailImageAdapter(this, imageUrlList));
    }

}