package com.yhy.evtor;

import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.emitter.Emitter;
import com.yhy.evtor.manager.ObserverManager;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:07
 * version: 1.0.0
 * desc   : 事件总线
 */
public class Evtor {

    private static volatile Evtor evtor;

    private Evtor() {
        if (null != evtor) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
    }

    /**
     * 获取单例实例
     *
     * @return 事件总线对象
     */
    public static Evtor evtor() {
        if (null == evtor) {
            synchronized (Evtor.class) {
                if (null == evtor) {
                    evtor = new Evtor();
                }
            }
        }
        return evtor;
    }

    /**
     * 注册事件观察者
     *
     * @param observer 事件观察者
     */
    public void observe(Object observer) {
        ObserverManager.manager().observe(observer);
    }

    /**
     * 注销事件观察者
     *
     * @param observer 时间观察者
     */
    public void cancel(Object observer) {
        ObserverManager.manager().cancel(observer);
    }

    /**
     * 获取广播事件发射器
     *
     * @return 事件发射器
     */
    public Emitter subscribe() {
        return subscribe(Caches.caches().getSubscriberBroadcast());
    }

    /**
     * 获取订阅者事件发射器
     *
     * @param subscriber 接收事件的订阅者名称
     * @return 事件发射器
     */
    public Emitter subscribe(String subscriber) {
        return Caches.caches().getEmitter(subscriber);
    }
}
