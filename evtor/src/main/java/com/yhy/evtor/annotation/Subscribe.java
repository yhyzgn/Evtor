package com.yhy.evtor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件订阅
 * <p>
 * Created on 2022-10-01 22:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Subscribe {

    /**
     * 订阅一个事件
     *
     * @return 事件名，默认使用 {@link Subscribe} 所注解的方法名称
     */
    String[] value() default {};

    /**
     * 是否接收广播事件
     *
     * @return 是否接收广播事件¬
     */
    boolean broadcast() default false;
}
