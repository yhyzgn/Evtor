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

    static List<Method> methodListOf(Class<?> clazz, Class<? extends Annotation> annotation) {
        return methodListOf(clazz, annotation, Object.class);
    }

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

    static void doMethodWith(Class<?> clazz, Class<? extends Annotation> annotation, MethodCallback callback) {
        doMethodWith(clazz, annotation, callback, Object.class);
    }

    static void doMethodWith(Class<?> clazz, Class<? extends Annotation> annotation, MethodCallback callback, Class<?> until) {
        List<Method> mdList = methodListOf(clazz, annotation, until);
        mdList.forEach(it -> {
            if (null != callback) {
                callback.doWith(it.getDeclaringClass(), it);
            }
        });
    }

    @FunctionalInterface
    interface MethodCallback {

        void doWith(Class<?> clazz, Method method);
    }
}
