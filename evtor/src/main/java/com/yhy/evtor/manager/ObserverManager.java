package com.yhy.evtor.manager;

import com.yhy.evtor.annotation.Subscribe;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:34
 * version: 1.0.0
 * desc   :
 */
public class ObserverManager {
    private static final String SUBSCRIBER_GLOBAL = "subscriber-global";

    private static volatile ObserverManager manager;

    // 事件注册的类与事件订阅者关系map
    private static final Map<Class<?>, Set<Subscription>> CLASS_SUBSCRIPTION_MAP = new LinkedHashMap<>();
    // 订阅者名称与类和事件方法的映射map
    private static final Map<String, Map<Class<?>, Set<SubscriberMethod>>> SUBSCRIBER_METHOD_MAP = new LinkedHashMap<>();

    private ObserverManager() {
        if (null != manager) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
    }

    public static ObserverManager get() {
        if (null == manager) {
            synchronized (ObserverManager.class) {
                if (null == manager) {
                    manager = new ObserverManager();
                }
            }
        }
        return manager;
    }

    public void register(Object observer) {
        Class<?> clazz = observer.getClass();
        Set<Subscription> subscriptionSet = getSubscriptionSet(clazz);
        scanSubscriber(clazz, subscriptionSet);
    }

    public void cancel(Object observer) {
        Class<?> clazz = observer.getClass();
        // 注销缓存中关系
        if (CLASS_SUBSCRIPTION_MAP.containsKey(clazz)) {
            CLASS_SUBSCRIPTION_MAP.remove(clazz);
        }
        // 注销订阅者与类和方法之间的映射关系
        Method[] methods = clazz.getDeclaredMethods();
        Subscribe subscribe;
        for (Method method : methods) {
            subscribe = method.getAnnotation(Subscribe.class);
            if (null != subscribe) {
                if (subscribe.value().length > 0) {
                    for (String subscriber : subscribe.value()) {
                        if (SUBSCRIBER_METHOD_MAP.containsKey(subscriber) && SUBSCRIBER_METHOD_MAP.get(subscriber).containsKey(clazz)) {
                            SUBSCRIBER_METHOD_MAP.get(subscriber).remove(clazz);
                        }
                    }
                } else {
                    if (SUBSCRIBER_METHOD_MAP.containsKey(SUBSCRIBER_GLOBAL) && SUBSCRIBER_METHOD_MAP.get(SUBSCRIBER_GLOBAL).containsKey(clazz)) {
                        SUBSCRIBER_METHOD_MAP.get(SUBSCRIBER_GLOBAL).remove(clazz);
                    }
                }
            }
        }
    }

    private void scanSubscriber(Class<?> clazz, Set<Subscription> subscriptionSet) {
        Method[] methods = clazz.getDeclaredMethods();
        Subscribe subscribe;
        SubscriberMethod subscriberMethod;
        for (Method method : methods) {
            subscribe = method.getAnnotation(Subscribe.class);
            if (null != subscribe) {
                subscriberMethod = new SubscriberMethod(method);
                if (subscribe.value().length > 0) {
                    // 指定事件
                    for (String subscriber : subscribe.value()) {
                        // 添加事件到订阅管理中
                        addSubscription(subscriber, subscriptionSet, subscriberMethod);
                        // 添加事件到订阅者映射关系map中
                        addSubscriberMethod(subscriber, clazz, subscriberMethod);
                    }
                } else {
                    // 全局事件
                    // 如果没有指定事件订阅者，则将方法添加到全局事件订阅
                    addSubscription(SUBSCRIBER_GLOBAL, subscriptionSet, subscriberMethod);
                    // 添加事件到订阅者映射关系map中
                    addSubscriberMethod(SUBSCRIBER_GLOBAL, clazz, subscriberMethod);
                }
            }
        }
        CLASS_SUBSCRIPTION_MAP.put(clazz, subscriptionSet);
    }

    private void addSubscriberMethod(String subscriber, Class<?> clazz, SubscriberMethod method) {
        if (!SUBSCRIBER_METHOD_MAP.containsKey(subscriber)) {
            SUBSCRIBER_METHOD_MAP.put(subscriber, new LinkedHashMap<Class<?>, Set<SubscriberMethod>>());
        }
        if (!SUBSCRIBER_METHOD_MAP.get(subscriber).containsKey(clazz)) {
            SUBSCRIBER_METHOD_MAP.get(subscriber).put(clazz, new LinkedHashSet<SubscriberMethod>());
        }
        SUBSCRIBER_METHOD_MAP.get(subscriber).get(clazz).add(method);
    }

    private void addSubscription(String subscriber, Set<Subscription> subscriptionSet, SubscriberMethod subscriberMethod) {
        Subscription subscription = findSubscription(subscriber, subscriptionSet);
        if (null == subscription) {
            subscription = new Subscription(subscriber);
        }
        subscription.addMethod(subscriberMethod);
        subscriptionSet.add(subscription);
    }

    public Subscription findSubscription(String subscriber, Set<Subscription> subscriptionSet) {
        for (Subscription subscription : subscriptionSet) {
            if (subscriber.equals(subscription.subscriber)) {
                return subscription;
            }
        }
        return null;
    }

    private Set<Subscription> getSubscriptionSet(Class<?> clazz) {
        Set<Subscription> subscriptionSet = CLASS_SUBSCRIPTION_MAP.get(clazz);
        if (null == subscriptionSet) {
            subscriptionSet = new LinkedHashSet<>();
        }
        return subscriptionSet;
    }
}
