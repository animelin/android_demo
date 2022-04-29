package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class School extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        Button school = (Button) findViewById(R.id.school);
        WebView webView = (WebView) findViewById(R.id.web_view);

        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url); // 根据传入的参数再去加载新的网页
                        return true;    // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
                    }
                });
                webView.loadUrl("http://www.gdpu.edu.cn");
            }
        });
    }
}