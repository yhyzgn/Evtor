package com.yhy.evtor.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * <p>
 * Created on 2022-10-01 22:25
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ReflectionUtils {

    /**
     * 获取类中被注解的方法列表
     * <p>
     * 包括各级父类，顶层到 {@code Object.class} 为止
     *
     * @param clazz      某个类
     * @param annotation 注解类
     * @return 方法列表
     */
    static List<Method> methodListOf(Class<?> clazz, Class<? extends Annotation> annotation) {
        return methodListOf(clazz, annotation, Object.class);
    }

    /**
     * 获取类中被注解的方法列表
     * <p>
     * 包括各级父类，顶层到指定的类为止，默认到 {@code Object.class} 类
     *
     * @param clazz      某个类
     * @param annotation 注解类
     * @param until      直到该类为止
     * @return 方法列表
     */
    static List<Method> methodListOf(Class<?> clazz, Class<? extends Annotation> annotation, Class<?> until) {
        if (null == clazz || null != until && clazz == until || clazz == Object.class) {
            return Collections.emptyList();
        }
        Method[] mds = clazz.getDeclaredMethods();
        List<Method> mdList = Arrays.stream(mds).filter(it -> it.isAnnotationPresent(annotation)).collect(Collectors.toList());
        // 递归找父类
        mdList.addAll(methodListOf(clazz.getSuperclass(), annotation, until));
        return mdList;
    }

    /**
     * 让某个类下被注解的所有方法执行某逻辑
     * <p>
     * 包括各级父类，顶层到指定的类为止，默认到 {@code Object.class} 类
     *
     * @param clazz      某个类
     * @param annotation 注解类
     * @param callback   执行回调
     */
    static void doMethodWith(Class<?> clazz, Class<? extends Annotation> annotation, MethodCallback callback) {
        doMethodWith(clazz, annotation, callback, Object.class);
    }

    /**
     * 让某个类下被注解的所有方法执行某逻辑
     * <p>
     * 包括各级父类，顶层到指定的类为止，默认到 {@code Object.class} 类
     *
     * @param clazz      某个类
     * @param annotation 注解类
     * @param callback   执行回调
     * @param until      直到该类为止
     */
    static void doMethodWith(Class<?> clazz, Class<? extends Annotation> annotation, MethodCallback callback, Class<?> until) {
        List<Method> mdList = methodListOf(clazz, annotation, until);
        mdList.forEach(it -> {
            if (null != callback) {
                callback.doWith(it.getDeclaringClass(), it);
            }
        });
    }

    /**
     * 方法执行回调
     */
    @FunctionalInterface
    interface MethodCallback {

        /**
         * 方法执行回调
         *
         * @param clazz  当前递归的类
         * @param method 当前方法
         */
        void doWith(Class<?> clazz, Method method);
    }
}
