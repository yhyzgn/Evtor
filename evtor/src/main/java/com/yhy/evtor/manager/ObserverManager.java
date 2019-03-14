package com.yhy.evtor.manager;

import com.yhy.evtor.annotation.Subscribe;
import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.subscribe.SubscriberMethod;
import com.yhy.evtor.subscribe.Subscription;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:34
 * version: 1.0.0
 * desc   : 观察者管理器
 */
public class ObserverManager {
    // 单例实例
    private static volatile ObserverManager manager;

    private ObserverManager() {
        if (null != manager) {
            throw new UnsupportedOperationException("Singleton class can not be instantiated.");
        }
    }

    /**
     * 获取单例对象
     *
     * @return 单例管理器
     */
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

    /**
     * 注册观察者
     *
     * @param observer 观察者，一般为this
     */
    public void register(Object observer) {
        Class<?> clazz = observer.getClass();
        // 缓存当前观察者
        Caches.caches().register(observer);
        // 扫描订阅者..们
        scanSubscriber(clazz, getSubscriptionSet(clazz));
    }

    /**
     * 注销观察者
     *
     * @param observer 观察者，一般为this
     */
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
                    if (subscribe.broadcast()) {
                        // 注销全局广播事件
                        Caches.caches().removeBroadcastSubscriberMethod(clazz);
                    } else {
                        // 未指定订阅者名称，默认以方法名为名称
                        Caches.caches().removeSubscriberMethod(method.getName(), clazz);
                    }
                }
            }
        }
    }

    /**
     * 扫描事件订阅者
     *
     * @param clazz           观察者对应的类
     * @param subscriptionSet 观察者中的所有订阅者
     */
    private void scanSubscriber(Class<?> clazz, Set<Subscription> subscriptionSet) {
        Method[] methods = clazz.getDeclaredMethods();
        Subscribe subscribe;
        SubscriberMethod subscriberMethod;
        for (Method method : methods) {
            subscribe = method.getAnnotation(Subscribe.class);
            if (null != subscribe) {
                subscriberMethod = new SubscriberMethod(method, subscribe.broadcast());
                // 将订阅者..们也都保存在订阅方法上
                subscriberMethod.subscriberList = new LinkedList<>(Arrays.asList(subscribe.value()));

                if (subscribe.broadcast()) {
                    // 添加全局的广播事件
                    Caches.caches().addBroadcastSubscriberMethod(clazz, subscriberMethod);
                } else {
                    if (subscribe.value().length > 0) {
                        // 指定事件
                        for (String subscriber : subscribe.value()) {
                            // 添加事件到订阅管理中
                            addSubscription(subscriber, subscriptionSet, subscriberMethod);
                            // 添加事件到订阅者映射关系map中
                            Caches.caches().addSubscriberMethod(subscriber, clazz, subscriberMethod);
                        }
                    } else {
                        // 未指定订阅者名称，默认以方法名为名称
                        addSubscription(method.getName(), subscriptionSet, subscriberMethod);
                        // 添加事件到订阅者映射关系map中
                        Caches.caches().addSubscriberMethod(method.getName(), clazz, subscriberMethod);
                    }
                }
            }
        }
        Caches.caches().addClassAndSubscriptionSet(clazz, subscriptionSet);
    }

    /**
     * 添加订阅关系
     *
     * @param subscriber       订阅者
     * @param subscriptionSet  观察者中的所有订阅者
     * @param subscriberMethod 订阅的方法信息
     */
    private void addSubscription(String subscriber, Set<Subscription> subscriptionSet, SubscriberMethod subscriberMethod) {
        Subscription subscription = findSubscription(subscriber, subscriptionSet);
        if (null == subscription) {
            subscription = new Subscription(subscriber);
        }
        subscription.addMethod(subscriberMethod);
        subscriptionSet.add(subscription);
    }

    /**
     * 获取订阅关系
     *
     * @param subscriber      订阅者
     * @param subscriptionSet 观察者中的所有订阅者
     * @return 订阅关系
     */
    public Subscription findSubscription(String subscriber, Set<Subscription> subscriptionSet) {
        for (Subscription subscription : subscriptionSet) {
            if (subscriber.equals(subscription.subscriber)) {
                return subscription;
            }
        }
        return null;
    }

    /**
     * 获取观察者对应的所有订阅者
     *
     * @param clazz 观察者的类
     * @return 对应的所有订阅者
     */
    private Set<Subscription> getSubscriptionSet(Class<?> clazz) {
        Set<Subscription> subscriptionSet = Caches.caches().getSubscriptionSet(clazz);
        if (null == subscriptionSet) {
            subscriptionSet = new LinkedHashSet<>();
        }
        return subscriptionSet;
    }
}
