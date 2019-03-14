package com.yhy.evtor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author : 颜洪毅
 * e-mail : yhyzgn@gmail.com
 * time   : 2019-03-13 22:04
 * version: 1.0.0
 * desc   : 订阅事件接受者
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Subscribe {

    /**
     * 订阅者..们的名称..们
     *
     * @return 订阅者..们的名称..们
     */
    String[] value() default {};

    /**
     * 是否是全局广播事件
     * <p>
     * 默认为 false
     * <p>
     * 如果订阅为广播事件的话，value 值将被忽略
     *
     * @return 是否是广播事件
     */
    boolean broadcast() default false;
}
