package com.yhy.evtor.manager;

import com.yhy.evtor.annotation.Subscribe;
import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.subscribe.SubscriberMethod;
import com.yhy.evtor.subscribe.Subscription;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:34
 * version: 1.0.0
 * desc   :
 */
public class ObserverManager {
    // 单例实例
    private static volatile ObserverManager manager;

    private ObserverManager() {
        if (null != manager) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
    }

    public static ObserverManager manager() {
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
        // 缓存当前观察者
        Caches.caches().register(observer);
        // 扫描订阅者..们
        scanSubscriber(clazz, getSubscriptionSet(clazz));
    }

    public void cancel(Object observer) {
        Class<?> clazz = observer.getClass();
        // 注销缓存中关系
        Caches.caches().cancel(observer);
        // 注销订阅者与类和方法之间的映射关系
        Method[] methods = clazz.getDeclaredMethods();
        Subscribe subscribe;
        for (Method method : methods) {
            subscribe = method.getAnnotation(Subscribe.class);
            if (null != subscribe) {
                if (subscribe.value().length > 0) {
                    for (String subscriber : subscribe.value()) {
                        Caches.caches().removeSubscriberMethod(subscriber, clazz);
                    }
                } else {
                    Caches.caches().removeSubscriberMethod(Caches.caches().getSubscriberGlobal(), clazz);
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
                        Caches.caches().addSubscriberMethod(subscriber, clazz, subscriberMethod);
                    }
                } else {
                    // 全局事件
                    // 如果没有指定事件订阅者，则将方法添加到全局事件订阅
                    addSubscription(Caches.caches().getSubscriberGlobal(), subscriptionSet, subscriberMethod);
                    // 添加事件到订阅者映射关系map中
                    Caches.caches().addSubscriberMethod(Caches.caches().getSubscriberGlobal(), clazz, subscriberMethod);
                }
            }
        }
        Caches.caches().addClassAndSubscriptionSet(clazz, subscriptionSet);
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
        Set<Subscription> subscriptionSet = Caches.caches().getSubscriptionSet(clazz);
        if (null == subscriptionSet) {
            subscriptionSet = new LinkedHashSet<>();
        }
        return subscriptionSet;
    }
}
