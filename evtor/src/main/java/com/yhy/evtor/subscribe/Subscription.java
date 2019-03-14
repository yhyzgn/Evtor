package com.yhy.evtor.subscribe;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:39
 * version: 1.0.0
 * desc   : 订阅者及其方法
 */
public class Subscription {

    /**
     * 订阅者
     */
    public String subscriber;
    /**
     * 对应的方法列表
     */
    public Set<SubscriberMethod> methodSet;

    /**
     * 构造方法
     *
     * @param subscriber 订阅者
     */
    public Subscription(String subscriber) {
        this.subscriber = subscriber;
        methodSet = new LinkedHashSet<>();
    }

    /**
     * 添加订阅方法
     *
     * @param method 方法信息
     * @return 当前订阅者
     */
    public Subscription addMethod(SubscriberMethod method) {
        methodSet.add(method);
        return this;
    }
}
