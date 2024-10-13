package com.imagesharing.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.imagesharing.R;

public class SettingsActivity extends AppCompatActivity {

    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userId = getIntent().getLongExtra("userId", -1);

        // 设置点击事件
        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.editProfileLinearLayout).setOnClickListener(v -> navigateToEditProfile());
        findViewById(R.id.aboutUsLinearLayout).setOnClickListener(v -> navigateToAboutUs());
        findViewById(R.id.logoutLinearLayout).setOnClickListener(v -> handleLogout());
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void navigateToAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void handleLogout() {
        // 显示退出登录的消息
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();

        // 清除所有活动并启动新的登录活动
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 销毁当前活动
        finish();
    }
}
