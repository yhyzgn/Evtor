package com.yhy.evtor.simple;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Evtor.get().register(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }, 3000);
    }

    @Subscribe("login")
    public void onLogin() {
        Toast.makeText(this, "空参数Login’", Toast.LENGTH_SHORT).show();
    }

    @Subscribe("register")
    public void onRegister(String userId) {
        Toast.makeText(this, "onRegister : " + userId, Toast.LENGTH_SHORT).show();
    }

    @Subscribe({"login", "register", "update"})
    public void onUpdate(String subscriber, String nickname) {
        Toast.makeText(this, "update : " + subscriber + " : " + nickname, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onLogout() {
        Toast.makeText(this, "空参数onLogout’", Toast.LENGTH_SHORT).show();
    }
}
