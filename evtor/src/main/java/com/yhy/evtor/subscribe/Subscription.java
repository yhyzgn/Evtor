package com.yhy.evtor.subscribe;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:39
 * version: 1.0.0
 * desc   :
 */
public class Subscription {

    public String subscriber;
    public Set<SubscriberMethod> methodSet;

    public Subscription(String subscriber) {
        this.subscriber = subscriber;
        methodSet = new LinkedHashSet<>();
    }

    public Subscription addMethod(SubscriberMethod method) {
        methodSet.add(method);
        return this;
    }
}
