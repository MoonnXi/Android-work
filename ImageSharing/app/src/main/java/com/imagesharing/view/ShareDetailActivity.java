package com.imagesharing.view;

import android.os.Bundle;
import android.util.Log;
<<<<<<< Updated upstream
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
=======
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
>>>>>>> Stashed changes

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imagesharing.R;
<<<<<<< Updated upstream
import com.imagesharing.adapter.CommentListAdapter;
=======
>>>>>>> Stashed changes
import com.imagesharing.adapter.DetailImageAdapter;
import com.imagesharing.util.HeadersUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
<<<<<<< Updated upstream
import java.util.ArrayList;
=======
>>>>>>> Stashed changes
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
<<<<<<< Updated upstream
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
=======
import okhttp3.OkHttpClient;
>>>>>>> Stashed changes
import okhttp3.Response;

public class ShareDetailActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView ivUsername;
    private TextView tvTitle;
    private TextView tvContent;
    private GridView gvImage;
<<<<<<< Updated upstream
    private ImageView ivImage;

    private ListView commentList;
    private EditText etContent;
    private Button btnComment;

    private Long userId;
    private Long shareId;
    private String avatar;
    private String username;
    private String userName;
=======

    private Long userId;
    private Long shareId;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 点击输入框时键盘不遮挡输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_share_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("userId", 0);
        shareId = getIntent().getLongExtra("shareId", 0);
<<<<<<< Updated upstream
        avatar = getIntent().getStringExtra("avatar");
        username = getIntent().getStringExtra("username");
        userName = getIntent().getStringExtra("userName");
=======
>>>>>>> Stashed changes

        ivAvatar = findViewById(R.id.iv_avatar);
        ivUsername = findViewById(R.id.tv_username);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        gvImage = findViewById(R.id.gv_image);
<<<<<<< Updated upstream
        ivImage = findViewById(R.id.iv_image);

        // 返回图标
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        // 评论区
        commentList = findViewById(R.id.comment_list);
        etContent = findViewById(R.id.et_content);
        btnComment = findViewById(R.id.btn_comment);

        initShareDetail();

        initCommentList();

        btnCommentClick();

    }

    // 评论按钮点击事件
    private void btnCommentClick() {
        btnComment.setOnClickListener(v -> {
            if (etContent.getText().toString().isEmpty()) {
                Toast.makeText(this, "你还没有写任何内容", Toast.LENGTH_SHORT).show();
            } else {
                addFirstComment();
                etContent.setText("");
                initCommentList();
            }
        });
    }

=======

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        initShareDetail();

    }

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            Log.e("initShareDetail", "callBackInitShareDetail " + e.getMessage());
=======
            Log.e("initShareDetail", "callBackInitShareDetail" + e.getMessage());
>>>>>>> Stashed changes
        }
    };

    // 初始化分享详情UI界面
    private void updateShareDetail(JSONObject data) {
        runOnUiThread(() -> {
            try {
                // 加载头像
<<<<<<< Updated upstream
                Glide.with(this)
                        .load(avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.girlpng)
                        .into(ivAvatar);

                ivUsername.setText(username);
=======
                String avatar = data.getString("avatar");
                Log.d("updateShareDetail", "头像地址：" + avatar);
                Glide.with(this)
                        .load(avatar)
                        .apply(new RequestOptions().placeholder(R.drawable.girlpng))
                        .into(ivAvatar);

                ivUsername.setText(data.getString("username"));
>>>>>>> Stashed changes
                tvTitle.setText(data.getString("title"));
                tvContent.setText(data.getString("content"));

                // 加载图片内容
                JSONArray imageUrlList = data.getJSONArray("imageUrlList");
<<<<<<< Updated upstream

                if (imageUrlList.length() == 1) {
                    Glide.with(this)
                            .load(imageUrlList.getString(0))
                            .apply(RequestOptions.centerCropTransform())
                            .into(ivImage);
                } else {
                    loadImages(imageUrlList);
                }
=======
                loadImages(imageUrlList);
>>>>>>> Stashed changes

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 加载图片内容
    private void loadImages(JSONArray imageUrlList) throws JSONException {
        gvImage.setAdapter(new DetailImageAdapter(this, imageUrlList));
    }

<<<<<<< Updated upstream
    // 初始化评论列表
    private void initCommentList() {
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

            client.newCall(request).enqueue(callBackInitCommentList);

        }).start();
    }

    // 评论列表回调
    private final Callback callBackInitCommentList = new Callback() {

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

                    updateCommentList(records);

                }
                Log.d("initShareDetail", "一级评论列表请求" + msg);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("initShareDetail", "callBackInitCommentList " + e.getMessage());
        }
    };

    // 初始化一级评论列表UI
    private void updateCommentList(List<JSONObject> records) {
        runOnUiThread(() -> {
            commentList.setAdapter(new CommentListAdapter(records, this));
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
            Log.d("ShareDetailActivity", response.toString());
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.d("ShareDetailActivity", e.toString());
        }
    };

=======
>>>>>>> Stashed changes
}