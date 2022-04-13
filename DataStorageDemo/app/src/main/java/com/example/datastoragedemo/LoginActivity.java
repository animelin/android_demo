package com.example.datastoragedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = (Button) findViewById(R.id.button);
        EditText accountEdit = (EditText) findViewById(R.id.edit_text);
        EditText passwordEdit = (EditText) findViewById(R.id.edit_text1);
        CheckBox rememberPass =(CheckBox) findViewById(R.id.checkbox);
//        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        Boolean isremember = pref.getBoolean("remember_password",false);
        if (isremember){
            String account = pref.getString("account","");
            String password = pref.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (account.equals("admin") && password.equals("123456")) {
                    SharedPreferences.Editor editor = pref.edit();
                    if (rememberPass.isChecked()) { // 检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else {
                        editor.putBoolean("remember_password", false);
                        editor.clear();
                    }
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "用户或密码错误！请重试",
                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,
                        "跳转注册", Toast.LENGTH_LONG).show();
            }
        });
    }
}