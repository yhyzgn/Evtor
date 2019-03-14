package com.yhy.evtor.simple;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Evtor.evtor().register(this);

        new Thread() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "跳转登录页面", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }, 3000);
            }
        }.start();
    }

    @Subscribe("login")
    public void onLogin() {
        log("空参数Login");
    }

    @Subscribe("register")
    public void onRegister(String data) {
        log("onRegister : " + data);
    }

    @Subscribe("register")
    public void onRegister2(String data) {
        log("onRegister2 : " + data);
    }

    @Subscribe({"login", "register", "update"})
    public void onUpdate(String subscriber, String data) {
        log("update : " + subscriber + " : " + data);
    }

    @Subscribe
    public void register() {
        log("以方法名为订阅者，哈哈哈");
    }

    @Subscribe
    public void onLogout() {
        log("空参数onLogout");
    }

    @Subscribe(broadcast = true)
    public void global() {
        log("这是一个无参数全局方法");
    }

    @Subscribe(broadcast = true)
    public void global(String data) {
        log("有参数全局方法，data = " + data);
    }

    @Subscribe(broadcast = true)
    public void global(String subscriber, String data) {
        log("有两个参数全局方法，subscriber = " + subscriber + "，data = " + data);
    }

    private void log(String log) {
        Log.i(getClass().getSimpleName(), log);
    }
}
