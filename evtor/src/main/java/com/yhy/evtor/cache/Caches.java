package com.yhy.evtor.cache;

import android.os.Handler;
import android.os.Looper;

import com.yhy.evtor.emitter.Emitter;
import com.yhy.evtor.subscribe.SubscriberMethod;
import com.yhy.evtor.subscribe.Subscription;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-14 00:32
 * version: 1.0.0
 * desc   : 全局缓存类
 */
@SuppressWarnings("ConstantConditions")
public class Caches {
    private static volatile Caches caches;

    // 事件处理器
    private Handler mHandler;
    // 全局订阅者
    private static final String SUBSCRIBER_BROADCAST = "subscriber-broadcast";
    // 类与观察者对象的映射
    private Map<Class<?>, WeakReference<Object>> mClassObserverMap;
    // 事件注册的类与事件订阅者关系map
    private Map<Class<?>, Set<Subscription>> mClassSubscriptionMap;
    // 订阅者名称与类和事件方法的映射map
    private Map<String, Map<Class<?>, Set<SubscriberMethod>>> mSubscriberMethodMap;
    // 保存订阅者与事件发射器
    private Map<String, Emitter> mEmitterMap;

    private Caches() {
        if (null != caches) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
        mHandler = new Handler(Looper.getMainLooper());
        mClassObserverMap = new LinkedHashMap<>();
        mClassSubscriptionMap = new LinkedHashMap<>();
        mSubscriberMethodMap = new LinkedHashMap<>();
        mEmitterMap = new LinkedHashMap<>();
    }

    /**
     * 获取单例缓存对象
     *
     * @return 缓存对象
     */
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

    /**
     * 获取事件处理器
     *
     * @return 事件处理器
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 获取全局广播事件订阅者名称
     *
     * @return 广播事件订阅者名称
     */
    public String getSubscriberBroadcast() {
        return SUBSCRIBER_BROADCAST;
    }

    /**
     * 注册观察者
     *
     * @param observer 观察者
     */
    public void observe(Object observer) {
        mClassObserverMap.put(observer.getClass(), new WeakReference<>(observer));
    }

    /**
     * 注销观察者
     *
     * @param observer 观察者
     */
    public void cancel(Object observer) {
        mClassObserverMap.remove(observer.getClass());
    }

    /**
     * 获取观察者对象
     *
     * @param clazz 观察者
     * @return 观察者对象
     */
    public Object getObserver(Class<?> clazz) {
        WeakReference<Object> reference = mClassObserverMap.get(clazz);
        return null != reference ? reference.get() : null;
    }

    /**
     * 缓存事件观察者与订阅者..们
     *
     * @param clazz           观察者类
     * @param subscriptionSet 订阅者
     */
    public void addClassAndSubscriptionSet(Class<?> clazz, Set<Subscription> subscriptionSet) {
        mClassSubscriptionMap.put(clazz, subscriptionSet);
    }

    /**
     * 获取观察者对应的订阅者
     *
     * @param clazz 观察者类
     * @return 订阅者
     */
    public Set<Subscription> getSubscriptionSet(Class<?> clazz) {
        return mClassSubscriptionMap.get(clazz);
    }

    /**
     * 缓存订阅者
     *
     * @param subscriber 订阅者名称
     * @param clazz      对应的观察者类
     * @param method     对应的方法信息
     */
    public void addSubscriberMethod(String subscriber, Class<?> clazz, SubscriberMethod method) {
        if (!mSubscriberMethodMap.containsKey(subscriber)) {
            mSubscriberMethodMap.put(subscriber, new LinkedHashMap<Class<?>, Set<SubscriberMethod>>());
        }
        if (!mSubscriberMethodMap.get(subscriber).containsKey(clazz)) {
            mSubscriberMethodMap.get(subscriber).put(clazz, new LinkedHashSet<SubscriberMethod>());
        }
        mSubscriberMethodMap.get(subscriber).get(clazz).add(method);
    }

    /**
     * 移除订阅者
     *
     * @param subscriber 订阅者名称
     * @param clazz      对应的观察者类
     */
    public void removeSubscriberMethod(String subscriber, Class<?> clazz) {
        if (mSubscriberMethodMap.containsKey(subscriber)) {
            mSubscriberMethodMap.get(subscriber).remove(clazz);
        }
        if (mSubscriberMethodMap.get(subscriber).isEmpty()) {
            mSubscriberMethodMap.remove(subscriber);
        }
    }

    /**
     * 获取订阅者对应的方法
     *
     * @param subscriber 订阅者名称
     * @return 方法
     */
    public Map<Class<?>, Set<SubscriberMethod>> getClassMethodSet(String subscriber) {
        return mSubscriberMethodMap.get(subscriber);
    }

    /**
     * 缓存全局广播订阅者
     *
     * @param clazz            对应的观察者类
     * @param subscriberMethod 对应的方法信息
     */
    public void addBroadcastSubscriberMethod(Class<?> clazz, SubscriberMethod subscriberMethod) {
        addSubscriberMethod(SUBSCRIBER_BROADCAST, clazz, subscriberMethod);
    }

    /**
     * 移除全局广播订阅者
     *
     * @param clazz 对应的观察者类
     */
    public void removeBroadcastSubscriberMethod(Class<?> clazz) {
        removeSubscriberMethod(SUBSCRIBER_BROADCAST, clazz);
    }

    /**
     * 缓存并获取事件发射器
     *
     * @param subscribe 订阅者
     * @return 事件发射器
     */
    public Emitter getEmitter(String subscribe) {
        Emitter emitter = mEmitterMap.get(subscribe);
        if (null == emitter) {
            emitter = Emitter.create(subscribe);
            mEmitterMap.put(subscribe, emitter);
        }
        return emitter;
    }
}
