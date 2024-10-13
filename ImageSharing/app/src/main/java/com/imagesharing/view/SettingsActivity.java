package com.imagesharing.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

        // 返回图标
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

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
        startActivity(intent);
    }

    private void navigateToAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void handleLogout() {

        finish();

        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
