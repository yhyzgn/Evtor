package com.yhy.evtor;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

/**
 * 观察者，订阅了事件的对象信息
 * <p>
 * Created on 2022-10-01 22:54
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Observer {
    private Class<?> clazz;
    private String name;
    private Method method;
    private boolean broadcast;
    private SoftReference<Object> target;

    public Class<?> getClazz() {
        return clazz;
    }

    public Observer setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getName() {
        return name;
    }

    public Observer setName(String name) {
        this.name = name;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public Observer setMethod(Method method) {
        this.method = method;
        return this;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public Observer setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
        return this;
    }

    public Object getTarget() {
        return target.get();
    }

    public Observer setTarget(Object target) {
        this.target = new SoftReference<>(target);
        return this;
    }
}
