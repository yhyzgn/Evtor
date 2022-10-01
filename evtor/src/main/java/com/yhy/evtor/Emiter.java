package com.yhy.evtor;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件发射器
 * <p>
 * Created on 2022-10-02 00:13
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Emiter {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final List<Observer> observerList;

    private Emiter(List<Observer> observerList) {
        this.observerList = observerList;
    }

    /**
     * 执行具体事件
     *
     * @param parameters 参数列表
     */
    public void emit(Object... parameters) {
        if (null == observerList || observerList.isEmpty()) {
            return;
        }
        observerList.iterator().forEachRemaining(it -> {
            mHandler.post(() -> {
                if (null != it && null != it.getTarget() && null != it.getMethod()) {
                    Method method = it.getMethod();
                    method.setAccessible(true);
                    try {
                        // 对齐方法参数列表
                        Object[] alignedParameters = alignParameters(method, parameters);
                        if (null == alignedParameters || alignedParameters.length == 0) {
                            // 执行空参方法
                            method.invoke(it.getTarget());
                            return;
                        }
                        // 参数匹配的方法
                        method.invoke(it.getTarget(), alignedParameters);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e.getMessage());
                    }
                }
            });
        });
    }

    /**
     * 对齐参数（实参）列表
     * <p>
     * 形参与实参对齐（目前仅实现数量上对齐，未对类型进行匹配）
     *
     * @param method     方法
     * @param parameters 实参列表
     * @return 对齐后的实参列表
     */
    private Object[] alignParameters(Method method, Object[] parameters) {
        Type[] types = method.getGenericParameterTypes();
        if (types.length == 0) {
            return null;
        }

        int needCount = types.length;
        int realCount = null == parameters ? 0 : parameters.length;
        // 形参数量 - 实参数量
        int delta = needCount - realCount;
        int abs = Math.abs(delta);
        if (delta > 0) {
            // 形参数量 > 实参数量
            // 需要在实参列表后追加 abs 位 null 来达到参数数量匹配的条件
            List<Object> pmList = null != parameters ? Arrays.stream(parameters).collect(Collectors.toList()) : new ArrayList<>();
            for (int i = 0; i < abs; i++) {
                pmList.add(null);
            }
            return pmList.toArray();
        }
        if (delta < 0) {
            // 形参数量 < 实参数量
            // 需要从实参列表中截取前 needCount 位重新构建成参数数组
            return Arrays.copyOf(parameters, needCount);
        }
        // 刚刚好
        return parameters;
    }

    /**
     * 创建一个事件发射器
     *
     * @param observerList 事件订阅者
     * @return 发射器对象
     */
    public static Emiter with(List<Observer> observerList) {
        return new Emiter(observerList);
    }
}
