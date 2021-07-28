package com.weibo.live.rtmp.message.media;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
// 注解的生命周期(定义被它所注解的注解保留多久)
@Retention(RetentionPolicy.RUNTIME)
// 这个注解只是用来标注生成javadoc的时候是否会被记录。
@Documented
public @interface Order {
    int value() default 0;
}
