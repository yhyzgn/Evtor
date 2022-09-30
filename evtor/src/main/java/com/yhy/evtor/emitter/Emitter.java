package com.yhy.evtor.emitter;

import com.yhy.evtor.cache.Caches;
import com.yhy.evtor.subscribe.SubscriberMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-14 00:32
 * version: 1.0.0
 * desc   : 事件发射器
 */
public class Emitter {
    private final String mSubscriber;

    private Emitter(String subscriber) {
        mSubscriber = subscriber;
    }

    /**
     * 创建发射器
     *
     * @param subscriber 订阅者名称
     * @return 事件发射器
     */
    public static Emitter create(String subscriber) {
        return new Emitter(subscriber);
    }

    /**
     * 发射事件
     * <p>
     * 不发送数据
     */
    public void emit() {
        emit(null);
    }

    /**
     * 发射事件
     *
     * @param data 需要传递的数据
     */
    public void emit(final Object data) {
        Caches.instance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                // 1、订阅者被指定为专门订阅某个事件，
                try {
                    // 先执行指定订阅者的事件
                    emit(Caches.instance().getClassMethodSet(mSubscriber));
                    // 再执行全局广播事件
                    emit(Caches.instance().getClassMethodSet(Caches.instance().getSubscriberBroadcast()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * 发射事件
             * @param classMethodMap 订阅者方法
             * @throws InvocationTargetException 方法执行异常
             * @throws IllegalAccessException 方法访问异常
             */
            private void emit(Map<Class<?>, Set<SubscriberMethod>> classMethodMap) throws InvocationTargetException, IllegalAccessException {
                if (null == classMethodMap) {
                    return;
                }
                Class<?> clazz;
                Set<SubscriberMethod> methodSet;
                Object observer;
                for (Map.Entry<Class<?>, Set<SubscriberMethod>> et : classMethodMap.entrySet()) {
                    clazz = et.getKey();
                    methodSet = et.getValue();
                    observer = Caches.instance().getObserver(clazz);
                    if (null == observer) continue;
                    for (SubscriberMethod method : methodSet) {
                        // 触发事件
                        emit(method, observer);
                    }
                }
            }

            /**
             * 发射事件
             * @param method 方法信息
             * @param observer 观察者
             * @throws InvocationTargetException 方法执行异常
             * @throws IllegalAccessException 方法访问异常
             */
            private void emit(SubscriberMethod method, Object observer) throws InvocationTargetException, IllegalAccessException {
                int paramCount = method.method.getParameterTypes().length;
                if (paramCount == 0) {
                    // 当前方法不需要参数
                    method.method.invoke(observer);
                } else if (paramCount == 1 && null != data) {
                    // 需要一个参数来接收事件数据
                    method.method.invoke(observer, data);
                } else if ((method.broadcast || method.subscriberList.size() > 1) && paramCount == 2 && null != data) {
                    // 如果是广播事件，或者订阅者指定为多个时，才允许有两个参数的订阅者方法
                    // 需要一个参数来接收订阅者名称，另一个参数来接收事件数据
                    method.method.invoke(observer, mSubscriber, data);
                } else if (null != data) {
                    // 此类订阅者不支持多参数
                    throw new IllegalArgumentException("The arguments of evtor emitter subscriber is illegal, maybe too many arguments, check it please.");
                }
            }
        });
    }
}
