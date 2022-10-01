package com.yhy.evtor.simple;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Evtor.instance.register(this);

        TextView tvEvtor = findViewById(R.id.tv_evtor);
        tvEvtor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EvtorActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Evtor.instance.unregister(this);
    }

    @Subscribe("single")
    public void single() {
        log("单一订阅 ——　无数据");
    }

    @Subscribe("single")
    public void single(String data) {
        log("单一订阅 ——　有数据：" + data);
    }

    @Subscribe
    public void defSingle() {
        log("默认单一订阅 ——　无数据");
    }

    @Subscribe
    public void defSingle(String data) {
        log("默认单一订阅 ——　有数据：" + data);
    }

    @Subscribe({"multi-1", "multi-2"})
    public void multi() {
        log("多订阅 ——　无数据");
    }

    @Subscribe({"multi-1", "multi-2"})
    public void multi(String data) {
        log("多订阅 ——　有数据：" + data);
    }

    @Subscribe({"multi-1", "multi-2"})
    public void multi(String subscriber, String data) {
        log("多订阅 ——　订阅者：" + subscriber + "，有数据：" + data);
    }

    @Subscribe(broadcast = true)
    public void broadcast() {
        log("广播订阅 ——　无数据");
    }

    @Subscribe(broadcast = true)
    public void broadcast(String data) {
        log("广播订阅 ——　有数据：" + data);
    }

    @Subscribe(broadcast = true)
    public void broadcast(String subscriber, String data) {
        log("广播订阅 ——　订阅者：" + subscriber + "，有数据：" + data);
    }

    private void log(String text) {
        Log.i(getClass().getSimpleName(), text);
    }
}
