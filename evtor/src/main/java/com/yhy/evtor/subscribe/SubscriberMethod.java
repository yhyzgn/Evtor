package com.yhy.evtor.subscribe;

import java.lang.reflect.Method;
import java.util.List;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:45
 * version: 1.0.0
 * desc   : 订阅者方法信息
 */
public class SubscriberMethod {
    /**
     * 对应的方法
     */
    public Method method;
    /**
     * 是否为广播事件
     */
    public boolean broadcast;
    /**
     * 订阅者..们
     */
    public List<String> subscriberList;

    /**
     * 构造方法
     *
     * @param method    方法
     * @param broadcast 是否广播
     */
    public SubscriberMethod(Method method, boolean broadcast) {
        this.method = method;
        this.broadcast = broadcast;
        this.method.setAccessible(true);
    }
}
