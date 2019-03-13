package com.yhy.evtor.emitter;

import android.os.Handler;
import android.os.Looper;

import com.yhy.evtor.annotation.Subscribe;
import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.subscribe.SubscriberMethod;

import java.util.Map;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-14 00:32
 * version: 1.0.0
 * desc   :
 */
public class EvtorEmitter {
    private Handler mHandler;

    private String mSubscriber;
    private Map<Class<?>, Set<SubscriberMethod>> mClassMethodMap;

    private EvtorEmitter(String subscriber) {
        mHandler = new Handler(Looper.getMainLooper());
        mSubscriber = subscriber;
        mClassMethodMap = Caches.caches().getClassMethodSet(mSubscriber);
    }

    public static EvtorEmitter create(String subscriber) {
        return new EvtorEmitter(subscriber);
    }

    public void emmit() {
        emmit(null);
    }

    public void emmit(final Object data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // 1、订阅者被指定为专门订阅某个事件，
                Class<?> clazz;
                Set<SubscriberMethod> methodSet;
                Object observer;
                Subscribe subscribe;
                int methodParamCount;
                try {
                    for (Map.Entry<Class<?>, Set<SubscriberMethod>> et : mClassMethodMap.entrySet()) {
                        clazz = et.getKey();
                        methodSet = et.getValue();
                        observer = Caches.caches().getObserver(clazz);

                        for (SubscriberMethod sm : methodSet) {
                            subscribe = sm.method.getAnnotation(Subscribe.class);
                            methodParamCount = sm.method.getParameterTypes().length;
                            if (subscribe.value().length == 1) {
                                // 已经指定专门的订阅者，此时只执行无参数方法和一个参数的方法
                                if (methodParamCount == 0) {
                                    // 当前方法不需要参数
                                    sm.method.invoke(observer);
                                } else if (methodParamCount == 1) {
                                    // 需要一个参数来接收事件数据
                                    sm.method.invoke(observer, data);
                                } else {
                                    // 此类订阅者不支持多参数
                                    throw new IllegalArgumentException("The arguments of evtor emitter subscriber is illegal, maybe too many arguments, check it please.");
                                }
                            } else {
                                // 指定多个或者未指定任何订阅者
                                // 除包含单一订阅者情况外，还有第三种情况，即两个参数，需要将订阅者名称也发回去，以便判断是哪个订阅者接收到事件
                                if (methodParamCount == 0) {
                                    // 当前方法不需要参数
                                    sm.method.invoke(observer);
                                } else if (methodParamCount == 1) {
                                    // 需要一个参数来接收事件数据
                                    sm.method.invoke(observer, data);
                                } else if (methodParamCount == 2) {
                                    // 需要一个参数来接收事件数据
                                    sm.method.invoke(observer, mSubscriber, data);
                                } else {
                                    // 此类订阅者不支持多参数
                                    throw new IllegalArgumentException("The arguments of evtor emitter subscriber is illegal, maybe too many arguments, check it please.");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
