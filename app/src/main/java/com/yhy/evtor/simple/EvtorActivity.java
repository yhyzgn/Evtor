package com.yhy.evtor.simple;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yhy.evtor.Evtor;

import java.util.Random;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 23:36
 * version: 1.0.0
 * desc   :
 */
public class EvtorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evtor);

        Evtor.instance.register(this);

        TextView tvSingle = findViewById(R.id.tv_single);
        TextView tvDefSingle = findViewById(R.id.tv_def_single);
        TextView tvMulti = findViewById(R.id.tv_multi);
        TextView tvBroadcast = findViewById(R.id.tv_broadcast);

        tvSingle.setOnClickListener(v -> {
            Evtor.instance.subscribe("single").emit("single-data");
            log("============================================================================================================================");
            Evtor.instance.register(EvtorActivity.this);
        });

        tvDefSingle.setOnClickListener(v -> {
            Evtor.instance.subscribe("defSingle").emit("defSingle-data");
            log("============================================================================================================================");
        });

        tvMulti.setOnClickListener(v -> {
            String subscriber = "multi-" + (new Random().nextInt(2) + 1);
            Evtor.instance.subscribe(subscriber).emit(subscriber + "-data", "这是 data", "第三个参数");
            log("============================================================================================================================");
        });

        tvBroadcast.setOnClickListener(v -> {
            Evtor.instance.broadcast().emit("广播参数1", "广播参数-data", "这是 data", "第四个参数");
            log("============================================================================================================================");
        });
    }

    private void log(String text) {
        Log.i(getClass().getSimpleName(), text);
    }
}
