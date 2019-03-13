package com.yhy.evtor;

import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.emitter.EvtorEmitter;
import com.yhy.evtor.manager.ObserverManager;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:07
 * version: 1.0.0
 * desc   :
 */
public class Evtor {

    private static volatile Evtor evtor;

    private Evtor() {
        if (null != evtor) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
    }

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

    public Evtor register(Object observer) {
        ObserverManager.manager().register(observer);
        return this;
    }

    public void cancel(Object observer) {
        ObserverManager.manager().cancel(observer);
    }

    public EvtorEmitter evt() {
        return evt(Caches.caches().getSubscriberGlobal());
    }

    public EvtorEmitter evt(String subscriber) {
        return EvtorEmitter.create(subscriber);
    }
}
