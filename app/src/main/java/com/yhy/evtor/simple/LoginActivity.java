package com.yhy.evtor.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

        Evtor.get().register(this);
    }

    @Subscribe("login")
    public void onLogin() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Evtor.get().cancel(this);
    }
}
