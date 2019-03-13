package com.yhy.evtor.simple;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 23:36
 * version: 1.0.0
 * desc   :
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Evtor.evtor().register(this);

        new Thread() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Evtor.evtor().evt("register").emmit("听说注册成功了。。");
                        Evtor.evtor().evt("register").emmit();
                    }
                }, 2000);
            }
        }.start();
    }

    @Subscribe("login")
    public void onLogin() {
    }

    @Subscribe("register")
    public void onRegister(String data) {
        log("LoginActivity register : " + data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Evtor.evtor().cancel(this);
    }

    private void log(String log) {
        Log.i(getClass().getSimpleName(), log);
    }
}
