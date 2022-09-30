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

    private TextView tvSingle;
    private TextView tvDefSingle;
    private TextView tvMulti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evtor);

        tvSingle = findViewById(R.id.tv_single);
        tvDefSingle = findViewById(R.id.tv_def_single);
        tvMulti = findViewById(R.id.tv_multi);

        tvSingle.setOnClickListener(v -> {
            Evtor.evtor().subscribe("single").emit("single-data");
            log("============================================================================================================================");
        });

        tvDefSingle.setOnClickListener(v -> {
            Evtor.evtor().subscribe("defSingle").emit("defSingle-data");
            log("============================================================================================================================");
        });

        tvMulti.setOnClickListener(v -> {
            String subscriber = "multi-" + (new Random().nextInt(2) + 1);
            Evtor.evtor().subscribe(subscriber).emit(subscriber + "-data");
            log("============================================================================================================================");
        });
    }

    private void log(String text) {
        Log.i(getClass().getSimpleName(), text);
    }
}
