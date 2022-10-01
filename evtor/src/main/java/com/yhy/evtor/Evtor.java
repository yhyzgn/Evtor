package com.yhy.evtor;

import android.os.Handler;
import android.os.Looper;

import com.yhy.evtor.annotation.Subscribe;
import com.yhy.evtor.utils.ReflectionUtils;

import java.util.List;

/**
 * 事件总线对外接口
 * <p>
 * Created on 2022-10-01 22:49
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Evtor {
    public final static Evtor instance = new Evtor() {
    };

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Evtor() {
    }

    public void register(Object target) {
        ReflectionUtils.doMethodWith(target.getClass(), Subscribe.class, (clazz, method) -> {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (null != subscribe) {
                    String[] names = subscribe.value();
                    if (names.length == 0) {
                        names = new String[]{method.getName()};
                    }
                    for (String name : names) {
                        Observer obs = new Observer()
                                .setClazz(clazz)
                                .setMethod(method)
                                .setName(name)
                                .setTarget(target)
                                .setBroadcast(subscribe.broadcast());
                        Registry.instance.register(obs);
                    }
                }
            }
        });
    }

    public void unregister(Object target) {
        Registry.instance.unregister(target);
    }

    /**
     * 获取订阅者事件执行器
     *
     * @param name 订阅者名称
     * @return 事件执行器
     */
    public Emiter subscribe(String name) {
        List<Observer> observerList = Registry.instance.observerList(name);
        return Emiter.with(observerList);
    }

    /**
     * 获取广播执行器
     *
     * @return 广播事件执行器
     */
    public Emiter broadcast() {
        List<Observer> broadcastList = Registry.instance.broadcastList();
        return Emiter.with(broadcastList);
    }
}
