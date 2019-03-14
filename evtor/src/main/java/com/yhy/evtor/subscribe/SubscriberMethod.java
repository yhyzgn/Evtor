package com.yhy.evtor.subscribe;

import java.lang.reflect.Method;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:45
 * version: 1.0.0
 * desc   :
 */
public class SubscriberMethod {
    public Method method;

    public SubscriberMethod(Method method) {
        this.method = method;
        this.method.setAccessible(true);
    }
}
