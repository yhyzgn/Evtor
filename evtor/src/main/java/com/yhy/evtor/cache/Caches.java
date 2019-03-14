package com.yhy.evtor.cache;

import com.yhy.evtor.emitter.Emitter;
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
    private static final String SUBSCRIBER_BROADCAST = "subscriber-broadcast";
    // 类与观察者对象的映射
    private Map<Class<?>, Object> mClassObserverMap;
    // 事件注册的类与事件订阅者关系map
    private Map<Class<?>, Set<Subscription>> mClassSubscriptionMap;
    // 订阅者名称与类和事件方法的映射map
    private Map<String, Map<Class<?>, Set<SubscriberMethod>>> mSubscriberMethodMap;

    private Map<String, Emitter> mEmitterMap;

    private Caches() {
        if (null != caches) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
        mClassObserverMap = new LinkedHashMap<>();
        mClassSubscriptionMap = new LinkedHashMap<>();
        mSubscriberMethodMap = new LinkedHashMap<>();
        mEmitterMap = new LinkedHashMap<>();
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
        return SUBSCRIBER_BROADCAST;
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

    public Caches removeSubscriberMethod(String subscriber, Class<?> clazz) {
        if (mSubscriberMethodMap.containsKey(subscriber)) {
            mSubscriberMethodMap.get(subscriber).remove(clazz);
        }
        if (mSubscriberMethodMap.get(subscriber).isEmpty()) {
            mSubscriberMethodMap.remove(subscriber);
        }
        return this;
    }

    public Map<Class<?>, Set<SubscriberMethod>> getClassMethodSet(String subscriber) {
        return mSubscriberMethodMap.get(subscriber);
    }

    public Set<String> getSubscribers() {
        return mSubscriberMethodMap.keySet();
    }

    public void addBroadcastSubscriberMethod(Class<?> clazz, SubscriberMethod subscriberMethod) {
        for (String subscriber : mSubscriberMethodMap.keySet()) {
            Caches.caches().addSubscriberMethod(subscriber, clazz, subscriberMethod);
        }
        addSubscriberMethod(SUBSCRIBER_BROADCAST, clazz, subscriberMethod);
    }

    public void removeBroadcastSubscriberMethod(Class<?> clazz) {
        Set<String> subscriberSet = getSubscribers();
        for (String subscriber : subscriberSet) {
            removeSubscriberMethod(subscriber, clazz);
        }
        removeSubscriberMethod(SUBSCRIBER_BROADCAST, clazz);
    }

    public Caches addEmitter(String subscriber, Emitter emitter) {
        mEmitterMap.put(subscriber, emitter);
        return this;
    }

    public Emitter getEmitter(String subscribe) {
        Emitter emitter = mEmitterMap.get(subscribe);
        if (null == emitter) {
            emitter = Emitter.create(subscribe);
            addEmitter(subscribe, emitter);
        }
        return emitter;
    }
}
