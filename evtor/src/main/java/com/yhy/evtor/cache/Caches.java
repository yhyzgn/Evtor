package com.yhy.evtor.cache;

import com.yhy.evtor.subscribe.SubscriberMethod;
import com.yhy.evtor.subscribe.Subscription;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-14 00:32
 * version: 1.0.0
 * desc   :
 */
@SuppressWarnings("ConstantConditions")
public class Caches {
    private static volatile Caches caches;

    // 全局订阅者
    public String mSubscriberGlobal = "subscriber-global";
    // 类与观察者对象的映射
    private Map<Class<?>, Object> mClassObserverMap;
    // 事件注册的类与事件订阅者关系map
    private Map<Class<?>, Set<Subscription>> mClassSubscriptionMap;
    // 订阅者名称与类和事件方法的映射map
    private Map<String, Map<Class<?>, Set<SubscriberMethod>>> mSubscriberMethodMap;

    private Caches() {
        if (null != caches) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
        mClassObserverMap = new LinkedHashMap<>();
        mClassSubscriptionMap = new LinkedHashMap<>();
        mSubscriberMethodMap = new LinkedHashMap<>();
    }

    public static Caches caches() {
        if (null == caches) {
            synchronized (Caches.class) {
                if (null == caches) {
                    caches = new Caches();
                }
            }
        }
        return caches;
    }

    public String getSubscriberGlobal() {
        return mSubscriberGlobal;
    }

    public Caches register(Object observer) {
        mClassObserverMap.put(observer.getClass(), observer);
        return this;
    }

    public Object getObserver(Class<?> clazz) {
        return mClassObserverMap.get(clazz);
    }

    public Caches cancel(Object observer) {
        mClassObserverMap.remove(observer.getClass());
        return this;
    }

    public Caches addClassAndSubscriptionSet(Class<?> clazz, Set<Subscription> subscriptionSet) {
        mClassSubscriptionMap.put(clazz, subscriptionSet);
        return this;
    }

    public Set<Subscription> getSubscriptionSet(Class<?> clazz) {
        return mClassSubscriptionMap.get(clazz);
    }

    public Caches addSubscriberMethod(String subscriber, Class<?> clazz, SubscriberMethod method) {
        if (!mSubscriberMethodMap.containsKey(subscriber)) {
            mSubscriberMethodMap.put(subscriber, new LinkedHashMap<Class<?>, Set<SubscriberMethod>>());
        }
        if (!mSubscriberMethodMap.get(subscriber).containsKey(clazz)) {
            mSubscriberMethodMap.get(subscriber).put(clazz, new LinkedHashSet<SubscriberMethod>());
        }
        mSubscriberMethodMap.get(subscriber).get(clazz).add(method);
        return this;
    }

    public Map<Class<?>, Set<SubscriberMethod>> getClassMethodSet(String subscriber) {
        return mSubscriberMethodMap.get(subscriber);
    }

    public Caches removeSubscriberMethod(String subscriber, Class<?> clazz) {
        if (mSubscriberMethodMap.containsKey(subscriber)) {
            mSubscriberMethodMap.get(subscriber).remove(clazz);
        }
        if (mSubscriberMethodMap.get(subscriber).isEmpty()) {
            mSubscriberMethodMap.remove(subscriber);
        }
        return this;
    }
}
