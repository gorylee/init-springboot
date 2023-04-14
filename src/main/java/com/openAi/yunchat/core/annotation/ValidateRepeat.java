package com.openAi.yunchat.core.annotation;

import java.lang.annotation.*;

/**
 * 重复提交校验注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateRepeat {
    long value() default 3L;//默认3秒
}
